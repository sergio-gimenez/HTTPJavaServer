# NPAW Tech - test

## Server

* El servidor HTTP está basado en la librería [HttpServer](https://docs.oracle.com/javase/8/docs/jre/api/net/httpserver/spec/com/sun/net/httpserver/HttpServer.html).

* El servidor escucha por el **puerto 8080**.

## Base de datos

* Para obtener la información, se usa una base de datos SQL generada con sqlite3.

* El acceso a ella se gestiona en la clase `DBHandler`.

* Para el acceso, se usan librerías del package [java.sql](https://docs.oracle.com/javase/7/docs/api/java/sql/package-summary.html).

* Para compilar el código también ha sido necesaria importar la librería externa [SQLite JDBC Driver](https://bitbucket.org/xerial/sqlite-jdbc/src/default/)

## Generación XML

* Para generar la respuesta xml se genera un archivo en la clase `CreateXMLFile` usando librerías del package [javax.xml](https://docs.oracle.com/javase/8/docs/api/index.html?javax/xml/parsers/package-summary.html).

* La generación del código único de view se genera mediante códigos uuid, a través de la librería java [UUID](https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html).

* El balance del tráfico hacia los clusters se modeliza con la generación aleatoria de números generada por la librería Math de java.
