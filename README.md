# **Resumen del proyecto**

Este proyecto es la continuación del proyecto de gestión de biblioteca.
Me enfocaré en agregar una interfaz gráfica de usuario (GUI) para interactuar con la base de datos MYSQL
Todo se está realizando mediante el editor de texto *VS code*
**NOTA**
Aún se están realizando cambios y configuraciones del espacio de trabajo, por lo tanto unicamente verá los archivos .json, .xml y .md  que se están utilizando para la configuración del proyecto. Más adelante se subirán las clases necesarias para que todo funcione


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

- [Connector-J](https://central.sonatype.com/artifact/com.mysql/mysql-connector-j) Conexión a la base de datos
- [maeven](https://maven.apache.org/install.html) Gestión del proyecto
- [JavaFX](https://gluonhq.com/products/javafx/) para crear la interfaz de usuario
  Además está utilizando el gestor de la base de datos MySQL.
  Está utilizando el servidor local de Apache y el local host de phpMyAdmin.
  por lo tanto debe tener instalado [XAMPP](https://www.apachefriends.org/es/index.html)

## INSTALACIÓN Y REQUISITOS

- En su archivo pom.xml deberá agregar connector-j y javaFX con las versiones que necesite
- [CONNECTOR-J](https://central.sonatype.com/artifact/com.mysql/mysql-connector-j)
- [javaFX](https://gluonhq.com/products/javafx/) asegurese de descargar el SDK
- Debe tener instalado [Maven](https://maven.apache.org/download.cgi) en su computadora
- Debe tener instalado Java 8 o superior (java 17 recomendado)
- Debe tener instalado [XAMPP](https://www.apachefriends.org/es/index.html)

### EXPLICACIÓN PARA CONFIGURACIÓN DE VARIABLES DE ENTORNO Y MAVEN

**En windows, si no tiene las variables de entorno configuradas deberá hacerlo de la sigiente forma.**
**Por ahora todo el proyecto se está configurando por medio de windows, si usted usa otro Sistema Operativo deberá buscar los recursos en la red para adaptar el proyecto**

0. - En la barra de búsqueda de windows escribir *Advanced* allí verá la opciones de las configuraciones avanzadas para las variables de entorno
1. - Abrir la configuración avanzada del sistema
2. - Ir a *variables de entorno*
3. - Ir a *variables de entorno* para el sistema
4. - dar click en `nueva...`
5. - Nombrar la nueva variable como `JAVA_HOME` y añadir el *PATH* `C:\Program files\java\jdk-17\` Recuerde usar los nombres correctos de sus archivos y su versión del jdk
6. - Ir a *variables de entorno* de nuevo
7. - En *variables del sistema* buscará la variable *Path*  y dar click en *editar*
8. - Ir a la parte superior derecha de la ventana y dar click en *nueva*
9. - Añadir la ruta de su archivo *maven* de la siguente manera `C:\Users\< Nombre del usuario >\Downloads\apache-maven-3.8.8-bin\apache-maven-3.8.8\bin`
10. - Abrir `CMD` como **administrador**
11. - Escribir `mvn --version` para verificar que Maven se encuentra instalado
12. - Verificar su archivo `pom.xml` y tener todo configurado de manera correcta
13. - En el `CMD` ejecutar los siguientes comandos para instalar las dependencias: `mvn install`
14. - verificar que [javaFX](https://gluonhq.com/products/javafx/) esté instalado en su proyecto
15. - ir a las variables de entorno  y buscar la variable `PATH` y agregar la ruta de su archivo de javaFX de la siguiente manera `Files\Java\openjfx-17.0.12_windows-x64_bin-sdk\javafx-sdk-17.0.12\bin`
16. - deberá descargar o copiar los archivos `.json` y agregarlos a su proyecto
17. - Deberá actualizar su archivo `pom.xml` (vaadin no es necesario  agregarlo a la lista de dependencias)



Si siguió todos los pasos de forma correcta en el *CMD* debería ver un mensaje diciendo **BUILD SUCCESS**
de lo contrario puede reportar el error en [REPORTAR_ERROR_GITHUB](https://github.com/DanielRestrepoGaleano/LibraryManagementUX/issues) o con el siguiente formulario [REPORTAR_ERROR_FORMS](https://docs.google.com/forms/d/1OxRtiVPGTAUtvkKE_opcWedZ7b5dZMVU5F3T7YdZRw0)

## Changelog

### **21/09/2024**

- Se ha configurado el espacio de trabajo e instalado depedencias necesarias
- Se ha agregado la documentación para la instalación y configuración del proyecto
