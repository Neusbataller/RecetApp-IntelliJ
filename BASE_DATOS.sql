-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:        9.6.0 - Homebrew
-- SO del servidor:              macos26.3
-- HeidiSQL Versión:            12.16.1.1
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Volcando estructura de base de datos para recetas_db
CREATE DATABASE IF NOT EXISTS `recetas_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `recetas_db`;

-- Volcando estructura para tabla recetas_db.alergias
CREATE TABLE IF NOT EXISTS `alergias` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.alergias: ~6 rows (aproximadamente)
INSERT INTO `alergias` (`id`, `nombre`) VALUES
	(1, 'Ninguna'),
	(2, 'Lactosa'),
	(3, 'Gluten'),
	(4, 'Frutos secos'),
	(5, 'Marisco'),
	(7, 'Lacteos');

-- Volcando estructura para tabla recetas_db.categorias
CREATE TABLE IF NOT EXISTS `categorias` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.categorias: ~8 rows (aproximadamente)
INSERT INTO `categorias` (`id`, `nombre`) VALUES
	(1, 'Vegetariano'),
	(2, 'Vegano'),
	(3, 'Desayunos'),
	(4, 'Comidas'),
	(5, 'Cenas'),
	(6, 'Postres'),
	(7, 'Sin Gluten'),
	(8, 'Rápido');

-- Volcando estructura para tabla recetas_db.favoritos
CREATE TABLE IF NOT EXISTS `favoritos` (
  `usuario_id` bigint NOT NULL,
  `receta_id` bigint NOT NULL,
  `fecha_agregado` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`usuario_id`,`receta_id`),
  KEY `idx_favoritos_usuario` (`usuario_id`),
  KEY `idx_favoritos_receta` (`receta_id`),
  CONSTRAINT `favoritos_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `favoritos_ibfk_2` FOREIGN KEY (`receta_id`) REFERENCES `recetas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.favoritos: ~3 rows (aproximadamente)
INSERT INTO `favoritos` (`usuario_id`, `receta_id`, `fecha_agregado`) VALUES
	(1, 1, '2026-04-08 07:52:36'),
	(2, 4, '2026-04-08 07:52:36'),
	(3, 6, '2026-04-08 07:52:36');

-- Volcando estructura para tabla recetas_db.ingredientes
CREATE TABLE IF NOT EXISTS `ingredientes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.ingredientes: ~24 rows (aproximadamente)
INSERT INTO `ingredientes` (`id`, `nombre`) VALUES
	(1, 'Masa para pizza'),
	(2, 'Salsa de tomate'),
	(3, 'Queso mozzarella'),
	(4, 'Lechuga romana'),
	(5, 'Pechuga de pollo'),
	(6, 'Salsa César'),
	(7, 'Avena'),
	(8, 'Huevo'),
	(9, 'Plátano maduro'),
	(10, 'Leche o bebida vegetal'),
	(11, 'Garbanzos cocidos'),
	(12, 'Espinacas frescas'),
	(13, 'Leche de coco'),
	(14, 'Curry en polvo'),
	(15, 'Chocolate negro 70%'),
	(16, 'Mantequilla'),
	(17, 'Huevos'),
	(18, 'Azúcar'),
	(19, 'Harina'),
	(20, 'Pollo'),
	(21, 'Tortillas de maíz'),
	(22, 'Piña en trozos'),
	(23, 'Espinacas'),
	(24, 'Manzana verde');

-- Volcando estructura para tabla recetas_db.ingredientes_receta
CREATE TABLE IF NOT EXISTS `ingredientes_receta` (
  `receta_id` bigint NOT NULL,
  `ingrediente_id` bigint NOT NULL,
  `cantidad` decimal(10,2) DEFAULT NULL,
  `unidad` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`receta_id`,`ingrediente_id`),
  KEY `idx_ingredientes_receta` (`receta_id`),
  KEY `idx_ingredientes_ing` (`ingrediente_id`),
  CONSTRAINT `ingredientes_receta_ibfk_1` FOREIGN KEY (`receta_id`) REFERENCES `recetas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ingredientes_receta_ibfk_2` FOREIGN KEY (`ingrediente_id`) REFERENCES `ingredientes` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.ingredientes_receta: ~24 rows (aproximadamente)
