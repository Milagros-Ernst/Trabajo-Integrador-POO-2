# Especificación de requisitos de software

## Enunciado del problema

Nuestro cliente actualmente gestiona su facturación de servicios de forma manual, por lo que carece de capacidad para manejar eficientemente la complejidad de la legislación fiscal Argentina (IVA) y automatizar las tareas de facturación recurrentes.

La falta de un sistema integrado le genera tres problemas principales:
1) **Riesgo de Incumplimento Fiscal:** La gestión manual de las diferentes condiciones fiscales de los clientes es propensa a errores en el cálculo de impuestos y en la generación de facturas, exponiendo a nuestro cliente a posibles sanciones y retrabajos contables.
2) **Ineficiencia operativa:** El proceso de **facturación masiva por periodo** es una tarea manual, lenta y laboriosa, que consume una cantidad significativa de horas de trabajo para el personal. Esto retrasa el envío de facturas a los clientes, impactando negativamente en el flujo de caja.
3) **Falta de Trazabilidad y Control Financiero:** No existe un registro centralizado y fiable del estado de cuenta de los clientes. 
El seguimiento de pagos, la aplicación de notas de crédito y la conciliación de saldos son procesos difíciles de auditar cuando se realizan manualmente. Esto impide tener una visión clara y en tiempo real de la **deuda** y **cobranza**.

El sistema actual es incapaz de escalar y adaptare a las necesidades del negocio sin incrementar exponencialmente los errores y costos operativos. 

## Clientes potenciales

El actor principal afectado por este problema es la **empresa prestadora de servicios**; tanto el dueño como sus empleados. 

Los **empleados** son los encargados de realizar la facturación y el seguimiento de cuentas de los clientes. Son los principales afectados por la ineficiencia operativa del sistema actual.

El **dueño** de la empresa, por otra parte, es el principal afectado a nivel estratégico. 
La falta de trazabilidad y control financiero hace que se dificulte tener un seguimiento claro de las deudas y cobranzas de su empresa. 
Otro problema que afecta a este actor es el riesgo de incumplimiento fiscal, ya que como responsable legal de la empresa, un error de facturación puede llevarlo a pagar costosas multas o sanciones por parte de ARCA, 
como también honorarios extras a contadores por retrabajos contables. 

Un beneficiario indirecto es el **cliente final** de la empresa. Aunque este no usará el sistema, también se ve afectado por los problemas actuales: recibe facturas con demoras, posiblemente con errores, o debe esperar respuestas lentas a sus consultas.
 
## Solución propuesta
La solución propuesta es un sistema de facturación diseñado para gestionar la emisión de comprobantes de servicios, ajustándose automáticamente a la condición fiscal (IVA) de cada cliente.

El sistema permitirá administrar de forma centralizada tanto el **catálogo de servicios** (permitiendo agregar, modificar y eliminar prestaciones) como la **gestión completa de las cuentas de clientes**.
Esta gestión centralizada garantizará la **trazabilidad total** de los pagos, las deudas pendientes y el historial fiscal, simplificando radicalmente el seguimiento financiero.

El núcleo funcional del sistema será su motor de facturación, el cual permitirá al cliente realizar una **facturación masiva por período** para todas las cuentas activas. Asímismo, ofrecerá la flexibilidad de emitir **facturaciones individuales** cuando sea necesario.

## Requisitos

Los requisitos del sistema enumerados en historias de usuario son los siguientes:

### HU 01 - Alta Cliente

#### Descripción
Como **Administrador** quiero **registrar nuevos clientes** para **habilitar su cuenta.**

#### Criterios de aceptación
- Campos obligatorios: razon social, nombre, CUIT, DNI, email, telefono, domicilio, condicion IVA, estado de cuenta.
- El sistema debe validar que el CUIT y DNI no este repetido.


### HU 02 - Modificar Cliente

#### Descripción
Como **Administrador** quiero **modificar datos del cliente** para **facturar con datos correctos.**

#### Criterios de aceptación
-  Cambios en la condicion de IVA afecta solo a futuras facturas.
- No se puede modificar el DNI o CUIT a uno registrado.
- Puede cambiar el estado de la cuenta a activa o inactiva.

#### Notas técnicas
El sistema debe validar los permisos del usuario


### HU 03 - Asignar servicios a cliente

#### Descripción
Como **Administrador** quiero **modificar el estado del servicio a un cliente** para **usarlos en la facturación.**

#### Criterios de aceptación
- El sistema lista los servicios disponibles y los contratados por el cliente.
- Si se da de baja un servicio, se excluye de la facturación.
- Si el alta del servicio es a mitad de mes, el sistema marca que puede facturarse proporcional.

#### Notas técnicas
Relación cliente–servicios (N:M)


