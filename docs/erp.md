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

Escriban algunas oraciones que describan cómo la solución de software resolverá el problema descrito anteriormente.

## Requisitos

Enumeren los requisitos en formato de historias de usuarios. Piensen que tipos de requisitos son.

## Arquitectura de software

¿Será esta una aplicación web / de escritorio / móvil *(todas o algún otro tipo)*? ¿Se ajustaría a la arquitectura de software Cliente-Servidor? ¿Qué lenguajes de programación, frameworks, bases de datos,... se utilizarán para desarrollar e implementar el software?



## Requerimientos funcionales

### Gestión de cuentas
El sistema deberá permitir la gestión de cuentas, deberá contar con:
* Alta de cuentas
* Baja de cuentas
* Modificación de cuentas

Existe una cuenta por cliente, en donde se asocian los servicios del mismo. Una cuenta tiene condición de IVA y en base a esta se realiza la facturación correspondiente.
Es importante el estado de la cuenta, teniendo en cuenta si esta adeuda algún servicio.

### Gestión de servicios
Para la gestión de servicios, el sistema debe contar con un ABM de servicios. Agregar precios. Servicio clásico, servicio Premium.

Debe permitir suspender o renaudar el servicio dependiendo del estado de la cuenta, si esta adeuda más de ...?

### Facturación
El sistema deberá permitir la facturación de servicios de cada cuenta según su condición de IVA. 
Deberá tener módulos de facturación masiva como facturación individual.
También debe facturar individualmente
