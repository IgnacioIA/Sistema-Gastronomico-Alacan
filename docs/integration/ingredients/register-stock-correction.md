# Caso de Uso: Registrar Corrección de Stock

## Objetivo

Reconciliar el stock del sistema contra un recuento físico real, en cualquier dirección (hacia arriba o hacia abajo), dejando trazabilidad completa del ajuste.

## Flujo del Usuario

1. El usuario cuenta físicamente el stock de un ingrediente y encuentra una diferencia contra lo que el sistema tiene registrado.
2. Selecciona "Registrar corrección" e ingresa la cantidad real que contó (no cuánto quiere sumar o restar).
3. El sistema calcula la diferencia contra el stock actual y ajusta en la dirección correspondiente. Si no hay diferencia, no se genera ningún cambio.

## Endpoint

`/ingredients/{id}/correcciones`

## Método HTTP

`POST`

## Path Variables

| Variable | Tipo | Notas |
|---|---|---|
| `id` | number | Id del artículo (ingrediente) |

## Query Params

Ninguno.

## Request JSON

```json
{
  "cantidadReal": 35
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `cantidadReal` | number | Sí | La cantidad física contada. Debe ser `>= 0` |

**Diseño deliberado:** a diferencia de "ingreso" y "pérdida" (dirección fija, implícita en el nombre de la acción), una corrección es inherentemente bidireccional — el usuario no sabe ni necesita saber si el sistema tiene de más o de menos. Por eso el contrato pide la cantidad real contada, no una cantidad con signo ni un `sentido`: la dirección la calcula el Backend comparando contra `Stock.cantidadActual`. Esto representa mejor la intención real del usuario (recontar y reconciliar) que exponer los parámetros técnicos internos de `Stock.registrarIngreso`/`registrarConsumo`.

**Nunca enviar desde el Frontend:** el tipo de movimiento (`AJUSTE`, fijado por el Backend), el sentido (calculado), la fecha/hora.

## Response JSON

`200 OK` — misma forma que el resto de las respuestas de ingrediente.

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `200 OK` | Corrección registrada (o sin cambios, si la cantidad real coincide con la actual) |
| `400 Bad Request` | `cantidadReal` negativa |
| `404 Not Found` | El `id` no corresponde a ningún artículo |
| `409 Conflict` | El artículo está desactivado |

## Errores posibles

| `code` | status | Causa |
|---|---|---|
| `VALIDATION_ERROR` | 400 | `cantidadReal` negativa |
| `ARTICLE_NOT_FOUND` | 404 | El `id` no existe |
| `ARTICULO_INACTIVO` | 409 | El artículo está desactivado |

## Notas

- Genera un `Movimiento` de tipo `AJUSTE`, con `sentido` `ENTRADA` o `SALIDA` según corresponda. Si `cantidadReal` coincide exactamente con el stock actual, no se genera ningún `Movimiento` — un movimiento representa un hecho ocurrido (contrato 5.6), y no ocurrió ningún cambio.
- **Reemplaza a `RegistrarAjusteStockService`**, que se eliminó: no tenía ningún endpoint (solo lo usaba un test como fixture, igual que pasó con `CrearArticuloService` — ver ADR 0001, secciones 14 y 17). El test `EjecutarProduccionIntegrationTest` se migró para cargar su stock de prueba directamente vía `Stock.registrarIngreso` + repositorio, sin pasar por ningún caso de uso de aplicación.
- Validado con 6 tests automatizados (`RegistrarCorreccionStockApiTest`: corrección hacia arriba, hacia abajo, sin cambios, cantidad negativa, artículo desactivado, artículo inexistente) y en vivo contra MySQL (las tres direcciones).

## Consumido por

Pantalla de Gestión de Inventario de Ingredientes → acción "Registrar corrección" sobre un ingrediente existente.
