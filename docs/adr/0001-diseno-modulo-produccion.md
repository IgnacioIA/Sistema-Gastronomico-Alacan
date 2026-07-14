# ADR 0001 — Diseño del módulo Producción

Estado: Aceptado
Fecha: 2026-07-06
Relacionado: `docs/modules/produccion.md`, `.claude/architecture.md`, `.claude/engineering-playbook.md`

---

## 1. Producto e Ingrediente se unifican en Articulo

**Contexto:** Catalogo modela `Producto` e `Ingrediente` como clases separadas y no relacionadas entre sí, cada una con su propio repositorio.

**Decisión:** Ambas colapsan en un único concepto, `Articulo`, clasificado por `TipoArticulo`.

**Por qué:** El contrato de dominio (sección 5.1) establece que "toda la producción gira alrededor de Articulo" y que cualquier recurso administrado (ingrediente, preparación, producto, envase, descartable) es, en esencia, lo mismo desde el punto de vista de la fabricación: algo que se puede requerir en una receta, tener stock y generar movimientos. Mantener dos jerarquías paralelas (Producto por un lado, Ingrediente por otro) obligaría a duplicar Stock, Movimiento y las reglas de disponibilidad para cada una — exactamente el tipo de duplicación que el playbook pide evitar.

**Consecuencias:** Todo lo que hoy diferencia a un Producto de un Ingrediente dejará de expresarse como "clase distinta" y pasará a expresarse como "valor de `TipoArticulo`" distinto sobre el mismo tipo de entidad.

---

## 2. Producción es un proceso de dominio, no una entidad

**Contexto:** El enunciado original mencionaba "Producción" como si fuera el nombre del módulo y, potencialmente, de una entidad central.

**Decisión:** Producción no se persiste. No existe una tabla ni un agregado `Produccion`. Es un servicio de dominio / caso de uso que orquesta `Receta`, `Stock` y `Movimiento`.

**Por qué:** El contrato es explícito en que no existe `OrdenProduccion` en esta etapa. Todo lo que "ocurre" al producir queda registrado como `Movimiento` (el hecho histórico). No hay ninguna pregunta de negocio (sección 2 del contrato) que requiera consultar "una producción pasada" como entidad propia — se responde con movimientos. Modelar Producción como entidad hoy sería anticipar un requisito (trazabilidad de órdenes) que no fue pedido.

**Consecuencias:** Si en el futuro se pide trazar "quién ejecutó qué producción y cuándo" de forma más rica que la suma de sus movimientos, eso es un ADR nuevo (promover Producción a entidad), no una extensión silenciosa de este.

---

## 3. Una receta produce exactamente un artículo

**Decisión:** `Receta` tiene una única relación "produce" hacia `Articulo`, cardinalidad 1. Nunca una lista.

**Por qué:** Es una regla de negocio explícita y no negociable (contrato 5.3, reafirmada en el pedido de implementación). Técnicamente, permitir múltiples artículos producidos por receta rompería la trazabilidad de "cuánto cuesta/rinde producir X" y complicaría el cálculo de disponibilidad sin ningún caso de uso real que lo justifique hoy.

**Consecuencias:** Si un proceso real produce subproductos (mermas aprovechables, por ejemplo), eso se modela como recetas separadas, no como una receta multi-salida.

---

## 4. No existe producción automática de recetas dependientes (sin multinivel automático)

**Contexto:** `docs/modules/produccion.md` dejaba esto como pregunta abierta.

**Decisión (ahora cerrada por el pedido de implementación):** Si un artículo requerido (por ejemplo, una Preparación) no tiene stock suficiente, la producción **falla e informa el faltante**. No se dispara automáticamente la receta de esa Preparación para fabricarla en cadena.

**Por qué:** Encadenar producciones automáticamente introduce complejidad real (¿qué pasa si la sub-receta también tiene faltantes? ¿en qué orden se resuelven ramas? ¿es atómico todo el árbol?) sin que exista todavía un caso de uso que lo pida. Preferimos un comportamiento simple y predecible: cada ejecución de producción trabaja sobre un único nivel de receta y stock existente.

**Consecuencias:** Producir una Preparación agotada es una acción manual y explícita del usuario (ejecutar la receta de la Preparación primero), no un efecto colateral de producir el artículo que la contiene.

---

