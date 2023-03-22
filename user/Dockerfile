FROM adoptopenjdk/openjdk11
COPY /build/libs/user-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080/tcp
ENTRYPOINT ["java", "-jar", "app.jar"]