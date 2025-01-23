# Usar uma imagem base do OpenJDK
FROM openjdk:21-jdk-slim

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo JAR gerado pelo Maven para o contêiner
COPY target/clickclack-0.0.1-SNAPSHOT.jar /app/clickclack.jar

# Comando para rodar a aplicação
CMD ["java", "-jar", "clickclack.jar"]

# Exponha a porta que sua aplicação vai rodar
EXPOSE 8080
