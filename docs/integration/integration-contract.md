# Índice de Contratos de Integración

Este documento indexa todos los casos de uso integrados entre Backend y Frontend. Reglas generales en `INTEGRATION_GUIDE.md`.

## Ingredients

| Caso de uso | Endpoint | Estado | Documento |
|---|---|---|---|
| Crear Ingrediente | `POST /ingredients` | ✅ Implementado y validado | [ingredients/create-ingredient.md](ingredients/create-ingredient.md) |
| Listar Ingredientes | `GET /ingredients` | ✅ Implementado y validado | [ingredients/list-ingredients.md](ingredients/list-ingredients.md) |
| Actualizar Ingrediente | `PUT /ingredients/{id}` | ✅ Implementado y validado | [ingredients/update-ingredient.md](ingredients/update-ingredient.md) |
| Desactivar Ingrediente | `POST /ingredients/{id}/desactivar` | ✅ Implementado y validado | [ingredients/deactivate-ingredient.md](ingredients/deactivate-ingredient.md) |
| Registrar ingreso de stock | `POST /ingredients/{id}/ingresos` | ✅ Implementado y validado | [ingredients/register-stock-entry.md](ingredients/register-stock-entry.md) |
| Registrar pérdida de stock | `POST /ingredients/{id}/perdidas` | ✅ Implementado y validado | [ingredients/register-stock-loss.md](ingredients/register-stock-loss.md) |
| Registrar corrección de stock | `POST /ingredients/{id}/correcciones` | ✅ Implementado y validado | [ingredients/register-stock-correction.md](ingredients/register-stock-correction.md) |
| Consultar movimientos | `GET /ingredients/{id}/movimientos` | ✅ Implementado y validado | [ingredients/list-stock-movements.md](ingredients/list-stock-movements.md) |

Con esto, los 8 casos de uso aprobados para la pantalla de Gestión de Inventario de Ingredientes están completos.

## Catalogs

Catálogos de referencia, reutilizables por cualquier módulo — se listan completos, sin paginar (ver `INTEGRATION_GUIDE.md`).

| Caso de uso | Endpoint | Estado | Documento |
|---|---|---|---|
| Consultar Unidades de Medida | `GET /measurement-units` | ✅ Implementado y validado | [measurement-units/list-measurement-units.md](measurement-units/list-measurement-units.md) |
| Consultar Tipos de Artículo | `GET /article-types` (a confirmar) | ⏳ Pendiente | — |
| Consultar Tipos de Movimiento | `GET /movement-types` (a confirmar) | ⏳ Pendiente | — |

## Recipes

Pendiente — sin casos de uso iniciados.

## Production

Pendiente — sin casos de uso iniciados.

## Stock

Pendiente — sin casos de uso iniciados.
