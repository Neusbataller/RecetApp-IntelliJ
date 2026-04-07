
# Recetas API Documentation

> ⚠️ **Nota importante:**
>
> Por ahora, la API solo dispone de los endpoints de registro, login y gestión de alergias. Próximamente se irán añadiendo nuevos endpoints (como gestión de recetas, favoritos, etc.) y se avisará al equipo frontend cuando estén disponibles. ¡Mantente atento a las actualizaciones!

Bienvenido a la documentación oficial de la API de Recetas. Esta API permite gestionar usuarios, alergias y recetas, facilitando la integración con aplicaciones móviles como React Native.

---

## Autenticación

La mayoría de los endpoints requieren autenticación mediante JWT. El token debe enviarse en el header:

```
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
  "usuario": { ...datos usuario... }
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
  ...
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
// Obtener todas las alergias desde la API
const getAlergias = async () => {
  const response = await fetch('http://localhost:8080/api/alergias');
  if (!response.ok) throw new Error('Error al obtener alergias');
  const alergias = await response.json();
  return alergias; // [{ id: 1, nombre: 'Gluten' }, ...]
};

// Uso en un componente React Native
useEffect(() => {
  getAlergias()
    .then(setAlergias)
    .catch(console.error);
}, []);
```

---


### Crear una alergia

- **POST** `/api/alergias`
- **Body:**
```json
{ "nombre": "Frutos secos" }
```

---

### Eliminar una alergia (solo admin)

- **DELETE** `/api/alergias/{id}`
- **Descripción:** Este endpoint elimina una alergia de la base de datos. Solo debe ser usado por administradores. Los usuarios normales no pueden eliminar alergias globales.
- **Respuesta exitosa:**
```
Alergia eliminada correctamente
```
- **Respuesta si no existe:**
```
Alergia no encontrada
```

> ⚠️ **Nota:** Los usuarios solo pueden gestionar (agregar/quitar) sus propias alergias personales, pero no pueden eliminar alergias de la base de datos global. Este endpoint es exclusivo para administración.

## Notas
- Todos los endpoints devuelven errores en formato `{ "error": "mensaje" }` o texto plano.
- Para pruebas, puedes usar herramientas como Postman o Insomnia.
- Para utilizar la api en local : `http://localhost:8080`.

---