# Recetas API Documentation

> ⚠️ **Nota importante:**
>
> Por ahora, la API solo dispone de los endpoints de registro, login y gestión de alergias. Próximamente se irán añadiendo nuevos endpoints (como gestión de recetas, favoritos, etc.) y se avisará al equipo frontend cuando estén disponibles. ¡Mantente atento a las actualizaciones!

Bienvenido a la documentación oficial de la API de Recetas. Esta API permite gestionar usuarios, alergias y recetas, facilitando la integración con aplicaciones móviles como React Native.

---

## Autenticación

La mayoría de los endpoints requieren autenticación mediante JWT. El token debe enviarse en el header:

```http
Authorization: Bearer <token>
```

Obtén el token usando el endpoint de login.

---

## Endpoints de Usuario

### Registro de usuario

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

### Login

- **POST** `/api/usuarios/login`
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
  "usuario": { "id": 1, "nombre": "Juan" }
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
// Guarda data.token para futuras peticiones
```

---

### Obtener usuario autenticado

- **GET** `/api/usuarios/me`
- **Headers:**
  - Authorization: Bearer `<token>`
- **Respuesta:**
```json
{
  "id": 1,
  "nombre": "Juan",
  "apellidos": "Pérez",
  "correo": "juan@mail.com"
}
```

#### Ejemplo React Native
```js
await fetch('http://localhost:8080/api/usuarios/me', {
  headers: { Authorization: `Bearer ${token}` }
});
```

---

### Actualizar perfil

- **PUT** `/api/usuarios/me`
- **Headers:** Authorization
- **Body:**
```json
{
  "nombre": "Nuevo nombre",
  "apellidos": "Nuevos apellidos",
  "correo": "nuevo@mail.com"
}
```
- **Respuesta:**
```json
{
  "id": 1,
  "nombre": "Nuevo nombre",
  "apellidos": "Nuevos apellidos",
  "correo": "nuevo@mail.com"
}
```

#### Ejemplo React Native
```js
await fetch('http://localhost:8080/api/usuarios/me', {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`
  },
  body: JSON.stringify({ nombre, apellidos, correo })
});
```

---

### Cambiar contraseña

- **PATCH** `/api/usuarios/me/password`
- **Headers:** Authorization
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

## Endpoints de Alergias

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

## Notas
- Todos los endpoints devuelven errores en formato `{ "error": "mensaje" }` o texto plano.
- Para pruebas, puedes usar herramientas como Postman o Insomnia.
- Para utilizar la api en local : `http://localhost:8080`.
- Swagger API : `http://localhost:8080/swagger-ui/index.html`
- openAPI: `http://localhost:8080/v3/api-docs`

---
