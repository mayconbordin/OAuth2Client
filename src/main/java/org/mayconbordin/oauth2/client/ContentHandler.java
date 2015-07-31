package org.mayconbordin.oauth2.client;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.util.EntityUtils;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Class for handling content from HTTP entities, with auto-detection and parsing 
 * of JSON, XML and URL encoded content types.
 * 
 * @author Maycon Bordin <mayconbordin@gmail.com>
 */
public class ContentHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ContentHandler.class);
    private static JSONParser jsonParser;
    private static DocumentBuilder xmlDocumentBuilder;
    
    /**
     * Extracts the HTTP entity from the response by using the {@link #handleEntity(org.apache.http.HttpEntity)} method.
     * 
     * @param response The HTTP response object.
     * @return The parsed data from the entity as a key/value hashmap.
     * @throws OAuth2Exception If the entity can't be parsed or the content type is not supported.
     * @throws IOException If the entity content can't be read as a string.
     */
    public static Map<String, Object> handleResponse(HttpResponse response) throws OAuth2Exception, IOException {
        return handleEntity(response.getEntity());
    }
    
    /**
     * Parses the HTTP entity based on its content type and returns the parsed data.
     * 
     * @param entity The HTTP entity object.
     * @return The parsed data from the entity as a key/value hashmap.
     * @throws OAuth2Exception If the entity can't be parsed or the content type is not supported.
     * @throws IOException If the entity content can't be read as a string.
     */
    public static Map<String, Object> handleEntity(HttpEntity entity) throws OAuth2Exception, IOException {
        String contentType = OAuth2Constants.JSON_CONTENT;
        String content = readHttpEntity(entity);

        if (entity.getContentType() != null) {
            contentType = entity.getContentType().getValue();
        }

        if (contentType.contains(OAuth2Constants.JSON_CONTENT)) {
            return handleJsonResponse(content);
        } else if (contentType.contains(OAuth2Constants.URL_ENCODED_CONTENT)) {
            return handleURLEncodedResponse(content);
        } else if (contentType.contains(OAuth2Constants.XML_CONTENT)) {
            return handleXmlResponse(content);
        } else {
            throw new UnsupportedContentType(contentType);
        }
    }

    /**
     * Handles a JSON object string.
     * 
     * @param content The string to be parsed.
     * @return The parsed data.
     * @throws ParseErrorException
     */
    public static Map<String, Object> handleJsonResponse(String content) throws ParseErrorException {
        try {
            return (Map<String, Object>) getJsonParser().parse(content);
        } catch (ParseException e) {
            LOG.error("JSON parse error: " + e.getMessage());
            throw new ParseErrorException("json", content, e);
        }
    }

    /**
     * Handles an URL encoded string.
     * 
     * @param content The string to be parsed.
     * @return The parsed data.
     */
    public static Map<String, Object> handleURLEncodedResponse(String content) {
        Map<String, Object> response = new HashMap<>();

        List<NameValuePair> list = URLEncodedUtils.parse(content, StandardCharsets.UTF_8);

        for (NameValuePair pair : list) {
            response.put(pair.getName(), pair.getValue());
        }

        return response;
    }

    /**
     * Handles a XML string.
     * 
     * @param content The string to be parsed.
     * @return The parsed data.
     * @throws ParseErrorException 
     */
    public static Map<String, Object> handleXmlResponse(String content) throws ParseErrorException {
        Map<String, Object> oauthResponse = new HashMap<>();
        
        try {
            Document doc = getXmlDocumentBuilder().parse(new InputSource(new StringReader(content)));
            parseXmlDoc(null, doc, oauthResponse);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("XML parse error: " + e.getMessage());
            throw new ParseErrorException("xml", content, e);
        }
        
        return oauthResponse;
    }

    /**
     * Traverses the XML document, adding each tag and its contents to a map object.
     * 
     * @param element The current element to be traversed or null if is the root of the document.
     * @param doc The document to be traversed.
     * @param response The object to be used to store the XML tags.
     */
    protected static void parseXmlDoc(Element element, Document doc, Map<String, Object> response) {
        NodeList child = null;
        
        if (element == null) {
            child = doc.getChildNodes();
        } else {
            child = element.getChildNodes();
        }
        
        for (int j = 0; j < child.getLength(); j++) {
            if (child.item(j).getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                org.w3c.dom.Element childElement = (org.w3c.dom.Element) child.item(j);
                
                if (childElement.hasChildNodes()) {
                    response.put(childElement.getTagName(), childElement.getTextContent());
                    parseXmlDoc(childElement, null, response);
                }

            }
        }
    }
    
    /**
     * Reads the contents of an HTTP entity into an string.
     * 
     * @param entity
     * @return
     * @throws IOException 
     */
    public static String readHttpEntity(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }

    protected static JSONParser getJsonParser() {
        if (jsonParser == null) {
            jsonParser = new JSONParser();
        }
        return jsonParser;
    }

    protected static DocumentBuilder getXmlDocumentBuilder() throws ParserConfigurationException {
        if (xmlDocumentBuilder == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            xmlDocumentBuilder = factory.newDocumentBuilder();
        } else {
            xmlDocumentBuilder.reset();
        }
        
        return xmlDocumentBuilder;
    }
}
