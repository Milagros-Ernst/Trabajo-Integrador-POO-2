# Historias de Usuario
## Cliente
## HU 01 - Alta Cliente

Como **administrdor** quiero **registrar nuevos clientes** 
para **habilitar su cuenta** 

Criterios de aceptación
- Campos obligatorios: razon social, nombre, CUIT, DNI, email, telefono, domicilio, condicion IVA, estado de cuenta.
- El sistema debe validar que el CUIT y DNI no este repetido.
- La cuenta se crea asociado a un cliente.
-  El sitema valida que el cliente no este registrado previamente.

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

## Facturacion 
## HU 04 - Definir periodo de facturacion

Como **administrador** 
quiero **seleccionar fechas de inicio y fin** 
para **el periodo mensual a facturar**

Criterios de aceptación
- El periodo debe de ser un mes.
- La fecha de inicio debe de ser menos a la de fin.

## HU 05 - Facturacion masiva manual

Como **administrador** 
quiero **iniciar la facturacion masiva** 
para **todas las cuentas activas**

Criterios de aceptación
- Solo las cuentas con estado activa se deben incluir.
- Se registran fecha, vencimiento, cantidad de facturas, periodo facturado y empleado responsable.

Notas técnicas
- Queda registrado la facturacion masiva.

## HU 06 - Facturacion individual

Como **administrador** 
quiero **emitir una factura** 
para **un cliente y los servicios contratados**

Criterios de aceptación
- Permite seleccionar cliente, periodo y servicios a facturar.
- Se registra fecha de emision, vencimiento y empleado emisor.

Notas técnicas
- Respeta el estado de cuenta: solo Activa genera factura; Suspendida y Baja no.

## HU 07 - Detalle de factura 

Como **administrador** 
quiero **que el sistema genere los items correspondientes a los servicios** 
para **mostrar en la factura los conceptos y montos facturados**

Criterios de aceptación
- Cada servicio contratado del cliente es un item del detalle.
- Si un servicio fue contratado el dia 15 o en adelante, se le cobra un monto proporcional al monto mensual.
- En los items de descuento los importes son negativos y se deben de justificar el motivo.

Notas técnicas
- El proporcional se calcula automaticamente segun dias restantes al mes.
- El descuento se carga manualmente.

## HU 08 - Registro de facturacion masiva

Como **administrador** 
quiero **registrar cada ejecucion de facturacion masiva** 
para **tener un historial de cada proceso.**

Criterios de aceptación
- Permite consutar y filtrar los registros por perirodo o fecha de ejecucion.
- No se puede modificar y eliminar manualmente.

Notas técnicas
- Interfaz con listado y filtros por fecha o periodo.
- Las facturaciones masivas se registran automaticamente al presionar el boton de facturar.