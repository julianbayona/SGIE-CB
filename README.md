# Monolito modular con Spring Boot

Base de proyecto para trabajar con una arquitectura de **monolito modular** usando **Spring Boot**.  
Cada módulo está organizado con el patrón **por capas**:

- `presentacion`
- `aplicacion`
- `dominio`
- `infraestructura`

## Estructura

```text
src
├── main
│   ├── java/com/ejemplo/monolitomodular
│   │   ├── shared
│   │   ├── clientes
│   │   │   ├── presentacion
│   │   │   ├── aplicacion
│   │   │   ├── dominio
│   │   │   └── infraestructura
│   │   └── productos
│   │       ├── presentacion
│   │       ├── aplicacion
│   │       ├── dominio
│   │       └── infraestructura
│   └── resources
└── test
```

## Criterios arquitectónicos

- Es una sola aplicación Spring Boot, un solo despliegue, una sola base de código.
- Cada módulo encapsula su lógica y expone casos de uso hacia fuera.
- `presentacion` depende de `aplicacion`.
- `aplicacion` coordina casos de uso y depende de puertos del `dominio`.
- `infraestructura` implementa puertos del `dominio`.
- `dominio` no depende de Spring.

## Módulos incluidos

- `clientes`
- `productos`

Ambos están creados como ejemplo y pueden servir de plantilla para nuevos módulos de negocio.

## Cómo ejecutar

```bash
mvn spring-boot:run
```

## Configuracion para despliegue

La aplicacion toma configuracion desde variables de entorno. El archivo `.env.example` contiene los nombres esperados para base de datos, notificaciones y calendario.

Variables principales:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=sgie
DB_USERNAME=postgres
DB_PASSWORD=postgres

SGIE_NOTIFICACIONES_PERSONAL_GRUPO_WHATSAPP_ID=grupo-personal@g.us
SGIE_NOTIFICACIONES_PRUEBA_PLATO_CHEF_TELEFONO=573001111111
SGIE_NOTIFICACIONES_PRUEBA_PLATO_GERENTE_TELEFONO=573002222222
SGIE_NOTIFICACIONES_PRUEBA_PLATO_TESORERO_TELEFONO=573003333333

SGIE_CALENDARIO_PRUEBA_PLATO_CHEF_CORREO=chef@club.com
SGIE_CALENDARIO_PRUEBA_PLATO_GERENTE_CORREO=gerente@club.com
SGIE_CALENDARIO_PRUEBA_PLATO_TESORERO_CORREO=tesorero@club.com
SGIE_CALENDARIO_EVENTO_CONFIRMADO_ASISTENTES_CORREOS=gerente@club.com,tesorero@club.com
```

En desarrollo pueden usarse valores locales. En produccion deben configurarse directamente en el servidor o plataforma de despliegue, sin guardar credenciales reales en Git.

## Endpoints de ejemplo

- `POST /api/clientes`
- `GET /api/clientes`
- `GET /api/clientes/{id}`
- `POST /api/productos`
- `GET /api/productos`
- `GET /api/productos/{id}`

## Cómo crear un nuevo módulo

1. Crear el paquete raíz del módulo, por ejemplo `inventario`.
2. Crear sus capas: `presentacion`, `aplicacion`, `dominio`, `infraestructura`.
3. Definir entidades y puertos en `dominio`.
4. Definir casos de uso y servicios en `aplicacion`.
5. Implementar controladores en `presentacion`.
6. Implementar repositorios, adaptadores o integraciones en `infraestructura`.
