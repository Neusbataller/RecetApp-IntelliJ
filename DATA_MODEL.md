# Modelo de Datos — `recetas_db`

Este documento describe el esquema de base de datos del sistema de recetas. Incluye la descripción de cada tabla, sus campos y las relaciones entre ellas.

---

## Descripción general

La base de datos `recetas_db` da soporte a una aplicación de gestión y descubrimiento de recetas de cocina. Los usuarios pueden registrarse, publicar recetas, organizarlas en listas personales, marcarlas como favoritas y valorarlas. Las recetas pueden clasificarse por categorías y contienen ingredientes con cantidades detalladas. Además, el sistema gestiona alergias alimentarias por usuario y dispone de un mecanismo de recuperación de contraseña.

---

## Entidades

### `usuarios`
Usuarios registrados en la aplicación.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT | Identificador interno (PK) |
| `nombre` | VARCHAR(100) | Nombre del usuario |
| `apellidos` | VARCHAR(150) | Apellidos del usuario |
| `correo` | VARCHAR(150) | Email único del usuario |
| `password` | VARCHAR(255) | Contraseña cifrada (bcrypt) |
| `fecha_creacion` | TIMESTAMP | Fecha de registro |

---

### `alergias`
Catálogo de alergias alimentarias disponibles en el sistema.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT | Identificador interno (PK) |
| `nombre` | VARCHAR(150) | Nombre de la alergia (único) |

**Valores actuales:** Ninguna, Lactosa, Gluten, Frutos secos, Marisco, Lácteos.

---

### `usuario_alergias`
Tabla pivote que asocia usuarios con sus alergias alimentarias.

| Campo | Tipo | Descripción |
|---|---|---|
| `usuario_id` | BIGINT | FK → `usuarios` |
| `alergia_id` | BIGINT | FK → `alergias` |

La clave primaria es compuesta: `(usuario_id, alergia_id)`.

---

### `categorias`
Categorías para clasificar las recetas.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT | Identificador interno (PK) |
| `nombre` | VARCHAR(100) | Nombre de la categoría (único) |

**Valores actuales:** Vegetariano, Vegano, Desayunos, Comidas, Cenas, Postres, Sin Gluten, Rápido.

---

### `recetas`
Recetas publicadas por los usuarios.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT | Identificador interno (PK) |
| `usuario_id` | BIGINT | FK → `usuarios` (nullable; SET NULL si el usuario se elimina) |
| `titulo` | VARCHAR(150) | Título de la receta |
| `descripcion` | TEXT | Descripción breve |
| `preparacion` | TEXT | Pasos de preparación |
| `tiempo_preparacion_min` | INT UNSIGNED | Tiempo de preparación en minutos |
| `tiempo_coccion_min` | INT UNSIGNED | Tiempo de cocción en minutos |
| `porciones` | INT | Número de porciones |
| `dificultad` | VARCHAR(20) | Nivel de dificultad (`fácil`, `media`, etc.) |
| `imagen_url` | VARCHAR(500) | URL de la imagen de portada |
| `fecha_creacion` | TIMESTAMP | Fecha de publicación |

---

### `receta_categorias`
Tabla pivote que asocia recetas con sus categorías (relación muchos a muchos).

| Campo | Tipo | Descripción |
|---|---|---|
| `receta_id` | BIGINT | FK → `recetas` |
| `categoria_id` | BIGINT | FK → `categorias` |

La clave primaria es compuesta: `(receta_id, categoria_id)`.

---

### `ingredientes`
Catálogo global de ingredientes disponibles.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT | Identificador interno (PK) |
| `nombre` | VARCHAR(150) | Nombre del ingrediente (único) |

---

### `ingredientes_receta`
Tabla pivote que vincula ingredientes con recetas, incluyendo cantidad y unidad.

| Campo | Tipo | Descripción |
|---|---|---|
| `receta_id` | BIGINT | FK → `recetas` |
| `ingrediente_id` | BIGINT | FK → `ingredientes` |
| `cantidad` | DECIMAL(10,2) | Cantidad necesaria |
| `unidad` | VARCHAR(50) | Unidad de medida (g, ml, unidad, cucharadas, etc.) |