INSERT INTO `ingredientes_receta` (`receta_id`, `ingrediente_id`, `cantidad`, `unidad`) VALUES
	(1, 1, 1.00, 'unidad'),
	(1, 2, 150.00, 'ml'),
	(1, 3, 200.00, 'g'),
	(2, 4, 1.00, 'unidad'),
	(2, 5, 200.00, 'g'),
	(2, 6, 50.00, 'ml'),
	(3, 7, 60.00, 'g'),
	(3, 8, 1.00, 'unidad'),
	(3, 9, 1.00, 'unidad'),
	(3, 10, 50.00, 'ml'),
	(4, 11, 400.00, 'g'),
	(4, 12, 150.00, 'g'),
	(4, 13, 250.00, 'ml'),
	(4, 14, 2.00, 'cucharadas'),
	(5, 15, 200.00, 'g'),
	(5, 16, 100.00, 'g'),
	(5, 17, 3.00, 'unidades'),
	(5, 18, 150.00, 'g'),
	(5, 19, 80.00, 'g'),
	(6, 20, 500.00, 'g'),
	(6, 21, 8.00, 'unidades'),
	(6, 22, 100.00, 'g'),
	(7, 23, 1.00, 'taza'),
	(7, 24, 1.00, 'unidad');

-- Volcando estructura para evento recetas_db.limpiar_tokens_expirados
DELIMITER //
CREATE EVENT `limpiar_tokens_expirados` ON SCHEDULE EVERY 1 DAY STARTS '2026-04-08 09:52:36' ON COMPLETION NOT PRESERVE ENABLE DO DELETE FROM recuperacion_password
    WHERE usado = TRUE
       OR fecha_expiracion < NOW()//
DELIMITER ;

-- Volcando estructura para tabla recetas_db.listas
CREATE TABLE IF NOT EXISTS `listas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `imagen_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_id` (`usuario_id`,`nombre`),
  CONSTRAINT `listas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.listas: ~7 rows (aproximadamente)
INSERT INTO `listas` (`id`, `usuario_id`, `nombre`, `imagen_url`, `fecha_creacion`) VALUES
	(1, 1, 'Desayuno', NULL, '2026-04-08 07:52:36'),
	(2, 1, 'Almuerzo', NULL, '2026-04-08 07:52:36'),
	(3, 1, 'Comida', NULL, '2026-04-08 07:52:36'),
	(4, 1, 'Merienda', NULL, '2026-04-08 07:52:36'),
	(5, 1, 'Cena', NULL, '2026-04-08 07:52:36'),
	(6, 2, 'Detox / Saludable', NULL, '2026-04-08 07:52:36'),
	(7, 3, 'Cheat Meals', NULL, '2026-04-08 07:52:36');

-- Volcando estructura para tabla recetas_db.lista_recetas
CREATE TABLE IF NOT EXISTS `lista_recetas` (
  `lista_id` bigint NOT NULL,
  `receta_id` bigint NOT NULL,
  `fecha_agregado` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`lista_id`,`receta_id`),
  KEY `idx_lista_recetas_lista` (`lista_id`),
  KEY `idx_lista_recetas_receta` (`receta_id`),
  CONSTRAINT `lista_recetas_ibfk_1` FOREIGN KEY (`lista_id`) REFERENCES `listas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `lista_recetas_ibfk_2` FOREIGN KEY (`receta_id`) REFERENCES `recetas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.lista_recetas: ~6 rows (aproximadamente)
INSERT INTO `lista_recetas` (`lista_id`, `receta_id`, `fecha_agregado`) VALUES
	(5, 1, '2026-04-08 07:52:36'),
	(5, 6, '2026-04-08 07:52:36'),
	(6, 4, '2026-04-08 07:52:36'),
	(6, 7, '2026-04-08 07:52:36'),
	(7, 1, '2026-04-08 07:52:36'),
	(7, 5, '2026-04-08 07:52:36');

