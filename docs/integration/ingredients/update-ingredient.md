# Caso de Uso: Actualizar Ingrediente

## Objetivo

Modificar la identidad de un ingrediente (nombre, descripción, unidad de medida) y su configuración de inventario (stock mínimo) en una única operación atómica, protegiendo la integridad de los datos ya registrados.

## Flujo del Usuario

1. El usuario abre un ingrediente existente desde la pantalla de Gestión de Inventario y presiona "Editar".
2. El formulario se precarga con los valores actuales: nombre, descripción, unidad de medida, stock mínimo.
3. El usuario modifica los campos que necesite y confirma.
4. Si el ingrediente ya tiene stock físico o movimientos registrados y el usuario intentó cambiar la unidad de medida, el sistema rechaza el cambio explicando el motivo — el resto de los campos sí se actualiza.

## Endpoint

`/ingredients/{id}`

## Método HTTP

`PUT`

## Path Variables

| Variable | Tipo | Notas |
|---|---|---|
| `id` | number | Id del artículo (ingrediente) a actualizar |

## Query Params

Ninguno.

## Request JSON

```json
{
  "nombre": "Harina 000",
  "descripcion": "Harina de trigo para panificados",
  "unidadMedidaId": 2,
  "stockMinimo": 15
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `nombre` | string | Sí | No puede estar vacío |
| `descripcion` | string | No | `null` la deja vacía |
| `unidadMedidaId` | number | Sí | Debe existir. Si difiere de la unidad actual, se valida la regla de bloqueo (ver Notas) |
| `stockMinimo` | number | Sí | `>= 0` |

**Nunca enviar desde el Frontend:** `tipoArticulo`, `cantidadActual`, `cantidadReservada`, `movimientos`, `id`, `activo` (la desactivación es un caso de uso propio, no parte de esta actualización).

## Response JSON

`200 OK` — misma forma que la respuesta de "Crear Ingrediente":

```json
{
  "id": 6,
  "nombre": "Harina 000",
  "tipoArticulo": { "id": 1, "nombre": "INGREDIENTE" },
  "unidadMedida": { "id": 2, "nombre": "KILOGRAMO", "abreviatura": "kg" },
  "descripcion": "Harina de trigo para panificados",
  "stock": {
    "cantidadActual": 0,
    "cantidadReservada": 0,
    "cantidadDisponible": 0,
    "stockMinimo": 15,
    "necesitaReposicion": true
  }
}
```

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `200 OK` | Ingrediente actualizado correctamente |
| `400 Bad Request` | Nombre vacío, stock mínimo negativo |
| `404 Not Found` | El `id` del ingrediente no existe, o `unidadMedidaId` no existe |
| `409 Conflict` | Se intentó cambiar la unidad de medida de un artículo que ya tiene stock físico o movimientos registrados |

## Errores posibles

| `code` | status | Causa |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Nombre vacío o stock mínimo negativo |
| `ARTICLE_NOT_FOUND` | 404 | El `id` no corresponde a ningún artículo |
| `UNIDAD_MEDIDA_NOT_FOUND` | 404 | `unidadMedidaId` no existe |
| `UNIDAD_MEDIDA_BLOQUEADA` | 409 | El artículo ya tiene stock físico (`cantidadActual != 0`) o al menos un `Movimiento` registrado, y se intentó cambiar su unidad de medida |

## Notas

- **Regla de bloqueo de unidad de medida (ADR 0001, sección 16):** solo se valida cuando `unidadMedidaId` difiere del actual. Si el artículo no tiene stock físico ni movimientos, el cambio se permite sin restricciones. Una vez que existe cualquier historial de inventario, la unidad queda bloqueada de forma permanente — no hay conversión automática entre unidades.
- Nombre, descripción y stock mínimo siempre son editables, incluso si el cambio de unidad fue rechazado — son operaciones independientes dentro de la misma transacción.
- No se valida `Articulo.activo` en este caso de uso: editar identidad/catálogo no se considera una "operación de inventario" en el sentido de la sección 15 del ADR (que restringe altas/consumos de stock sobre artículos desactivados). Un ingrediente desactivado puede seguir editándose — por ejemplo, para corregir un dato antes de decidir si se reactiva.
- Validado con 6 tests automatizados (`ActualizarIngredienteApiTest`: actualización completa sin historial, bloqueo con stock físico, edición de otros campos con historial presente, nombre vacío, artículo inexistente, unidad inexistente) y en vivo contra MySQL.

## Consumido por

Pantalla de Gestión de Inventario de Ingredientes → acción "Editar" sobre un ingrediente existente.
