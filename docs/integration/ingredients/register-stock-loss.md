# Caso de Uso: Registrar Pérdida de Stock

## Objetivo

Registrar una salida de stock por pérdida (rotura, vencimiento, derrame, etc.) sobre un ingrediente existente, dejando trazabilidad completa del hecho.

## Flujo del Usuario

1. El usuario selecciona un ingrediente y elige "Registrar pérdida".
2. Ingresa la cantidad perdida.
3. El sistema descuenta la cantidad del stock actual y registra el movimiento correspondiente. Si la cantidad supera el stock disponible, rechaza la operación sin modificar nada.

## Endpoint

`/ingredients/{id}/perdidas`

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
  "cantidad": 10
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `cantidad` | number | Sí | Debe ser `> 0` y no puede superar el stock disponible |

**Nunca enviar desde el Frontend:** el tipo de movimiento (`DESPERDICIO`, fijado por el Backend), el sentido (`SALIDA`, implícito), la fecha/hora.

## Response JSON

`200 OK` — misma forma que el resto de las respuestas de ingrediente (ver `create-ingredient.md`).

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `200 OK` | Pérdida registrada correctamente |
| `400 Bad Request` | Cantidad inválida (`<= 0`), o mayor al stock disponible |
| `404 Not Found` | El `id` no corresponde a ningún artículo |
| `409 Conflict` | El artículo está desactivado |

## Errores posibles

| `code` | status | Causa |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Cantidad `<= 0`, o mayor al stock disponible actual |
| `ARTICLE_NOT_FOUND` | 404 | El `id` no existe |
| `ARTICULO_INACTIVO` | 409 | El artículo está desactivado |

## Notas

- Genera un `Movimiento` de tipo `DESPERDICIO`, sentido `SALIDA` — tipo ya seedeado, sin cambios de datos.
- Igual estructura que "Registrar ingreso de stock", cambiando `Stock.registrarIngreso` por `Stock.registrarConsumo` y el `TipoMovimiento`/`Sentido` correspondientes.
- **Decisión sobre el código de error de stock insuficiente:** se usa `VALIDATION_ERROR` (400), no un código dedicado — es el mismo criterio que ya aplicaba `RegistrarAjusteStockService` para su dirección de salida. Es una decisión consciente, no una omisión: introducir un código nuevo solo para esto habría significado un tercer tipo de "stock insuficiente" en el sistema (ya existe `INSUFFICIENT_STOCK` para "Ejecutar Producción", con una forma distinta — lista de faltantes por receta) sin un beneficio claro para este caso, que es de un solo artículo.
- Validado con 5 tests automatizados (`RegistrarPerdidaStockApiTest`: pérdida simple, pérdida mayor al disponible, cantidad inválida, artículo desactivado, artículo inexistente) y en vivo contra MySQL.

## Consumido por

Pantalla de Gestión de Inventario de Ingredientes → acción "Registrar pérdida" sobre un ingrediente existente.
