FROM eclipse-temurin:26-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
