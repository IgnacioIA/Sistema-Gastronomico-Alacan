# Caso de Uso: Crear Ingrediente

## Objetivo

Dar de alta un artículo de tipo Ingrediente en el catálogo de Producción, con su stock inicial y stock mínimo opcionales, en una única operación atómica.

## Flujo del Usuario

1. El usuario entra a la pantalla Ingredientes y presiona "Agregar ingrediente".
2. Se abre un modal con: Nombre, Unidad de medida, Descripción (opcional).
3. Existe una sección opcional "Registrar stock inicial" con: Cantidad inicial, Stock mínimo (ambos independientes entre sí; el usuario puede cargar solo uno de los dos, ambos, o ninguno).
4. Al confirmar:
   - Se crea el Artículo (tipo `INGREDIENTE`, fijado por el Backend).
   - Se crea el Stock asociado, con el `stockMinimo` informado (o 0 si no se informó).
   - Si se informó `cantidadInicial > 0`: se registra el ingreso de stock y un `Movimiento` de tipo `ALTA`.
   - Se muestra una notificación de éxito y el nuevo ingrediente aparece en el listado.

## Endpoint

`/ingredients`

## Método HTTP

`POST`

## Path Variables

Ninguno.

## Query Params

Ninguno.

## Request JSON

```json
{
  "nombre": "Harina 000",
  "unidadMedidaId": 1,
  "descripcion": "Harina de trigo",
  "stockMinimo": 5,
  "stockInicial": {
    "cantidad": 25
  }
}
```

Si el usuario no registra stock inicial:

```json
{
  "nombre": "Harina 000",
  "unidadMedidaId": 1,
  "descripcion": "Harina de trigo",
  "stockMinimo": 5
}
```

| Campo | Tipo | Requerido | Notas |
|---|---|---|---|
| `nombre` | string | Sí | No puede estar vacío |
| `unidadMedidaId` | number | Sí | Debe existir (ver `UnidadMedida`) |
| `descripcion` | string | No | — |
| `stockMinimo` | number | No | Default `0` si se omite |
| `stockInicial` | object | No | Si se omite, el artículo queda con stock en `0` |
| `stockInicial.cantidad` | number | Sí (dentro del bloque) | Debe ser `> 0` para que se registre movimiento |

**Nunca enviar desde el Frontend:** `TipoArticulo` (se fuerza a `INGREDIENTE`), `id`, fechas, `cantidadReservada`, ni el tipo/sentido del `Movimiento` inicial.

## Response JSON

`201 Created`

```json
{
  "id": 12,
  "nombre": "Harina 000",
  "tipoArticulo": { "id": 1, "nombre": "INGREDIENTE" },
  "unidadMedida": { "id": 1, "nombre": "KILOGRAMO", "abreviatura": "kg" },
  "descripcion": "Harina de trigo",
  "stock": {
    "cantidadActual": 25,
    "cantidadReservada": 0,
    "cantidadDisponible": 25,
    "stockMinimo": 5,
    "necesitaReposicion": false
  }
}
```

Header `Location: /ingredients/{id}`.

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `201 Created` | Ingrediente creado correctamente |
| `400 Bad Request` | Datos inválidos (nombre vacío, cantidades negativas, etc.) |
| `404 Not Found` | `unidadMedidaId` no existe |
| `500 Internal Server Error` | Error de configuración interna (ver Notas) |

## Errores posibles

| `code` | status | Causa |
|---|---|---|
| `VALIDATION_ERROR` | 400 | Nombre vacío, cantidad negativa, stock mínimo negativo, etc. |
| `UNIDAD_MEDIDA_NOT_FOUND` | 404 | El `unidadMedidaId` enviado no existe |
| `INTERNAL_CONFIGURATION_ERROR` | 500 | Falta el seed de `TipoArticulo=INGREDIENTE` o `TipoMovimiento=ALTA` — no debería ocurrir nunca en un ambiente correctamente inicializado |

Formato de error, ver `INTEGRATION_GUIDE.md` (RFC 7807 + `code`).

## Notas

- Primer caso de uso de Producción con capa `api`. Incluye el `GlobalExceptionHandler` del módulo, que mapea toda la jerarquía de excepciones de dominio existente (no solo las de este caso de uso).
- `CrearIngredienteService` es autocontenido: no reutiliza ningún otro caso de uso de creación de artículos genérico (ese código, `CrearArticuloService`, se eliminó por no representar una acción real del usuario — ver ADR 0001).
- `unidadMedida` y `tipoArticulo` se devuelven como objetos completos, no como strings, para que el Frontend no necesite mapear códigos a etiquetas.
- Validado end-to-end vía HTTP real contra MySQL (perfil `dev`) y con tests automatizados (`CrearIngredienteApiTest`, `EjecutarProduccionIntegrationTest`).

## Consumido por

Pantalla Ingredientes → botón "Agregar ingrediente" → modal de alta.

## Pantalla

Ingredientes

## Componente React

A definir por el equipo de Frontend (proyecto externo a este repositorio). Este documento es el contrato estable contra el cual ese componente debe integrarse.