## 5. El stock disponible se calcula, nunca se almacena

**Decisión:** `cantidadDisponible = cantidadActual - cantidadReservada` se calcula en el momento de la consulta.

**Por qué:** Persistir un valor derivado crea una segunda fuente de verdad. Cualquier actualización de `cantidadActual` o `cantidadReservada` que olvide recalcular el derivado deja el sistema en un estado inconsistente sin que nada lo detecte. Calcularlo siempre garantiza que sea imposible que diverja.

**Consecuencias:** El costo es una resta en memoria por consulta — irrelevante en performance frente al riesgo de inconsistencia evitado.

---

## 6. Toda producción es atómica

**Decisión:** La ejecución completa de una receta (verificación, registro de movimientos, actualización de stock del artículo producido y de los consumidos) ocurre dentro de una única transacción. Si falta disponibilidad de cualquier artículo requerido, no se modifica absolutamente nada.

**Por qué:** Es una regla de negocio explícita del dominio, y además es la única forma de mantener la invariante "todo movimiento de stock está respaldado por su movimiento correspondiente" (contrato 5.6) sin ventanas donde el stock quede desincronizado del historial.

**Consecuencias:** La responsabilidad de demarcar la transacción vive en la capa de Application (caso de uso `EjecutarProduccion` o equivalente), no en el dominio — el dominio no conoce transacciones, según `architecture.md` ("No Spring" en la capa Domain).

---

## 7. Stock y Movimiento se actualizan en la misma transacción

**Decisión:** No existe un paso intermedio donde el stock quede actualizado sin su movimiento correspondiente, ni viceversa.

**Por qué:** Es un corolario directo de la decisión 6 y de la invariante de la entidad `Movimiento` ("todo cambio de stock debe estar respaldado por al menos un movimiento"). Separarlos en transacciones distintas abriría una ventana de inconsistencia detectable (stock cambiado sin explicación histórica, o movimiento registrado que nunca impactó el stock).

**Consecuencias:** Cualquier futuro requisito de "eventual consistency" entre stock y movimiento (por ejemplo, para escalar a múltiples sucursales) requeriría revisar este ADR explícitamente.

---

## 8. TipoArticulo y TipoMovimiento son clasificaciones abiertas, modeladas como entidades persistentes

> **Revisión (2026-07-06):** esta sección reemplaza la decisión técnica original de este ADR. La versión anterior aceptaba un `enum` de Java como implementación inicial. Se revirtió explícitamente: se detectó como contradicción durante la revisión de consistencia previa a la implementación y el usuario confirmó el cambio. Se deja registrada la razón del cambio en vez de reescribir la historia.

**Decisión:** `TipoArticulo` y `TipoMovimiento` no se modelan como enums cerrados ni en el dominio ni en la implementación. Se modelan como **entidades persistentes** (id + nombre, sin lógica de negocio), de forma que agregar un tipo nuevo sea un dato nuevo y no un cambio de código ni una migración de esquema.

**Por qué:** El contrato de dominio (5.2 y 5.7) es explícito: los valores dados son "los identificados hoy", no una lista definitiva. Un enum de Java sigue exigiendo una recompilación y un despliegue para incorporar un tipo nuevo — no cumple genuinamente la extensibilidad que el contrato pide. Una entidad persistente sí lo hace.

**Consecuencias:**
- `Articulo.tipoArticulo` y `Movimiento.tipoMovimiento` son relaciones (`@ManyToOne`) hacia estas entidades, no campos `@Enumerated`.
- Los valores iniciales conocidos (Ingrediente, Preparación, Producto, Envase, Descartable / Producción, Compra, Ajuste, Desperdicio, Reserva, Liberación) se cargan mediante un **script de datos (seed)**, no mediante un caso de uso de alta — en esta etapa no existe "Crear TipoArticulo"/"Crear TipoMovimiento" como caso de uso. Gestionar tipos en tiempo de ejecución (crear, editar, deshabilitar) queda diferido a una etapa posterior, cuando exista un consumidor real de esa capacidad (por ejemplo, un panel de administración).
- `Sentido` (ENTRADA/SALIDA, ver decisión 10) **no** sigue este mismo tratamiento: es un concepto estructural y cerrado (siempre dos valores, no es una clasificación de negocio que el usuario vaya a extender), y permanece como `enum` de Java.

---

