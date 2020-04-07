# GOMAR_sanidad
Android app to store Fichas information in order to comply with Health requirements.
Cuando en una tienda de alimentación se realiza un procesado propio se tiene que rellenar una ficha con información sobre los componentes y lotes de los productos utilizados en el mismo. Esto es un requisito del Ministerio de Sanidad para permitir la trazabilidad de los productos utilizados y para estandarizar un mecanismo de consulta a los consumidores interesados o con alergías y/o intolerancias.
Esta aplicación nace para cumplir con los requisitos descritos arriba, así como automatizar el proceso al máximo, ahorrar papel y tiempo.


## Installation

To build the apk you can follow [this tutorial](https://developer.android.com/studio/build/building-cmdline) on the directory in which the project has been downloaded or you can build it using Android Studio.
Once the apk file is built, the installation will be done as a normal Android application.

## Functionality

La aplication utiliza una versión de SQLLite para la gestión de la BD.
La ventana principal de la aplicación es la siguiente:

Nueva ficha -> Permite crear nuevas fichas.
Consultar fichas -> Permite consultar las diferentes fichas almacenadas en la BD.
Ajustes -> Permite añadir información sobre la cuenta de Google Drive en la que persistir una copia de la BD y cambiar la imagen principal de la aplicación (Work in progress).

![Main_view](https://user-images.githubusercontent.com/17985894/78711546-6e4d7480-7917-11ea-8101-18da4b41871b.PNG)

La ventana para crear una nueva ficha es la siguiente, puede apreciarse el botón para añadir/eliminar componentes, de forma que se permite la adición de un número ilimitado de componentes a la ficha del producto. En la esquina superior derecha podemos guardar la ficha en la BD, guardarla como patrón o importar un patrón.
También se implementan reglas para controlar la integridad de los datos que se introducen en la BD.

![Nueva_ficha](https://user-images.githubusercontent.com/17985894/78719860-ec644800-7924-11ea-8c00-4699e1bf8283.png)

La ventana para consultar las fichas actúa como una GUI para hacer consultas, de forma sencilla, a la BD. Una vez hemos seleccionado las fichas que queremos ver, se reutilizará la ventana anterior, sin posibilidad de interactuar con ella, para las fichas seleccionadas. Se permite la exportación de esas fichas, tanto conjunta como individualmente, a PDF.

![Consultar_fichas](https://user-images.githubusercontent.com/17985894/78719876-f25a2900-7924-11ea-937f-d0c698df5f3f.png)



## FAQ
**¿Cómo automatiza el proceso?**
La aplicación implementa funcionalidad para guardar determinadas fichas como patrón, de forma que dichas fichas puedan ser importadas cuando queramos rellenar una nueva ficha. Al importar una ficha como patrón se rellenarán la mayoría de los campos en la nueva ficha basándose en el patrón y se cambiará algún valor determinada como: fecha de creación, lote...
También se implementa un pequeño sistema de predicción de la entrada en los diferentes campos de texto libre por el que se sugieren opciones dependiendo de los valores almacenados en la BD, de esta forma, cuanto más se use la aplicación y más datos tenga, más eficiente será el proceso de generación de una nueva ficha.

**¿Cómo se pueden exportar los datos de la BD para imprimirlos o para presentarlos a un cliente o inspector?**
En la sección para consultar las fichas (Ver más arriba) hay diferentes filtros para seleccionar las fichas que nos interesa exportar, una vez que seleccionamos dichas fichas podemos consultarlas o exportarlas en formato PDF. Estos PDF se almacenarán en la memoria interna del teléfono y contienen una representación de la ficha.


