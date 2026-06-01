# Etapa 1: Build (Compilação)
# Usamos uma imagem do Maven com Java 21 para compilar o código
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build


# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo de configuração do Maven e as dependências (otimiza o cache)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o cṕdigo fonte e gera o arquivo .jar (pulando os testes para ser mais rápido)
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: RunTime (Execução)
# Usamos uma imagem leve apenas com o JRE (Java Runtime Environment)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# INSTALADOR DO PACOTE DO APLINE
RUN apk --no-cache add curl

# Criação de um usuário chamado "aluno"
RUN addgroup -S aluno && adduser -S aluno -G aluno
USER aluno

# Agora o COPY precida dar permissão para esse usuário
COPY --from=build --chown=aluno:aluno /app/target/*.jar app.jar

# Copia o .jar gerado na etapa anterior para esta nova imagem
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta que a aplicação usa
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT [ "java", "-jar", "app.jar"]
