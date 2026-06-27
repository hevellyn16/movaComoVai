# 🗺️ Mova — Descubra o que fazer em Sobral

**Mova** é uma plataforma de recomendação personalizada de eventos e atividades culturais voltada para turistas e moradores de **Sobral - CE**. O sistema cruza o perfil de interesses do usuário com os eventos disponíveis na cidade para gerar um feed de recomendações com percentual de compatibilidade (match), planos de atividades agrupados e muito mais.

---

## 📋 Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Estrutura do Repositório](#estrutura-do-repositório)
- [Configuração e Execução](#configuração-e-execução)
  - [🐳 Opção 1: Docker Compose (recomendado)](#-opção-1-docker-compose-recomendado)
  - [⚙️ Opção 2: Execução Manual (sem Docker)](#️-opção-2-execução-manual-sem-docker)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Documentação da API](#documentação-da-api)
- [Testes](#testes)
- [Principais Funcionalidades](#principais-funcionalidades)

---

## Sobre o Projeto

A proposta do **Mova** é resolver a falta de centralização e qualidade das informações sobre eventos locais em Sobral. Em vez de buscar em múltiplas fontes, o usuário responde a um **onboarding** rápido com suas preferências (gastronomia, cultura, vida noturna, gratuito etc.) e informa o contexto da saída (sozinho, casal, grupo ou com crianças). A partir disso, o sistema entrega um **feed personalizado** ordenado por relevância.

O projeto possui duas partes:

| Parte | Tecnologia | Porta padrão |
|---|---|---|
| **Backend** (API REST) | Java 21 + Spring Boot 4 | `8082` (Docker) / `8080` (local) |
| **Frontend** (Web App) | Next.js 16 + React 19 + TypeScript | `3000` |

---

## Tecnologias

### Backend (`back/mova`)

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.6 | Framework principal |
| Spring Security | — | Autenticação e autorização |
| Spring Data JPA + Hibernate | — | ORM e acesso ao banco |
| Flyway | — | Migrations de banco de dados |
| PostgreSQL | 16 | Banco de dados relacional |
| Auth0 Java JWT | 4.5.2 | Geração e validação de tokens JWT |
| SpringDoc OpenAPI (Swagger) | 3.0.0 | Documentação interativa da API |
| Spring Mail + Thymeleaf | — | Envio de e-mails com templates HTML |
| Lombok | — | Redução de boilerplate |
| JaCoCo | 0.8.13 | Cobertura de testes (mínimo 80%) |
| Testcontainers | 1.20.4 | Testes de integração com banco real |
| Docker + Docker Compose | — | Containerização |

### Frontend (`front/mova`)

| Tecnologia | Versão | Uso |
|---|---|---|
| Next.js | 16.2.7 | Framework React com App Router |
| React | 19.2.4 | Biblioteca de UI |
| TypeScript | ^5 | Tipagem estática |
| Tailwind CSS | ^4 | Estilização |
| Axios | ^1.18.0 | Requisições HTTP para a API |

---

## Arquitetura

O backend segue os princípios de **Clean Architecture** e **SOLID**, com as camadas claramente separadas:

```
src/main/java/com/eng/software/mova/
├── application/
│   ├── dto/           # Data Transfer Objects (request/response)
│   └── service/       # Casos de uso / regras de negócio
├── domain/
│   ├── model/         # Entidades e enums do domínio
│   └── port/          # Interfaces (portas) para inversão de dependência
├── infrastructure/
│   ├── adapter/       # Implementações concretas (JPA, e-mail, specs)
│   ├── config/        # Configurações (CORS, segurança, etc.)
│   └── security/      # Filtros JWT e configurações Spring Security
└── shared/
    ├── exceptions/    # Exceções customizadas
    └── utils/         # Utilitários compartilhados
```

O frontend utiliza o **App Router** do Next.js, com rotas organizadas em grupos:

```
front/mova/
├── app/
│   ├── (auth)/        # Login, registro, recuperação de senha
│   └── (main)/        # Feed, eventos, favoritos, perfil, gestão admin
├── components/        # Componentes reutilizáveis
├── hooks/             # Custom hooks React
├── services/          # Camada de serviço (chamadas à API)
├── store/             # Gerenciamento de estado global
├── types/             # Tipos TypeScript
└── utils/             # Funções utilitárias
```

---

## Pré-requisitos

Certifique-se de ter instalado em sua máquina:

### Para a opção Docker (recomendada)
- [Docker](https://docs.docker.com/get-docker/) >= 24
- [Docker Compose](https://docs.docker.com/compose/install/) >= 2.x
- [Node.js](https://nodejs.org/) >= 20 + npm (para o frontend)

### Para execução manual (sem Docker)
- [Java JDK 21](https://adoptium.net/)
- [Maven](https://maven.apache.org/download.cgi) >= 3.9 (ou usar o wrapper `./mvnw` incluso)
- [PostgreSQL](https://www.postgresql.org/download/) >= 16
- [Node.js](https://nodejs.org/) >= 20 + npm

---

## Estrutura do Repositório

```
movaComoVai/
├── back/
│   └── mova/                  # API Spring Boot
│       ├── src/
│       ├── Dockerfile
│       ├── docker-compose.yaml
│       ├── .env.example
│       ├── pom.xml
│       └── mvnw
└── front/
    └── mova/                  # Aplicação Next.js
        ├── app/
        ├── components/
        ├── package.json
        └── ...
```

---

## Configuração e Execução

### 🐳 Opção 1: Docker Compose (recomendado)

Esta opção sobe automaticamente o banco PostgreSQL e a API Spring Boot em containers. Você só precisa rodar o frontend separadamente.

#### Passo 1 — Clone o repositório

```bash
git clone <url-do-repositorio>
cd movaComoVai
```

#### Passo 2 — Configure as variáveis de ambiente do backend

```bash
cd back/mova
cp .env.example .env
```

Edite o arquivo `.env` com suas configurações:

```env
# Banco de dados
POSTGRES_DB=postgres_db
POSTGRES_USER=postgres_user
POSTGRES_PASSWORD=suaSenhaSegura

# URL de conexão (usada pela API dentro do Docker)
SPRING_DATASOURCE_URL=jdbc:postgresql://mova_db:5432/postgres_db

# E-mail (opcional — necessário para recuperação de senha)
MAIL_USERNAME=seuemail@gmail.com
MAIL_PASSWORD=sua_senha_de_app_gmail
```

> **Nota sobre e-mail:** Para usar o Gmail, gere uma [Senha de App](https://myaccount.google.com/apppasswords) em vez de usar sua senha normal.

#### Passo 3 — Suba o backend com Docker Compose

```bash
# Ainda dentro de back/mova/
docker compose up -d --build
```

Aguarde o build e a inicialização. Você pode acompanhar os logs com:

```bash
docker compose logs -f mova-api
```

A API estará disponível em: **http://localhost:8082**

#### Passo 4 — Configure e inicie o frontend

Em um novo terminal:

```bash
cd front/mova
npm install
npm run dev
```

O frontend estará disponível em: **http://localhost:3000**

#### Passo 5 — Verificar se tudo está rodando

```bash
# Verificar containers ativos
docker ps

# Testar se a API responde
curl http://localhost:8082/actuator/health
# ou acesse a documentação Swagger:
# http://localhost:8082/mova/swagger-ui.html
```

---

### ⚙️ Opção 2: Execução Manual (sem Docker)

#### Backend

##### Passo 1 — Configure o banco de dados PostgreSQL

Crie o banco e o usuário no seu PostgreSQL local:

```sql
-- Execute no psql ou em alguma IDE (ex: DBeaver, pgAdmin)
CREATE DATABASE postgres_db;
CREATE USER postgres_user WITH ENCRYPTED PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE postgres_db TO postgres_user;
```

##### Passo 2 — Configure as variáveis de ambiente

```bash
cd back/mova
cp .env.example .env
```

Edite o `.env` para apontar para o banco local:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres_db
POSTGRES_DB=postgres_db
POSTGRES_USER=postgres_user
POSTGRES_PASSWORD=password

MAIL_USERNAME=
MAIL_PASSWORD=
```

##### Passo 3 — Execute o backend

**Usando o Maven Wrapper (recomendado, não precisa de Maven instalado):**

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

**Ou, se tiver o Maven instalado globalmente:**

```bash
mvn spring-boot:run
```

O Flyway aplicará automaticamente todas as migrations do banco na primeira inicialização.

A API estará disponível em: **http://localhost:8080**

##### Passo 4 — Execute o frontend

```bash
cd front/mova
npm install
npm run dev
```

O frontend estará disponível em: **http://localhost:3000**

> **Atenção:** Se estiver rodando o backend na porta `8080` (sem Docker), verifique se o frontend está configurado para apontar para `http://localhost:8080`. Com Docker, a porta exposta é `8082`.

---

## Variáveis de Ambiente

### Backend (`back/mova/.env`)

| Variável | Padrão | Descrição |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/postgres_db` | URL de conexão JDBC ao PostgreSQL |
| `POSTGRES_DB` | `postgres_db` | Nome do banco de dados |
| `POSTGRES_USER` | `postgres_user` | Usuário do banco de dados |
| `POSTGRES_PASSWORD` | `password` | Senha do banco de dados |
| `MAIL_USERNAME` | _(vazio)_ | E-mail para envio (Gmail recomendado) |
| `MAIL_PASSWORD` | _(vazio)_ | Senha de app do e-mail |
| `JWT_SECRET` | `MySecretKeyDefaultToDevelopment` | Chave secreta para assinar tokens JWT |
| `JWT_EXPIRATION` | `86400000` | Validade do token JWT em ms (padrão: 24h) |
| `JWT_RESET_EXPIRATION` | `900000` | Validade do token de reset de senha em ms (15min) |
| `APP_BASE_URL` | `http://localhost:8080` | URL base da API |
| `PASSWORD_RESET_BASE_URL` | `http://localhost:3000` | URL base do frontend (para links de e-mail) |
| `FILE_UPLOAD_DIR` | `./uploads` | Diretório para upload de arquivos/avatares |

> ⚠️ **Em produção:** substitua `JWT_SECRET` por uma chave longa e aleatória, e use HTTPS.

---

## Documentação da API

A API conta com documentação interativa gerada automaticamente pelo **SpringDoc OpenAPI (Swagger UI)**.

Após iniciar o backend, acesse:

- **Swagger UI:** http://localhost:8080/mova/swagger-ui.html (local) ou http://localhost:8082/mova/swagger-ui.html (Docker)
- **OpenAPI JSON:** http://localhost:8080/mova/v3/api-docs

### Principais grupos de rotas

| Grupo | Prefixo | Descrição |
|---|---|---|
| Autenticação | `/auth` | Login, SSO Google/Apple, JWT |
| Usuários | `/users` | Cadastro, perfil, preferências, onboarding |
| Tags | `/tags` | Categorias de interesse |
| Locais | `/venues` | Cadastro e busca de locais |
| Eventos | `/events` | CRUD de eventos, imagens, programação |
| Recomendações | `/recommendations` | Feed personalizado e planos de atividades |
| Favoritos | `/events/{id}/favorites` | Gerenciamento de favoritos |
| Comentários | `/events/{id}/comments` | Comentários e respostas |
| Busca | `/search` | Busca textual e filtros avançados |

---

## Testes

O projeto backend utiliza **JUnit 5**, **Testcontainers** (banco PostgreSQL real em container para testes de integração) e exige **cobertura mínima de 80%** (verificada pelo JaCoCo).

### Executar os testes

```bash
cd back/mova

# Rodar todos os testes
./mvnw test

# Rodar testes e verificar cobertura mínima
./mvnw verify

# Gerar relatório de cobertura (disponível em target/site/jacoco/index.html)
./mvnw jacoco:report
```

> **Atenção:** Os testes de integração utilizam Testcontainers, então o **Docker precisa estar em execução** na máquina mesmo rodando os testes manualmente.

---

## Principais Funcionalidades

- 🔐 **Autenticação** com e-mail/senha, via tokens JWT
- 📝 **Onboarding** com seleção de tags de interesse e contexto da saída
- 📅 **Cadastro administrativo** de eventos com campos detalhados (localização, preço, classificação indicativa, acessibilidade)
- 🎯 **Feed de recomendações** ordenado por percentual de match com o perfil do usuário
- 🗓️ **Planos de atividades** agrupando eventos próximos geograficamente ou logicamente
- ❤️ **Favoritos e confirmação de presença** para retroalimentar o motor de recomendação
- 🔍 **Busca textual** e **filtros avançados** (data, preço, bairro)
- 💬 **Comentários e respostas** nos eventos
- 📧 **Recuperação de senha** via e-mail com link temporário
- 📖 **Documentação OpenAPI** (Swagger UI) integrada

---

> Projeto desenvolvido como trabalho prático de Engenharia de Software, focado em Sobral - CE 🌵
