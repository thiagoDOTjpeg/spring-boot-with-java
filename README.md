# Spring Boot com Java
Este repositório contém uma API RESTful completa desenvolvida com Spring Boot e Java. O projeto demonstra as melhores práticas de desenvolvimento, incluindo arquitetura em camadas, segurança com JWT, versionamento de API, migrações de banco de dados com Flyway, e muito mais.

# ✨ Funcionalidades
CRUD de Pessoas e Livros: Operações completas de Criação, Leitura, Atualização e Exclusão para as entidades Person e Book.

Autenticação e Autorização: Segurança robusta com Spring Security e JSON Web Tokens (JWT).

Paginação e Ordenação: Suporte para paginação e ordenação nos endpoints de listagem.

Upload e Download de Arquivos: Endpoints para upload de arquivos únicos e múltiplos, e download dos mesmos.

Exportação de Dados: Funcionalidade para exportar dados de pessoas em formatos PDF, XLSX e CSV.

Importação de Dados: Capacidade de importar dados de pessoas a partir de arquivos XLSX e CSV.

Envio de E-mails: Serviço para envio de e-mails simples e com anexos.

Geração de QR Code: Geração de QR Code para a URL do perfil do usuário.

Documentação com Swagger: Documentação da API gerada automaticamente com SpringDoc (Swagger).

CORS: Configuração de Cross-Origin Resource Sharing (CORS) para permitir requisições de diferentes origens.

Tratamento de Exceções: Manipulação centralizada de exceções para respostas de erro consistentes.

Testes: Testes de unidade e de integração para garantir a qualidade do código.

Migrações de Banco de Dados: Versionamento e gerenciamento do banco de dados com Flyway.

Containerização com Docker: Configuração para executar a aplicação e o banco de dados em contêineres Docker.

# 🚀 Tecnologias Utilizadas
Backend:

Java 21

Spring Boot 3.4.2

Spring Security

Spring Data JPA

Maven

Banco de Dados:

MySQL 9.1.0

Flyway (para migrações)

Testes:

JUnit 5

Mockito

RestAssured

Testcontainers

## Documentação:

SpringDoc (Swagger)

## Outros:

Docker e Docker Compose

Dozer Mapper (para mapeamento de objetos)

Apache POI (para manipulação de arquivos Excel)

Apache Commons CSV (para manipulação de arquivos CSV)

JasperReports (para geração de relatórios em PDF)

ZXing (para geração de QR Code)

# ✅ Pré-requisitos
Antes de começar, você precisará ter instalado em sua máquina:

JDK 21

Maven

# Docker e Docker Compose

▶️ Como Executar o Projeto
Com Docker (Recomendado)
A maneira mais simples de executar a aplicação é utilizando o Docker Compose.

Bash

# Clone o repositório
`git clone https://github.com/thiagoDOTjpeg/spring-boot-with-java.git`

# Navegue até o diretório do docker-compose.yml
`cd spring-boot-with-java`

# Suba os contêineres
`docker-compose up -d`
A aplicação estará disponível em http://localhost:80.

# Localmente
Você também pode executar a aplicação diretamente na sua máquina.

Bash

# Navegue até o diretório do projeto
`cd spring-boot-with-java/spring-boot-with-java`

# Instale as dependências
`mvn clean install`

# Execute a aplicação
`mvn spring-boot:run`
A aplicação estará disponível em http://localhost:80.

# API Endpoints
A documentação completa da API, gerada pelo Swagger, está disponível em:

Swagger UI: http://localhost/swagger-ui/index.html

# Aqui estão alguns dos principais endpoints:

```
Método HTTP

Endpoint

Descrição

POST

/auth/signin

Autentica um usuário e retorna um token.

POST

/auth/createUser

Cria um novo usuário.

GET

/api/v1/person

Lista todas as pessoas com paginação.

GET

/api/v1/person/{id}

Busca uma pessoa pelo ID.

POST

/api/v1/person

Adiciona uma nova pessoa.

PUT

/api/v1/person

Atualiza os dados de uma pessoa.

DELETE

/api/v1/person/{id}

Deleta uma pessoa.

GET

/api/v1/book

Lista todos os livros com paginação.

GET

/api/v1/book/{id}

Busca um livro pelo ID.

POST

/api/v1/book

Adiciona um novo livro.

PATCH

/api/v1/book

Atualiza os dados de um livro.

DELETE

/api/v1/book/{id}

Deleta um livro.

POST

/api/v1/file/uploadFile

Faz upload de um arquivo.

GET

/api/v1/file/downloadFile/{fileName}

Faz download de um arquivo.

POST

/api/v1/email

Envia um e-mail simples.
```

# Exportar para as Planilhas
🗄️ Banco de Dados
O projeto utiliza o Flyway para gerenciar as migrações do banco de dados. Os scripts de migração estão localizados em src/main/resources/db/migration. Ao iniciar a aplicação, o Flyway aplicará automaticamente as migrações pendentes, garantindo que o schema do banco de dados esteja sempre atualizado.

As migrações incluem a criação das tabelas person, books, users, permission e a inserção de dados iniciais.

# 🐳 Docker
O arquivo docker-compose.yml na raiz do projeto orquestra a execução da aplicação e do banco de dados MySQL em contêineres Docker. Isso facilita a configuração do ambiente de desenvolvimento e garante a portabilidade da aplicação.

Serviço db: Contêiner do banco de dados MySQL.

Serviço spring_boot_with_java: Contêiner da aplicação Spring Boot.

# 👨‍💻 Autor
Feito por Thiago