## 9. Separación de datos comerciales y su impacto en Pedido

**Contexto — conflicto detectado durante el análisis:**

El contrato de Producción (sección 4) es explícito: el módulo **no calcula costos ni precios**. Pero el `Producto` actual de Catalogo mezcla dos responsabilidades en una sola clase:

- Datos de fabricación: `nombreProducto`, `recetas` (lista de ingredientes).
- Datos comerciales: `precioVenta`, `descripcionAlPublico`, `activo`.

Además, el módulo `Pedido` — que ya existe, está en desarrollo activo (con cambios sin commitear al momento de este análisis) y **no está dentro del alcance de esta tarea** — depende directamente de esos datos comerciales y de la estructura de receta actual:

- `Pedido.agregarProducto(Producto, ...)` y `Pedido.agregarCombo(Combo, ...)` leen `precioVenta` / `precioCombo` y el nombre directamente del agregado de Catalogo para construir el snapshot del ítem.
- `CalculadorConsumoIngredientes` (en `Pedido.domain.service`) ya calcula qué ingredientes consume un pedido recorriendo `Producto.getRecetas()` y `Combo.getOpciones()` — es decir, hoy implementa, dentro de Pedido, exactamente la responsabilidad que el contrato de Producción asigna al proceso "Producción" (preguntas de negocio 4 y 5).

Esto significa que **no es posible implementar Articulo sin precio y reestructurar Receta sin romper Pedido tal como está hoy**, salvo que se resuelva antes uno de estos tres caminos:

**Opción A — Alcance mínimo, romper Pedido temporalmente.** Implementar Producción estrictamente per contrato y dejar que Pedido quede roto hasta un refactor posterior. Riesgo: Pedido tiene trabajo en curso sin commitear; sería destructivo y no fue pedido.

**Opción B — Extraer ya un concepto comercial nuevo.** Crear en este mismo trabajo un módulo/concepto (`Comercial` o similar) que envuelva a Articulo con precio, descripción pública y estado activo, y migrar `Pedido`/`CalculadorConsumoIngredientes` a consumirlo. Resuelve el conflicto de raíz pero expande el alcance muy por fuera de "implementar Producción" y toca código de Pedido con cambios sin commitear.

**Opción C (recomendada) — Migración en dos fases.** Construir el módulo Producción completo y correcto en un paquete nuevo (`com.Alacan.demo.Produccion`), sin tocar `Catalogo` ni `Pedido` todavía. `Catalogo` queda marcado como deprecado pero funcional. Se abre explícitamente un ítem de seguimiento: "migrar Pedido de Catalogo a Producción + nuevo concepto comercial", a resolver como tarea separada una vez Producción esté validado. `Combo`/`OpcionCombo` (funcionalidad comercial) tampoco se tocan ahora — se documentan como pertenecientes a un futuro módulo comercial/menú, no a Producción.

**Decisión:** Opción C — migración en dos fases. Se construye el módulo `Produccion` completo y correcto en un paquete nuevo (`com.Alacan.demo.Produccion`), sin tocar `Catalogo` ni `Pedido` en esta tarea. `Catalogo` queda marcado como deprecado pero funcional. `Combo`/`OpcionCombo` no se tocan — quedan documentados como pertenecientes a un futuro módulo comercial/menú. La migración de `Pedido` (y la extracción del concepto comercial que hoy vive mezclado en `Producto`) queda como ítem de seguimiento explícito, fuera del alcance de esta tarea.

**Por qué:** Evita trabajo destructivo sobre código de `Pedido` con cambios sin commitear, respeta el principio de no introducir cambios no pedidos, y permite validar Producción de forma aislada antes de acoplar nada. El costo — convivir brevemente con `Catalogo` y `Produccion` en paralelo — es explícito, documentado y temporal.

---

## 10. Movimiento incorpora un atributo `sentido` (ENTRADA / SALIDA)

**Contexto — vacío detectado durante el diseño de detalle:** `TipoMovimiento` (contrato 5.7) no alcanza para reconstruir el stock a partir del historial. El propio flujo de producción (contrato 7.1) genera, con el mismo `tipo = Producción`, un movimiento que resta stock (consumo de un insumo) y otro que suma stock (ingreso del artículo producido). Con un único campo `tipo`, un movimiento de tipo Producción es ambiguo: no se puede saber si sumó o restó `cantidadActual`. Esto entra en conflicto directo con la invariante de la sección 5.6 del contrato ("el stock actual debería ser, conceptualmente, reconstruible a partir de la suma de sus movimientos").

