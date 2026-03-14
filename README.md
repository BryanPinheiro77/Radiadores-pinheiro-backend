# 🔧 Radiadores Pinheiro — Backend

Sistema de Gestão Operacional para a oficina Radiadores Pinheiro.

---

## 🛠️ Stack

- **Java 21** + **Spring Boot 4**
- **Spring Security** + **JWT**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** (Supabase)
- **SpringDoc OpenAPI** (Swagger UI)
- **OpenPDF** (geração de PDF)
- **Lombok**
- **Maven**

---

## 📦 Módulos

| Módulo | Status |
|--------|--------|
| `auth` | ✅ Concluído |
| `user` | ✅ Concluído |
| `category` | ✅ Concluído |
| `product` | ✅ Concluído |
| `sale` | ✅ Concluído |
| `despesa` | ✅ Concluído |
| `restock` | ✅ Concluído |
| `report` | ✅ Concluído |

---

## 🚀 Como rodar localmente

### Pré-requisitos

- Java 21+
- Maven
- Conta no [Supabase](https://supabase.com)
- IntelliJ IDEA (recomendado)

### Configuração

1. Clone o repositório:
```bash
git clone -b develop https://github.com/BryanPinheiro77/Radiadores-pinheiro-backend.git
cd Radiadores-pinheiro-backend
```

2. Crie o arquivo `src/main/resources/application-local.properties` (não versionado):
```properties
spring.datasource.url=jdbc:postgresql://<seu-host-supabase>:5432/postgres
spring.datasource.username=<seu-usuario-supabase>
spring.datasource.password=<sua-senha-supabase>
jwt.secret=<seu-jwt-secret>
```

3. Configure o perfil `local` no IntelliJ:
   - **Run → Edit Configurations**
   - Em **Environment variables** adicione: `SPRING_PROFILES_ACTIVE=local`

4. Rode a aplicação:
```bash
./mvnw spring-boot:run
```

---

## 📄 Documentação da API

Com a aplicação rodando, acesse:

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Autenticação

1. Crie um usuário via `POST /users`
2. Faça login via `POST /auth/login` — retorna um token JWT
3. No Swagger, clique em **Authorize 🔒** e cole o token

---

## 🗂️ Endpoints principais

| Módulo | Base URL |
|--------|----------|
| Auth | `/auth/login` |
| Usuários | `/users` |
| Categorias | `/categories` |
| Produtos | `/products` |
| Vendas | `/sales` |
| Despesas | `/api/expenses` |
| Reposição de estoque | `/restock/suggestions`, `/restock/orders` |
| Relatórios | `/api/reports` |

---

## 🌿 Branches

| Branch | Descrição |
|--------|-----------|
| `main` | Produção |
| `develop` | Integração |
| `feat/bryan` | Desenvolvimento Bryan |
| `feat/luiz` | Desenvolvimento Luiz |

**Fluxo:** `feat/*` → PR para `develop` → revisão → merge

---

## 📁 Estrutura de Pacotes

```
com.radiadorespinheiro
├── auth/          # Autenticação JWT
├── user/          # Gestão de usuários
├── category/      # Categorias de produto
├── product/       # Produtos e estoque
├── sale/          # Vendas e serviços
├── expense/       # Despesas e categorias de despesa
├── restock/       # Reposição de estoque e geração de PDF
├── report/        # Relatórios e indicadores financeiros
└── common/        # Exceções, configurações globais
```

---

## 👥 Time

- **Bryan Pinheiro** — [@BryanPinheiro77](https://github.com/BryanPinheiro77)
- **Luiz Fernando** — [@LuizFernandoReisFranca](https://github.com/luizfernandoreisfranca)

---

## ⚖️ Licença

Este projeto está licenciado sob a [MIT License](LICENSE).
