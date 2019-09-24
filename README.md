## Laboratorio AWS - AREP
### Modularización con Virtualización - Cliente Servidor

Se desarrolló una aplicación web Cliente servidor, que permite responder a múltiples peticiones concurrentemente sin dañar la modularización utilizando virtualización. La virtualización permite ejecutar varios sistemas operativos dentro de un mismo servidor físico.
Para realizar las peticiones se utilizó Amazon, que es una plataforma de computación en la nube que ofrece servicios de infraestructura para servicios web.

___
### Instalación - Uso del proyecto como librería
Si desea usar éste repositorio como librería en su proyecto, realice los siguientes pasos:

• Descargue o clone él repositorio Proyecto1-AREP: <https://github.com/acai-bjca/Proyecto1-AREP.git>

• Agregue la siguiente dependencia al pom de su proyecto:
``` xml
 <dependency>
	<groupId>apps</groupId>
    <artifactId>ClienteServidorAWS-AREP</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

• Importe el proyecto en la clase que lo requiera:
import apps.*;

___
### Documentación

Para leer la documentación diríjase a: <https://github.com/acai-bjca/ClienteServidorAWS-AREP/tree/master/src/main/resources/documentacion/apidocs/apps>

___
### Despliegue

El link de la aplicacion web desplegada en heroku es: <https://webserviceaws.herokuapp.com>

[![Heroku](https://www.herokucdn.com/deploy/button.svg)](https://webserviceaws.herokuapp.com)
___
### Construido con

• Java  
• [Maven] (https://maven.apache.org/) - Gestión de dependencias

___
### Autor

**Amalia Inés Alfonso Campuzano** 

Estudiante de la Escuela Colombiana de Ingeniería Julio Garavito

Ingeniería de Sistemas
___
### Licencia

Este proyecto está licenciado bajo la Licencia GNU - vea el archivo [LICENSE.md] (LICENSE.md) para más detalles.

