## HU-01


Como **empleado de facturacion** 
quiero **registrar nuevos clientes** 
para **emitir facturas** 



## Criterios de aceptación

- [ ] El empleado debe poder acceder al sistema y al ABM de clientes.
- [ ] El sistema debe validar que el CUIT no este repetido.
- [ ] Todos los campos obligatorios deben completarse para guardar al cliente.
- [ ] El sitema valida que el cliente no este registrado previamente.

## Notas técnicas

- Validar el formato del CUIT, 11 digitos numericos.
- Guardar los datos de forma persistente.

## HU-02


Como **empleado de facturacion** 
quiero **emitir una factura individual** 
para **registrar la venta de un servicio** 



## Criterios de aceptación

- [ ] Se debe de seleccionar el cliente, el periodo y los items de servicio.
- [ ] El sistema debe calcular automaticamente el IVA segun la condicion fiscal.
- [ ] Debe poder imprimirse o exportarse a PDF..

## Notas técnicas

- Se debe de generar un numero de factura unico.

