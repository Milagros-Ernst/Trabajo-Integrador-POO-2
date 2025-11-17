/*

  Este archivo contiene el código para las siguientes páginas:
  - gestion-clientes-inicio.html
  - gestion-clientes-detalle.html
  - gestion-servicio-abm.html

 */
document.addEventListener('DOMContentLoaded', () => {
    

    //LÓGICA PARA: gestion-clientes-inicio.html

    const formClienteInicio = document.getElementById('form-cliente');
    const btnAltaCliente = document.getElementById('btn-alta-form');

    if (formClienteInicio && btnAltaCliente) {

        const btnCancelar = document.getElementById('btn-cancelar-edicion');
        const formFieldset = document.getElementById('form-fieldset');
        const topActions = document.getElementById('form-top-actions');
        const bottomActions = document.getElementById('abm-bottom-actions');

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

            fetch('/api/clientes', {
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


    //LÓGICA PARA: gestion-servicio-abm.html

    const formServicios = document.getElementById('form-servicios');

    if (formServicios) {

        const btnAlta = document.getElementById('btn-alta-form');
        const btnCancelar = document.getElementById('btn-cancelar');
        const formFieldset = document.getElementById('form-fieldset');
        const topActions = document.getElementById('form-top-actions');
        const bottomActions = document.getElementById('abm-bottom-actions');


        function habilitarFormularioServicio() {
            formFieldset.disabled = false;
            topActions.style.display = 'flex';
            bottomActions.style.display = 'none';
            formServicios.reset();
            const firstInput = formFieldset.querySelector('input');
            if (firstInput) {
                firstInput.focus();
            }
        }

        function deshabilitarFormularioServicio() {
            formFieldset.disabled = true;
            topActions.style.display = 'none';
            bottomActions.style.display = 'block';
            formServicios.reset();
        }

        btnAlta.addEventListener('click', habilitarFormularioServicio);

        btnCancelar.addEventListener('click', (e) => {
            e.preventDefault();
            deshabilitarFormularioServicio();
        });

        formServicios.addEventListener('submit', (e) => {
            e.preventDefault();

            const servicioData = {
                nombre: document.getElementById('nombre').value,
                descripcion: document.getElementById('descripcion').value,
                precioUnitario: document.getElementById('precio-base').value,
                tipoIva: document.getElementById('iva').value,
                estadoServicio: 'ALTA'
            };

            fetch('/api/servicios', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(servicioData),
            })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Error al crear el servicio. Verifique los datos.');
                    }
                })
                .catch(error => {
                    console.error('Error en el fetch:', error);
                    alert('Error de red. No se pudo conectar con el servidor.');
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

        btnModificar.addEventListener('click', habilitarFormularioModif);

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

            fetch(`/api/clientes/${clienteId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(clienteData),
            })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        return response.text().then(text => {
                            throw new Error(text || 'Error al modificar el cliente');
                        });
                    }
                })
                .then(data => {
                    location.reload();
                })
                .catch(error => {
                    alert('Error al modificar el cliente: ' + error.message);
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

