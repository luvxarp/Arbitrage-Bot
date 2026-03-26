# faz o build da aplicacao
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# o -DskipTests é pra nao rodar os testes no build do docker, vai mais rapido
RUN ./mvnw clean package -DskipTests 2>/dev/null || \
    (apk add --no-cache maven && mvn clean package -DskipTests)

# imagem final menor, so com o JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/arb-bot-*.jar app.jar

EXPOSE 8080

# coloca suas chaves aqui ou no docker-compose
ENV SPRING_PROFILES_ACTIVE=default

ENTRYPOINT ["java", "-jar", "app.jar"]