### HU 04 - Alta de servicio

#### Descripción
Como **Administrador** quiero **crear un nuevo servicio con nombre, descripción, precio y tipo de IVA** para **poder usarlo al momento de generar una factura.**

#### Descripción
- La opción "Nuevo Servicio" debe estar accesible desde la página principal de "Gestión de Servicios".
- El formulario debe solicitar: Nombre, Descripción, Precio Unitario y Tipo de IVA (ej. 21%, 10.5%, 0%).
- Al hacer clic en "Guardar", el sistema debe validar los datos.
- Si los datos son válidos, el servicio se crea y se redirige a la lista de servicios, mostrando el nuevo ítem.
- Si los datos no son válidos, se deben mostrar mensajes de error claros junto a los campos correspondientes (ej. "El nombre no puede estar vacío").

#### Notas técnicas
- Se debe usar la anotación Valid en el controlador para disparar la validación del objeto Servicio.
- La entidad Servicio debe tener las siguientes anotaciones de validación:
- nombre: NotBlank (no puede ser nulo ni solo espacios en blanco).
- precioUnitario: NotNull y PositiveOrZero (el precio puede ser 0 o mayor).
- tipoIva: NotNull (debe seleccionar una opción).
- El endpoint debe ser POST /servicios, siguiendo la convención de la API.


### HU 05 - Modificación de servicio

#### Descripción
Como **Administrador** quiero **modificar un servicio existente** para **actualizar su precio, descripción o tipo de IVA.**

#### Criterios de aceptación
- La opción "Editar" debe estar disponible en la lista de servicios, junto a cada ítem.
- Al hacer clic en "Editar", se debe navegar a una página con el formulario de edición, mostrando los datos actuales del servicio.
- El formulario de edición es el mismo que el de creación, pero con los campos pre-cargados.
- Al hacer clic en "Guardar", se deben validar los datos y, si son correctos, actualizar el servicio en la base de datos.
- Tras guardar, se debe redirigir a la lista de servicios donde se verán los cambios reflejados.

#### Notas técnicas
- El endpoint debe ser PUT /servicios/{id}, siguiendo la convención de la API.
- Se deben aplicar las mismas anotaciones de validación (NotBlank , PositiveOrZero ) que en la creación del servicio.
- La página de edición podría reutilizar la misma plantilla HTML que la de "Nuevo Servicio" (ej. actualizarServicio.html basado en actualizarPersona.html).


### HU 06 - Baja lógica de servicio

#### Descripción
Como **Administrador** quiero **desactivar un servicio obsoleto** para **que no aparezca como opción al facturar, pero sin borrar el historial.**

#### Criterios de aceptación
- La opción "Desactivar" debe estar disponible en la lista de servicios (en lugar de un borrado físico).
- Al hacer clic en "Desactivar", el sistema debe solicitar una confirmación (ej. "¡Está seguro?").
- Si se confirma, el servicio debe marcarse como "inactivo" y ya no debe aparecer en la lista principal de servicios.
- El servicio desactivado NO debe aparecer en el selector de ítems al crear una nueva factura.
- La lista de servicios debe tener un filtro o switch para "Mostrar servicios inactivos". (opcional)

#### Notas técnicas
- Esto implementa una baja lógica (soft delete). Se requiere añadir un campo boolean activo a la entidad Servicio.
- El endpoint DELETE /servicios/{id} no borrará el registro (DELETE de SQL), sino que ejecutará una actualización (UPDATE) para setear activo = false.
- Las consultas para listar servicios (para facturar o para la lista principal del ABM) deberán filtrar por WHERE activo = true.


### HU 07 - Facturacion masiva manual

#### Descripción
Como **Administrador** quiero **iniciar la facturacion masiva** para **todas las cuentas activas.**

#### Criterios de aceptación
- Solo incluye cuentas Activas.
- Se registran fecha, vencimiento, cantidad de facturas, periodo facturado y empleado responsable.
- Debe generar un log a modo de historial de las veces que se realizó una facturación masiva.
- Debe poder seleccionarse un periodo mensual de facturación o facturar en el instante.
- En la factura estan detallados todos los conceptos correspondientes.

#### Notas técnicas
- Queda registrado la facturacion masiva.
- Mantiene numeración correlativa.


### HU 08 - Facturacion individual

#### Descripción
Como **Administrador** quiero **emitir una factura** para **un cliente y los servicios contratados.**

#### Criterios de aceptación

- Se debe seleccionar cliente, período y servicios a incluir.
- Si un servicio comenzó a mitad de mes, calcular proporcional
- Registrar fecha de emisión, vencimiento, responsable y condición de IVA vigente del cliente al momento de emitir.
- Facturación unicamente a cuentas Activas.
- En la factura estan detallados todos los conceptos correspondientes.

