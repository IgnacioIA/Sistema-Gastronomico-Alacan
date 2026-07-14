# Caso de Uso: Consultar Movimientos de un Ingrediente

## Objetivo

Mostrar el historial completo y paginado de movimientos de inventario de un ingrediente (altas, compras, pérdidas, correcciones, consumos por producción), del más reciente al más antiguo.

## Flujo del Usuario

1. El usuario abre el detalle de un ingrediente en la pantalla de Gestión de Inventario y accede a su historial de movimientos.
2. El sistema devuelve los movimientos paginados, ordenados del más reciente al más antiguo.

## Endpoint

`/ingredients/{id}/movimientos`

## Método HTTP

`GET`

## Path Variables

| Variable | Tipo | Notas |
|---|---|---|
| `id` | number | Id del artículo (ingrediente) |

## Query Params

| Parámetro | Tipo | Requerido | Default | Notas |
|---|---|---|---|---|
| `page` | number | No | `0` | Base cero |
| `size` | number | No | `15` | |

No hay `search`, `sort` ni filtros: el orden es siempre cronológico descendente (más reciente primero) — es la única forma en que tiene sentido leer un historial. Si en el futuro hace falta filtrar por tipo de movimiento o rango de fechas, se agrega como extensión de este mismo contrato.

## Request JSON

No aplica (GET sin body).

## Response JSON

`200 OK`

```json
{
  "content": [
    {
      "id": 14,
      "tipoMovimiento": { "id": 3, "nombre": "AJUSTE" },
      "sentido": "ENTRADA",
      "cantidad": 5,
      "momento": "2026-07-11T10:37:19.17277"
    },
    {
      "id": 13,
      "tipoMovimiento": { "id": 4, "nombre": "DESPERDICIO" },
      "sentido": "SALIDA",
      "cantidad": 5,
      "momento": "2026-07-11T10:37:19.053312"
    }
  ],
  "page": 0,
  "size": 15,
  "totalElements": 4,
  "totalPages": 1,
  "first": true,
  "last": true,
  "empty": false
}
```

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `200 OK` | Historial obtenido (puede venir vacío si el ingrediente nunca tuvo movimientos) |
| `404 Not Found` | El `id` no corresponde a ningún artículo |

## Errores posibles

| `code` | status | Causa |
|---|---|---|
| `ARTICLE_NOT_FOUND` | 404 | El `id` no existe |

## Notas

- El dominio y el caso de uso base (`ListarMovimientosService`, `MovimientoRepository.buscarPorArticuloId`) ya existían desde el diseño original del módulo Producción — este caso de uso solo necesitó agregar la variante paginada (`buscarPorArticuloIdPaginado`) y exponerla como endpoint, ya que hasta ahora ningún controller la consumía.
- Orden por `momento` descendente, con `id` descendente como desempate — necesario porque dos movimientos de una misma operación (o de operaciones muy próximas) pueden compartir el mismo instante, y el orden no debe quedar ambiguo.
- Validado con 4 tests automatizados (`ListarMovimientosApiTest`: historial con varias operaciones y orden correcto, paginación, ingrediente sin movimientos, artículo inexistente) y en vivo contra MySQL.

## Consumido por

Pantalla de Gestión de Inventario de Ingredientes → detalle de un ingrediente → historial de movimientos.
