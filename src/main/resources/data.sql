-- Seed de clasificaciones abiertas del modulo Produccion (ADR 0001, seccion 8).
-- Los valores iniciales identificados en docs/modules/produccion.md; agregar nuevos
-- tipos en el futuro es un INSERT, no un cambio de codigo.
--
-- Idempotente a proposito: en H2 (perfil test) la base es en memoria y se recrea en
-- cada corrida, pero en MySQL (perfil dev) la base persiste entre reinicios de la app,
-- y spring.sql.init.mode=always vuelve a correr este script en cada arranque.

INSERT INTO tipo_articulo (id, nombre) SELECT 1, 'INGREDIENTE' WHERE NOT EXISTS (SELECT 1 FROM tipo_articulo WHERE id = 1);
INSERT INTO tipo_articulo (id, nombre) SELECT 2, 'PREPARACION' WHERE NOT EXISTS (SELECT 1 FROM tipo_articulo WHERE id = 2);
INSERT INTO tipo_articulo (id, nombre) SELECT 3, 'PRODUCTO' WHERE NOT EXISTS (SELECT 1 FROM tipo_articulo WHERE id = 3);
INSERT INTO tipo_articulo (id, nombre) SELECT 4, 'ENVASE' WHERE NOT EXISTS (SELECT 1 FROM tipo_articulo WHERE id = 4);
INSERT INTO tipo_articulo (id, nombre) SELECT 5, 'DESCARTABLE' WHERE NOT EXISTS (SELECT 1 FROM tipo_articulo WHERE id = 5);

INSERT INTO tipo_movimiento (id, nombre) SELECT 1, 'PRODUCCION' WHERE NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = 1);
INSERT INTO tipo_movimiento (id, nombre) SELECT 2, 'COMPRA' WHERE NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = 2);
INSERT INTO tipo_movimiento (id, nombre) SELECT 3, 'AJUSTE' WHERE NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = 3);
INSERT INTO tipo_movimiento (id, nombre) SELECT 4, 'DESPERDICIO' WHERE NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = 4);
INSERT INTO tipo_movimiento (id, nombre) SELECT 5, 'RESERVA' WHERE NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = 5);
INSERT INTO tipo_movimiento (id, nombre) SELECT 6, 'LIBERACION' WHERE NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = 6);
-- ALTA: ingreso inicial de stock al crear un articulo (caso de uso Crear Ingrediente).
-- Distinto de AJUSTE (corrige algo existente) y de COMPRA (operacion comercial, aun no modelada).
INSERT INTO tipo_movimiento (id, nombre) SELECT 7, 'ALTA' WHERE NOT EXISTS (SELECT 1 FROM tipo_movimiento WHERE id = 7);

-- Unidades de medida (Articulo.unidadMedida). Misma logica que TipoArticulo/TipoMovimiento:
-- clasificacion abierta como entidad persistente, agregar una nueva es un INSERT.
INSERT INTO unidad_medida (id, nombre, abreviatura) SELECT 1, 'GRAMO', 'g' WHERE NOT EXISTS (SELECT 1 FROM unidad_medida WHERE id = 1);
INSERT INTO unidad_medida (id, nombre, abreviatura) SELECT 2, 'KILOGRAMO', 'kg' WHERE NOT EXISTS (SELECT 1 FROM unidad_medida WHERE id = 2);
INSERT INTO unidad_medida (id, nombre, abreviatura) SELECT 3, 'MILILITRO', 'ml' WHERE NOT EXISTS (SELECT 1 FROM unidad_medida WHERE id = 3);
INSERT INTO unidad_medida (id, nombre, abreviatura) SELECT 4, 'LITRO', 'l' WHERE NOT EXISTS (SELECT 1 FROM unidad_medida WHERE id = 4);
INSERT INTO unidad_medida (id, nombre, abreviatura) SELECT 5, 'UNIDAD', 'u' WHERE NOT EXISTS (SELECT 1 FROM unidad_medida WHERE id = 5);
