#!/bin/bash
# =============================================================================
# ms-product-catalog — Exemplos de cURL para todos os endpoints
# Base URL: http://localhost:8080/api/v1/products
# =============================================================================

BASE_URL="http://localhost:8080/api/v1/products"


# =============================================================================
# 1. POST /api/v1/products — Criar produto (201 Created)
# =============================================================================

echo ">>> Criando produto 1: Notebook"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Notebook Dell XPS 15",
    "description": "Notebook premium com tela OLED 15 polegadas, processador Intel Core i9",
    "category": "Informatica",
    "brand": "Dell",
    "price": 12999.90,
    "active": true
  }' | jq .

echo ""
echo ">>> Criando produto 2: Smartphone"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Galaxy S24 Ultra",
    "description": "Smartphone top de linha com camera de 200MP e S Pen integrada",
    "category": "Smartphones",
    "brand": "Samsung",
    "price": 8499.00,
    "active": true
  }' | jq .

echo ""
echo ">>> Criando produto 3: Fone de ouvido"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "WH-1000XM5",
    "description": "Headphone com cancelamento de ruido ativo de classe mundial",
    "category": "Audio",
    "brand": "Sony",
    "price": 2199.90,
    "active": true
  }' | jq .

echo ""
echo ">>> Criando produto 4: Inativo (para testar filtros)"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Produto Descontinuado",
    "description": "Este produto nao esta mais disponivel",
    "category": "Outros",
    "brand": "Generica",
    "price": 99.00,
    "active": false
  }' | jq .


# =============================================================================
# 2. GET /api/v1/products/{id} — Buscar produto por ID (200 OK)
# =============================================================================
# Substitua {ID} pelo id retornado na criação acima

echo ""
echo ">>> Buscando produto por ID (substitua {ID} pelo id real)"
# curl -s -X GET "$BASE_URL/{ID}" | jq .

# Exemplo com ID fixo:
# curl -s -X GET "$BASE_URL/65f1a2b3c4d5e6f7a8b9c0d1" | jq .


# =============================================================================
# 3. PUT /api/v1/products/{id} — Atualizar produto (200 OK)
# =============================================================================

echo ""
echo ">>> Atualizando produto por ID (substitua {ID} pelo id real)"
# curl -s -X PUT "$BASE_URL/{ID}" \
#   -H "Content-Type: application/json" \
#   -d '{
#     "name": "Notebook Dell XPS 15 - Atualizado",
#     "description": "Versao atualizada com 64GB RAM e SSD de 2TB",
#     "category": "Informatica",
#     "brand": "Dell",
#     "price": 14999.90,
#     "active": true
#   }' | jq .


# =============================================================================
# 4. GET /api/v1/products?page=0&size=10 — Listar ativos com paginação (200 OK)
# =============================================================================

echo ""
echo ">>> Listando todos os produtos ativos (pagina 0, tamanho 10)"
curl -s -X GET "$BASE_URL?page=0&size=10" | jq .

echo ""
echo ">>> Listando pagina 1 com tamanho 2"
curl -s -X GET "$BASE_URL?page=1&size=2" | jq .

echo ""
echo ">>> Listando apenas com parametro size"
curl -s -X GET "$BASE_URL?size=5" | jq .


# =============================================================================
# 5. POST /api/v1/products/search — Pesquisa por texto (200 OK)
# =============================================================================

echo ""
echo ">>> Pesquisando por 'notebook' (busca em name, description, category e brand)"
curl -s -X POST "$BASE_URL/search" \
  -H "Content-Type: application/json" \
  -d '{ "query": "notebook" }' | jq .

echo ""
echo ">>> Pesquisando por 'samsung' (case-insensitive)"
curl -s -X POST "$BASE_URL/search" \
  -H "Content-Type: application/json" \
  -d '{ "query": "SAMSUNG" }' | jq .


# =============================================================================
# 6. BATCH OPERATIONS — Criar e Atualizar em Lote
# =============================================================================

echo ""
echo ">>> [BATCH] Criando múltiplos Smartphones (iPhone 17, Galaxy S25, Pixel 9)"
curl -s -X POST "$BASE_URL/batch" \
  -H "Content-Type: application/json" \
  -d '[
  {
    "name": "iPhone 17 Pro Max",
    "description": "Smartphone top de linha da Apple com sistema de câmera Fusion Pro tripla de 48MP",
    "category": "Smartphones",
    "brand": "Apple",
    "price": 9000.00,
    "active": true
  },
  {
    "name": "iPhone 17 Pro",
    "description": "Versão compacta do Pro Max com tela OLED de 6.3 polegadas e chip A19 Pro",
    "category": "Smartphones",
    "brand": "Apple",
    "price": 8000.00,
    "active": true
  },
  {
    "name": "Samsung Galaxy S25 Ultra",
    "description": "Smartphone premium da Samsung com câmera de 200MP e S Pen integrada",
    "category": "Smartphones",
    "brand": "Samsung",
    "price": 8499.00,
    "active": true
  },
  {
    "name": "Google Pixel 9 Pro XL",
    "description": "Flagship do Google com câmera computacional de 50MP e Android 15 puro",
    "category": "Smartphones",
    "brand": "Google",
    "price": 7299.00,
    "active": true
  }
]' | jq .

echo ""
echo ">>> [BATCH] Atualizando preços em lote (Exemplo - Requer IDs reais)"
# curl -s -X PUT "$BASE_URL/batch" \
#   -H "Content-Type: application/json" \
#   -d '[
#   {
#     "id": "{ID_IPHONE_17}",
#     "name": "iPhone 17 Pro Max",
#     "description": "...",
#     "category": "Smartphones",
#     "brand": "Apple",
#     "price": 8500.00, 
#     "active": true
#   },
#   {
#     "id": "{ID_GALAXY_S25}",
#     "name": "Samsung Galaxy S25 Ultra",
#     "description": "...",
#     "category": "Smartphones",
#     "brand": "Samsung",
#     "price": 7999.00,
#     "active": true
#   }
# ]' | jq .


# =============================================================================
# 7. DELETE /api/v1/products/{id} — Desativar produto / soft delete (204 No Content)
# =============================================================================

echo ""
echo ">>> Desativando produto por ID (substitua {ID} pelo id real)"
# curl -s -X DELETE "$BASE_URL/{ID}" -o /dev/null -w "HTTP Status: %{http_code}\n"


# =============================================================================
# CENÁRIOS DE ERRO — Validação e not found
# =============================================================================

echo ""
echo ">>> [ERRO 400] Criando produto com campos obrigatórios ausentes"
curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Produto sem nome, categoria, brand e price"
  }' | jq .

echo ""
echo ">>> [ERRO 400] Pesquisa com query vazia"
curl -s -X POST "$BASE_URL/search" \
  -H "Content-Type: application/json" \
  -d '{ "query": "" }' | jq .

# =============================================================================
# ACTUATOR — Health check
# =============================================================================

echo ""
echo ">>> Health check"
curl -s http://localhost:8080/actuator/health | jq .
