# Recetas API Documentation

> ⚠️ **Nota importante:**
>
> La API se encuentra en constante desarrollo...

---

## Autenticación

La mayoría de los endpoints requieren autenticación mediante JWT. El token debe enviarse en el header:

```http
Authorization: Bearer <token>
```

Obtén el token usando el endpoint de login.

---

## Endpoints de Usuario

### 1) Registro de usuario
- **POST** `/api/usuarios/register`
- **Body:**
```json
{
  "nombre": "Juan",
  "apellidos": "Pérez",
  "correo": "juan@mail.com",
  "password": "123456",
  "alergias": ["Gluten", "Lácteos"]
}
```
- **Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidos": "Pérez",
  "correo": "juan@mail.com",
  "alergias": ["Gluten", "Lácteos"],
  "favoritos": [],
  "fechaCreacion": "2026-04-07T12:00:00"
}
```

#### Ejemplo React Native
```js
await fetch('http://localhost:8080/api/usuarios/register', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ nombre, apellidos, correo, password, alergias })
});
```

---

### 2) Login (JSON)
- **POST** `/api/usuarios/login`
- **Content-Type:** `application/json`
- **Body:**
```json
{
  "correo": "juan@mail.com",
  "password": "123456"
}
```
- **Respuesta:**
```json
{
  "token": "<jwt>",
  "usuario": {
    "id": 1,
    "nombre": "Juan",
    "apellidos": "Pérez",
    "correo": "juan@mail.com"
  }
}
```

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/usuarios/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ correo, password })
});
const data = await res.json();
```

---

### 3) Login (form-urlencoded)
- **POST** `/api/usuarios/login`
- **Content-Type:** `application/x-www-form-urlencoded`
- **Body:** `correo=juan@mail.com&password=123456`
- **Respuesta:** igual que login JSON

#### Ejemplo React Native
```js
const body = new URLSearchParams({ correo, password }).toString();
const res = await fetch('http://localhost:8080/api/usuarios/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
  body
});
const data = await res.json();
```

---

### 4) Obtener usuario autenticado (perfil completo)
- **GET** `/api/usuarios/me`
- **Headers:** `Authorization: Bearer <token>`
- **Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidos": "Pérez",
  "correo": "juan@mail.com",
  "alergias": ["Gluten", "Lácteos"],
  "favoritos": [
    {
      "recetaId": 3,
      "titulo": "Pasta rápida",
      "imagenUrl": "https://example.com/pasta.jpg",
      "fechaAgregado": "2026-04-08T12:30:00"
    }
  ],
  "fechaCreacion": "2026-04-07T12:00:00"
}
```

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/usuarios/me', {
  headers: { Authorization: `Bearer ${token}` }
});
const me = await res.json();
```

---

### 5) Actualizar perfil del usuario autenticado
- **PUT** `/api/usuarios/me`
- **Headers:** `Authorization`
- **Body:**
```json
{
  "nombre": "Nuevo nombre",
  "apellidos": "Nuevos apellidos",
  "correo": "nuevo@mail.com"
}
```
- **Respuesta:** usuario actualizado

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/usuarios/me', {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify({ nombre, apellidos, correo })
});
const updated = await res.json();
```

---

### 6) Cambiar contraseña del usuario autenticado
- **PATCH** `/api/usuarios/me/password`
- **Headers:** `Authorization`
- **Body:**
```json
{
  "passwordActual": "123456",
  "passwordNueva": "nuevaPassword"
}
```
- **Respuesta:**
```json
"Contraseña actualizada correctamente"
```

#### Ejemplo React Native
```js
await fetch('http://localhost:8080/api/usuarios/me/password', {
  method: 'PATCH',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify({ passwordActual, passwordNueva })
});
```

---

### 7) Reemplazar todas las alergias del usuario autenticado
- **PUT** `/api/usuarios/me/alergias`
- **Headers:** `Authorization`
- **Body:**
```json
{
  "alergias": ["Gluten", "Lácteos"]
}
```
- **Respuesta:** usuario actualizado

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/usuarios/me/alergias', {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify({ alergias: ['Gluten', 'Lácteos'] })
});
const updated = await res.json();
```

---

