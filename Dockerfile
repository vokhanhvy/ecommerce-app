FROM openjdk:11-jre-slim
COPY target/ecommerce.jar ecommerce.jar
ENTRYPOINT ["java", "-jar", "ecommerce.jar"]