-- Volcando estructura para tabla recetas_db.recetas
CREATE TABLE IF NOT EXISTS `recetas` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint DEFAULT NULL,
  `titulo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `preparacion` text COLLATE utf8mb4_unicode_ci,
  `tiempo_preparacion_min` int unsigned DEFAULT NULL,
  `tiempo_coccion_min` int unsigned DEFAULT NULL,
  `porciones` int DEFAULT NULL,
  `dificultad` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `imagen_url` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_recetas_usuario` (`usuario_id`),
  KEY `idx_recetas_dificultad` (`dificultad`),
  CONSTRAINT `recetas_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.recetas: ~7 rows (aproximadamente)
INSERT INTO `recetas` (`id`, `usuario_id`, `titulo`, `descripcion`, `preparacion`, `tiempo_preparacion_min`, `tiempo_coccion_min`, `porciones`, `dificultad`, `imagen_url`, `fecha_creacion`) VALUES
	(1, 1, 'Pizza Margarita', 'Clásica pizza italiana con albahaca.', '1. Amasar. 2. Poner tomate y queso. 3. Hornear a 220° 15 min.', 10, 15, 2, 'fácil', 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002', '2026-04-08 07:52:36'),
	(2, 1, 'Ensalada César', 'Fresca ensalada con pollo y salsa César.', '1. Cortar lechuga. 2. Hacer pollo a la plancha. 3. Mezclar con salsa.', 15, 0, 2, 'fácil', 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9', '2026-04-08 07:52:36'),
	(3, 2, 'Tortitas de Avena', 'Desayuno saludable y rápido, ideal para empezar el día.', '1. Triturar avena. 2. Mezclar con huevo y plátano. 3. Hacer a la plancha.', 10, 5, 2, 'fácil', 'https://images.unsplash.com/photo-1528207776546-365bb710ee93', '2026-04-08 07:52:36'),
	(4, 2, 'Curry de Garbanzos', 'Plato vegano lleno de sabor y especias orientales.', '1. Sofreír cebolla. 2. Añadir especias y tomate. 3. Cocer garbanzos y leche de coco 10 min.', 10, 15, 4, 'fácil', 'https://images.unsplash.com/photo-1585937421612-70a008356fbe', '2026-04-08 07:52:36'),
	(5, 3, 'Brownie de Chocolate', 'Postre clásico, denso y jugoso por dentro.', '1. Fundir chocolate y mantequilla. 2. Mezclar huevos y azúcar. 3. Hornear a 180° 20 min.', 15, 20, 8, 'media', 'https://images.unsplash.com/photo-1606313564200-e75d5e30476c', '2026-04-08 07:52:36'),
	(6, 3, 'Tacos al Pastor', 'Auténtico sabor de la calle mexicana en casa.', '1. Macerar pollo con achiote. 2. Freír. 3. Servir en tortillas con piña y cilantro.', 20, 10, 4, 'media', 'https://images.unsplash.com/photo-1565299585323-38d6b0865bfc', '2026-04-08 07:52:36'),
	(7, 4, 'Batido Verde', 'Smoothie detox con espinacas y manzana.', '1. Lavar ingredientes. 2. Triturar todo con hielo en la batidora.', 5, 0, 1, 'fácil', 'https://images.unsplash.com/photo-1610832958506-aa56368176cf', '2026-04-08 07:52:36');

-- Volcando estructura para tabla recetas_db.receta_categorias
CREATE TABLE IF NOT EXISTS `receta_categorias` (
  `receta_id` bigint NOT NULL,
  `categoria_id` bigint NOT NULL,
  PRIMARY KEY (`receta_id`,`categoria_id`),
  KEY `categoria_id` (`categoria_id`),
  CONSTRAINT `receta_categorias_ibfk_1` FOREIGN KEY (`receta_id`) REFERENCES `recetas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `receta_categorias_ibfk_2` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.receta_categorias: ~18 rows (aproximadamente)