**Decisión:** `Movimiento` incorpora un atributo `sentido` (`ENTRADA` / `SALIDA`), ortogonal a `tipo`. `tipo` clasifica el motivo del movimiento (Producción, Compra, Ajuste, Desperdicio, Reserva, Liberación); `sentido` indica si la cantidad sumó o restó sobre `cantidadActual` (o, en el caso de Reserva/Liberación, sobre `cantidadReservada`). `cantidad` se almacena siempre en valor absoluto (positivo); el signo lo aporta `sentido`, nunca el número.

**Por qué:** Es la opción que no obliga a anticipar, para cada futuro `TipoMovimiento` (Compra, Ajuste, etc.), si su dirección es fija o variable — el mismo patrón de "tipo + dirección" sirve para todos sin excepciones especiales. Mantiene `TipoMovimiento` como clasificación abierta (decisión 8) sin que agregar un tipo nuevo obligue a decidir de nuevo cómo se combina con la dirección.

**Consecuencias:** `Stock.cantidadActual` se actualiza según `sentido` (ENTRADA suma, SALIDA resta) y `tipo` determina si el movimiento afecta `cantidadActual` (Producción, Compra, Ajuste, Desperdicio) o `cantidadReservada` (Reserva, Liberación) — esta segunda distinción (qué campo de Stock afecta cada tipo) queda documentada en la sección de diseño de detalle, no repetida aquí.

---

## 11. Receta incorpora un atributo `activo`; unicidad de "receta que produce un artículo" se valida solo entre recetas activas

**Contexto:** se introdujo la regla "un artículo solo puede ser producido por una única receta activa a la vez", no presente en la versión original del contrato.

**Decisión:** `Receta` incorpora un campo `activo` (booleano). La invariante de unicidad (un artículo tiene como máximo una receta que lo produce) se aplica **solo entre recetas activas** — puede existir más de una receta histórica para el mismo artículo, siempre que a lo sumo una esté activa.

**Por qué:** Sin este campo, cambiar la forma de producir un artículo (por ejemplo, una nueva versión de la receta) obligaría a borrar la receta anterior, perdiendo su historial y rompiendo la trazabilidad de los `Movimiento` ya generados con esa receta (que siguen referenciando artículos, no la receta en sí, pero conceptualmente conviene poder auditar qué receta estuvo vigente en cada momento). Desactivar en vez de borrar es el mismo principio ya aplicado a `Movimiento`: preferir historial sobre destrucción de datos.

**Consecuencias:** Los casos de uso de creación de receta (`CrearRecetaService`) deben desactivar cualquier receta activa previa del mismo artículo antes de activar la nueva (o rechazar la creación si ya existe una activa, exigiendo una desactivación explícita primero — este matiz de UX/flujo se resuelve en la implementación del caso de uso, no cambia el modelo). La consulta de "receta vigente de un artículo" siempre filtra por `activo = true`.

---

## 12. UnidadMedida se modela como entidad persistente, igual que TipoArticulo y TipoMovimiento

**Contexto:** al diseñar el primer caso de uso real de creación de artículos ("Crear Ingrediente"), apareció la necesidad de cuantificar un artículo (kg, litros, unidades, etc.). El `Articulo` original no tenía este concepto.

**Decisión:** `UnidadMedida` es una entidad persistente (`id`, `nombre`, `abreviatura`), no un `enum` ni un `String` libre. `Articulo` gana una relación obligatoria hacia `UnidadMedida`.

**Por qué:** Es exactamente el mismo tipo de concepto que `TipoArticulo`/`TipoMovimiento` — una clasificación que hoy tiene un conjunto conocido de valores (GRAMO, KILOGRAMO, MILILITRO, LITRO, UNIDAD) pero que no se puede asumir cerrada para siempre. Tratarla distinto (por ejemplo como `String` libre) reintroduciría exactamente el problema que la decisión 8 ya resolvió para los otros dos casos: valores inconsistentes, sin validación, y una migración de código para agregar uno nuevo.

