# Caso de Uso: Registrar Ingreso de Stock

## Objetivo

Registrar el ingreso de una cantidad de stock a un ingrediente ya existente (por ejemplo, tras una compra), dejando trazabilidad completa del hecho — nunca escribiendo el stock directamente.

## Flujo del Usuario

1. El usuario selecciona un ingrediente en la pantalla de Gestión de Inventario y elige "Registrar ingreso".
2. Ingresa la cantidad que ingresó.
3. El sistema suma la cantidad al stock actual y registra el movimiento correspondiente.

## Endpoint

`/ingredients/{id}/ingresos`

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
  "cantidad": 40
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `cantidad` | number | Sí | Debe ser `> 0` |

**Nunca enviar desde el Frontend:** el tipo de movimiento (`COMPRA`, fijado por el Backend), el sentido (`ENTRADA`, implícito en la operación), la fecha/hora.

## Response JSON

`200 OK` — misma forma que el resto de las respuestas de ingrediente:

```json
{
  "id": 9,
  "nombre": "Curcuma",
  "tipoArticulo": { "id": 1, "nombre": "INGREDIENTE" },
  "unidadMedida": { "id": 1, "nombre": "GRAMO", "abreviatura": "g" },
  "descripcion": null,
  "activo": true,
  "stock": {
    "cantidadActual": 40,
    "cantidadReservada": 0,
    "cantidadDisponible": 40,
    "stockMinimo": 10,
    "necesitaReposicion": false
  }
}
```

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `200 OK` | Ingreso registrado correctamente |
| `400 Bad Request` | Cantidad inválida (`<= 0`) |
| `404 Not Found` | El `id` no corresponde a ningún artículo |
| `409 Conflict` | El artículo está desactivado |

## Errores posibles

| `code` | status | Causa |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Cantidad `<= 0` |
| `ARTICLE_NOT_FOUND` | 404 | El `id` no existe |
| `ARTICULO_INACTIVO` | 409 | El artículo está desactivado — no admite nuevas operaciones de inventario (ADR 0001, sección 15) |

## Notas

- Genera un `Movimiento` de tipo `COMPRA`, sentido `ENTRADA` — reutiliza un tipo ya seedeado, no fue necesario agregar ninguno nuevo.
- Es un caso de uso específico e independiente (ADR 0001, sección 17): no comparte servicio de aplicación con "pérdida" ni "corrección", aunque las tres reutilizan el mismo DTO de request (`{ "cantidad": ... }`) porque el único dato que aporta el usuario es idéntico en los tres casos.
- Es el primer caso de uso de escritura que valida `Articulo.activo` — "Actualizar Ingrediente" deliberadamente no lo hace (ver `docs/integration/ingredients/update-ingredient.md`), pero registrar movimientos de inventario sí es exactamente el tipo de operación que el ADR 0001 sección 15 restringe sobre artículos desactivados.
- Refactor realizado de paso: `CrearIngredienteResultado` y `ActualizarIngredienteResultado` (idénticas en forma: `Articulo` + `Stock`) se consolidaron en un único `IngredienteConStockResultado`, reutilizado también por este caso de uso, para no triplicar la misma clase.
- Validado con 4 tests automatizados (`RegistrarIngresoStockApiTest`: ingreso simple, ingresos acumulativos, cantidad inválida, artículo desactivado, artículo inexistente) y en vivo contra MySQL.

## Consumido por

Pantalla de Gestión de Inventario de Ingredientes → acción "Registrar ingreso" sobre un ingrediente existente.
