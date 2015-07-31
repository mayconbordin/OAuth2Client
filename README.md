# OAuth2Client
[![Build Status](https://travis-ci.org/mayconbordin/OAuth2Client.svg)](https://travis-ci.org/mayconbordin/OAuth2Client) [![Release](https://img.shields.io/github/release/mayconbordin/OAuth2Client.svg?label=JitPack)](https://jitpack.io/#mayconbordin/OAuth2Client)

A simple OAuth2 client for Java.

This library has its API based on [danielsz/oauth2-client](https://github.com/danielsz/oauth2-client).

## Installation

Add the JitPack repository to your `<repositories>` list in the `pom.xml` file:

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```

Then add the dependency to your `pom.xml` file:

```xml
<dependency>
  <groupId>com.github.mayconbordin</groupId>
  <artifactId>OAuth2Client</artifactId>
  <version>1.0</version>
</dependency>
```

For more information on how to build the library with other tools (Gradle, Sbt, Leiningen) see the [JitPack documentation](https://jitpack.io/docs/BUILDING/).

## Usage

### Client credentials grant

```java
OAuth2Client client = OAuth2Client.withClientCredentialsGrant(
    "username", "password", "client_id", "client_secret",
    "scope_one,scope_two", "http://host/oauth/access_token"
);

AccessToken token = client.getAccessToken();
```

### Password grant

```java
OAuth2Client client = OAuth2Client.withPasswordGrant(
    "username", "password", "client_id", "client_secret",
    "scope_one,scope_two", "http://host/oauth/access_token"
);

AccessToken token = client.getAccessToken();
String resource = token.getResource("http://localhost/api/user_info");
```

### Thrown Exceptions

 - `UnauthorizedClientException`: if the client is not authorized to make the request.
 - `ServerErrorException`: if the server has thrown and error.
 - `ResourceNotFoundException`: if the request URI could not be found.
 - `ParseErrorException`: if the response content can't be parsed.
 - `UnsupportedContentType`: if the content type return from the provider is not supported.
 - `InvalidRequestException`: if the request to the provider is malformed, with missing arguments.
 - `OAuth2Exception`: any other error that occurs. All the other exceptions extend this one.
