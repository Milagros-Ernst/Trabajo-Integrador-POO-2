/*

  Este archivo contiene el código para las siguientes páginas:
  - gestion-clientes-inicio.html
  - gestion-clientes-detalle.html
  - gestion-servicio-abm.html
  - facturacion-*.html

 */
document.addEventListener('DOMContentLoaded', () => {
    

    //LÓGICA PARA: gestion-clientes-inicio.html

    const formClienteInicio = document.getElementById('form-cliente');
    const btnAltaCliente = document.getElementById('btn-alta-form');

    if (formClienteInicio && btnAltaCliente) {

        const btnCancelar = document.getElementById('btn-cancelar-edicion');
        const btnBajaCliente = document.getElementById('btn-baja-form');
        const btnModificarCliente = document.getElementById('btn-modificar-form');
        const formFieldset = document.getElementById('form-fieldset');
        const topActions = document.getElementById('form-top-actions');
        const bottomActions = document.getElementById('abm-bottom-actions');

        // Deshabilitar botones Baja y Modificar (no se usan en esta vista)
        if (btnBajaCliente) btnBajaCliente.disabled = true;
        if (btnModificarCliente) btnModificarCliente.disabled = true;

        function habilitarFormularioAlta() {
            formFieldset.disabled = false;
            topActions.style.display = 'flex';
            bottomActions.style.display = 'none';
            formClienteInicio.reset();
            const firstInput = document.getElementById('nombre');
            if (firstInput) {
                firstInput.focus();
            }
        }

        function deshabilitarFormularioAlta() {
            formFieldset.disabled = true;
            topActions.style.display = 'none';
            bottomActions.style.display = 'block';
            formClienteInicio.reset();
        }


        btnAltaCliente.addEventListener('click', habilitarFormularioAlta);

        btnCancelar.addEventListener('click', (e) => {
            e.preventDefault();
            deshabilitarFormularioAlta();
        });

        formClienteInicio.addEventListener('submit', (e) => {
            e.preventDefault();

            const clienteData = {
                nombre: document.getElementById('nombre').value,
                apellido: document.getElementById('apellido').value,
                telefono: document.getElementById('telefono').value,
                mail: document.getElementById('email').value,
                direccion: document.getElementById('direccion').value,
                direccionFiscal: document.getElementById('direccion-fiscal').value,
                condIVA: document.getElementById('condicion-fiscal').value,
                tipoDocumento: document.getElementById('tipo-doc').value,
                numeroDocumento: document.getElementById('nro-doc').value,
                estadoCuenta: 'ACTIVA'
            };

            fetch('/clientes', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(clienteData),
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Error al crear el cliente. Verifique los datos.');
                    }
                })
                .catch(error => {
                    console.error('Error en el fetch:', error);
                    alert('Error de red. No se pudo conectar con el servidor.');
                });
        });
    }