#### Notas técnicas
- Mantiene numeración correlativa.


### HU 09 - Ver estado de cuenta del cliente

#### Descripción
Como **administrador** quiero **ver el estado de cuenta** para **conocer facturas emitidas, pagadas e impagas.**

#### Criterios de aceptación
- Listar facturas por estado: Vigente / Vencida / Anulada / Pagada / Parcialmente pagada.
- El sistema debe mostrar el saldo actual del cliente, calculado como la suma total de las facturas emitidas no anuladas.


### HU 10 - Emisión de recibos de pago

#### Descripción
Como **Administrador** quiero **emitir un recibo de pago** para **dejar constancia de las facturas y servicios abonados por el cliente.**

#### Criterios de aceptación
- El recibo se puede emitir tanto por pagos totales como parciales de una o más facturas.
- El recibo debe incluir el número de recibo, cliente, fecha, importe total, detalle de facturas pagadas y métodos de pago utilizados.
- Si el pago es parcial, el recibo debe mostrar el saldo pendiente de cada factura.
- Se debe poder consultar y descargar el recibo desde el estado de cuenta del cliente.
- El sistema debe permitir reimprimir o descargar copias del recibo, manteniendo el mismo número y datos originales.

#### Notas técnicas
- El número de recibo se genera mediante secuencia correlativa independiente de facturas o notas de crédito.


### HU 11 - Anulación de factura

#### Descripción
Como **Administrador** quiero **anular una factura emitida mediante una nota de crédito** para **revertir completamente la operación y mantener la trazabilidad del proceso.**

#### Criterios de aceptación
- Solo se deben poder anular facturas emitidas.
- Se debe registrar motivo de anulación y responsable.
- La anulación genera automáticamente una Nota de Crédito total que referencia a la factura original.
- La factura cambia su estado a Anulada y no puede volver a anularse.
- La factura anulada deja de considerarse en el saldo del cliente.
- Se debe poder consultar el vínculo entre la factura anulada y la nota de crédito generada.


### HU 12 - Registrar pago total

#### Descripción
Como **Administrador** quiero **registrar el pago total de una o varias facturas** para **mantener el estado de cuenta del cliente actualizado.**

#### Criterios de aceptación
- Se debe poder seleccionar el cliente y una o varias facturas pendientes.
- El importe total registrado debe coincidir exactamente con el saldo de las facturas seleccionadas.
- Los datos obligatorios son: fecha del pago, importe total, método de pago y responsable.
- Una vez que se registra el pago las facturas cambian de estado a Pagada.
- El sistema debe impedir registrar un pago total si alguna de las facturas seleccionadas ya se encuentra Pagada o Anulada.

#### Notas técnicas
- Entidad Pago con relación 1:1 a Factura.


### HU 13 - Registrar pago parcial

#### Descripción
Como **Administrador** quiero **registrar pagos parciales sobre una o varias facturas** para **reflejar el saldo pendiente real y mantener actualizado el estado de cuenta del cliente.**

#### Criterios de aceptación
- Se debe poder seleccionar el cliente y una o más facturas impagas o parcialmente pagadas.
- El sistema debe permitir ingresar un importe menor al saldo pendiente de cada factura.
- Se registran los datos obligatorios: fecha, importe abonado, método de pago, responsable y observaciones (opcional).
- El saldo pendiente de cada factura se actualiza automáticamente.
- Si el saldo llega a cero, la factura pasa a estado Pagada; si no, permanece Parcialmente pagada.
- No se calculan intereses ni recargos por mora.
- Los pagos parciales pueden realizarse en diferentes fechas y con distintos métodos de pago.

#### Notas técnicas
- Recalcular saldo acumulado del cliente.


### HU 14 - Informe de historial de facturacion masiva

#### Descripción
Como **Administrador** quiero **ver las facturas afectadas por la facturación masiva** para **saber a quienes y qué se facturó.**

#### Criterios de aceptación
- El sistema permite consultar/filtrar por período o fecha de ejecución.
- Las facturas afectadas se incorporan al historial automáticamente al facturar.

#### Notas técnicas
- Interfaz con listado y filtros por fecha o periodo.
- Las facturaciones masivas se registran automaticamente en el historial al presionar el boton de facturar.


## Arquitectura de software

La solución será una **Aplicación Web**, diseñada bajo una arquitectura **Cliente-Servidor**.

* El **Servidor (backend)** centralizará toda la lógica de negocio y el acceso a datos. El **Cliente (frontend)** será la interfaz de usuario renderizada en el navegador web del usuario.
* Se utilizará el lenguaje **Java** con el framework Spring Boot.
* Se empleará una **Base de Datos** en PostgreSQL para garantizar la integridad y persistencia de los datos.



