Gestor de Datos - Práctica AA1

Aplicación de escritorio JavaFX para la gestión de Socios y Préstamos, desarrollada como parte de la práctica AA1.
Descripción

Esta aplicación permite realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar) sobre Socios y Préstamos, persistiendo la información en una base de datos MariaDB.
Características Principales

    Gestión de Socios: Alta, baja y modificación de socios.
    Gestión de Préstamos: Asignación de préstamos a socios.
    Validaciones de Negocio:
        Un socio no puede tener más de 5 préstamos activos.
    Interfaz Gráfica: Construida con JavaFX y FXML.
    Conexión Inteligente: Detecta automáticamente credenciales locales o por defecto para facilitar el trabajo en equipo.

Requisitos Previos

    Java JDK 21 o superior.
    Maven (incluido en la mayoría de IDEs o instalable por separado).
    MariaDB ejecutándose en el puerto 3306.
        Base de datos: gestordatos (se crea automáticamente si no existe).

Puesta en Marcha
1. Clonar el Repositorio

git clone https://github.com/Willyknox/AA1--Equipo7-PrestamosSocios.git
cd AA1--Equipo7-PrestamosSocios

2. Configuración de Base de Datos

La aplicación intentará conectarse automáticamente. Si tienes una contraseña específica, edita src/main/resources/db.properties:

db.url=jdbc:mariadb://localhost:3306/gestordatos
db.user=tu_usuario
db.password=tu_contraseña

Si no modificas este archivo, la aplicación probará automáticamente con credenciales por defecto (root/root, root/1234, etc.) hasta conectar.
3. Ejecutar la Aplicación

Puedes arrancar la aplicación directamente usando el script incluido (Windows PowerShell):

.\run_app.ps1

O usando Maven directamente:

mvn javafx:run

Estructura del Proyecto

    src/main/java: Código fuente Java.
    src/main/resources: Archivos FXML, estilos CSS y configuración.
    src/main/resources/db.db: Script de inicialización de la base de datos (si es necesario).

Autores

    Equipo 7

