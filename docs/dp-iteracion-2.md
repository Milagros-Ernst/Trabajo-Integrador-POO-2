# Trabajo en equipo
Para la planificación y ejecución de esta segunda iteración, el equipo distribuyó las responsabilidades de la siguiente manera: 
* Ernst Milagros Shaiel: Desarrollo de las historias de usuario relacionadas con la gestión de Servicios y la emisión de recibos de pago.
* Menacho Natalia Agustina: Desarrollo de las historias de usuario relacionadas con la Anulación de Facturas y el Informe de Facturación.
* Roko María Guillermina: Creación de Pantallas y Frontend
* Verón Juan Manuel: Desarrollo de las historias de usuario relacionadas con el registro de Pagos.

# Diseño Orientado a Objetos
Para el desarrollo del diagrama de clases trabajamos en la página Draw.io. El link del mismo está dentro de /diagramas/links.

### Diagrama de Clases
![Diagrama de clases](../diagramas/DiagramaDeClases-Integrador-POO-II.png)

# TENDRÍAMOS QUE VER SI SE CAMBIARON MÁS COSAS
El mismo cuenta con las clases Cuenta, Cliente, Administrador, Factura, Detalle_Factura, Servicio, NotaCredito, DetalleNota y Pago.
Además de enumeradores como, EstadoServicio, EstadoCuenta, CondicionIVA, EstadoFactura, MetodoPago, TipoDocumento, TipoComprobante

# Wireframe y Casos de uso
Los diseños de esta iteración se utilizan de la siguiente manera:
### Pantalla de Inicio
![Pantalla de inicio](../wireframes-y-pantallas/pantallas-2iteracion/inicio.png)

Esta es la primer pantalla que se va a encontrar al iniciar el sistema. En esta iteración se mantuvieron los mismos botones; **Clientes**, que lleva a la pantalla para gestionar clientes, **Servicios**, que lleva a la pantalla para gestionar servicios, **Facturación**, que lleva a la pantalla para gestionar la facturación y finalmente, el botón **Pagos**, el cual lo lleva a la pantalla para gestionar pagos. 

### Pantalla de Clientes-Inicio
![Pantalla de clientes-inicio](../wireframes-y-pantallas/pantallas-2iteracion/pantalla-inicio-clientes.png)

Luego de presionar el botón **Clientes**, se encontrará con esta pantalla, en donde deberá seleccionar qué quiere realizar. Por un lado, el botón para **gestión de clientes** donde podrá gestionar sus clientes, tanto darlos de alta, baja o modificar sus datos, como asignarles servicios.
El botón **Historial de facturación** lo redirige una pantalla donde, por medio de un combobox, deberá seleccionar un cliente. Luego de seleccionarlo, podrá ver su historial de facturación, como sus facturas y pagos.

### Pantalla de gestión de clientes
![Pantalla de gestión de clientes](../wireframes-y-pantallas/pantallas-2iteracion/gestion-clientes-inicio.png)

En esta pantalla usted podrá gestionar sus clientes. La pantalla se divide en tres partes. Un formulario donde ingresar datos de clientes, debajo se tienen dos tablas, una para clientes activos y otra con clientes inactivos (o dados de baja). En la tabla de clientes activos puede seleccionar el botón *'Ver mas detalles'* donde podrá visualizar los servicios asignados al cliente, con la posibilidad de asignarle servicios.

En la tabla de clientes inactivos, tendrá la posibilidad de reactivarlo por medio del botón *Reactivar*. 

Finalmente, al final se tienen tres botones; **Alta**, que habilita el formulario para ingresar los datos de un nuevo cliente, **Baja** y **Modificar**, los cuales se habilitan al momento de ingresar al detalle de un cliente específico.

### Pantalla de detalle de cliente
![Pantalla de detalle de cliente](../wireframes-y-pantallas/pantallas-2iteracion/detalle-cliente.png)

En esta pantalla se observan todos los datos del cliente seleccionado. En primera instancia se ve el formulario con sus datos cargados, junto a un botón *'Ver historial de facturación'* que lo redirige al historial de facturación de dicho cliente. Debajo se ve una tabla con los servicios contratados por el mismo, junto con un botón para asignarle servicios.
Debajo de la tabla de servicios contratados observa que continúa la tabla de clientes activos. 

Finalmente, observa los botones **Alta**, **Baja** y **Modificar**. El botón *Alta* lo redirige a la pantalla de gestión de clientes y habilita los campos para dar de alta a un nuevo cliente. El botón de *Baja* da de baja al cliente seleccionado y finalmente el botón de *Modificar* habilita los campos con los datos del cliente, para poder modificarlos. 

### Pantalla de historial de facturación
![Pantalla de historial de facturación](../wireframes-y-pantallas/pantallas-2iteracion/facturacion-clientes.png)