**Consecuencias:** Nuevo repositorio `UnidadMedidaRepository`, seed idempotente en `data.sql` con los 5 valores conocidos (los mismos que existían como `enum Catalogo.UnidadMedida`). Sin caso de uso de alta en esta etapa — mismo criterio que `TipoArticulo`/`TipoMovimiento` (decisión 8).

---

## 13. Stock incorpora `stockMinimo` y el comportamiento `necesitaReposicion()`

**Decisión:** `Stock` gana un campo `stockMinimo` (no nulo, default cero) y un único método de comportamiento, `necesitaReposicion()` (`cantidadActual < stockMinimo`).

**Por qué:** El umbral de reposición es una configuración permanente del inventario, no un dato calculado exclusivamente para el Frontend — pertenece al dominio (mismo criterio que llevó a modelar `cantidadDisponible()` como comportamiento en vez de dejar que cada consumidor la recalculara). Se descartó explícitamente tener dos métodos (`necesitaReposicion()` y `estaPorDebajoDelMinimo()`) por expresar la misma regla de negocio con nombres distintos — duplicación sin beneficio.

**Consecuencias:** `Stock` tiene un segundo constructor, `Stock(Articulo, BigDecimal stockMinimo)`, además del existente `Stock(Articulo)` (que delega con `stockMinimo = 0`). No se agregó una forma de modificar `stockMinimo` después de creado el `Stock` porque ningún caso de uso actual lo requiere todavía (se agregará cuando exista "Editar Ingrediente").

---

## 14. TipoMovimiento gana el valor `ALTA`; CrearArticuloService se elimina en favor de CrearIngredienteService

**Contexto:** "Crear Ingrediente" necesita registrar el ingreso de stock inicial como un movimiento trazable, distinto de `AJUSTE` (corrige algo existente) y de `COMPRA` (operación comercial no modelada todavía).

**Decisión (parte 1):** se agrega `ALTA` a `TipoMovimiento` vía seed en `data.sql` — ningún cambio de código, validando en la práctica la extensibilidad prometida por la decisión 8.

**Decisión (parte 2):** se elimina `CrearArticuloService` (creaba un `Articulo` genérico + `Stock` vacío) y se reemplaza por `CrearIngredienteService`, específico y autocontenido.

**Por qué:** `CrearArticuloService` nunca representó una acción real de un usuario — ningún controller lo invocaba; su único consumidor era el propio test de integración de "Ejecutar Producción", que lo usaba como atajo para crear datos de prueba. Mantenerlo habría significado sostener una abstracción sin caso de uso real detrás, exactamente lo que el principio "la API representa casos de uso, no CRUD sobre entidades" busca evitar. `CrearIngredienteService` no lo reutiliza como base porque sus requisitos ya divergen (unidad de medida, stock mínimo, stock inicial opcional, movimiento condicional) — reutilizar hubiera significado partir la transacción de creación de `Stock` en dos pasos, o acoplar dos servicios de aplicación sin ahorrar código real.

**Consecuencias:** El test `EjecutarProduccionIntegrationTest` ya no depende de ningún caso de uso de aplicación para construir sus artículos de prueba (`Harina`, `Pan`) — construye `Articulo`/`Stock` directamente vía sus repositorios, que es el patrón esperado para fixtures de test que no son el objeto bajo prueba.

---

## 15. Articulo incorpora `activo` (desactivación lógica, sin eliminación física)

**Contexto:** la pantalla de Gestión de Inventario de Ingredientes necesita poder dar de baja un ingrediente sin destruir su historial. Surgió durante la revisión de diseño de esa pantalla (no en el diseño original del contrato de Producción).

**Decisión:** `Articulo` incorpora un campo `activo` (booleano, default `true`). Significado exacto:

- Oculta el artículo de la operación diaria (listados, búsquedas por defecto).
- No permite nuevas operaciones de inventario sobre el artículo (crear movimientos, ejecutar producción que lo consuma como insumo) mientras esté desactivado.
- Preserva intactas sus `Receta`s, `Movimiento`s y todo su historial — nunca se borra nada.
- Permite reactivación futura (volver `activo = true`).
- Un artículo con referencias activas (ej. usado en una `Receta` vigente) se desactiva igual; la diferencia es únicamente el mensaje mostrado al usuario, no el comportamiento del sistema.

