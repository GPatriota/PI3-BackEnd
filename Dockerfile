# Stage 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copia os arquivos do Maven
COPY pom.xml .
COPY src ./src

# Faz o build da aplicação (pulando os testes para build mais rápido)
RUN mvn clean package -DskipTests

# Stage 2: Imagem final otimizada
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o JAR da aplicação do stage de build
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta (o Render vai usar a variável PORT)
EXPOSE 8080

# Comando para rodar a aplicação com porta dinâmica
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-Dserver.port=${PORT:-8080}", "-jar", "app.jar"]