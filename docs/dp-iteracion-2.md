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
![Pantalla de inicio](../wireframes-y-pantallas/pantallas-2iteracion/pantalla-inicio-clientes.png)

Luego de presionar el botón **Clientes**, se encontrará con esta pantalla, en donde deberá seleccionar qué quiere realizar. Por un lado, el botón para **gestión de clientes** donde podrá gestionar sus clientes, tanto darlos de alta, baja o modificar sus datos, como asignarles servicios.
El botón **Historial de facturación** lo redirige una pantalla donde, por medio de un combobox, deberá seleccionar un cliente. Luego de seleccionarlo, podrá ver su historial de facturación, como sus facturas y pagos.

### Pantalla de Gestión de clientes
![Pantalla de inicio](../wireframes-y-pantallas/pantallas-2iteracion/gestion-clientes-inicio.png)

En esta pantalla usted podrá gestionar sus clientes. La pantalla se divide en tres partes. Un formulario donde ingresar datos de clientes, debajo se tienen dos tablas, una para clientes activos y otra con clientes inactivos (o dados de baja). En la tabla de clientes activos puede seleccionar el botón *'Ver mas detalles'* donde podrá visualizar los servicios asignados al cliente, con la posibilidad de asignarle servicios.

En la tabla de clientes inactivos, tendrá la posibilidad de reactivarlo por medio del botón *Reactivar*. 

Finalmente, al final se tienen tres botones; **Alta**, que habilita el formulario para ingresar los datos de un nuevo cliente, **Baja** y **Modificar**, los cuales se habilitan al momento de ingresar al detalle de un cliente específico.

### Pantalla de detalle de cliente
![Pantalla de inicio](../wireframes-y-pantallas/pantallas-2iteracion/detalle-cliente.png)

En esta pantalla se observan todos los datos del cliente seleccionado. En primera instancia se ve el formulario con sus datos cargados, junto a un botón *'Ver historial de facturación'* que lo redirige al historial de facturación de dicho cliente. Debajo se ve una tabla con los servicios contratados por el mismo, junto con un botón para asignarle servicios.
Debajo de la tabla de servicios contratados observa que continúa la tabla de clientes activos. 

Finalmente, observa los botones **Alta**, **Baja** y **Modificar**. El botón *Alta* lo redirige a la pantalla de gestión de clientes y habilita los campos para dar de alta a un nuevo cliente. El botón de *Baja* da de baja al cliente seleccionado y finalmente el botón de *Modificar* habilita los campos con los datos del cliente, para poder modificarlos. 



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

###### **Menacho Natalia Agustina**

###### **Roko María Guillermina**

###### **Verón Juan Manuel**


