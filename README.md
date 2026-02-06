# Gestor de Datos (Socio/Prestamo)

Aplicación de escritorio JavaFX para la gestión de Socios y Préstamos.

## Requisitos Previos

*   Java JDK 21
*   Maven
*   MariaDB Server (corriendo en `localhost:3306`)

## Configuración de Base de Datos

1.  Asegúrate de tener MariaDB iniciado.
2.  Ejecuta el script `db_schema.sql` para crear la base de datos y las tablas necesarias.
    ```bash
    mysql -u root -p < db_schema.sql
    ```
3.  La aplicación está configurada para conectarse a `jdbc:mariadb://localhost:3306/gestordatos` con usuario `root` y sin contraseña.
    Si necesitas cambiar esto, edita el fichero `src/main/resources/db.properties`.

## Ejecución

Para compilar y ejecutar la aplicación utiliza Maven:

```bash
mvn clean javafx:run
```

## Funcionalidades

*   **Gestión de Socios**: Alta, Baja, Modificación y Navegación de socios. Validación de DNI.
*   **Gestión de Préstamos**: Alta, Baja, Modificación y Navegación de préstamos asociados a socios.
*   **Persistencia**: Los datos se guardan automáticamente en la base de datos MariaDB.

## Estructura del Proyecto

*   `model`: Entidades `Socio` y `Prestamo`.
*   `dao`: Capa de acceso a datos (JDBC).
*   `controller`: Controladores de las vistas JavaFX.
*   `view`: Ficheros FXML de la interfaz.
*   `util`: Clase `GestorDatos` como fachada del servicio.

## Notas de Implementación

*   Se utiliza el patrón DAO para separar la lógica de negocio del acceso a datos.
*   Se utiliza `DatabaseConnection` (Singleton) para gestionar la conexión JDBC.
*   La interfaz permite navegar registro a registro ("First", "Prev", "Next", "Last").

## Decisiones de Diseño e IA

Se ha utilizado IA para generar la estructura inicial (Boilerplate) de las clases DAO y los ficheros FXML, acelerando el desarrollo de código repetitivo. Las validaciones de negocio (DNI regex) y la lógica de conexión se han revisado manualmente para cumplir con los requisitos.
