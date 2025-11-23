# 📚 Documentação da API - Habitus

## 📋 **USER HABITS**

### 1. **Listar metas**
- **Método:** `GET`
- **URI:** `/api/user-habits`
- **Query Params:** 
  - `userId` (obrigatório) - ID do usuário
  - `habitId` (opcional) - Filtrar por tipo de hábito
- **Response:**
```json
[
  {
    "id": 7,
    "userId": 1,
    "habitId": 1,
    "habitName": "Beber agua",
    "measurementUnitId": 1,
    "measurementUnitSymbol": "ml",
    "dailyGoal": 2000,
    "weeklyFrequency": 7,
    "startDate": "2025-11-23",
    "endDate": "2025-12-31"
  }
]
```

---

### 2. **Buscar meta por ID**
- **Método:** `GET`
- **URI:** `/api/user-habits/{id}`
- **Response:** Objeto UserHabit completo
```json
{
  "id": 7,
  "userId": 1,
  "habitId": 1,
  "habit": { ... },
  "measurementUnitId": 1,
  "measurementUnit": { ... },
  "dailyGoal": 2000,
  "weeklyFrequency": 7,
  "startDate": "2025-11-23",
  "endDate": null
}
```

---

### 3. **Criar nova meta**
- **Método:** `POST`
- **URI:** `/api/user-habits`
- **Payload:**
```json
{
  "userId": 1,
  "habitId": 1,
  "measurementUnitId": 1,
  "dailyGoal": 2000,
  "weeklyFrequency": 5
}
```
- **Conversões automáticas:**
  - `measurementUnitId: 2` (L) → `1` (ml), multiplica dailyGoal × 1000
  - `measurementUnitId: 4` (h) → `3` (min), multiplica dailyGoal × 60
- **Response:** Status 201 Created
```json
{
  "id": 8,
  "userId": 1,
  "habitId": 1,
  "measurementUnitId": 1,
  "dailyGoal": 2000,
  "weeklyFrequency": 5,
  "startDate": "2025-11-23",
  "endDate": null
}
```

---

### 4. **Editar meta**
- **Método:** `PUT`
- **URI:** `/api/user-habits/{id}`
- **Payload (todos os campos são opcionais):**
```json
{
  "measurementUnitId": 2,
  "dailyGoal": 2.5,
  "weeklyFrequency": 7,
  "startDate": "2025-11-23",
  "endDate": "2025-12-31"
}
```
- **Conversões:** Mesmas do create (L→ml, h→min)
- **Response:** UserHabit atualizado (200 OK) ou 404 Not Found
```json
{
  "id": 7,
  "userId": 1,
  "habitId": 1,
  "measurementUnitId": 1,
  "dailyGoal": 2500,
  "weeklyFrequency": 7,
  "startDate": "2025-11-23",
  "endDate": "2025-12-31"
}
```

---

### 5. **Deletar meta**
- **Método:** `DELETE`
- **URI:** `/api/user-habits/{id}?userId={userId}`
- **Query Param:** 
  - `userId` (obrigatório) - Para validação de propriedade
- **Response:** 
  - `204 No Content` - Sucesso
  - `403 Forbidden` - Meta não pertence ao usuário ou não existe

---

## 📊 **RELATÓRIOS**

### 6. **Histórico de registros**
- **Método:** `GET`
- **URI:** `/api/relatorios/historico`
- **Query Params (todos obrigatórios):** 
  - `userId` - ID do usuário
  - `habitId` - ID do hábito
  - `dataInicio` - Data inicial (formato: yyyy-MM-dd)
  - `dataFim` - Data final (formato: yyyy-MM-dd, exclusiva)
- **Response:**
```json
{
  "info": {
    "name": "Beber agua",
    "unit": "ml",
    "dailyGoal": 2000
  },
  "metrics": {
    "weeklyAverage": 1850.5,
    "bestRecord": 2500
  },
  "chart": [
    {
      "date": "2025-11-21",
      "total": 1800,
      "dailyGoal": 2000
    },
    {
      "date": "2025-11-22",
      "total": 2100,
      "dailyGoal": 2000
    }
  ]
}
```
- **Observações:**
  - Timezone: America/Sao_Paulo (UTC-3)
  - `dailyGoal` no chart reflete a meta ativa em cada data específica
  - `dataFim` é exclusiva (não inclui registros dessa data)
  - `weeklyAverage` considera apenas dias com registros
  - `bestRecord` é o maior valor registrado no período

---

## 🔍 **Exemplos de uso com cURL:**

### Listar metas

```bash
# Listar todas as metas de um usuário
curl "http://localhost:8080/api/user-habits?userId=1"

# Filtrar por tipo de hábito específico
curl "http://localhost:8080/api/user-habits?userId=1&habitId=1"
```

### Criar meta

```bash
# Criar meta simples
curl -X POST -H "Content-Type: application/json" \
  -d '{"userId":1,"habitId":1,"measurementUnitId":1,"dailyGoal":2000,"weeklyFrequency":5}' \
  "http://localhost:8080/api/user-habits"

# Criar meta com conversão (2.5L → 2500ml)
curl -X POST -H "Content-Type: application/json" \
  -d '{"userId":1,"habitId":1,"measurementUnitId":2,"dailyGoal":2.5,"weeklyFrequency":5}' \
  "http://localhost:8080/api/user-habits"
```

### Editar meta

```bash
# Editar apenas a meta diária
curl -X PUT -H "Content-Type: application/json" \
  -d '{"dailyGoal":3000}' \
  "http://localhost:8080/api/user-habits/7"

# Editar múltiplos campos
curl -X PUT -H "Content-Type: application/json" \
  -d '{"dailyGoal":2500,"weeklyFrequency":7,"endDate":"2025-12-31"}' \
  "http://localhost:8080/api/user-habits/7"

# Trocar unidade (2.5L → 2500ml)
curl -X PUT -H "Content-Type: application/json" \
  -d '{"measurementUnitId":2,"dailyGoal":2.5}' \
  "http://localhost:8080/api/user-habits/7"
```

### Deletar meta

```bash
# Deletar meta (requer userId para validação)
curl -X DELETE "http://localhost:8080/api/user-habits/7?userId=1"
```

### Relatório histórico

```bash
# Buscar relatório de novembro de 2025
curl "http://localhost:8080/api/relatorios/historico?userId=1&habitId=1&dataInicio=2025-11-01&dataFim=2025-11-30"

# Buscar relatório dos últimos 7 dias
curl "http://localhost:8080/api/relatorios/historico?userId=1&habitId=1&dataInicio=2025-11-16&dataFim=2025-11-23"
```

---

## 📝 **Notas importantes:**

### Conversões de Unidades
- **Litros (L) para Mililitros (ml):** ID 2 → ID 1, multiplica por 1000
- **Horas (h) para Minutos (min):** ID 4 → ID 3, multiplica por 60

### Validações
- Delete requer que a meta pertença ao usuário solicitante
- Update não permite alterar `userId`, `habitId` ou `id`
- Create encerra automaticamente metas ativas do mesmo hábito (seta `endDate = hoje`)

### Timezone
- Todos os registros utilizam timezone America/Sao_Paulo (UTC-3)
- Conversões de data são feitas automaticamente no backend
