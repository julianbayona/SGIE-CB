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
