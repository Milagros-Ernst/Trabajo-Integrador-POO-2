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
        deshabilitarFormularioAlta();

        btnAltaCliente.addEventListener('click', habilitarFormularioAlta);

        btnCancelar.addEventListener('click', (e) => {
            e.preventDefault();
            deshabilitarFormularioAlta();
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
        const btnBaja= document.getElementById('btn-baja');

        // Variables de estado
        let servicioSeleccionadoId = null;

        // 1. Lógica de Selección de Tabla
        const filas = document.querySelectorAll('.fila-tabla');
        console.log("Filas encontradas:", filas.length);

        filas.forEach(fila => {
            fila.addEventListener('click', () => {
                // A. Reseteo visual de selección
                filas.forEach(f => f.classList.remove('fila-seleccionada'));
                fila.classList.add('fila-seleccionada');

                servicioSeleccionadoId = fila.getAttribute('data-id');
                console.log("Fila seleccionada ID:", servicioSeleccionadoId);

                document.getElementById('nombre').value = fila.getAttribute('data-nombre');
                document.getElementById('descripcion').value = fila.getAttribute('data-descripcion');
                document.getElementById('precio-base').value = fila.getAttribute('data-precio');

                const ivaSelect = document.getElementById('iva');
                const ivaValor = fila.getAttribute('data-iva');
                if (ivaValor) {
                    ivaSelect.value = ivaValor;
                }
            });
        });

        const urlParams = new URLSearchParams(window.location.search);
        const idEditar = urlParams.get('idEditar');

        if (idEditar) {
            // Buscamos la fila que tiene ese ID
            const filaAEditar = document.querySelector(`.fila-tabla[data-id="${idEditar}"]`);

            if (filaAEditar) {
                // 1. Simulamos clic en la fila para cargar los datos en los inputs
                filaAEditar.click();

                // 2. Simulamos clic en el botón Modificar para habilitar el formulario
                if (btnModificar) {
                    btnModificar.click();
                }

                // 3. Scroll suave hacia el formulario
                formServicios.scrollIntoView({ behavior: 'smooth' });
            }
        }

        // habilitar formulario
        function habilitarFormularioServicio() {
            formFieldset.disabled = false;
            topActions.style.display = 'flex';
            bottomActions.style.display = 'none';
        }

        // deshabilitar formulario
        function deshabilitarFormularioServicio() {
            formFieldset.disabled = true;
            topActions.style.display = 'none';
            bottomActions.style.display = 'block';
            formServicios.reset();
            servicioSeleccionadoId = null;
            filas.forEach(f => f.classList.remove('fila-seleccionada'));
            // Resetear la acción por defecto a CREAR
            formServicios.action = '/servicios';
        }

        // evento alta
        if (btnAlta) {
            btnAlta.addEventListener('click', () => {
                formServicios.reset();
                formServicios.action = '/servicios';
                habilitarFormularioServicio();
            });
        }

        // evento modificar
        if (btnModificar) {
            btnModificar.addEventListener('click', () => {
                if (!servicioSeleccionadoId) {
                    alert("Selecciona un servicio primero.");
                    return;
                }
                // Configuramos el formulario para EDITAR (POST /servicios/editar/{id})
                formServicios.action = '/servicios/editar/' + servicioSeleccionadoId;
                habilitarFormularioServicio();
            });
        }

        // evento baja
        if (btnBaja) {
            btnBaja.addEventListener('click', () => {
                if (!servicioSeleccionadoId) {
                    alert("Selecciona un servicio primero.");
                    return;
                }
                if (confirm("¿Eliminar este servicio?")) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '/servicios/' + servicioSeleccionadoId;
                    document.body.appendChild(form);
                    form.submit();
                }
            });
        }

        // Cancelar
        if (btnCancelar) {
            btnCancelar.addEventListener('click', (e) => {
                e.preventDefault();
                deshabilitarFormularioServicio();
            });
        }

    }

    // LÓGICA PARA: gestion-clientes-detalle.html

    const formClienteDetalle = document.getElementById('form-cliente');
    const btnModificar = document.getElementById('btn-modificar-form');

    if (formClienteDetalle && btnModificar) {

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

        // evento modificar Cliente
        btnModificar.addEventListener('click', habilitarFormularioModif);
        btnCancelar.addEventListener('click', (e) => {
            e.preventDefault();
            deshabilitarFormularioModif();
        });


        formContainer.querySelectorAll('select').forEach(select => {
            select.disabled = true;
        });
    }

    // lógica facturación que le agregué para mas felicidad

    const selectAllCheckbox = document.getElementById('seleccionar-todo');
    const facturacionForm = document.getElementById('facturacion-form-group');

    if (selectAllCheckbox) {
        const serviceCheckboxes = document.querySelectorAll('.checkbox-servicio');

        selectAllCheckbox.addEventListener('change', function() {
            const isChecked = this.checked;
            serviceCheckboxes.forEach(checkbox => {
                checkbox.checked = isChecked;
            });
        });

        serviceCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', function() {

                if (!this.checked) {
                    selectAllCheckbox.checked = false;
                }

                const allChecked = Array.from(serviceCheckboxes).every(cb => cb.checked);
                if (allChecked) {
                    selectAllCheckbox.checked = true;
                }
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
    // administrar-pagos.html

    const checkTodosPagos = document.getElementById('check-todos-pagos');
    const checksFactura = document.querySelectorAll('.check-factura');
    const displayTotal = document.getElementById('total-seleccionado');
    const btnProceder = document.getElementById('btn-proceder-pago');

    if (checkTodosPagos && checksFactura.length > 0) {

        function calcularTotalPagos() {
            let total = 0;
            let haySeleccionados = false;

            checksFactura.forEach(check => {
                if (check.checked) {
                    // Leemos el atributo data-saldo que pusimos en el HTML
                    const saldo = parseFloat(check.getAttribute('data-saldo'));
                    if (!isNaN(saldo)) {
                        total += saldo;
                    }
                    haySeleccionados = true;
                }
            });

            // Actualizamos el texto del total
            if (displayTotal) {
                displayTotal.textContent = total.toLocaleString('es-AR', {
                    style: 'currency',
                    currency: 'ARS'
                });
            }

            // Habilitar/Deshabilitar botón
            if (btnProceder) {
                btnProceder.disabled = !haySeleccionados;
                // Cambio visual opcional
                btnProceder.style.opacity = haySeleccionados ? '1' : '0.6';
            }
        }

        // Evento para el checkbox "Seleccionar Todos"
        checkTodosPagos.addEventListener('change', function() {
            const isChecked = this.checked;
            checksFactura.forEach(check => {
                check.checked = isChecked;
            });
            calcularTotalPagos();
        });

        // Eventos para cada checkbox individual
        checksFactura.forEach(check => {
            check.addEventListener('change', function() {
                // Si desmarco uno, desmarco el "Todos"
                if (!this.checked) {
                    checkTodosPagos.checked = false;
                }
                // Si marco todos manualmente, marco el "Todos"
                const allChecked = Array.from(checksFactura).every(c => c.checked);
                if (allChecked) {
                    checkTodosPagos.checked = true;
                }

                calcularTotalPagos();
            });
        });
    }


});



