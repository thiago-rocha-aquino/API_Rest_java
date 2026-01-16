# Task Manager API

API REST completa para gerenciamento de projetos e tarefas, desenvolvida com Spring Boot  e Java .

## Sobre o Projeto

O Task Manager API é um sistema backend robusto que permite:
- Gerenciamento de usuários com autenticação JWT
- Criação e gerenciamento de projetos
- Controle de tarefas com status (TODO, DOING, DONE)
- Atribuição de tarefas a usuários


## Arquitetura

```
src/main/java/com/taskmanager/
├── config/           # Configurações (Security, OpenAPI)
├── controller/       # Controllers REST
├── dto/
│   ├── request/      # DTOs de entrada
│   └── response/     # DTOs de saída
├── entity/           # Entidades JPA
├── exception/        # Exceções customizadas
├── mapper/           # Conversores Entity <-> DTO
├── repository/       # Repositórios JPA
├── security/         # Configuração JWT
└── service/          # Lógica de negócio
```

## Modelo de Dados

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│    User     │       │   Project   │       │    Task     │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ id          │──┐    │ id          │──┐    │ id          │
│ name        │  │    │ name        │  │    │ title       │
│ email       │  │    │ description │  │    │ description │
│ password    │  └───<│ owner_id    │  └───<│ project_id  │
│ createdAt   │       │ createdAt   │       │ status      │
└─────────────┘       └─────────────┘       │ deadline    │
       │                                    │ assigned_id │
       └───────────────────────────────────>│ createdAt   │
                                            └─────────────┘
```

## Endpoints da API

### Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/register` | Registrar novo usuário |
| POST | `/auth/login` | Login e obtenção de token |
| POST | `/auth/refresh` | Renovar token de acesso |

### Usuários
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/users` | Listar todos os usuários |
| GET | `/users/{id}` | Buscar usuário por ID |
| PUT | `/users/{id}` | Atualizar usuário |
| DELETE | `/users/{id}` | Excluir usuário |

### Projetos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/projects` | Criar novo projeto |
| GET | `/projects` | Listar projetos do usuário |
| GET | `/projects/{id}` | Buscar projeto por ID |
| PUT | `/projects/{id}` | Atualizar projeto |
| DELETE | `/projects/{id}` | Excluir projeto |

### Tarefas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/projects/{projectId}/tasks` | Criar tarefa no projeto |
| GET | `/projects/{projectId}/tasks` | Listar tarefas do projeto |
| GET | `/tasks/{id}` | Buscar tarefa por ID |
| PUT | `/tasks/{id}` | Atualizar tarefa |
| PATCH | `/tasks/{id}/status` | Alterar status da tarefa |
| DELETE | `/tasks/{id}` | Excluir tarefa |

## Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- PostgreSQL 15+ (ou Docker)

### Executar Localmente

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/task-manager-api.git
cd task-manager-api
```

2. **Configure o banco de dados**

Crie um banco PostgreSQL:
```sql
CREATE DATABASE taskmanager;
```

3. **Configure as variáveis de ambiente** (ou edite application.yml)
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/taskmanager
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=sua_senha
export JWT_SECRET=sua_chave_secreta_base64
```

4. **Execute a aplicação**
```bash
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

### Executar com Docker

1. **Build e execução**
```bash
docker-compose up -d
```

2. **Verificar logs**
```bash
docker-compose logs -f api
```

3. **Parar os containers**
```bash
docker-compose down
```

## Documentação da API (Swagger)

Após iniciar a aplicação, acesse:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Como autenticar no Swagger

1. Execute o endpoint `/auth/register` ou `/auth/login`
2. Copie o `accessToken` da resposta
3. Clique no botão "Authorize" no Swagger
4. Cole o token no formato: `Bearer {seu_token}`
5. Clique em "Authorize"

## Exemplos de Requisições

### Registrar Usuário
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "senha123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@email.com",
    "password": "senha123"
  }'
```

### Criar Projeto
```bash
curl -X POST http://localhost:8080/projects \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_token}" \
  -d '{
    "name": "Meu Projeto",
    "description": "Descrição do projeto"
  }'
```

### Criar Tarefa
```bash
curl -X POST http://localhost:8080/projects/1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_token}" \
  -d '{
    "title": "Implementar login",
    "description": "Criar tela de autenticação",
    "deadline": "2025-12-31",
    "assignedUserId": 1
  }'
```

### Atualizar Status da Tarefa
```bash
curl -X PATCH http://localhost:8080/tasks/1/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {seu_token}" \
  -d '{
    "status": "DOING"
  }'
```

## Executar Testes

```bash
# Todos os testes
mvn test

# Testes com cobertura
mvn test jacoco:report
```

## Estrutura do Projeto

```
task-manager-api/
├── src/
│   ├── main/
│   │   ├── java/com/taskmanager/
│   │   │   ├── config/
│   │   │   │   ├── OpenApiConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ProjectController.java
│   │   │   │   ├── TaskController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── entity/
│   │   │   │   ├── Project.java
│   │   │   │   ├── Task.java
│   │   │   │   ├── TaskStatus.java
│   │   │   │   └── User.java
│   │   │   ├── exception/
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── UnauthorizedException.java
│   │   │   ├── mapper/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── JwtService.java
│   │   │   ├── service/
│   │   │   └── TaskManagerApplication.java
│   │   └── resources/
│   │       ├── db/migration/
│   │       │   ├── V1__create_users_table.sql
│   │       │   ├── V2__create_projects_table.sql
│   │       │   └── V3__create_tasks_table.sql
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Regras de Negócio

- Um usuário pode ter vários projetos
- Um projeto pode ter várias tarefas
- Uma tarefa pertence a um único projeto
- Uma tarefa pode ser atribuída a um usuário
- Apenas o dono do projeto pode alterar ou excluir suas tarefas
- Datas de prazo não podem ser no passado
- Status inicial da tarefa é sempre TODO