**Por qué:** Es el mismo patrón ya usado en `Receta.activo` (decisión 11) y en `Movimiento` como registro inmutable: preferir historial sobre destrucción de datos. La alternativa (eliminación física) rompería la integridad referencial con `Movimiento`/`RecetaDetalle` o forzaría un cascade delete sobre el historial, que el dominio evita deliberadamente en todos lados.

**Consecuencias:** Casos de uso de escritura sobre stock/producción (`EjecutarProduccionService`, y los casos de uso de movimiento de la decisión 17) deben validar `articulo.isActivo()` antes de operar. Las consultas de listado filtran por `activo = true` salvo que se pida explícitamente lo contrario (filtro "Inactivos").

---

## 16. La unidad de medida de un Articulo se bloquea una vez que existe historial de inventario

**Contexto:** se evaluó permitir cambiar `unidadMedida` libremente como "corrección de datos". Se identificó un riesgo real: `Stock.cantidadActual/cantidadReservada/stockMinimo` y todo `RecetaDetalle.cantidadRequerida` que referencia el artículo están expresados en esa unidad: cambiarla sin conversión reinterpreta silenciosamente esos números bajo una unidad distinta (ej. 50 pasa de significar 50kg a significar 50g).

**Decisión:** `unidadMedida` solo puede modificarse mientras el artículo **no tenga stock físico (`cantidadActual = 0`) ni movimientos registrados**. Una vez que existe cualquier historial de inventario, la unidad queda bloqueada permanentemente para ese artículo. No se implementan conversiones automáticas entre unidades — si hiciera falta cambiarla después de tener historial, la vía es dar de baja el artículo y crear uno nuevo con la unidad correcta.

**Por qué:** Es una protección barata contra un error caro y silencioso — exactamente el tipo de decisión que "no agregar reglas para no complicar el flujo" pasaría por alto hasta que ya sea tarde (stock y recetas con magnitudes incorrectas, sin ningún error visible).

**Consecuencias:** El caso de uso "Actualizar Ingrediente" debe consultar `Stock` y el historial de `Movimiento` antes de aceptar un cambio de `unidadMedida`, y rechazarlo (no ignorarlo silenciosamente) si ya existe historial.

---

## 17. Movimientos de inventario: casos de uso específicos por motivo de negocio, no un único servicio genérico

**Contexto:** `RegistrarAjusteStockService` (existente) fija `TipoMovimiento = AJUSTE` sin importar el motivo real. La pantalla de inventario necesita distinguir "registrar ingreso", "registrar pérdida" y "registrar corrección" como acciones separadas y trazables.

**Decisión:** se implementan casos de uso de aplicación independientes para cada motivo. Cada uno decide internamente su `TipoMovimiento` correspondiente; el Frontend nunca lo envía. `RegistrarAjusteStockService` se reevalúa en ese momento (probablemente absorbido por el caso de uso de "corrección", que es semánticamente el mismo `TipoMovimiento = AJUSTE`).

**Por qué:** Es la misma razón que llevó a reemplazar `CrearArticuloService` por `CrearIngredienteService` (decisión 14): un servicio genérico no representa un caso de uso real, y dejar que el llamador elija el `TipoMovimiento` traslada al Frontend una decisión de dominio que le corresponde al Backend.

**Consecuencias:** El mapeo exacto motivo → `TipoMovimiento`/`Sentido` se termina de definir cuando se diseñe el contrato de cada caso de uso puntual (no se fija de antemano en este ADR).

**Mapeo resuelto (se completa a medida que se implementa cada caso de uso):**

| Caso de uso | `TipoMovimiento` | `Sentido` | Estado |
|---|---|---|---|
| Registrar ingreso de stock | `COMPRA` (reutilizado, ya seedeado) | `ENTRADA` | Implementado |
| Registrar pérdida de stock | `DESPERDICIO` (reutilizado, ya seedeado) | `SALIDA` | Implementado |
| Registrar corrección de stock | `AJUSTE` (reutilizado, ya seedeado) | calculado (`cantidadReal` vs. `cantidadActual`) | Implementado — reemplazó a `RegistrarAjusteStockService` |

Ninguno de los tres necesitó un `TipoMovimiento` nuevo — los tres motivos ya estaban contemplados en el seed original de `data.sql`.

