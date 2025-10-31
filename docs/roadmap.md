# Historias de Usuario
## Cliente
## HU 01 - Alta Cliente

Como **administrdor** quiero **registrar nuevos clientes** para **habilitar su cuenta** 

Criterios de aceptación
- Campos obligatorios: razon social, nombre, CUIT, DNI, email, telefono, domicilio, condicion IVA, estado de cuenta.
- El sistema debe validar que el CUIT y DNI no este repetido.
- La cuenta se crea asociado a un cliente.
- El sistema valida que el cliente no este registrado previamente.

Notas técnicas
- La relacion entre cliente y cuenta es 1 a 1.

## HU 02 - Modificar cliente

Como **administrador** quiero **modificar datos del cliente** para **facturar con datos correctos** 

Criterios de aceptación
- Cambios en la condicion de IVA afecta solo a futuras facturas.
- No se puede modificar el DNI o CUIT a uno registrado.
- Puede cambiar el estado de la cuenta a activa, suspendida o baja.

Notas técnicas
- (ver)

## HU 03 - Baja cliente

Como **administrador** quiero **cambiar el estado de cuenta del cliente** para **no generar mas facturas a su nombre**

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

Como **administrador** quiero **seleccionar fechas de inicio y fin** para **el periodo mensual a facturar**

Criterios de aceptación
- El periodo debe de ser un mes.
- La fecha de inicio debe de ser menor a la fecha de fin.

Notas técnicas
- (ver)

## HU 06 - Facturacion masiva manual

Como **administrador** quiero **iniciar la facturacion masiva** para **todas las cuentas activas**

Criterios de aceptación
- Solo incluye cuentas Activas.
- Se registran fecha, vencimiento, cantidad de facturas, periodo facturado y empleado responsable.

Notas técnicas
- Queda registrado la facturacion masiva.
- Mantiene numeración correlativa.

## HU 07 - Facturacion individual

Como **administrador** quiero **emitir una factura** para **un cliente y los servicios contratados**

Criterios de aceptación
- Se debe seleccionar cliente, período y servicios a incluir.
- Si un servicio comenzó a mitad de mes, calcular proporcional
- Registrar fecha de emisión, vencimiento, responsable y condición de IVA vigente del cliente al momento de emitir.
- Facturación unicamente a cuentas Activas.

Notas técnicas
- Mantiene numeración correlativa.

## HU 08 - Detalle de factura 

Como **administrador** quiero **que el sistema genere los items correspondientes a los servicios** para **mostrar en la factura los conceptos y montos facturados**

Criterios de aceptación
- Cada servicio contratado del cliente es un item del detalle.
- Si un servicio fue contratado el dia 15 o en adelante, se le cobra un monto proporcional al monto mensual.
- En los items de descuento los importes son negativos y se deben de justificar el motivo.

Notas técnicas
- El proporcional se calcula según dias restantes al mes.
- El descuento se carga manualmente.

## HU 09 - Registro de facturacion masiva

Como **administrador** quiero **registrar cada ejecucion de facturacion masiva** para **tener un historial de cada proceso.**

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

Notas técnicas
- (ver)

## HU-11 — Anulación de factura

Como **administrador** quiero **anular una factura emitida mediante una nota de crédito** para **revertir completamente la operación y mantener la trazabilidad del proceso.**

Criterios de aceptación
- Solo se deben poder anular facturas emitidas.
- Se debe registrar motivo de anulación y responsable.
- La anulación genera automáticamente una Nota de Crédito total que referencia a la factura original.
- La factura cambia su estado a Anulada y no puede volver a anularse.
- La factura anulada deja de considerarse en el saldo del cliente.
- Se debe poder consultar el vínculo entre la factura anulada y la nota de crédito generada.

Notas técnicas
- (ver)

## Pagos

## HU-12 — Registrar pago total

Como **administrador** quiero **registrar el pago total de una o varias facturas**
para **mantener el estado de cuenta del cliente actualizado**

Criterios de aceptación
- Se debe poder seleccionar el cliente y una o varias facturas pendientes.
- El importe total registrado debe coincidir exactamente con el saldo de las facturas seleccionadas.
- Los datos obligatorios son: fecha del pago, importe total, método de pago y responsable.
- Una vez que se registra el pago las facturas cambian de estado a Pagada.
- El sistema debe impedir registrar un pago total si alguna de las facturas seleccionadas ya se encuentra Pagada o Anulada.

Notas técnicas
- Entidad Pago con relación 1:N a Factura.

## HU-13 — Registrar pago parcial

Como **administrador** quiero **registrar pagos parciales sobre una o varias facturas**
para **reflejar el saldo pendiente real y mantener actualizado el estado de cuenta del cliente.**

Criterios de aceptación
- Se debe poder seleccionar el cliente y una o más facturas impagas o parcialmente pagadas.
- El sistema debe permitir ingresar un importe menor al saldo pendiente de cada factura.
- Se registran los datos obligatorios: fecha, importe abonado, método de pago, responsable y observaciones (opcional).
- El saldo pendiente de cada factura se actualiza automáticamente.
- Si el saldo llega a cero, la factura pasa a estado Pagada; si no, permanece Parcialmente pagada.
- No se calculan intereses ni recargos por mora.
- Los pagos parciales pueden realizarse en diferentes fechas y con distintos métodos de pago.

Notas técnicas
- Recalcular saldo acumulado del cliente.

## HU-23 — Pagos adelantados

Como **administrador** quiero **registrar pagos adelantados** para **generar saldo a favor e imputarlo automáticamente en futuras facturas.**

Criterios de aceptación
- Se selecciona cliente y una o más facturas a pagar (total o parcialmente).
- Visualizar “Saldo a favor” en el estado de la cuenta.
- No se calculan intereses ni recargos por mora.

Notas técnicas
- (ver)

## HU-21 — Pagos con combinación de métodos de pago

Como **administrador** quiero **cargar pagos usando múltiples métodos combinados** para **reflejar la realidad de cobranza.**

Criterios de aceptación
- Métodos efectivo, transferencia, tarjeta, etc. (configurable).
- En una misma operación se pueden registrar varios métodos con sus importes y referencias.
- Persistir responsable y fecha/hora.

Notas técnicas
- Entidad Pago con colección MediosDePago.

## HU-13 — Emisión de recibos de pago

Como **administrador**
quiero **emitir un recibo de pago**
para **dejar constancia de las facturas y servicios abonados por el cliente.**

Criterios de aceptación
- El recibo se puede emitir tanto por pagos totales como parciales de una o más facturas.
- El recibo debe incluir el número de recibo, cliente, fecha, importe total, detalle de facturas pagadas y métodos de pago utilizados.
- Si el pago es parcial, el recibo debe mostrar el saldo pendiente de cada factura.
- Se debe poder consultar y descargar el recibo desde el estado de cuenta del cliente.
- El sistema debe permitir reimprimir o descargar copias del recibo, manteniendo el mismo número y datos originales.

Notas técnicas
- El número de recibo se genera mediante secuencia correlativa independiente de facturas o notas de crédito.
