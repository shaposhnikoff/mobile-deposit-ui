# mobile-deposit-ui

```
spring run app.groovy
```

Aside from standard Spring boot properties (eg: `--server.port=8081`), the following custom properties are supported:

* `api.host` - configures the domain to send the API request
* `api.proto` - configures the protocol to send the API request
* `api.port` - configures the port to send the API request

Per [Spring boot convention](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html), the custom properties can be set a number of ways.

Here's a full example:
```
mvn clean package && java -jar target/mobile-deposit-ui-1.0-SNAPSHOT.jar --api.host="bank-api.beedemo.net" --api.proto="http" --api.port="8080" --server.port=8081
```

Note you'll need to install the Spring Boot CLI https://spring.io/guides/gs/spring-boot-cli-and-js/#scratch