Luego de seleccionar un cliente desde el combobox, usted podrá observar el historial de facturación del mismo. Primero, se muestra la información del cliente, como *nombre completo*, *documento*, *condición fiscal* y *email*. Además de un acceso rápido a la gestión de pagos.

Debajo se observa el **Historial de facturas**, donde podrá ver el número de factura, período facturado, día de emisión, fecha de vencimiento, monto total y estado. Además cuenta con una columna **Acciones**, en donde tendrá distintas acciones dependiendo del estado de la factura.
    * Si la factura tiene estado `VIGENTE`, podrá realizar las siguientes acciones:
        * *Pagar:* redirige a la gestión de pagos para pagar la factura,
        * *Anular:* anulará la factura (pidiendo obligatoriamente un motivo),
        * *Ver:* ver el detalle de la factura e imprimirla.
    * Si la factura tiene estado `ANULADA`, tendrá la opción *Ver nota*, para visualizar la nota de crédito.
    * Si la factura tiene estado `PAGADA`, tendrá la opción *Ver*, para ver el detalle de la factura e imprimirla.
    * Si la factura tiene estado `PARCIAL`, tendrpa las opciones *Pagar* y *Ver*.

Posterior a esta tabla, y si corresponde, el sistema mostrará la deuda total pendiente junto a un botón para ir a pagar. 

Debajo de esta se encuentra una nueva tabla que muestra los pagos realizados con sus respectivos comprobantes. Donde muestra los datos del mismo junto a una columna donde podrá visualizar el comprobante e imprimirlo.

## Comprobantes
### Facturas
![Factura](../wireframes-y-pantallas/pantallas-2iteracion/factura-1.png)

Esta es la factura N° 1 emitida para un cliente. En el encabezado, a la izquierda se observan los datos del emisor. En el centro el tipo de factura (en este caso 'A') y a la derecha los datos de la factura, como su número, fecha de emisión, vencimiento y el cuit del emisor.
Debajo se obervan los datos del cliente facturado.

En el cuerpo de la factura se observa el detalle de cada servicio facturado (...)

# Backlog de iteraciones

* HU 05 - Modificación de servicio
* HU 06 - Baja lógica de servicio
* HU 09 - Ver estado de cuenta del cliente
* HU 10 - Emisión de recibos de pago
* HU 11 - Anulación de factura
* HU 12 - Registrar pago total
* HU 13 - Registrar pago parcial
* HU 14 - Informe de historial de facturación masiva

# Tareas

###### **Ernst Milagros Shaiel**

En esta segunda iteración trabajé en tres historias de usuario: **HU 05 – Modificación de servicio**, **HU 06 – Baja lógica de servicio** y **HU 10 – Emisión de recibos de pago**.

Para las HU 05 y HU 06 no fue necesario crear archivos nuevos ya que el alta de servicio estaba implementado desde la primera iteración, pero sí desarrollé toda la parte interna necesaria para que estas dos funcionalidades funcionen bien. En la **modificación de servicio** trabajé sobre el servicio y el controlador para permitir actualizar precio, descripción y tipo de IVA, reutilizando el formulario del alta pero con los datos cargados. También implementé las validaciones y la lógica para que los cambios se reflejen correctamente al guardar.

En la **baja lógica de servicio** agregué el campo `activo` y desarrollé toda la lógica para desactivar un servicio sin borrarlo. Ajusté los métodos del repositorio para que filtren únicamente los servicios activos, evitando que los inactivos aparezcan tanto en la lista de servicios como en la facturación. También implementé el controlador que se encarga de ejecutar la baja lógica.

La **HU 10 – Emisión de recibos** fue la más compleja porque tuve que armar todo el módulo desde cero. Creé los modelos `Recibo` y `DetalleRecibo`, donde este último actúa como tabla intermedia entre `Recibo` y `Factura`. Implementé la lógica para generar el número de recibo, calcular totales, manejar pagos totales y parciales y vincular correctamente cada recibo con una o varias facturas mediante los detalles. También armé el repositorio, el servicio y el controlador completos, y realicé el diseño de la vista del recibo.

Además, en esta iteración me encargué de desarrollar varias pruebas tanto de modelos como de servicios. Entre los **modelos** hice pruebas de: `Servicio`, `Recibo`, `Pago`, `Cuenta`, `Cliente` y `ClienteServicio`. En cuanto a **servicios**, realicé pruebas para: `ClienteService`, `ClienteServicioServicio`, `PagoServicio`, `ReciboServicio` y `ServicioServicio`. Para estas pruebas utilicé Mockito para simular repositorios y así poder enfocarme en la lógica interna. En varios casos tuve que ajustar pequeñas partes de la funcionalidad de recibos para asegurar que los tests funcionaran correctamente.

###### **Menacho Natalia Agustina**

###### **Roko María Guillermina**

###### **Verón Juan Manuel**


