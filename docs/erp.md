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
## Cliente
## HU 01 - Alta Cliente

Como **administrdor** quiero **registrar nuevos clientes**
para **habilitar su cuenta**

Criterios de aceptación
- Campos obligatorios: razon social, nombre, CUIT, DNI, email, telefono, domicilio, condicion IVA, estado de cuenta.
- El sistema debe validar que el CUIT y DNI no este repetido.
- La cuenta se crea asociado a un cliente.
-  El sistema valida que el cliente no este registrado previamente.

Notas técnicas
- La relacion entre cliente y cuenta es 1 a 1.

## HU 02 - Modificar cliente

Como **administrador**
quiero **modificar datos del cliente**
para **facturar con datos correctos**

Criterios de aceptación
- Cambios en la condicion de IVA afecta solo a futuras facturas.
- No se puede modificar el DNI o CUIT a uno registrado.
- Puede cambiar el estado de la cuenta a activa, suspendida o baja.

## HU 03 - Baja cliente

Como **administrador**
quiero **cambiar el estado de cuenta del cliente**
para **no generar mas facturas a su nombre**

Criterios de aceptación
- Filtros por nombre, CUIT/DNI, condición de IVA y estado (Activa, Suspendida, Baja).
- Los estados de cuentas activas o suspendidas pueden cambiar a baja.

Notas técnicas
- Actualizacion del estado afecta la logica de facturacion.

## HU-04 — Asignar servicios a cliente

Como **administrador** quiero **asignar/quitar servicios a un cliente** para **usarlos en la facturación.**

Criterios de aceptación
- Listar servicios disponibles y los contratados por el cliente.
- Guardar concepto del servicio e importe.
- Si el alta del servicio es a mitad de mes, el sistema marca que puede facturarse proporcional.

Notas técnicas
- Relación cliente–servicios (N:M) con vigencias (fecha desde/hasta).

## Facturacion
## HU 05 - Definir periodo de facturacion

Como **administrador**
quiero **seleccionar fechas de inicio y fin**
para **el periodo mensual a facturar**

Criterios de aceptación
- El periodo debe de ser un mes.
- La fecha de inicio debe de ser menor a la fecha de fin.

## HU 06 - Facturacion masiva manual

Como **administrador**
quiero **iniciar la facturacion masiva**
para **todas las cuentas activas**

Criterios de aceptación
- Solo incluye cuentas Activas.
- Se registran fecha, vencimiento, cantidad de facturas, periodo facturado y empleado responsable.

Notas técnicas
- Queda registrado la facturacion masiva.
- Mantiene numeración correlativa.

## HU 07 - Facturacion individual

Como **administrador**
quiero **emitir una factura**
para **un cliente y los servicios contratados**

Criterios de aceptación
- Se debe seleccionar cliente, período y servicios a incluir.
- Si un servicio comenzó a mitad de mes, calcular proporcional
- Registrar fecha de emisión, vencimiento, responsable y condición de IVA vigente del cliente al momento de emitir.
- Facturación unicamente a cuentas Activas.

Notas técnicas
- Mantiene numeración correlativa.

## HU 08 - Detalle de factura

Como **administrador**
quiero **que el sistema genere los items correspondientes a los servicios**
para **mostrar en la factura los conceptos y montos facturados**

Criterios de aceptación
- Cada servicio contratado del cliente es un item del detalle.
- Si un servicio fue contratado el dia 15 o en adelante, se le cobra un monto proporcional al monto mensual.
- En los items de descuento los importes son negativos y se deben de justificar el motivo.

Notas técnicas
- El proporcional se calcula según dias restantes al mes.
- El descuento se carga manualmente.

## HU 09 - Registro de facturacion masiva

Como **administrador**
quiero **registrar cada ejecucion de facturacion masiva**
para **tener un historial de cada proceso.**

Criterios de aceptación
- Permitir consultar/filtrar por período o fecha de ejecución.
- No se puede modificar y eliminar manualmente.

Notas técnicas
- Interfaz con listado y filtros por fecha o periodo.
- Las facturaciones masivas se registran automaticamente al presionar el boton de facturar.

## HU-10 — Ver estado de cuenta del cliente

Como **administrador** quiero **ver el estado de cuenta** para **conocer facturas emitidas, pagadas e impagas.**

Criterios de aceptación
- Listar facturas por estado: Vigente / Vencida / Anulada / Pagada / Parcialmente pagada.
- El sistema debe mostrar el saldo actual del cliente, calculado como la suma total de las facturas emitidas no anuladas.

## HU-11 — Anulación de factura
Como **administrador**
quiero **anular una factura emitida mediante una nota de crédito**
para **revertir completamente la operación y mantener la trazabilidad del proceso.**

Criterios de aceptación
- Solo se deben poder anular facturas emitidas.
- Se debe registrar motivo de anulación y responsable.
- La anulación genera automáticamente una Nota de Crédito total que referencia a la factura original.
- La factura cambia su estado a Anulada y no puede volver a anularse.
- La factura anulada deja de considerarse en el saldo del cliente.
- Se debe poder consultar el vínculo entre la factura anulada y la nota de crédito generada.

## Pagos

## HU-12 — Registrar Pagos
Como **administrador**
quiero **registrar el pago total o parcial de una o más facturas**
para **mantener actualizado el estado de cuenta del cliente.**

Criterios de aceptación
- Se debe poder seleccionar el cliente y una o varias facturas pendientes.
- El sistema debe permitir registrar pago total o parcial sobre cada factura.
- Se deben registrar los siguientes datos: fecha del pago, importe, método de pago y responsable.
- Si el pago cubre el total de la factura, esta pasa a Pagada; si no, a Parcialmente pagada.
- El saldo pendiente de la factura se actualiza automáticamente luego del registro.
- No se calculan intereses ni recargos por mora.

Notas técnicas
- Cada pago se almacena en una entidad Pago vinculada a las facturas afectadas.

## HU-13 — Emisión de recibos
Como **administrador**
quiero **emitir un recibo de pago**
para **dejar constancia de las facturas y servicios abonados por el cliente.**

Criterios de aceptación
- El recibo debe incluir el número de recibo, cliente, fecha, importe total, detalle de facturas pagadas y métodos de pago utilizados.
- Se debe poder consultar y descargar el recibo desde el estado de cuenta del cliente.
- La numeración de recibos debe ser correlativa e independiente de facturas o notas de crédito.

Notas técnicas
- Cada recibo corresponde a uno o varios pagos efectuados en una misma operación.

## Arquitectura de software

La solución será una **Aplicación Web**, diseñada bajo una arquitectura **Cliente-Servidor**.

* El **Servidor (backend)** centralizará toda la lógica de negocio y el acceso a datos. El **Cliente (frontend)** será la interfaz de usuario renderizada en el navegador web del usuario.
* Se utilizará el lenguaje **Java** con el framework Spring Boot.
* Se empleará una **Base de Datos** en PostgreSQL para garantizar la integridad y persistencia de los datos.