### 8) Agregar alergias al usuario autenticado (sin borrar las anteriores)
- **POST** `/api/usuarios/me/alergias`
- **Headers:** `Authorization: Bearer <token>`
- **Body:**
```json
{
  "alergias": ["Marisco", "Frutos secos"]
}
```
- **Descripción:** añade las alergias indicadas a las que el usuario ya tenía.
- **Respuesta:** usuario actualizado (con lista de alergias y favoritos).

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/usuarios/me/alergias', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify({ alergias: ['Marisco', 'Frutos secos'] })
});
const updated = await res.json();
```

---

### 9) Eliminar una alergia específica del usuario autenticado
- **DELETE** `/api/usuarios/me/alergias/{nombre}`
- **Headers:** `Authorization: Bearer <token>`
- **Body:** sin body
- **Descripción:** elimina una alergia concreta del usuario autenticado.
- **Ejemplo de ruta:** `/api/usuarios/me/alergias/Gluten`
- **Respuesta:** usuario actualizado (con la alergia removida).

#### Ejemplo React Native
```js
const nombre = encodeURIComponent('Gluten');
const res = await fetch(`http://localhost:8080/api/usuarios/me/alergias/${nombre}`, {
  method: 'DELETE',
  headers: { Authorization: `Bearer ${token}` }
});
const updated = await res.json();
```

---

### 10) Obtener usuario por ID
- **GET** `/api/usuarios/{id}`
- **Body:** sin body
- **Respuesta:** usuario encontrado o 404

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
  headers: { Authorization: `Bearer ${token}` }
});
const data = await res.json();
```

---

## Endpoints de Alergias

### Obtener mis alergias (requiere token)
- **GET** `/api/alergias/me`
- **Headers:** `Authorization: Bearer <token>`
- **Descripción:** devuelve únicamente las alergias asociadas al usuario autenticado por JWT.
- **Respuesta:**
```json
[
  { "id": 1, "nombre": "Gluten" },
  { "id": 2, "nombre": "Lácteos" }
]
```

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/alergias/me', {
  headers: { Authorization: `Bearer ${token}` }
});
const misAlergias = await res.json();
```

---

### Listar todas las alergias

- **GET** `/api/alergias`
- **Respuesta:**
```json
[
  { "id": 1, "nombre": "Gluten" },
  { "id": 2, "nombre": "Lácteos" }
]
```

#### Ejemplo React Native
```js
const getAlergias = async () => {
  const response = await fetch('http://localhost:8080/api/alergias');
  if (!response.ok) throw new Error('Error al obtener alergias');
  const alergias = await response.json();
  return alergias;
};

useEffect(() => {
  getAlergias().then(setAlergias).catch(console.error);
}, []);
```

---

### Crear una alergia

- **POST** `/api/alergias`
- **Body:**
```json
{ "nombre": "Frutos secos" }
```
- **Respuesta:**
```json
{ "id": 6, "nombre": "Frutos secos" }
```

#### Ejemplo React Native
```js
await fetch('http://localhost:8080/api/alergias', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify({ nombre: 'Frutos secos' })
});
```

---

### Eliminar una alergia (solo admin)

- **DELETE** `/api/alergias/{id}`
- **Descripción:** Este endpoint elimina una alergia de la base de datos. Solo debe ser usado por administradores. Los usuarios normales no pueden eliminar alergias globales.
- **Respuesta exitosa:**
```text
Alergia eliminada correctamente
```
- **Respuesta si no existe:**
```text
Alergia no encontrada
```

#### Ejemplo React Native
```js
await fetch(`http://localhost:8080/api/alergias/${id}`, {
  method: 'DELETE',
  headers: { Authorization: `Bearer ${token}` }
});
```

> ⚠️ **Nota:** Los usuarios solo pueden gestionar (agregar/quitar) sus propias alergias personales, pero no pueden eliminar alergias de la base de datos global. Este endpoint es exclusivo para administración.

---

## Endpoints de Recetas

### Listar recetas
- **GET** `/api/recetas/getAll`
- **Query opcional:** `titulo`
- **Body:** sin body
- **Respuesta:**
```json
[
  {
    "id": 1,
    "usuarioId": 1,
    "titulo": "Pasta rápida",
    "descripcion": "Ideal para diario",
    "preparacion": "Hervir pasta y mezclar",
    "tiempoPreparacionMin": 10,
    "tiempoCoccionMin": 12,
    "porciones": 2,
    "dificultad": "media",
    "imagenUrl": "https://example.com/pasta.jpg",
    "fechaCreacion": "2026-04-08T11:00:00",
    "ingredientes": [
      { "id": 1, "nombre": "Pasta", "cantidad": 200.00, "unidad": "g" }
    ],
    "tags": ["Comidas", "Rápido"]
  }
]
```

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/recetas/getAll?titulo=pasta');
const recetas = await res.json();
```

