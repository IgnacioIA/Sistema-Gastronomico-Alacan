# Caso de Uso: Consultar Catálogo de Unidades de Medida

## Objetivo

Exponer el catálogo completo de unidades de medida para que el Frontend pueda poblar selectores (alta/edición de Ingredientes, y a futuro cualquier pantalla que cree o edite un `Articulo`), sin depender de una lista hardcodeada en el cliente.

## Flujo del Usuario

1. El Frontend necesita mostrar un selector de unidad de medida (por ejemplo, en el modal de "Agregar ingrediente" o "Editar ingrediente").
2. Antes de renderizar el formulario, consulta este endpoint una vez y usa la lista completa para poblar el selector.

## Endpoint

`/measurement-units`

## Método HTTP

`GET`

## Path Variables

Ninguno.

## Query Params

Ninguno — este catálogo no se pagina (ver Notas).

## Request JSON

No aplica (GET sin body).

## Response JSON

`200 OK`

```json
[
  { "id": 1, "nombre": "GRAMO", "abreviatura": "g" },
  { "id": 2, "nombre": "KILOGRAMO", "abreviatura": "kg" },
  { "id": 3, "nombre": "MILILITRO", "abreviatura": "ml" },
  { "id": 4, "nombre": "LITRO", "abreviatura": "l" },
  { "id": 5, "nombre": "UNIDAD", "abreviatura": "u" }
]
```

Nota la forma: es un **array plano**, no el envelope de paginación (`{ "content": [...], "page": ... }`) que usan los demás listados de esta API. Ver Notas.

## Códigos HTTP

| Código | Cuándo |
|---|---|
| `200 OK` | Catálogo obtenido |

## Errores posibles

Ninguno específico de este caso de uso — es una lectura sin parámetros ni validaciones.

## Notas

- **Por qué no está anidado en `/ingredients`:** `UnidadMedida` es un atributo de `Articulo` en general (ADR 0001, sección 12), no exclusivo de Ingredientes. Cuando existan pantallas de Preparaciones o Productos, van a necesitar el mismo catálogo — anidarlo bajo `/ingredients` habría acoplado un recurso genérico a una pantalla específica. Vive en su propio controller (`MeasurementUnitController`).
- **Por qué no se pagina:** es un catálogo de referencia (mismo tratamiento conceptual que `TipoArticulo`/`TipoMovimiento`, ver ADR 0001 secciones 8 y 12), no una colección de negocio que crece sin límite. El caso de uso que lo consume (poblar un selector) necesita la lista completa en una sola respuesta; paginarlo obligaría al Frontend a múltiples requests solo para llenar un `<select>`. Ver la regla general en `INTEGRATION_GUIDE.md`.
- El DTO de respuesta (`UnidadMedidaResponse`) es el mismo que ya se usa anidado dentro de las respuestas de Ingredientes — no se creó un DTO nuevo.
- El servicio de aplicación (`ListarUnidadesMedidaService`) y el repositorio (`UnidadMedidaRepository.buscarTodas()`) son triviales a propósito: sin validaciones ni reglas de negocio, es una lectura directa de catálogo. `buscarTodas()` ya existía desde el diseño original del módulo — solo faltaba exponerlo.
- No existe (todavía) un caso de uso para crear/editar unidades de medida en tiempo de ejecución — el catálogo se carga por seed en `data.sql` (ADR 0001, sección 8). Si se necesita administración en runtime, es una extensión futura separada.
- Validado con test automatizado (`ListarUnidadesMedidaApiTest`: catálogo completo, forma de array plano) y en vivo contra MySQL.

## Consumido por

Cualquier pantalla que cree o edite un `Articulo` — hoy, el modal de alta/edición de Ingredientes.
