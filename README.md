# **Resumen del proyecto**

Este proyecto es la continuación del proyecto de gestión de biblioteca.
Me enfocaré en agregar una interfaz gráfica de usuario (GUI) para interactuar con la base de datos MYSQL
Todo se está realizando mediante el editor de texto *VS code*

## Tabla de contenido

1. [Resumen del proyecto](#resumen-del-proyecto)
2. [Objetivos](#objetivos)
3. [Estructura del proyecto](#estructura-del-proyecto)
4. [Dependecias](#dependecias)
5. [Instalación y requisitos](#instalación-y-requisitos)
6. [Configuración del entorno](#explicación-para-configuración-de-variables-de-entorno-y-maven)

## Objetivos

1. - Agregar una GUI para interactuar con la base de datos MYSQL
2. - Mantener las funcionalidades de proyecto anterior [VER](https://github.com/DanielRestrepoGaleano/BibliotecaJava) como la gestión de libros, préstamos, etc.

## Estructura del proyecto

Este proyecto tiene la siguente estructura y se está trabajando con maeven.

- **`src/main/java`**: contiene el código fuente del proyecto
- **`src/main/resources`**: contiene los archivos de configuración, imágenes y recursos del proyecto
- **`src/test/java`**: contiene las pruebas unitarias del proyecto
- **`pom.xml`**: archivo de configuración de Maven

## Dependecias

El proyecto utiliza las siguientes dependencias, también adjunto links de descarga.

- [Connector-J](https://dev.mysql.com/downloads/connector/j/) Conexión a la base de datos
- [maeven](https://maven.apache.org/install.html) Gestión del proyecto
- vaadin para crear la GUI

## INSTALACIÓN Y REQUISITOS

- En su archivo pom.xml deberá agregar connector-j y vaadin con las versiones que necesite
- [CONNECTOR-J](https://central.sonatype.com/artifact/com.mysql/mysql-connector-j)
- [VAADIN](https://central.sonatype.com/artifact/com.vaadin/vaadin)
- Debe tener instalado Maven en su computadora
- Debe tener instalado Java 8 o superior

### EXPLICACIÓN PARA CONFIGURACIÓN DE VARIABLES DE ENTORNO Y MAVEN

En windows, si no tiene las variables de entorno configuradas deberá hacerlo de la sigiente forma.

1. - Abrir la configuración avanzada del sistema
2. - Ir a variables de entorno
3. - Ir a variables de entorno para el sistema
4. - dar click en nueva...
5. - Nombrar la nueva variable como `JAVA_HOME` y añadir el PATH `C:\Program files\java\jdk-17\` Recuerde usar los nombres correctos de sus archivos y su versión del jdk
6. - Ir a variables de entorno de nuevo
7. - En variables del sistema buscará la variable *Path*  y dar click en editar
8. - Ir a la parte superior derecha de la ventana y dar click en nueva
9. - Añadir la ruta de su archivo maven de la siguente manera `C:\Users\< Nombre del usuario >\Downloads\apache-maven-3.8.8-bin\apache-maven-3.8.8\bin`
10. - Abrir CMD como administrador
11. - Escribir `mvn --version` para verificar que Maven se encuentra instalado
12. - Verificar su archivo pom.xml y tener todo configurado de manera correcta
13. - En el CMD ejecutar los siguientes comandos  para instalar las dependencias: `mvn install`

Si siguió todos los pasos de forma correcta en el cmd debería ver un mensaje diciendo **BUILD SUCCESS**
de lo contrario puede reportar el error en [REPORTAR_ERROR_GITHUB](https://github.com/DanielRestrepoGaleano/LibraryManagementUX/issues) o con el siguiente formulario [REPORTAR_ERROR_FORMS](https://docs.google.com/forms/d/1OxRtiVPGTAUtvkKE_opcWedZ7b5dZMVU5F3T7YdZRw0)