---

### Mis recetas (requiere token)
- **GET** `/api/recetas/getMine`
- **Body:** sin body
- **Respuesta:** array de recetas

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/recetas/getMine', {
  headers: { Authorization: `Bearer ${token}` }
});
const recetas = await res.json();
```

---

### Buscar por dificultad
- **GET** `/api/recetas/searchByDificultad?dificultad=media`
- **Body:** sin body
- **Respuesta:** array de recetas

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/recetas/searchByDificultad?dificultad=media');
const recetas = await res.json();
```

---

### Buscar por tiempo máximo
- **GET** `/api/recetas/searchByTiempoMax?maxTiempo=30`
- **Body:** sin body
- **Respuesta:** array de recetas

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/recetas/searchByTiempoMax?maxTiempo=30');
const recetas = await res.json();
```

---

### Búsqueda avanzada
- **GET** `/api/recetas/searchAdvanced?titulo=pasta&dificultad=media&tag=rapido&ingredientes=tomate,ajo&matchAll=true&maxTiempo=30`
- **Body:** sin body
- **Respuesta:** array de recetas

#### Ejemplo React Native
```js
const url = 'http://localhost:8080/api/recetas/searchAdvanced?titulo=pasta&dificultad=media&ingredientes=tomate,ajo&matchAll=true&maxTiempo=30';
const res = await fetch(url);
const recetas = await res.json();
```

---

### Buscar por ingrediente
- **GET** `/api/recetas/searchByIngrediente?ingrediente=tomate`
- **Body:** sin body
- **Respuesta:** array de recetas

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/recetas/searchByIngrediente?ingrediente=tomate');
const recetas = await res.json();
```

---

### Buscar por varios ingredientes
- **GET** `/api/recetas/searchByIngredientes?ingredientes=tomate,ajo&matchAll=true`
- **Body:** sin body
- **Respuesta:** array de recetas

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/recetas/searchByIngredientes?ingredientes=tomate,ajo&matchAll=true');
const recetas = await res.json();
```

---

### Buscar por tag
- **GET** `/api/recetas/searchByTag?tag=vegano`
- **Body:** sin body
- **Respuesta:** array de recetas

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/recetas/searchByTag?tag=vegano');
const recetas = await res.json();
```

---

### Obtener receta por id
- **GET** `/api/recetas/get/{id}`
- **Body:** sin body
- **Respuesta:** receta completa

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/recetas/get/${id}`);
const receta = await res.json();
```

---

### Crear receta (requiere token)
- **POST** `/api/recetas/create`
- **Body:**
```json
{
  "usuarioId": 1,
  "titulo": "Pasta rápida",
  "descripcion": "Ideal para diario",
  "preparacion": "Hervir pasta y mezclar",
  "tiempoPreparacionMin": 10,
  "tiempoCoccionMin": 12,
  "porciones": 2,
  "dificultad": "media",
  "imagenUrl": "https://example.com/pasta.jpg",
  "ingredientes": [
    { "nombre": "Pasta", "cantidad": 200, "unidad": "g" },
    { "nombre": "Tomate", "cantidad": 2, "unidad": "ud" }
  ]
}
```
- **Respuesta:** receta creada

#### Ejemplo React Native
```js
const payload = {
  usuarioId: 1,
  titulo: 'Pasta rápida',
  descripcion: 'Ideal para diario',
  preparacion: 'Hervir pasta y mezclar',
  tiempoPreparacionMin: 10,
  tiempoCoccionMin: 12,
  porciones: 2,
  dificultad: 'media',
  imagenUrl: 'https://example.com/pasta.jpg',
  ingredientes: [
    { nombre: 'Pasta', cantidad: 200, unidad: 'g' },
    { nombre: 'Tomate', cantidad: 2, unidad: 'ud' }
  ]
};

const res = await fetch('http://localhost:8080/api/recetas/create', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify(payload)
});
const receta = await res.json();
```