**Cierre de la sección:** con los tres casos de uso implementados, `RegistrarAjusteStockService` se eliminó (sin consumidor real, mismo motivo que llevó a eliminar `CrearArticuloService` en la decisión 14). El único caso de uso con contrato bidireccional es "corrección": en vez de que el Frontend envíe `cantidad` + `sentido` (los parámetros técnicos internos de `Stock`), envía `cantidadReal` (la cantidad física contada) y el Backend calcula la dirección — ver `docs/integration/ingredients/register-stock-correction.md` para el razonamiento completo.

---

## 18. Auditoría mínima: `fechaCreacion` en entidades principales, sin infraestructura de auditoría todavía

**Decisión:** se agrega `fechaCreacion` a `Articulo` y `Receta` — las entidades "principales" con ciclo de vida propio y sentido de negocio en tenerla. No se agrega a `Stock` (su creación coincide siempre con la del `Articulo`, sería redundante), ni a `RecetaDetalle` (hijo de `Receta`, sin ciclo de vida propio), ni a `TipoArticulo`/`TipoMovimiento`/`UnidadMedida` (datos de catálogo cargados por seed, no entidades de negocio con historia propia). `Movimiento` ya tiene `momento`, que cumple ese rol — no se duplica.

No se agregan todavía: `fechaActualizacion`, `usuarioCreacion`, `usuarioActualizacion`, ni una clase base de auditoría compartida. Se agregan cuando exista un caso de uso real que los necesite.

**Por qué:** Evita una migración de datos futura cuando aparezcan casos de uso de auditoría u ordenamiento temporal, sin construir infraestructura de auditoría genérica que todavía nadie pidió.

**Nota de implementación (Listar Ingredientes):** `ddl-auto=update` de Hibernate agrega columnas nuevas a tablas ya existentes en MySQL, pero no rellena valores para las filas previas — quedaron con `activo=false`/`fecha_creacion=NULL`. En este proyecto no representa un riesgo real (datos de desarrollo), pero si el dominio agrega un campo no-nulo sobre una tabla con datos reales en el futuro, va a hacer falta un script de migración explícito, no alcanza con dejar que Hibernate actualice el esquema.

---

## 19. Listados de gestión: proyección de lectura dedicada, sin mapeo JPA bidireccional entre agregados

**Contexto:** "Listar Ingredientes" necesita filtrar y ordenar simultáneamente por campos de `Articulo` (nombre, activo, unidadMedida) y de `Stock` (cantidadActual, stockMinimo) con paginación correcta a nivel de base de datos — no alcanza con traer todo y paginar en memoria.

**Decisión:** se agrega `ArticuloRepository.buscarConStockPaginado(...)`, implementado con JPA Criteria API (`EntityManager` directo, no derived queries de Spring Data), que arma un query con dos raíces (`Articulo` y `Stock`) correlacionadas explícitamente, sin agregar una relación JPA bidireccional entre ambas entidades. El resultado se expone como `ArticuloConStock`, una proyección de solo lectura (no una entidad, no un agregado) que vive en un paquete nuevo, `domain.query`, junto con los demás tipos de paginación/filtro/orden (`Paginacion`, `FiltroListaArticulos`, `OrdenIngrediente`, `ResultadoPaginado<T>`).

**Por qué:** `Articulo` no conoce su `Stock` a propósito (decisión de diseño original del módulo — ver sección 6 de `docs/modules/produccion.md`: son agregados separados, cada uno controla su propia consistencia en escritura). Agregar una relación bidireccional solo para poder hacer joins de lectura violaría esa separación para beneficio de un único caso de uso de consulta. Las derived queries de Spring Data tampoco alcanzan acá: no soportan filtros dinámicos combinables ni ordenar por una expresión calculada (`cantidadDisponible`, `necesitaReposicion`) cruzando dos raíces sin relación mapeada. Un query de lectura no está obligado a respetar los límites de agregado de escritura — es una práctica común y no viola DDD: los límites de agregado protegen invariantes de consistencia transaccional, no las consultas.

**Consecuencias:** Cualquier futuro listado equivalente (Preparaciones, Productos) puede reusar el mismo método de repositorio sin cambios, porque `TipoArticulo` es un parámetro explícito, no algo fijado internamente. Si en el futuro aparecen más combinaciones de filtros/orden que compliquen mantener esto a mano con Criteria API, vale la pena evaluar `Specification<T>` de Spring Data o una librería de queries dedicada — no se adoptó ahora por no ser necesario todavía.
