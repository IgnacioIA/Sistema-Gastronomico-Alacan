# Guía de Integración Backend ↔ Frontend

Versión: 1.0

Este documento contiene únicamente las reglas **generales** de integración, válidas para todos los casos de uso. Cada caso de uso puntual se documenta por separado (ver `integration-contract.md` como índice).

El Frontend que consume esta API vive en un repositorio distinto — este backend no lo scaffoldea ni lo contiene. Esta guía y los contratos documentados son el punto de contacto entre ambos proyectos.

---

## Principios

- La API representa **casos de uso**, no operaciones CRUD sobre entidades internas. Un endpoint expresa la intención del usuario, no la estructura del dominio.
- El dominio nunca se modifica únicamente para conveniencia del Frontend. Toda modificación del dominio debe estar justificada por una necesidad real del negocio (ver ADRs en `docs/adr/`).
- Si un caso de uso requiere crear o modificar varias entidades, el Backend lo hace en una única transacción. El Frontend nunca orquesta múltiples llamadas para lograr una operación atómica.

## Paginación

Todas las **colecciones de negocio** (Ingredientes, Movimientos, y las que se agreguen a futuro — Recetas, Producción) se exponen paginadas desde el día uno.

**Excepción — catálogos de referencia:** entidades de clasificación abierta (`UnidadMedida`, `TipoArticulo`, `TipoMovimiento` — ver ADR 0001, secciones 8 y 12) se devuelven **completas, como array plano**, sin envelope de paginación ni parámetros `page`/`size`. Son listas acotadas en la práctica (catálogos de configuración, no datos de negocio que crecen sin límite) y el caso de uso que las consume — poblar un selector — necesita el conjunto completo en una sola respuesta. Ver `integration-contract.md`, sección "Catalogs".

**Parámetros estándar (colecciones de negocio):**

| Parámetro | Requerido | Default | Notas |
|---|---|---|---|
| `page` | No | `0` | Base cero |
| `size` | No | `15` | Configurable por request |
| `search` | No | — | Búsqueda de texto libre; no se crean endpoints `/search` dedicados salvo justificación técnica |
| `sort` | No | — | Formato `campo,direccion` (ej. `nombre,asc`) |

Ejemplo: `GET /ingredients?page=0&size=15&search=harina&sort=nombre,asc`

**Forma de la respuesta paginada:**

```json
{
  "content": [],
  "page": 0,
  "size": 15,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true,
  "empty": true
}
```

## Manejo de errores (RFC 7807)

Todas las respuestas de error usan `ProblemDetail` (RFC 7807), con una propiedad adicional `code` — un identificador **estable** que el Frontend usa para personalizar la experiencia de usuario. El Frontend nunca debe depender del texto de `detail` para tomar decisiones: ese campo es para debugging humano y puede cambiar.

```json
{
  "type": "about:blank",
  "title": "Unidad de medida no encontrada",
  "status": 404,
  "detail": "Unidad de medida no encontrada: id=9999",
  "instance": "/ingredients",
  "code": "UNIDAD_MEDIDA_NOT_FOUND"
}
```

Los códigos de dominio se documentan por caso de uso, en la tabla de "Errores posibles" de cada documento.

## Datos controlados por el sistema

Ningún contrato de request debe aceptar desde el Frontend un valor que el dominio ya puede determinar por sí mismo. Ejemplos ya aplicados: el tipo de artículo cuando el caso de uso es específico de una categoría (ej. `TipoArticulo=INGREDIENTE` en "Crear Ingrediente"), el `TipoMovimiento` y el sentido de cualquier movimiento de inventario (cada caso de uso — ingreso, pérdida, corrección — lo decide internamente, nunca lo recibe del Frontend), el momento (`fecha/hora`) de cualquier `Movimiento` o `fechaCreacion`, IDs, y cualquier cálculo derivado (ej. `cantidadDisponible`, `necesitaReposicion`).

## Estructura de la documentación

```
docs/integration/
├── INTEGRATION_GUIDE.md       (este archivo)
├── integration-contract.md    (índice de todos los casos de uso)
├── ingredients/
│   └── create-ingredient.md
├── measurement-units/
│   └── list-measurement-units.md
├── recipes/
├── production/
└── stock/
```

Cada caso de uso tiene su propio documento, con el formato fijo definido en `integration-contract.md`. La documentación se actualiza junto con el código — nunca queda desincronizada.
