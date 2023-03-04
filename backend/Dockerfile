FROM openjdk:11
ENV JAVA_OPTS="-Dspring.datasource.url=jdbc:h2:file:./db"
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false"

ADD . .
RUN ./gradlew --no-daemon build -x check 
FROM openjdk:11
COPY --from=0 build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
