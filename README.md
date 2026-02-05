# Board Management

## Video de apresentação

[Video Youtube](https://www.youtube.com/watch?v=GtRN16DV7y4)

Link: https://www.youtube.com/watch?v=GtRN16DV7y4

## 📌 Visão Geral

O **Board Management** é uma aplicação full stack para gerenciamento de quadros no estilo **Kanban**.
O sistema permite criar **boards**, **colunas** e **cards**, além de mover cards entre colunas respeitando regras de negócio bem definidas (por exemplo, um card só pode ser movido dentro do mesmo board).

O projeto foi desenvolvido seguindo o padrão de **Arquitetura em Camadas (Controller → Service → Repository)**, com foco em separação de responsabilidades, clareza no código e facilidade de manutenção e testes.

![alt text](image-1.png)

---

## 🧩 Funcionalidades Principais

- Criar e listar **Boards**
- Criar **Colunas** associadas a um Board
- Criar, atualizar e remover **Cards**
- Mover **Cards** entre Colunas (somente dentro do mesmo Board)
- Validação de dados de entrada
- Aplicação de regras de negócio no back-end
- Testes unitários para Services
- Testes de Controllers utilizando MockMvc

---

## 🛠️ Tecnologias Utilizadas

### Back-end
- **Java 25**
- **Spring Boot 4.1**
- Spring Web (API REST)
- Spring Validation
- Spring Data JPA
- **PostgreSQL**
- Maven
- JUnit 5
- Mockito

### Front-end
- **ReactJS**
- TypeScript
- Axios
- Vite
- Node.js
- npm ou yarn

---

## ⚙️ Back-end — Instalação, Build e Execução

### 📦 Pré-requisitos

- Java 25
- Maven
- PostgreSQL

---

### 🗄️ Configuração do Banco de Dados

Crie um banco de dados no PostgreSQL e configure as credenciais no arquivo
`application.yml` ou `application.properties`.

Exemplo:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/board_management
    username: postgres
    password: postgres
```

---

### 📥 Instalação das Dependências

```bash
mvn clean install
```

---

### 🧪 Execução dos Testes

```bash
mvn test
```

---

### 🏗️ Build da Aplicação

```bash
mvn clean package
```

O arquivo `.jar` será gerado na pasta `target/`.

---

### ▶️ Execução da Aplicação

**Via Maven**
```bash
mvn spring-boot:run
```

**Via JAR**
```bash
java -jar target/board-management-backend.jar
```

---

### 🌐 Acesso à API

Após iniciar a aplicação:

```
http://localhost:8080
```

---

## 🎨 Front-end — Execução

Na pasta do front-end:

```bash
npm install
npm run dev
```

ou

```bash
yarn install
yarn dev
```

A aplicação ficará disponível na porta exibida no terminal (geralmente `http://localhost:5173`).

---

## 🏗️ Arquitetura

O projeto segue o padrão:

**Controller → Service → Repository**

- Controller: entrada HTTP
- Service: regras de negócio
- Repository: persistência
- DTOs: transporte de dados entre camadas

---

## 👨‍💻 Autor

Fábio Vitor Tavares Furtado

Projeto desenvolvido como estudo prático de **Java moderno, Spring Boot e React**, com foco em boas práticas de arquitetura e testes.