La clave primaria es compuesta: `(receta_id, ingrediente_id)`.

---

### `favoritos`
Recetas marcadas como favoritas por cada usuario.

| Campo | Tipo | Descripción |
|---|---|---|
| `usuario_id` | BIGINT | FK → `usuarios` |
| `receta_id` | BIGINT | FK → `recetas` |
| `fecha_agregado` | TIMESTAMP | Fecha en que se marcó como favorita |

La clave primaria es compuesta: `(usuario_id, receta_id)`.

---

### `listas`
Listas personales creadas por los usuarios para organizar recetas.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT | Identificador interno (PK) |
| `usuario_id` | BIGINT | FK → `usuarios` |
| `nombre` | VARCHAR(100) | Nombre de la lista |
| `imagen_url` | VARCHAR(500) | URL de imagen de la lista (opcional) |
| `fecha_creacion` | TIMESTAMP | Fecha de creación |

Existe una restricción de unicidad sobre `(usuario_id, nombre)`: cada usuario no puede tener dos listas con el mismo nombre.

---

### `lista_recetas`
Tabla pivote que asocia recetas con las listas personales del usuario.

| Campo | Tipo | Descripción |
|---|---|---|
| `lista_id` | BIGINT | FK → `listas` |
| `receta_id` | BIGINT | FK → `recetas` |
| `fecha_agregado` | TIMESTAMP | Fecha en que se añadió la receta a la lista |

La clave primaria es compuesta: `(lista_id, receta_id)`.

---

### `valoraciones`
Puntuaciones y comentarios de los usuarios sobre las recetas.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT | Identificador interno (PK) |
| `usuario_id` | BIGINT | FK → `usuarios` |
| `receta_id` | BIGINT | FK → `recetas` |
| `puntuacion` | TINYINT UNSIGNED | Valoración del 1 al 5 (CHECK constraint) |
| `comentario` | TEXT | Comentario opcional |
| `fecha_creacion` | TIMESTAMP | Fecha de la valoración |

Existe una restricción de unicidad sobre `(usuario_id, receta_id)`: cada usuario solo puede valorar una receta una vez.

---

### `recuperacion_password`
Tokens temporales para el proceso de recuperación de contraseña.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT | Identificador interno (PK) |
| `usuario_id` | BIGINT | FK → `usuarios` |
| `token` | VARCHAR(255) | Token único de recuperación |
| `fecha_creacion` | TIMESTAMP | Fecha de generación del token |
| `fecha_expiracion` | DATETIME | Fecha límite de validez |
| `usado` | TINYINT(1) | Indica si el token ya fue utilizado |

Un evento programado (`limpiar_tokens_expirados`) se ejecuta diariamente para eliminar los tokens ya usados o caducados.

---

## Relaciones

```
usuarios ──< recetas              (un usuario publica muchas recetas)
usuarios ──< usuario_alergias     (un usuario puede tener varias alergias)
usuarios ──< favoritos            (un usuario puede tener muchos favoritos)
usuarios ──< listas               (un usuario puede crear muchas listas)
usuarios ──< valoraciones         (un usuario puede valorar muchas recetas)
usuarios ──< recuperacion_password

alergias ──< usuario_alergias

recetas ──< receta_categorias     (una receta puede pertenecer a varias categorías)
recetas ──< ingredientes_receta   (una receta tiene varios ingredientes)
recetas ──< favoritos
recetas ──< lista_recetas
recetas ──< valoraciones

categorias ──< receta_categorias

ingredientes ──< ingredientes_receta

listas ──< lista_recetas          (una lista puede contener varias recetas)
```

---

## Reglas de negocio

- Un usuario puede valorar una receta como máximo una vez. La puntuación debe estar entre 1 y 5.
- Dos listas del mismo usuario no pueden tener el mismo nombre.
- Si un usuario es eliminado, sus recetas no se borran; el campo `usuario_id` de la receta se establece a `NULL`.
- El resto de datos relacionados con el usuario (favoritos, listas, valoraciones, alergias, tokens) se eliminan en cascada al borrar el usuario.
- Los tokens de recuperación de contraseña expiran automáticamente; un evento diario limpia los tokens usados o caducados.