INSERT INTO `receta_categorias` (`receta_id`, `categoria_id`) VALUES
	(1, 1),
	(1, 5),
	(2, 4),
	(3, 1),
	(3, 3),
	(3, 8),
	(4, 2),
	(4, 4),
	(4, 7),
	(5, 1),
	(5, 6),
	(6, 4),
	(6, 5),
	(6, 7),
	(7, 2),
	(7, 3),
	(7, 7),
	(7, 8);

-- Volcando estructura para tabla recetas_db.recuperacion_password
CREATE TABLE IF NOT EXISTS `recuperacion_password` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_expiracion` datetime NOT NULL,
  `usado` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `token` (`token`),
  KEY `usuario_id` (`usuario_id`),
  KEY `idx_recuperacion_token` (`token`),
  KEY `idx_recuperacion_limpieza` (`fecha_expiracion`,`usado`),
  CONSTRAINT `recuperacion_password_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.recuperacion_password: ~0 rows (aproximadamente)

-- Volcando estructura para tabla recetas_db.usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellidos` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `correo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.usuarios: ~4 rows (aproximadamente)
INSERT INTO `usuarios` (`id`, `nombre`, `apellidos`, `correo`, `password`, `fecha_creacion`) VALUES
	(1, 'Borja', 'García', 'test@app.com', '$2b$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '2026-04-08 07:52:36'),
	(2, 'Ana', 'López', 'ana.lopez@app.com', '$2b$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '2026-04-08 07:52:36'),
	(3, 'Carlos', 'Martínez', 'carlos.m@app.com', '$2b$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '2026-04-08 07:52:36'),
	(4, 'Laura', 'Gómez', 'laura.g@app.com', '$2b$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '2026-04-08 07:52:36'),
	(5, 'Juan', 'Perez', 'juan@example.com', '$2a$10$jWsU5ebb.m3AI6zwT3nXXeW3A0tNRCFXKdMbtS7eTi2pXQ4VhULPq', '2026-04-08 07:57:39');

-- Volcando estructura para tabla recetas_db.usuario_alergias
CREATE TABLE IF NOT EXISTS `usuario_alergias` (
  `usuario_id` bigint NOT NULL,
  `alergia_id` bigint NOT NULL,
  PRIMARY KEY (`usuario_id`,`alergia_id`),
  KEY `alergia_id` (`alergia_id`),
  CONSTRAINT `usuario_alergias_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `usuario_alergias_ibfk_2` FOREIGN KEY (`alergia_id`) REFERENCES `alergias` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.usuario_alergias: ~4 rows (aproximadamente)
INSERT INTO `usuario_alergias` (`usuario_id`, `alergia_id`) VALUES
	(1, 1),
	(2, 2),
	(3, 1),
	(4, 3),
	(5, 3),
	(5, 7);

-- Volcando estructura para tabla recetas_db.valoraciones
CREATE TABLE IF NOT EXISTS `valoraciones` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `usuario_id` bigint NOT NULL,
  `receta_id` bigint NOT NULL,
  `puntuacion` tinyint unsigned NOT NULL,
  `comentario` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `usuario_id` (`usuario_id`,`receta_id`),
  KEY `idx_valoraciones_receta` (`receta_id`),
  CONSTRAINT `valoraciones_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `valoraciones_ibfk_2` FOREIGN KEY (`receta_id`) REFERENCES `recetas` (`id`) ON DELETE CASCADE,
  CONSTRAINT `valoraciones_chk_1` CHECK ((`puntuacion` between 1 and 5))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Volcando datos para la tabla recetas_db.valoraciones: ~4 rows (aproximadamente)
INSERT INTO `valoraciones` (`id`, `usuario_id`, `receta_id`, `puntuacion`, `comentario`, `fecha_creacion`) VALUES
	(1, 2, 1, 4, 'Muy buena, la hice con masa casera y quedó genial.', '2026-04-08 07:52:36'),
	(2, 1, 4, 5, 'Me encantó, le añadí un poco más de curry.', '2026-04-08 07:52:36'),
	(3, 4, 7, 5, 'Perfecto para después del gym.', '2026-04-08 07:52:36'),
	(4, 3, 5, 5, 'El mejor brownie que he probado.', '2026-04-08 07:52:36');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
recetas_db