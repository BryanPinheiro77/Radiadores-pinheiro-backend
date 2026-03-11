# 🔧 Radiadores Pinheiro — Backend

Sistema de Gestão Operacional para a oficina Radiadores Pinheiro.

---

## 🛠️ Stack

- **Java 21** + **Spring Boot 4**
- **Spring Security** + **JWT**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** (Supabase)
- **SpringDoc OpenAPI** (Swagger UI)
- **Lombok**
- **Maven**

---

## 📦 Módulos

| Módulo | Status |
|--------|--------|
| `auth` | ✅ Concluído |
| `usuario` | ✅ Concluído |
| `categoria` | ✅ Concluído |
| `produto` | ✅ Concluído |
| `venda` | 🚧 Em desenvolvimento |
| `despesa` | 🚧 Em desenvolvimento |
| `fornecedor` | 🚧 Em desenvolvimento |
| `relatorio` | 🚧 Em desenvolvimento |

---

## 🚀 Como rodar localmente

### Pré-requisitos

- Java 21+
- Maven
- Conta no [Supabase](https://supabase.com)

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
├── usuario/       # Gestão de usuários
├── categoria/     # Categorias de produto
├── produto/       # Produtos e estoque
├── venda/         # Vendas e serviços
├── despesa/       # Despesas
├── fornecedor/    # Fornecedores
├── relatorio/     # Relatórios e indicadores
└── common/        # Exceções, configurações globais
```

---

## 👥 Time

- **Bryan Pinheiro** — [@BryanPinheiro77](https://github.com/BryanPinheiro77)
- **Luiz Fernando** - [@LuizFernandoReisFranca](https://github.com/luizfernandoreisfranca)

  
## ⚖️ Licença

Este projeto está licenciado sob a [MIT License](LICENSE).
