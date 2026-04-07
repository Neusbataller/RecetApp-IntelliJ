-- ==========================================
-- BASE DE DATOS: recetas_db
-- Versión corregida con buenas prácticas
-- ==========================================

CREATE DATABASE IF NOT EXISTS recetas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE recetas_db;

-- ==========================================
-- ELIMINACIÓN DE TABLAS (orden por FK)
-- ==========================================

DROP TABLE IF EXISTS usuario_alergias;
DROP TABLE IF EXISTS lista_recetas;
DROP TABLE IF EXISTS listas;
DROP TABLE IF EXISTS favoritos;
DROP TABLE IF EXISTS receta_categorias;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS ingredientes_receta;
DROP TABLE IF EXISTS ingredientes;
DROP TABLE IF EXISTS valoraciones;
DROP TABLE IF EXISTS recuperacion_password;
DROP TABLE IF EXISTS recetas;
DROP TABLE IF EXISTS usuarios;
DROP TABLE IF EXISTS alergias;

-- ==========================================
-- CREACIÓN DE TABLAS
-- ==========================================

CREATE TABLE alergias (
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre         VARCHAR(100)        NOT NULL,
    apellidos      VARCHAR(150)        NOT NULL,
    correo         VARCHAR(150)        NOT NULL UNIQUE,
    -- Almacenar siempre el hash (bcrypt), nunca la contraseña en texto plano
    password_hash  VARCHAR(255)        NOT NULL,
    fecha_creacion TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- N:M usuarios ↔ alergias
-- Tabla de unión pura: sin id propio, PK compuesta
CREATE TABLE usuario_alergias (
    usuario_id BIGINT NOT NULL,
    alergia_id BIGINT NOT NULL,
    PRIMARY KEY (usuario_id, alergia_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (alergia_id) REFERENCES alergias(id)  ON DELETE CASCADE
);

CREATE TABLE recetas (
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT,
    -- CORRECCIÓN: toda receta debe tener un autor
    usuario_id            BIGINT,
    titulo                VARCHAR(150)                                     NOT NULL,
    descripcion           TEXT,
    preparacion           TEXT,
    -- Separar tiempos permite filtros más precisos ("lista en <10 min")
    tiempo_preparacion_min INT UNSIGNED,
    tiempo_coccion_min     INT UNSIGNED,
    porciones              TINYINT UNSIGNED,
    dificultad             ENUM('fácil', 'media', 'difícil'),
    imagen_url             VARCHAR(500),
    fecha_creacion         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- Catálogo de ingredientes independiente (permite filtrar recetas por ingrediente)
CREATE TABLE ingredientes (
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL UNIQUE
);

-- Relación ingrediente ↔ receta con cantidad y unidad
-- Tabla de unión pura: PK compuesta, sin id propio
CREATE TABLE ingredientes_receta (
    receta_id      BIGINT         NOT NULL,
    ingrediente_id BIGINT         NOT NULL,
    cantidad       DECIMAL(10, 2),
    unidad         VARCHAR(50),
    PRIMARY KEY (receta_id, ingrediente_id),
    FOREIGN KEY (receta_id)      REFERENCES recetas(id)      ON DELETE CASCADE,
    FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id) ON DELETE CASCADE
);

CREATE TABLE categorias (
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

-- Tabla de unión pura: PK compuesta, sin id propio
CREATE TABLE receta_categorias (
    receta_id    BIGINT NOT NULL,
    categoria_id BIGINT NOT NULL,
    PRIMARY KEY (receta_id, categoria_id),
    FOREIGN KEY (receta_id)    REFERENCES recetas(id)    ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
);

CREATE TABLE favoritos (
    usuario_id    BIGINT    NOT NULL,
    receta_id     BIGINT    NOT NULL,
    fecha_agregado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (usuario_id, receta_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (receta_id)  REFERENCES recetas(id)  ON DELETE CASCADE
);

CREATE TABLE valoraciones (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id     BIGINT            NOT NULL,
    receta_id      BIGINT            NOT NULL,
    puntuacion     TINYINT UNSIGNED  NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
    comentario     TEXT,
    fecha_creacion TIMESTAMP         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, receta_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (receta_id)  REFERENCES recetas(id)  ON DELETE CASCADE
);

CREATE TABLE listas (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id     BIGINT        NOT NULL,
    nombre         VARCHAR(100)  NOT NULL,
    imagen_url     VARCHAR(500),
    fecha_creacion TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, nombre),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla de unión pura: PK compuesta, sin id propio
CREATE TABLE lista_recetas (
    lista_id       BIGINT    NOT NULL,
    receta_id      BIGINT    NOT NULL,
    fecha_agregado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (lista_id, receta_id),
    FOREIGN KEY (lista_id)   REFERENCES listas(id)  ON DELETE CASCADE,
    FOREIGN KEY (receta_id)  REFERENCES recetas(id) ON DELETE CASCADE
);

CREATE TABLE recuperacion_password (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id       BIGINT       NOT NULL,
    token            VARCHAR(255) NOT NULL UNIQUE,
    fecha_creacion   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion DATETIME     NOT NULL,
    usado            BOOLEAN      NOT NULL DEFAULT FALSE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- ==========================================
-- ÍNDICES EXPLÍCITOS
-- Las FK no crean índices automáticamente en todas las configuraciones.
-- Son críticos para el rendimiento en JOINs y filtros frecuentes.
-- ==========================================

CREATE INDEX idx_recetas_usuario      ON recetas(usuario_id);
CREATE INDEX idx_recetas_dificultad   ON recetas(dificultad);
CREATE INDEX idx_ingredientes_receta  ON ingredientes_receta(receta_id);
CREATE INDEX idx_ingredientes_ing     ON ingredientes_receta(ingrediente_id);
CREATE INDEX idx_favoritos_usuario    ON favoritos(usuario_id);
CREATE INDEX idx_favoritos_receta     ON favoritos(receta_id);
CREATE INDEX idx_lista_recetas_lista  ON lista_recetas(lista_id);
CREATE INDEX idx_lista_recetas_receta ON lista_recetas(receta_id);
CREATE INDEX idx_valoraciones_receta  ON valoraciones(receta_id);
CREATE INDEX idx_recuperacion_token   ON recuperacion_password(token);
-- Índice parcial para limpiar tokens expirados/usados de forma eficiente
CREATE INDEX idx_recuperacion_limpieza ON recuperacion_password(fecha_expiracion, usado);

-- ==========================================
-- DATOS DE PRUEBA (MOCKS)
-- IMPORTANTE: Las contraseñas deben ser hashes bcrypt.
-- Los valores de ejemplo son hashes reales de '123456' y 'hasheado123'.
-- Nunca insertes contraseñas en texto plano, ni siquiera en desarrollo.
-- ==========================================

-- 1. Alergias
INSERT IGNORE INTO alergias (nombre) VALUES
    ('Ninguna'),
    ('Lactosa'),
    ('Gluten'),
    ('Frutos secos'),
    ('Marisco');

-- 2. Usuarios (con password_hash, no texto plano)
INSERT INTO usuarios (nombre, apellidos, correo, password_hash) VALUES
    ('Borja',  'García',   'test@app.com',       '$2b$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW'),
    ('Ana',    'López',    'ana.lopez@app.com',  '$2b$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW'),
    ('Carlos', 'Martínez', 'carlos.m@app.com',   '$2b$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW'),
    ('Laura',  'Gómez',    'laura.g@app.com',    '$2b$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW');

-- Variables de usuario
SET @id_borja  = (SELECT id FROM usuarios WHERE correo = 'test@app.com'       LIMIT 1);
SET @id_ana    = (SELECT id FROM usuarios WHERE correo = 'ana.lopez@app.com'  LIMIT 1);
SET @id_carlos = (SELECT id FROM usuarios WHERE correo = 'carlos.m@app.com'   LIMIT 1);
SET @id_laura  = (SELECT id FROM usuarios WHERE correo = 'laura.g@app.com'    LIMIT 1);

-- Variables de alergia
SET @al_ninguna = (SELECT id FROM alergias WHERE nombre = 'Ninguna'      LIMIT 1);
SET @al_lactosa = (SELECT id FROM alergias WHERE nombre = 'Lactosa'      LIMIT 1);
SET @al_gluten  = (SELECT id FROM alergias WHERE nombre = 'Gluten'       LIMIT 1);

-- 3. Asignación de alergias
INSERT INTO usuario_alergias (usuario_id, alergia_id) VALUES
    (@id_borja,  @al_ninguna),
    (@id_ana,    @al_lactosa),
    (@id_carlos, @al_ninguna),
    (@id_laura,  @al_gluten);

-- 4. Categorías
INSERT IGNORE INTO categorias (nombre) VALUES
    ('Vegetariano'),
    ('Vegano'),
    ('Desayunos'),
    ('Comidas'),
    ('Cenas'),
    ('Postres'),
    ('Sin Gluten'),
    ('Rápido');

-- 5. Recetas (con usuario_id, porciones, dificultad y tiempos separados)
INSERT INTO recetas (usuario_id, titulo, descripcion, preparacion, tiempo_preparacion_min, tiempo_coccion_min, porciones, dificultad, imagen_url) VALUES
    (@id_borja,  'Pizza Margarita',    'Clásica pizza italiana con albahaca.',
     '1. Amasar. 2. Poner tomate y queso. 3. Hornear a 220° 15 min.',
     10, 15, 2, 'fácil',   'https://images.unsplash.com/photo-1574071318508-1cdbab80d002'),

    (@id_borja,  'Ensalada César',     'Fresca ensalada con pollo y salsa César.',
     '1. Cortar lechuga. 2. Hacer pollo a la plancha. 3. Mezclar con salsa.',
     15, 0,  2, 'fácil',   'https://images.unsplash.com/photo-1550304943-4f24f54ddde9'),

    (@id_ana,    'Tortitas de Avena',  'Desayuno saludable y rápido, ideal para empezar el día.',
     '1. Triturar avena. 2. Mezclar con huevo y plátano. 3. Hacer a la plancha.',
     10, 5,  2, 'fácil',   'https://images.unsplash.com/photo-1528207776546-365bb710ee93'),

    (@id_ana,    'Curry de Garbanzos', 'Plato vegano lleno de sabor y especias orientales.',
     '1. Sofreír cebolla. 2. Añadir especias y tomate. 3. Cocer garbanzos y leche de coco 10 min.',
     10, 15, 4, 'fácil',   'https://images.unsplash.com/photo-1585937421612-70a008356fbe'),

    (@id_carlos, 'Brownie de Chocolate','Postre clásico, denso y jugoso por dentro.',
     '1. Fundir chocolate y mantequilla. 2. Mezclar huevos y azúcar. 3. Hornear a 180° 20 min.',
     15, 20, 8, 'media',   'https://images.unsplash.com/photo-1606313564200-e75d5e30476c'),

    (@id_carlos, 'Tacos al Pastor',    'Auténtico sabor de la calle mexicana en casa.',
     '1. Macerar pollo con achiote. 2. Freír. 3. Servir en tortillas con piña y cilantro.',
     20, 10, 4, 'media',   'https://images.unsplash.com/photo-1565299585323-38d6b0865bfc'),

    (@id_laura,  'Batido Verde',       'Smoothie detox con espinacas y manzana.',
     '1. Lavar ingredientes. 2. Triturar todo con hielo en la batidora.',
     5,  0,  1, 'fácil',   'https://images.unsplash.com/photo-1610832958506-aa56368176cf');

-- Variables de receta
SET @id_pizza    = (SELECT id FROM recetas WHERE titulo = 'Pizza Margarita'    LIMIT 1);
SET @id_ensalada = (SELECT id FROM recetas WHERE titulo = 'Ensalada César'     LIMIT 1);
SET @id_tortitas = (SELECT id FROM recetas WHERE titulo = 'Tortitas de Avena'  LIMIT 1);
SET @id_curry    = (SELECT id FROM recetas WHERE titulo = 'Curry de Garbanzos' LIMIT 1);
SET @id_brownie  = (SELECT id FROM recetas WHERE titulo = 'Brownie de Chocolate' LIMIT 1);
SET @id_tacos    = (SELECT id FROM recetas WHERE titulo = 'Tacos al Pastor'    LIMIT 1);
SET @id_batido   = (SELECT id FROM recetas WHERE titulo = 'Batido Verde'       LIMIT 1);

-- 6. Catálogo de ingredientes
INSERT IGNORE INTO ingredientes (nombre) VALUES
    ('Masa para pizza'), ('Salsa de tomate'), ('Queso mozzarella'),
    ('Lechuga romana'), ('Pechuga de pollo'), ('Salsa César'),
    ('Avena'), ('Huevo'), ('Plátano maduro'), ('Leche o bebida vegetal'),
    ('Garbanzos cocidos'), ('Espinacas frescas'), ('Leche de coco'), ('Curry en polvo'),
    ('Chocolate negro 70%'), ('Mantequilla'), ('Huevos'), ('Azúcar'), ('Harina'),
    ('Pollo'), ('Tortillas de maíz'), ('Piña en trozos'),
    ('Espinacas'), ('Manzana verde');

-- 7. Relación ingredientes ↔ recetas
-- Pizza Margarita
SET @ing_masa        = (SELECT id FROM ingredientes WHERE nombre = 'Masa para pizza'   LIMIT 1);
SET @ing_tomate      = (SELECT id FROM ingredientes WHERE nombre = 'Salsa de tomate'   LIMIT 1);
SET @ing_mozzarella  = (SELECT id FROM ingredientes WHERE nombre = 'Queso mozzarella'  LIMIT 1);
INSERT INTO ingredientes_receta (receta_id, ingrediente_id, cantidad, unidad) VALUES
    (@id_pizza, @ing_masa,       1,   'unidad'),
    (@id_pizza, @ing_tomate,     150, 'ml'),
    (@id_pizza, @ing_mozzarella, 200, 'g');

-- Ensalada César
SET @ing_lechuga = (SELECT id FROM ingredientes WHERE nombre = 'Lechuga romana'   LIMIT 1);
SET @ing_pollo   = (SELECT id FROM ingredientes WHERE nombre = 'Pechuga de pollo' LIMIT 1);
SET @ing_cesar   = (SELECT id FROM ingredientes WHERE nombre = 'Salsa César'      LIMIT 1);
INSERT INTO ingredientes_receta (receta_id, ingrediente_id, cantidad, unidad) VALUES
    (@id_ensalada, @ing_lechuga, 1,  'unidad'),
    (@id_ensalada, @ing_pollo,   200,'g'),
    (@id_ensalada, @ing_cesar,   50, 'ml');

-- Tortitas de Avena
SET @ing_avena   = (SELECT id FROM ingredientes WHERE nombre = 'Avena'                LIMIT 1);
SET @ing_huevo   = (SELECT id FROM ingredientes WHERE nombre = 'Huevo'                LIMIT 1);
SET @ing_platano = (SELECT id FROM ingredientes WHERE nombre = 'Plátano maduro'       LIMIT 1);
SET @ing_leche   = (SELECT id FROM ingredientes WHERE nombre = 'Leche o bebida vegetal' LIMIT 1);
INSERT INTO ingredientes_receta (receta_id, ingrediente_id, cantidad, unidad) VALUES
    (@id_tortitas, @ing_avena,   60, 'g'),
    (@id_tortitas, @ing_huevo,   1,  'unidad'),
    (@id_tortitas, @ing_platano, 1,  'unidad'),
    (@id_tortitas, @ing_leche,   50, 'ml');

-- Curry de Garbanzos
SET @ing_garbanzos = (SELECT id FROM ingredientes WHERE nombre = 'Garbanzos cocidos' LIMIT 1);
SET @ing_espinacas = (SELECT id FROM ingredientes WHERE nombre = 'Espinacas frescas' LIMIT 1);
SET @ing_lcococo   = (SELECT id FROM ingredientes WHERE nombre = 'Leche de coco'     LIMIT 1);
SET @ing_curry     = (SELECT id FROM ingredientes WHERE nombre = 'Curry en polvo'    LIMIT 1);
INSERT INTO ingredientes_receta (receta_id, ingrediente_id, cantidad, unidad) VALUES
    (@id_curry, @ing_garbanzos, 400, 'g'),
    (@id_curry, @ing_espinacas, 150, 'g'),
    (@id_curry, @ing_lcococo,   250, 'ml'),
    (@id_curry, @ing_curry,     2,   'cucharadas');

-- Brownie de Chocolate
SET @ing_choco  = (SELECT id FROM ingredientes WHERE nombre = 'Chocolate negro 70%' LIMIT 1);
SET @ing_mant   = (SELECT id FROM ingredientes WHERE nombre = 'Mantequilla'         LIMIT 1);
SET @ing_huevos = (SELECT id FROM ingredientes WHERE nombre = 'Huevos'              LIMIT 1);
SET @ing_azucar = (SELECT id FROM ingredientes WHERE nombre = 'Azúcar'              LIMIT 1);
SET @ing_harina = (SELECT id FROM ingredientes WHERE nombre = 'Harina'              LIMIT 1);
INSERT INTO ingredientes_receta (receta_id, ingrediente_id, cantidad, unidad) VALUES
    (@id_brownie, @ing_choco,  200, 'g'),
    (@id_brownie, @ing_mant,   100, 'g'),
    (@id_brownie, @ing_huevos, 3,   'unidades'),
    (@id_brownie, @ing_azucar, 150, 'g'),
    (@id_brownie, @ing_harina, 80,  'g');

-- Tacos al Pastor
SET @ing_pollo_t  = (SELECT id FROM ingredientes WHERE nombre = 'Pollo'            LIMIT 1);
SET @ing_tortilla = (SELECT id FROM ingredientes WHERE nombre = 'Tortillas de maíz' LIMIT 1);
SET @ing_pina     = (SELECT id FROM ingredientes WHERE nombre = 'Piña en trozos'   LIMIT 1);
INSERT INTO ingredientes_receta (receta_id, ingrediente_id, cantidad, unidad) VALUES
    (@id_tacos, @ing_pollo_t,  500, 'g'),
    (@id_tacos, @ing_tortilla, 8,   'unidades'),
    (@id_tacos, @ing_pina,     100, 'g');

-- Batido Verde
SET @ing_espinacas_b = (SELECT id FROM ingredientes WHERE nombre = 'Espinacas'   LIMIT 1);
SET @ing_manzana     = (SELECT id FROM ingredientes WHERE nombre = 'Manzana verde' LIMIT 1);
INSERT INTO ingredientes_receta (receta_id, ingrediente_id, cantidad, unidad) VALUES
    (@id_batido, @ing_espinacas_b, 1, 'taza'),
    (@id_batido, @ing_manzana,     1, 'unidad');

-- 8. Receta ↔ Categorías
-- Usamos IDs directos para mayor claridad (Vegetariano=1, Vegano=2, Desayunos=3,
-- Comidas=4, Cenas=5, Postres=6, Sin Gluten=7, Rápido=8)
SET @cat_veg  = (SELECT id FROM categorias WHERE nombre = 'Vegetariano' LIMIT 1);
SET @cat_vgn  = (SELECT id FROM categorias WHERE nombre = 'Vegano'      LIMIT 1);
SET @cat_des  = (SELECT id FROM categorias WHERE nombre = 'Desayunos'   LIMIT 1);
SET @cat_com  = (SELECT id FROM categorias WHERE nombre = 'Comidas'     LIMIT 1);
SET @cat_cen  = (SELECT id FROM categorias WHERE nombre = 'Cenas'       LIMIT 1);
SET @cat_pos  = (SELECT id FROM categorias WHERE nombre = 'Postres'     LIMIT 1);
SET @cat_sgl  = (SELECT id FROM categorias WHERE nombre = 'Sin Gluten'  LIMIT 1);
SET @cat_rap  = (SELECT id FROM categorias WHERE nombre = 'Rápido'      LIMIT 1);

INSERT INTO receta_categorias (receta_id, categoria_id) VALUES
    (@id_pizza,    @cat_cen), (@id_pizza,    @cat_veg),
    (@id_ensalada, @cat_com),
    (@id_tortitas, @cat_des), (@id_tortitas, @cat_veg), (@id_tortitas, @cat_rap),
    (@id_curry,    @cat_vgn), (@id_curry,    @cat_com), (@id_curry,    @cat_sgl),
    (@id_brownie,  @cat_pos), (@id_brownie,  @cat_veg),
    (@id_tacos,    @cat_com), (@id_tacos,    @cat_cen), (@id_tacos,    @cat_sgl),
    (@id_batido,   @cat_des), (@id_batido,   @cat_vgn), (@id_batido,   @cat_rap), (@id_batido, @cat_sgl);

-- 9. Favoritos
INSERT INTO favoritos (usuario_id, receta_id) VALUES
    (@id_borja,  @id_pizza),
    (@id_ana,    @id_curry),
    (@id_carlos, @id_tacos);

-- 10. Listas de Borja
INSERT INTO listas (usuario_id, nombre) VALUES
    (@id_borja, 'Desayuno'),
    (@id_borja, 'Almuerzo'),
    (@id_borja, 'Comida'),
    (@id_borja, 'Merienda'),
    (@id_borja, 'Cena');

SET @lista_cena_borja = (SELECT id FROM listas WHERE nombre = 'Cena' AND usuario_id = @id_borja LIMIT 1);
INSERT INTO lista_recetas (lista_id, receta_id) VALUES
    (@lista_cena_borja, @id_pizza),
    (@lista_cena_borja, @id_tacos);

-- Listas de Ana y Carlos
INSERT INTO listas (usuario_id, nombre) VALUES
    (@id_ana,    'Detox / Saludable'),
    (@id_carlos, 'Cheat Meals');

SET @lista_detox  = (SELECT id FROM listas WHERE nombre = 'Detox / Saludable' AND usuario_id = @id_ana    LIMIT 1);
SET @lista_cheat  = (SELECT id FROM listas WHERE nombre = 'Cheat Meals'        AND usuario_id = @id_carlos LIMIT 1);

INSERT INTO lista_recetas (lista_id, receta_id) VALUES
    (@lista_detox, @id_batido),
    (@lista_detox, @id_curry),
    (@lista_cheat, @id_brownie),
    (@lista_cheat, @id_pizza);

-- 11. Valoraciones de prueba
INSERT INTO valoraciones (usuario_id, receta_id, puntuacion, comentario) VALUES
    (@id_ana,    @id_pizza,   4, 'Muy buena, la hice con masa casera y quedó genial.'),
    (@id_borja,  @id_curry,   5, 'Me encantó, le añadí un poco más de curry.'),
    (@id_laura,  @id_batido,  5, 'Perfecto para después del gym.'),
    (@id_carlos, @id_brownie, 5, 'El mejor brownie que he probado.');

-- ==========================================
-- EVENTO: limpieza automática de tokens caducados
-- Requiere que el event_scheduler esté activo:
--   SET GLOBAL event_scheduler = ON;
-- ==========================================

DROP EVENT IF EXISTS limpiar_tokens_expirados;

CREATE EVENT limpiar_tokens_expirados
    ON SCHEDULE EVERY 1 DAY
    STARTS CURRENT_TIMESTAMP
    DO
    DELETE FROM recuperacion_password
    WHERE usado = TRUE
       OR fecha_expiracion < NOW();