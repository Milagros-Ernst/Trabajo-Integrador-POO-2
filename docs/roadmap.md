# Historias de Usuario
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
