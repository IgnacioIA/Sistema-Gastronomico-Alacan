# Caso de Uso: Desactivar Ingrediente

## Objetivo

Dar de baja lógica un ingrediente, preservando completamente su trazabilidad (stock, movimientos, recetas que lo referencian), y permitiendo al Frontend informar al usuario si el ingrediente tenía referencias activas al momento de desactivarlo.

## Flujo del Usuario

1. El usuario presiona "Desactivar" sobre un ingrediente en la pantalla de Gestión de Inventario.
2. El sistema desactiva el ingrediente sin excepción, tenga o no referencias activas.
3. Si el ingrediente estaba siendo usado como insumo en una o más recetas activas, el Frontend puede mostrar una advertencia adicional junto a la confirmación (usando el conteo que devuelve la respuesta) — el mensaje final es responsabilidad del Frontend, el Backend solo entrega el dato.
4. El ingrediente deja de aparecer en el listado por defecto (que solo muestra activos) y pasa a aparecer únicamente cuando se filtra explícitamente por `estado=INACTIVO`.

## Endpoint

`/ingredients/{id}/desactivar`

## Método HTTP

`POST`

## Path Variables

| Variable | Tipo | Notas |
|---|---|---|
| `id` | number | Id del artículo (ingrediente) a desactivar |

## Query Params

Ninguno.

## Request JSON

No aplica (POST sin body).

## Response JSON

`200 OK`

```json
{
  "ingrediente": {
    "id": 8,
    "nombre": "Pimienta",
    "tipoArticulo": { "id": 1, "nombre": "INGREDIENTE" },
    "unidadMedida": { "id": 1, "nombre": "GRAMO", "abreviatura": "g" },
    "descripcion": null,
    "activo": false,
    "stock": {
      "cantidadActual": 0,
      "cantidadReservada": 0,
      "cantidadDisponible": 0,
      "stockMinimo": 5,
      "necesitaReposicion": true
    }
  },
  "recetasActivasQueLoUtilizan": 2
}
```

`recetasActivasQueLoUtilizan` es la cantidad de recetas activas que requieren este ingrediente como insumo. Es puramente informativo — nunca bloquea la desactivación.

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `200 OK` | Ingrediente desactivado (siempre que exista, tenga o no referencias) |
| `404 Not Found` | El `id` no corresponde a ningún artículo |

## Errores posibles

| `code` | status | Causa |
|---|---|---|
| `ARTICLE_NOT_FOUND` | 404 | El `id` no corresponde a ningún artículo |

## Notas

- Operación idempotente: desactivar un ingrediente ya inactivo no falla, simplemente confirma el estado (y recalcula `recetasActivasQueLoUtilizan`).
- No existe eliminación física en ningún caso — `Movimiento`, `Stock` y `RecetaDetalle` que referencian este artículo permanecen intactos (ADR 0001, sección 15).
- Esta versión solo cubre la desactivación. La reactivación (volver `activo = true`) no fue parte del alcance aprobado para esta pantalla — se agregaría como una extensión simétrica si se pide.
- Validado con 4 tests automatizados (`DesactivarIngredienteApiTest`: sin referencias, con una receta activa que lo requiere, idempotencia, artículo inexistente) y en vivo contra MySQL, incluyendo la integración con el filtro `estado` de "Listar Ingredientes".

## Consumido por

Pantalla de Gestión de Inventario de Ingredientes → acción "Desactivar" sobre un ingrediente existente.