---

### Actualizar receta (requiere token)
- **PUT** `/api/recetas/update/{id}`
- **Body:** mismo formato que create
- **Respuesta:** receta actualizada

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/recetas/update/${id}`, {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify(payload)
});
const receta = await res.json();
```

---

### Eliminar receta (requiere token)
- **DELETE** `/api/recetas/delete/{id}`
- **Body:** sin body
- **Respuesta exitosa:**
```text
Receta eliminada correctamente
```

#### Ejemplo React Native
```js
await fetch(`http://localhost:8080/api/recetas/delete/${id}`, {
  method: 'DELETE',
  headers: { Authorization: `Bearer ${token}` }
});
```

---

## Endpoints de Valoraciones

> 🔐 **Autenticación en Valoraciones**
>
> - **Públicos:**
>   - `GET /api/recetas/{recetaId}/valoraciones`
>   - `GET /api/recetas/{recetaId}/valoraciones/stats`
> - **Requieren token (JWT):**
>   - `GET /api/recetas/{recetaId}/valoraciones/mia`
>   - `POST /api/recetas/{recetaId}/valoraciones`
>   - `PUT /api/recetas/{recetaId}/valoraciones/mia`
>   - `DELETE /api/recetas/{recetaId}/valoraciones/mia`

### Listar valoraciones de una receta (público)
- **GET** `/api/recetas/{recetaId}/valoraciones`
- **Body:** sin body
- **Respuesta:**
```json
[
  {
    "id": 10,
    "usuarioId": 2,
    "recetaId": 3,
    "puntuacion": 5,
    "comentario": "Me ha encantado",
    "fechaCreacion": "2026-04-08T15:00:00"
  }
]
```

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/recetas/${recetaId}/valoraciones`);
const valoraciones = await res.json();
```

---

### Obtener estadísticas de valoraciones de una receta (público)
- **GET** `/api/recetas/{recetaId}/valoraciones/stats`
- **Body:** sin body
- **Respuesta:**
```json
{
  "recetaId": 3,
  "mediaPuntuacion": 4.5,
  "totalValoraciones": 12
}
```

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/recetas/${recetaId}/valoraciones/stats`);
const stats = await res.json();
```

---

### Obtener mi valoración de una receta (requiere token)
- **GET** `/api/recetas/{recetaId}/valoraciones/mia`
- **Headers:** `Authorization: Bearer <token>`
- **Body:** sin body
- **Respuesta:**
```json
{
  "id": 21,
  "usuarioId": 1,
  "recetaId": 3,
  "puntuacion": 4,
  "comentario": "Muy buena, la repetiré",
  "fechaCreacion": "2026-04-08T15:05:00"
}
```

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/recetas/${recetaId}/valoraciones/mia`, {
  headers: { Authorization: `Bearer ${token}` }
});
const miValoracion = await res.json();
```

---

### Crear mi valoración de una receta (requiere token)
- **POST** `/api/recetas/{recetaId}/valoraciones`
- **Headers:** `Authorization: Bearer <token>`
- **Body:**
```json
{
  "puntuacion": 5,
  "comentario": "Receta top"
}
```
- **Respuesta:** valoración creada

#### Ejemplo React Native
```js
const payload = { puntuacion: 5, comentario: 'Receta top' };
const res = await fetch(`http://localhost:8080/api/recetas/${recetaId}/valoraciones`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify(payload)
});
const created = await res.json();
```

---

### Actualizar mi valoración de una receta (requiere token)
- **PUT** `/api/recetas/{recetaId}/valoraciones/mia`
- **Headers:** `Authorization: Bearer <token>`
- **Body:**
```json
{
  "puntuacion": 4,
  "comentario": "La actualizo después de repetirla"
}
```
- **Respuesta:** valoración actualizada

#### Ejemplo React Native
```js
const payload = { puntuacion: 4, comentario: 'La actualizo después de repetirla' };
const res = await fetch(`http://localhost:8080/api/recetas/${recetaId}/valoraciones/mia`, {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify(payload)
});
const updated = await res.json();
```

---

### Eliminar mi valoración de una receta (requiere token)
- **DELETE** `/api/recetas/{recetaId}/valoraciones/mia`
- **Headers:** `Authorization: Bearer <token>`
- **Body:** sin body
- **Respuesta exitosa:**
```text
Valoracion eliminada correctamente
```

#### Ejemplo React Native
```js
await fetch(`http://localhost:8080/api/recetas/${recetaId}/valoraciones/mia`, {
  method: 'DELETE',
  headers: { Authorization: `Bearer ${token}` }
});
```

---

## Endpoints de Favoritos

> 🔐 **Autenticación en Favoritos**
>
> - **No requiere token (público):** `GET /api/favoritos/countByReceta/{recetaId}`
> - **Sí requiere token (JWT):** `getMine`, `isFavorite`, `add`, `remove`, `toggle`

### Ver mis favoritos (requiere token)
- **GET** `/api/favoritos/getMine`
- **Body:** sin body
- **Respuesta:**
```json
[
  {
    "recetaId": 3,
    "titulo": "Pasta rápida",
    "imagenUrl": "https://example.com/pasta.jpg",
    "fechaAgregado": "2026-04-08T12:30:00"
  }
]
```

#### Ejemplo React Native
```js
const res = await fetch('http://localhost:8080/api/favoritos/getMine', {
  headers: { Authorization: `Bearer ${token}` }
});
const favoritos = await res.json();
```

---

### Saber si una receta está en favoritos (requiere token)
- **GET** `/api/favoritos/isFavorite/{recetaId}`
- **Body:** sin body
- **Respuesta:**
```json
true
```

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/favoritos/isFavorite/${recetaId}`, {
  headers: { Authorization: `Bearer ${token}` }
});
const isFavorite = await res.json();
```

---

### Contar favoritos de una receta (publico, NO requiere token)
- **GET** `/api/favoritos/countByReceta/{recetaId}`
- **Body:** sin body
- **Respuesta:**
```json
12
```

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/favoritos/countByReceta/${recetaId}`);
const totalFavoritos = await res.json();
```

---

### Añadir receta a favoritos (requiere token)
- **POST** `/api/favoritos/add/{recetaId}`
- **Body:** sin body
- **Respuesta:**
```json
{
  "recetaId": 3,
  "titulo": "Pasta rápida",
  "imagenUrl": "https://example.com/pasta.jpg",
  "fechaAgregado": "2026-04-08T12:30:00"
}
```

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/favoritos/add/${recetaId}`, {
  method: 'POST',
  headers: { Authorization: `Bearer ${token}` }
});
const favorito = await res.json();
```

---

### Eliminar receta de favoritos (requiere token)
- **DELETE** `/api/favoritos/remove/{recetaId}`
- **Body:** sin body
- **Respuesta exitosa:**
```text
Favorito eliminado
```

#### Ejemplo React Native
```js
await fetch(`http://localhost:8080/api/favoritos/remove/${recetaId}`, {
  method: 'DELETE',
  headers: { Authorization: `Bearer ${token}` }
});
```

---

### Toggle favorito (requiere token)
- **POST** `/api/favoritos/toggle/{recetaId}`
- **Body:** sin body
- **Respuesta (agrega):**
```json
{
  "favorito": true,
  "mensaje": "Favorito agregado"
}
```
- **Respuesta (elimina):**
```json
{
  "favorito": false,
  "mensaje": "Favorito eliminado"
}
```

#### Ejemplo React Native
```js
const res = await fetch(`http://localhost:8080/api/favoritos/toggle/${recetaId}`, {
  method: 'POST',
  headers: { Authorization: `Bearer ${token}` }
});
const result = await res.json();
```

---

## Notas
- Todos los endpoints devuelven errores en formato `{ "error": "mensaje" }` o texto plano.
- Para pruebas, puedes usar herramientas como Postman o Insomnia.
- Para utilizar la api en local : `http://localhost:8080`.
- Swagger API : `http://localhost:8080/swagger-ui/index.html`
- openAPI: `http://localhost:8080/v3/api-docs`