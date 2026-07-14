# Caso de Uso: Listar Ingredientes

## Objetivo

Obtener el listado paginado de ingredientes para la pantalla de Gestión de Inventario, combinando identidad del artículo (nombre, unidad, descripción, estado) y su inventario (stock actual, disponible, mínimo, si necesita reposición) en una sola vista, con búsqueda, filtros y ordenamiento resueltos en el Backend.

## Flujo del Usuario

1. El usuario entra a la pantalla de Gestión de Inventario de Ingredientes.
2. El sistema obtiene la primera página de ingredientes activos, ordenados por nombre.
3. El usuario puede buscar por texto, aplicar filtros (estado, inventario, unidad de medida) y cambiar el ordenamiento — cada acción dispara una nueva consulta al mismo endpoint con distintos parámetros.

## Endpoint

`/ingredients`

## Método HTTP

`GET`

## Path Variables

Ninguno.

## Query Params

| Parámetro | Tipo | Requerido | Default | Notas |
|---|---|---|---|---|
| `page` | number | No | `0` | Base cero |
| `size` | number | No | `15` | |
| `search` | string | No | — | Búsqueda por `nombre`, contiene, no sensible a mayúsculas |
| `sort` | string | No | `nombre,asc` | Formato `campo,direccion`. Campos válidos: `nombre`, `cantidadActual`, `cantidadDisponible`, `stockMinimo`, `necesitaReposicion`. Direcciones: `asc`, `desc` |
| `estado` | string | No | `ACTIVO` (implícito) | `ACTIVO` \| `INACTIVO`. Si se omite, solo se listan ingredientes activos — ver ADR 0001, sección 15 |
| `inventario` | string | No | — | `SIN_STOCK` \| `NECESITA_REPOSICION` \| `CON_STOCK`. Partición sin solapamiento: `SIN_STOCK` = cantidadActual = 0; `NECESITA_REPOSICION` = 0 < cantidadActual < stockMinimo; `CON_STOCK` = cantidadActual ≥ stockMinimo |
| `unidadMedidaId` | number | No | — | Filtra por una unidad de medida puntual |

Los tres filtros (`estado`, `inventario`, `unidadMedidaId`) son independientes y combinables entre sí (AND).

Ejemplo: `GET /ingredients?page=0&size=15&search=harina&sort=nombre,asc&inventario=NECESITA_REPOSICION`

## Request JSON

No aplica (GET sin body).

## Response JSON

`200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "nombre": "Harina 000",
      "unidadMedida": { "id": 1, "nombre": "KILOGRAMO", "abreviatura": "kg" },
      "descripcion": "Harina para panificados",
      "activo": true,
      "stock": {
        "cantidadActual": 50,
        "cantidadDisponible": 45,
        "cantidadReservada": 5,
        "stockMinimo": 20,
        "necesitaReposicion": false
      }
    }
  ],
  "page": 0,
  "size": 15,
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true,
  "empty": false
}
```

Cada elemento es una vista de gestión (no la entidad `Articulo`/`Stock` tal cual): combina campos de ambos agregados en una sola fila, pensada para la grilla de la pantalla.

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `200 OK` | Listado obtenido (puede venir vacío) |
| `400 Bad Request` | `sort`, `estado` o `inventario` con un valor no reconocido |

## Errores posibles

| `code` | status | Causa |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Formato de `sort` inválido, o valor de `estado`/`inventario` fuera de los permitidos |

## Notas

- El cruce entre `Articulo` y `Stock` (agregados separados en escritura, ver `docs/modules/produccion.md` sección 6) se resuelve con una consulta de lectura dedicada (`ArticuloRepository.buscarConStockPaginado`, implementada con Criteria API) — no hay mapeo JPA bidireccional entre ambos, ni falta hace: es una proyección exclusiva de lectura.
- Todos los ordenamientos y filtros se resuelven a nivel de base de datos, incluida la paginación — nunca se pagina en memoria.
- `necesitaReposicion` en la respuesta es el resultado de `Stock.necesitaReposicion()` (`cantidadActual < stockMinimo`), que también es verdadero para artículos sin stock (`cantidadActual = 0`). Es distinto del filtro `inventario=NECESITA_REPOSICION`, que excluye explícitamente los que están en `SIN_STOCK` para mantener las tres categorías sin solapamiento.
- Validado con 7 tests automatizados (`ListarIngredientesApiTest`: forma de la paginación, búsqueda, los tres filtros de inventario, orden descendente, tamaño de página, y error de validación) y en vivo contra MySQL.

## Consumido por

Pantalla de Gestión de Inventario de Ingredientes → grilla principal, buscador, filtros y encabezados ordenables.
