# Trabajo en equipo
Para la planificación y ejecución de esta primera iteración, el equipo distribuyó las responsabilidades de la siguiente manera: 
* Ernst Milagros Shaiel: Desarrollo de las historias de usuario relacionadas con la gestión de Servicios
* Menacho Natalia Agustina: Desarrollo de las historias de usuario relacionadas con la Facturación Masiva, Facturación individual y Anulación de factura
* Roko María Guillermina: Creación de Pantallas y Frontend
* Verón Juan Manuel: Desarrollo de las historias de usuario relacionadas con la gestión de Clientes

# Diseño Orientado a Objetos
Para el desarrollo del diagrama de clases trabajamos en la página Draw.io. El link del mismo está dentro de /diagramas/links.

### Diagrama de Clases
![Diagrama de clases](../diagramas/DiagramaDeClases-Integrador-POO-II.png)

El mismo cuenta con las clases Cuenta, Cliente, Administrador, Factura, Detalle_Factura, Servicio, NotaCredito, DetalleNota y Pago.
Además de enumeradores como, EstadoServicio, EstadoCuenta, CondicionIVA, EstadoFactura, MetodoPago, TipoDocumento, TipoComprobante

# Wireframe y Casos de uso
Los diseños se utilizan de la siguiente manera:
### Pantalla de inicio
![Pantalla de inicio](../wireframes-y-pantallas/pantallas/Pantalla-Inicio.jpg)

En esta pantalla de inicio se divide en tres partes. La primera sección, donde se encuentra el nombre del sistema. La sección del medio la cual posee tres botones: **Clientes**, que lleva a la pantalla para gestionar clientes, **Servicios**, que lleva a la pantalla para gestionar servicios, y finalmente el botón **Facturación**, el cual lo redirige a la pantalla para gestionar las facturaciones del sistema. 
En la última sección, se muestra la información académica y el grupo que presenta este trabajo.

### Pantalla de Gestión de Clientes
![Pantalla de Gestión de Clientes](../wireframes-y-pantallas/pantallas/Pantalla-gestionClientes.jpg)

Esta pantalla funciona para gestionar clientes del sistema. Nuevamente se divide en 3 secciones. En la primer sección se encuentra un formulario con campos bloqueados para la información personal de un cliente, esta sección se habilita en el momento que decida dar de alta a un nuevo cliente. Hay dos botones alineados a la derecha; **Confirmar** confirma los datos ingresados y finaliza con éxito el alta del nuevo cliente, **Cancelar** cancela el alta de un nuevo cliente. En la segunda sección, se encuentra una tabla con todos los clientes registrados en el sistema. Observe que en cada fila se encuentra un botón de acción denominado **"ver mas detalles"**, el cual lo redirigirá a la pantalla **Detalle de Cliente**, donde podrá modificar sus datos o asignarle servicios.
Finalmente, en la última sección, se encuentran los botones de **Alta**, **Baja** y **Modificar**. El botón de **Alta** habilita los campos de la primer sección, para que pueda cargar los datos de un nuevo usuario. El botón **Baja** se habilita en el momento que se seleccione un cliente de la tabla y lo da de baja. De igual manera, el botón **Modificar** se habilita al momento de seleccionar un cliente de la tabla y lo redirige a la pantalla **Detalle de Cliente** para continuar con la modificación.

### Pantalla de Gestión de Clientes - Detalle de Cliente
![Pantalla de Gestión de Clientes](../wireframes-y-pantallas/pantallas/Pantalla-gestionClientes-detalle.jpg)

Esta pantalla sirve para observar los detalles del cliente, modificarlo y asignarle servicios. Es muy similar a la pantalla anterior, solo que ésta muestra con más detalle la información de un cliente en específico. En la esquina izquierda se tiene un botón de navegación **Atrás** que nos lleva a la pantalla anterior. En la parte superior observamos los campos rellenados con la información personal del cliente elegido. Debajo, hay una tabla que muestra todos los servicios contratados por este cliente. Tenemos una columna de acción que permite ver las facturas del servico y otro que permite la baja del servicio a este cliente. Asimismo, debajo de esta tabla vemos un botón **Asignar Servicios**, el cual lo redirige a la pantalla de asignación de servicios a clientes. 
Luego de esta tabla sigue el flujo normal de la pantalla, vemos la misma tabla de clientes para la gestión de servicios y debajo los botones de **Alta**, **Baja** y **Modificacion**, aunque, el botón de alta permanece bloqueado. Desde esta pantalla podemos dar de baja al cliente o modificarlo. 

### Pantalla de Gestión de Clientes - Asignar Servicio
![Pantalla de Gestión de Clientes](../wireframes-y-pantallas/pantallas/Pantalla-gestionClientes-asignServi.jpg)

Esta pantalla permite la asignación de servicios a un cliente. En la parte superior izquierda de la pantalla observamos el botón de navegación **Atrás** que nos lleva a la pantalla anterior. Debajo observamos la información personal del cliente al cual vamos a asignar los servicios. Debajo Observamos dos tablas, primero la tabla de **Servicios contratados**, la cual muestra todos los servicios que el cliente tiene contrados. Observe que es la misma tabla que tenia en la pantalla de detalle anterior. Luego le sigue la tabla de **Servicios del sistema**, esta tabla lista todos los servicios que tiene el sistema y que el cliente no ha contratado aún. Esta tabla muestra el nombre, descripción, precio y % de iva del servicio. En la columna de acciones, tiene el botón **Asignar servicio** para asignarlo, y otro **Gestionar Servicio** que lo redirige a la pantalla de **Gestionar Servicios**

### Pantalla de Gestión de Clientes - Detalle de Cliente
![Pantalla de Gestión de Clientes](../wireframes-y-pantallas/pantallas/Pantalla-gestionClientes-detalle.jpg)

# Backlog de iteraciones

* HU 01 - Alta Cliente
* HU 03 - Asignar servicios a clientes
* HU 04 - Alta de servicio
* HU 07 - Facturación masiva manual
* HU 08 - Facturación individual
* HU 02 - Modificar cliente
* HU 11 - Anulación de factura

# Tareas
lista de q hacer para completas las uc
