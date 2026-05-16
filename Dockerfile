FROM eclipse-temurin:17.jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre
WORKDIR /app
copy --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "jar", "app.jar"]