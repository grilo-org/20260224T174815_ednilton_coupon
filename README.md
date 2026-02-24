# Coupon API — Outforce Technical Challenge

## Decisões Arquiteturais

### Arquitetura em Camadas (DDD leve)

```
domain/          → Aggregate Root, Value Objects, port (interface)
application/     → Use Cases focados (1 use case = 1 intenção)
infrastructure/  → Adaptadores: JPA, Web (Controller)
shared/          → Exceções e handlers transversais
```

**Sem GenericService.** Cada use case tem uma única responsabilidade:
- `CreateCouponUseCase` — criar cupom
- `DeleteCouponUseCase` — deletar cupom (soft delete)

### Value Object: `CouponCode`
Toda a lógica de sanitização (remoção de caracteres especiais) e validação dos 6 caracteres alfanuméricos vive no próprio Value Object, não em serviços ou controllers.

### Regras de Negócio no Domínio
O `Coupon` (Aggregate Root) encapsula todas as regras — o use case só orquestra:
- Código com exatamente 6 chars alfanuméricos (especiais são removidos)
- Desconto mínimo de 0.5
- Data de expiração não pode ser no passado
- Soft delete com proteção contra duplo delete

---

## Stack
- Java 21
- Spring Boot 3.4
- H2 (in-memory)
- Springdoc OpenAPI (Swagger)
- JUnit 5 + MockMvc

---

## Como rodar

### Local
```bash
./mvnw spring-boot:run
```

### Docker
```bash
docker compose up --build
```

### Testes
```bash
./mvnw test
```

---

## Endpoints

| Método | Path           | Descrição              |
|--------|----------------|------------------------|
| POST   | `/coupon`      | Criar cupom            |
| DELETE | `/coupon/{id}` | Deletar cupom (soft)   |

**Swagger UI:** http://localhost:8080/swagger-ui.html  
**H2 Console:** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:coupondb`)

---

## Exemplo de Requisição

```bash
curl -X POST http://localhost:8080/coupon \
  -H "Content-Type: application/json" \
  -d '{
    "code": "ABC-123",
    "description": "Desconto especial",
    "discountValue": 0.8,
    "expirationDate": "2026-12-01T00:00:00Z",
    "published": false
  }'
```

**Resposta (201):**
```json
{
  "id": "cef9d1e3-aae5-4ab6-a297-358c6032b1e7",
  "code": "ABC123",
  "description": "Desconto especial",
  "discountValue": 0.8,
  "expirationDate": "2026-12-01T00:00:00Z",
  "status": "ACTIVE",
  "published": false,
  "redeemed": false
}
```
