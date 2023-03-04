FROM openjdk:11
ENV JAVA_OPTS="-Dspring.datasource.url=jdbc:h2:file:./db"

ADD . .
RUN ./gradlew build -x check 

FROM openjdk:11
COPY --from=0 target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
