# 🍽️ Sistema Full-Stack de Gestión y Pedido de Comida

> **Proyecto completo (Full-Stack) para permitir a clientes buscar restaurantes, ver menús, agregar al carrito y realizar pedidos, y a propietarios gestionar menús, pedidos y eventos.**

---

## 📖 Descripción

Este repositorio contiene el código fuente del **Sistema de Gestión y Pedido de Comida**. Su objetivo es ofrecer:

* Un **backend** en Java/Spring Boot que expone APIs REST para gestionar usuarios, restaurantes, menús, pedidos, pagos y notificaciones.
* Un **frontend** en React.js que permite a los clientes navegar por restaurantes, ver menús, agregar productos al carrito y finalizar el pedido.
* Un **panel de administración** para que los propietarios de restaurantes gestionen su negocio: crear/editar restaurantes, agregar productos, ver pedidos, actualizar estados y configurar eventos.
* Integraciones con servicios externos:

  * **Cloudinary** para subir y guardar imágenes de restaurantes y productos.
  * **Stripe** para procesamiento de pagos con tarjeta.
  * **SendGrid (o similar)** para el envío de correos de notificación (confirmación de registro, actualización de estado de pedido, restablecimiento de contraseña).
* Autenticación y autorización basada en **JWT** (JSON Web Token) con Spring Security.
* Persistencia en **MySQL** usando Spring Data JPA.

---

## 🚀 Características Principales

### 🎯 Funcionalidades para Clientes

* **Registro / Login** usando email y contraseña (encriptación con BCrypt).
* Navegar lista de restaurantes y filtros por tipo de cocina.
* Visualizar menú detallado de cada restaurante.
* Filtrar productos (por categoría, disponibilidad, vegetariano, etc.).
* Agregar o eliminar productos a un carrito de compras.
* Finalizar pedido: seleccionar dirección de entrega, método de pago y confirmar.
* Historial de pedidos y seguimiento de estado.
* Guardar restaurantes como favoritos.
* Gestión de direcciones de entrega (CRUD).
* Ver eventos publicados por restaurantes.

### 🎯 Funcionalidades para Propietarios de Restaurante

* **Registro / Login** con rol `RESTAURANTE_OWNER`.
* Crear, editar y eliminar un restaurante (solo un restaurante por propietario).
* Configurar detalles del restaurante: nombre, descripción, tipo de cocina, horarios.
* Subir imágenes del restaurante (Cloudinary).
* Añadir, editar y eliminar productos (Food): nombre, descripción, precio, categoría, disponibilidad, imágenes, ingredientes.
* Organizar productos por categoría (`FoodCategory`) e ingredientes (`IngredientItem`, `IngredientCategory`).
* Ver lista de pedidos recibidos y actualizar estado: `PENDIENTE`, `EN_PREPARACIÓN`, `EN_CAMINO`, `ENTREGADO`.
* Crear y gestionar eventos asociados al restaurante (nombre, imagen, fechas, ubicación).
* Visualizar reporte básico de ventas (no implementado en este MVP).

### 🎯 Funcionalidades Generales

* **JWT** para autenticación stateless (Spring Security).
* **CORS** configurado para permitir peticiones desde el frontend (React en `http://localhost:3000` o dominio de producción).
* **Respuestas JSON** controladas con DTOs para ocultar contraseñas y datos sensibles.
* Envío de correos de notificación para registro, restablecimiento de contraseña y actualizaciones de pedidos.
* Manejo de errores y excepciones con un controlador global (`@ControllerAdvice`).
* Paginación y ordenación básica en listados de restaurantes y productos.
* Documentación de API (Swagger/OpenAPI) – pendiente de integración en este MVP.

---

## 🏗️ Arquitectura y Tecnologías

### Backend (Java / Spring Boot)

* **Spring Boot 3.x**
* **Spring Web** (rest controllers)
* **Spring Data JPA + Hibernate** (persistencia en MySQL)
* **Spring Security + JWT** (autenticación y autorización)
* **Spring Mail** (envío de correos)
* **Cloudinary Java SDK** (subida de imágenes)
* **Stripe Java SDK** (procesamiento de pagos)
* **Lombok** (reducción de boilerplate)
* **JWT (jjwt 0.11.1)**
* **MySQL 8.x**

### Frontend (React.js)

* **React.js 18.x**
* **React Router DOM** (ruteo)
* **Redux + Redux Toolkit** (gestión de estado)
* **Axios** (llamadas a APIs REST)
* **Tailwind CSS** + **Material UI (MUI)** (diseño y componentes)
* **Formik + Yup** (formularios y validación)
* **Cloudinary Upload Widget**
* **Stripe.js** (integración de pago en el frontend)

---

## 📂 Estructura del Proyecto

