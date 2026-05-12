FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace
COPY . .
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app
ENV JAVA_OPTS="-XX:MaxRAMPercentage=55 -XX:InitialRAMPercentage=20 -XX:MaxMetaspaceSize=192m -XX:ReservedCodeCacheSize=64m -XX:+ExitOnOutOfMemoryError"
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