// Lógica para el ABM de servicio y selección de filas
    const formServicios = document.getElementById('form-servicios');

    if (formServicios) {

        const btnAlta = document.getElementById('btn-alta-servicio');
        const btnModificar = document.getElementById('btn-modificar-servicio');
        const btnCancelar = document.getElementById('btn-cancelar');
        const formFieldset = document.getElementById('form-fieldset');
        const topActions = document.getElementById('form-top-actions');
        const bottomActions = document.getElementById('abm-bottom-actions');

        // Variables de estado
        let servicioSeleccionadoId = null;
        let modoEdicion = false;

        // 1. Lógica de Selección de Tabla
        const filas = document.querySelectorAll('.fila-tabla');
        console.log("Filas encontradas:", filas.length);

        filas.forEach(fila => {
            fila.addEventListener('click', () => {
                filas.forEach(f => f.classList.remove('fila-seleccionada'));
                fila.classList.add('fila-seleccionada');
                servicioSeleccionadoId = fila.getAttribute('data-id');
                console.log("Fila seleccionada ID:", servicioSeleccionadoId);
            });
        });

        // Funciones de habilitar/deshabilitar
        function habilitarFormularioServicio() {
            if (formFieldset) formFieldset.disabled = false;
            if (topActions) topActions.style.display = 'flex';
            if (bottomActions) bottomActions.style.display = 'none';
            const firstInput = formFieldset?.querySelector('input');
            if (firstInput) firstInput.focus();
        }

        function deshabilitarFormularioServicio() {
            if (formFieldset) formFieldset.disabled = true;
            if (topActions) topActions.style.display = 'none';
            if (bottomActions) bottomActions.style.display = 'block';
            formServicios.reset();
            modoEdicion = false;
            servicioSeleccionadoId = null;
            filas.forEach(f => f.classList.remove('fila-seleccionada'));
        }

        // Evento Alta
        if (btnAlta) {
            btnAlta.addEventListener('click', () => {
                modoEdicion = false;
                formServicios.reset();
                habilitarFormularioServicio();
            });
        }

        // Evento Modificar
        if (btnModificar) {
            btnModificar.addEventListener('click', () => {
                if (!servicioSeleccionadoId) {
                    alert("Por favor, selecciona un servicio de la tabla primero.");
                    return;
                }

                const filaActiva = document.querySelector(`.fila-tabla[data-id="${servicioSeleccionadoId}"]`);

                if (filaActiva) {
                    document.getElementById('nombre').value = filaActiva.getAttribute('data-nombre');
                    document.getElementById('descripcion').value = filaActiva.getAttribute('data-descripcion');
                    document.getElementById('precio-base').value = filaActiva.getAttribute('data-precio');
                    document.getElementById('iva').value = filaActiva.getAttribute('data-iva');

                    modoEdicion = true;
                    habilitarFormularioServicio();
                }
            });
        }

        // Evento Baja
        const btnBaja = document.getElementById('btn-baja');

        if (btnBaja) {
            btnBaja.addEventListener('click', () => {
                if (!servicioSeleccionadoId) {
                    alert("Por favor, selecciona un servicio de la tabla primero.");
                    return;
                }

                const confirmar = confirm("¿Estás seguro de que deseas dar de baja este servicio?");

                if (confirmar) {
                    fetch(`/servicios/${servicioSeleccionadoId}`, {
                        method: 'DELETE',
                        headers: { 'Content-Type': 'application/json' }
                    })
                        .then(response => {
                            if (response.ok) {
                                alert('Servicio dado de baja con éxito');
                                location.reload();
                            } else {
                                return response.json().then(data => {
                                    throw new Error(data.error || 'Error al dar de baja el servicio');
                                });
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('Error: ' + error.message);
                        });
                }
            });
        }

        // Evento Cancelar
        if (btnCancelar) {
            btnCancelar.addEventListener('click', (e) => {
                e.preventDefault();
                deshabilitarFormularioServicio();
            });
        }

        // Evento Submit/confirmar
        formServicios.addEventListener('submit', (e) => {
            e.preventDefault();

            const servicioData = {
                nombre: document.getElementById('nombre').value,
                descripcion: document.getElementById('descripcion').value,
                precioUnitario: document.getElementById('precio-base').value,
                tipoIva: document.getElementById('iva').value,
                estadoServicio: 'ALTA'
            };

            let url = '/servicios';
            let method = 'POST';

            if (modoEdicion && servicioSeleccionadoId) {
                url = `/servicios/${servicioSeleccionadoId}`;
                method = 'PUT';
            }

            fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(servicioData),
            })
                .then(response => {
                    if (response.ok) {
                        alert(modoEdicion ? 'Servicio modificado con éxito' : 'Servicio creado con éxito');
                        location.reload();
                    } else {
                        alert('Error al procesar la solicitud. Verifique los datos.');
                    }
                })
                .catch(error => {
                    console.error('Error en el fetch:', error);
                    alert('Error de red.');
                });
        });
    }


    // LÓGICA PARA: gestion-clientes-detalle.html

    const formClienteDetalle = document.getElementById('form-cliente');
    const btnModificar = document.getElementById('btn-modificar-form');
    const btnAltaDetalle = document.getElementById('btn-alta-form');

    if (formClienteDetalle && btnModificar && !btnAltaDetalle) {

        const btnCancelar = document.getElementById('btn-cancelar-edicion');
        const topActions = document.getElementById('form-top-actions');
        const bottomActions = document.getElementById('abm-bottom-actions');
        const formContainer = document.getElementById('form-inputs-container');
        const formFields = formContainer.querySelectorAll('input, select');

        const originalValues = new Map();
        formFields.forEach(field => {
            if (field.tagName === 'SELECT') {
                originalValues.set(field, field.selectedIndex);
            } else {
                originalValues.set(field, field.value);
            }
        });

        function habilitarFormularioModif() {
            formFields.forEach(field => {
                field.readOnly = false;
                field.disabled = false;
            });
            formContainer.classList.add('form-inputs-active');
            topActions.style.display = 'flex';
            bottomActions.style.display = 'none';
        }

        function deshabilitarFormularioModif() {
            formFields.forEach(field => {
                if (field.tagName === 'SELECT') {
                    field.selectedIndex = originalValues.get(field);
                } else {
                    field.value = originalValues.get(field);
                }
                field.readOnly = true;
                if (field.tagName === 'SELECT') {
                    field.disabled = true;
                }
            });
            formContainer.classList.remove('form-inputs-active');
            topActions.style.display = 'none';
            bottomActions.style.display = 'block';
        }

        // Evento Modificar Cliente
        btnModificar.addEventListener('click', habilitarFormularioModif);

        // Evento Baja Cliente
        const btnBajaCliente = document.getElementById('btn-baja-cliente');

        if (btnBajaCliente) {
            btnBajaCliente.addEventListener('click', () => {
                const urlParts = window.location.pathname.split('/');
                const clienteId = urlParts[urlParts.length - 1];

                if (!clienteId) {
                    alert('Error: No se pudo determinar el ID del cliente.');
                    return;
                }

                const confirmar = confirm("¿Estás seguro de que deseas dar de baja este cliente?");

                if (confirmar) {
                    fetch(`/clientes/${clienteId}`, {
                        method: 'DELETE',
                        headers: { 'Content-Type': 'application/json' }
                    })
                        .then(response => {
                            if (response.ok) {
                                alert('Cliente dado de baja con éxito');
                                window.location.href = '/clientes';  // Redirige a la lista
                            } else {
                                return response.text().then(text => {
                                    throw new Error(text || 'Error al dar de baja');
                                });
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('Error: ' + error.message);
                        });
                }
            });
        }

        btnCancelar.addEventListener('click', (e) => {
            e.preventDefault();
            deshabilitarFormularioModif();
        });

        formClienteDetalle.addEventListener('submit', (e) => {
            e.preventDefault();

            const urlParts = window.location.pathname.split('/');
            const clienteId = urlParts[urlParts.length - 1];

            if (!clienteId) {
                alert('Error: No se pudo determinar el ID del cliente desde la URL.');
                return;
            }

            const nombreEl = document.getElementById('nombre');
            const apellidoEl = document.getElementById('apellido');
            const telefonoEl = document.getElementById('telefono');
            const mailEl = document.getElementById('mail');
            const direccionEl = document.getElementById('direccion');
            const direccionFiscalEl = document.getElementById('direccionFiscal');
            const condIVAEl = document.getElementById('condIVA');
            const tipoDocumentoEl = document.getElementById('tipoDocumento');
            const numeroDocumentoEl = document.getElementById('numeroDocumento');

            const clienteData = {
                nombre: nombreEl?.value || '',
                apellido: apellidoEl?.value || '',
                telefono: telefonoEl?.value || '',
                mail: mailEl?.value || '',
                direccion: direccionEl?.value || '',
                direccionFiscal: direccionFiscalEl?.value || '',
                condIVA: condIVAEl?.value || '',
                tipoDocumento: tipoDocumentoEl?.value || '',
                numeroDocumento: numeroDocumentoEl?.value || ''
            };

            fetch(`/clientes/${clienteId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(clienteData),
            })
                .then(response => {
                    if (response.ok) {
                        alert('Cliente modificado con éxito');
                        location.reload();
                    } else {
                        throw new Error('Error al modificar el cliente');
                    }
                })
                .catch(error => {
                    alert(error.message);
                });
        });

        formContainer.querySelectorAll('select').forEach(select => {
            select.disabled = true;
        });
    }

    // lógica facturación que le agregué para mas felicidad

    const selectAllCheckbox = document.getElementById('seleccionar-todo');

    if (selectAllCheckbox) {
        const serviceCheckboxes = document.querySelectorAll('.checkbox-servicio');
        const facturacionForm = document.getElementById('facturacion-form-group');

        selectAllCheckbox.addEventListener('click', function() {
            serviceCheckboxes.forEach(checkbox => {
                checkbox.checked = this.checked;
            });
        });

        serviceCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('click', function() {
                const allChecked = Array.from(serviceCheckboxes).every(cb => cb.checked);
                selectAllCheckbox.checked = allChecked;
            });
        });

        if (facturacionForm) {
            facturacionForm.addEventListener('submit', function(e) {
                const checkedCount = document.querySelectorAll('.checkbox-servicio:checked').length;

                if (checkedCount === 0) {
                    e.preventDefault();
                    alert('Debe seleccionar al menos un servicio para facturar.');
                }
            });
        }
    }
});