```
/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/diver/
│   │   │   │   ├── config/             # Configuración de seguridad, CORS, Swagger
│   │   │   │   ├── controller/         # Controladores REST
│   │   │   │   ├── dto/                # Data Transfer Objects
│   │   │   │   ├── model/              # Entidades JPA (@Entity)
│   │   │   │   ├── repository/         # Repositorios JPA (interfaces)
│   │   │   │   ├── security/           # JWT, filtros, UserDetailsService
│   │   │   │   ├── service/            # Lógica de negocio (Services)
│   │   │   │   └── util/               # Utilidades (patrones, excepciones, etc.)
│   │   │   └── resources/
│   │   │       ├── application.properties  # Configuración general (DB, JWT, Cloudinary, Stripe)
│   │   │       └── static/            # Archivos estáticos si se requiere
│   │   └── test/                      # Pruebas unitarias (JUnit 5)
│   └── pom.xml                        # Dependencias y plugins (Maven)
│
└── frontend/
    ├── public/
    │   └── index.html
    ├── src/
    │   ├── assets/                    # Imágenes, fuentes, íconos
    │   ├── components/                # Componentes React reutilizables
    │   ├── features/                  # Redux slices / funcionalidad especíﬁca (auth, cart, etc.)
    │   ├── pages/                     # Vistas / páginas (Home, Login, Restaurante, etc.)
    │   ├── services/                  # Servicios de API (con Axios)
    │   ├── utils/                     # Funciones utilitarias (formatos, validadores, etc.)
    │   ├── App.jsx                    # Componente raíz / rutas
    │   └── index.jsx                  # Punto de entrada
    └── package.json                   # Dependencias y scripts (npm/yarn)
```

---

## ⚙️ Requisitos Previos

1. **Java 17+**
2. **Maven 3.x**
3. **Node.js 18.x y npm / yarn**
4. **MySQL 8.x**
5. Cuenta en **Cloudinary** y credenciales (API Key, API Secret, Cloud Name)
6. Cuenta en **Stripe** y credenciales (Publishable Key, Secret Key)
7. (Opcional) Cuenta en **SendGrid** o servicio de correo SMTP para notificaciones

---

## 🔧 Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/food-ordering-system.git
cd food-ordering-system
```

---

### 2. Configurar la Base de Datos (MySQL)

1. Crear base de datos:

   ```sql
   CREATE DATABASE foodsystem CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Crear un usuario y otorgar permisos (opcional):

   ```sql
   CREATE USER 'fooduser'@'localhost' IDENTIFIED BY 'foodpassword';
   GRANT ALL PRIVILEGES ON foodsystem.* TO 'fooduser'@'localhost';
   FLUSH PRIVILEGES;
   ```
3. Asegúrate de que MySQL esté corriendo (`systemctl start mysql` o similar).

---

### 3. Configurar Variables de Entorno (Backend)

En `backend/src/main/resources/application.properties`, completa con tus credenciales:

```properties
# ===== Datos de conexión MySQL =====
spring.datasource.url=jdbc:mysql://localhost:3306/foodsystem?useSSL=false&serverTimezone=UTC
spring.datasource.username=fooduser
spring.datasource.password=foodpassword
spring.jpa.hibernate.ddl-auto=update

# ===== JWT =====
jwt.secret=MI_CLAVE_SUPER_SECRETA_JWT_256_BITS
jwt.expiration=3600000   # 1 hora en milisegundos

# ===== Cloudinary =====
cloudinary.cloud-name=tuCloudName
cloudinary.api-key=tuApiKey
cloudinary.api-secret=tuApiSecret

# ===== Stripe =====
stripe.api.key=sk_test_tuStripeSecretKey

# ===== Correo (SendGrid / SMTP) =====
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey          # Para SendGrid, 'apikey' es usuario
spring.mail.password=tuSendGridKey
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

### 4. Configurar Variables de Entorno (Frontend)

En la raíz de `frontend/`, crea un archivo `.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_CLOUDINARY_UPLOAD_PRESET=tuUploadPreset
VITE_CLOUDINARY_API_URL=https://api.cloudinary.com/v1_1/tuCloudName/image/upload
VITE_STRIPE_PUBLISHABLE_KEY=pk_test_tuStripePublishableKey
```

> 🔒 **Importante:** Nunca subas archivos `.env` con credenciales sensibles a repositorios públicos.

---

## ▶️ Ejecución del Proyecto

### 1. Ejecutar el Backend (Spring Boot)

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

* El servidor arranca en `http://localhost:8080/`.
* Swagger (si se habilitó) estará en `http://localhost:8080/swagger-ui.html` (opcional).

---

### 2. Ejecutar el Frontend (React)

```bash
cd frontend
npm install       # o yarn install
npm run dev       # o yarn dev
```

* El cliente arranca en `http://localhost:3000/`.

---

## 🗂️ Endpoints Principales (Resumen)

### 📌 Autenticación

* `POST /api/auth/register` → Registro de usuario/propietario
* `POST /api/auth/login`    → Login, devuelve JWT

### 📌 Usuarios (Customer)

* `GET /api/users/me`              → Perfil del usuario
* `GET /api/users/orders`          → Historial de pedidos
* `GET /api/users/favorites`       → Restaurantes favoritos
* `POST /api/users/favorites/{id}` → Agregar restaurante favorito
* `DELETE /api/users/favorites/{id}`→ Eliminar favorito

### 📌 Restaurantes

* `GET /api/restaurants`          → Lista de restaurantes (paginada)
* `GET /api/restaurants/{id}`      → Detalle de restaurante y menú
* `POST /api/restaurants`          → Crear restaurante (solo OWNER)
* `PUT /api/restaurants/{id}`      → Editar restaurante (solo OWNER)
* `DELETE /api/restaurants/{id}`   → Eliminar restaurante (solo OWNER)

### 📌 Productos (Food)

* `POST /api/restaurants/{id}/foods`           → Agregar producto (OWNER)
* `PUT /api/restaurants/{rid}/foods/{fid}`      → Editar producto (OWNER)
* `DELETE /api/restaurants/{rid}/foods/{fid}`   → Eliminar producto (OWNER)

### 📌 Carrito y Pedido

* `GET /api/cart`              → Ver carrito actual (Customer)
* `POST /api/cart`             → Agregar producto al carrito
* `PUT /api/cart/{itemId}`     → Actualizar cantidad en carrito
* `DELETE /api/cart/{itemId}`  → Eliminar item del carrito
* `POST /api/orders`           → Crear un pedido (Customer)
* `GET /api/orders/{orderId}`  → Ver detalle de un pedido
* `GET /api/restaurants/{id}/orders`→ Ver pedidos del restaurante (OWNER)
* `PUT /api/restaurants/{rid}/orders/{oid}`→ Actualizar estado de pedido (OWNER)

### 📌 Categorías e Ingredientes

* `GET /api/restaurants/{id}/categories`           → Listar categorías
* `POST /api/restaurants/{id}/categories`           → Crear categoría (OWNER)
* `GET /api/restaurants/{rid}/categories/{cid}/ingredients`       → Listar ingredientes por categoría
* `POST /api/restaurants/{rid}/categories/{cid}/ingredients`      → Crear ingrediente (OWNER)

### 📌 Eventos

* `GET /api/restaurants/{id}/events`      → Listar eventos del restaurante
* `POST /api/restaurants/{id}/events`      → Crear evento (OWNER)
* `DELETE /api/restaurants/{rid}/events/{eid}`→ Eliminar evento (OWNER)

---

## 📁 Estructura de la Base de Datos (Resumen)

* **users** (`id`, `full_name`, `email`, `password`, `role`, `status`, `...`)
* **restaurants** (`id`, `owner_id`, `name`, `description`, `cuisine_type`, `address`, `contact_info`, `opening_hours`, `open`, `registration_date`, `...`)
* **foods** (`id`, `restaurant_id`, `name`, `description`, `price`, `category_id`, `available`, `is_vegetarian`, `is_seasonal`, `creation_date`, ...)
* **food\_categories** (`id`, `name`, `restaurant_id`)
* **ingredient\_categories** (`id`, `name`, `restaurant_id`)
* **ingredient\_items** (`id`, `name`, `category_id`, `restaurant_id`, `in_stock`)
* **events** (`id`, `restaurant_id`, `name`, `image_url`, `location`, `started_at`, `ends_at`)
* **orders** (`id`, `customer_id`, `restaurant_id`, `total_amount`, `order_status`, `created_at`, `delivery_address_id`, `total_items`, `total_price`, `...`)
* **order\_items** (`id`, `order_id`, `food_id`, `quantity`, `total_price`, `...`)
* **carts** (`id`, `customer_id`, `total`)
* **cart\_items** (`id`, `cart_id`, `food_id`, `quantity`, `total_price`, `...`)
* **addresses** (`id`, `user_id`, `street`, `city`, `postal_code`, `...`)
* **user\_favorites** (tabla intermedia para favoritos, `user_id`, `restaurant_id`)
* Tablas auxiliares de **ElementCollections** para imágenes, preferencias, etc.

---

## 🧪 Tests (Opcional)

* Se incluye un módulo de **test unitarios** con JUnit 5 y Mockito en `backend/src/test`.
* Comandos:

  ```bash
  cd backend
  mvn test
  ```

---

## 👥 Contribuciones

1. Haz un **fork** de este repositorio.
2. Crea una rama (`git checkout -b feature/nombre-feature`).
3. Realiza tus cambios y haz **commit** (`git commit -m "✨ Agrego nueva funcionalidad X"`).
4. Haz **push** a tu rama en GitHub (`git push origin feature/nombre-feature`).
5. Abre un **Pull Request** y describe brevemente los cambios.

---

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Consulta el fichero [LICENSE](LICENSE) para más detalles.

---

### ¡Gracias por revisar el proyecto!

Si tienes dudas o sugerencias, abre un [issue](https://github.com/tu-usuario/food-ordering-system/issues) o contáctame.
