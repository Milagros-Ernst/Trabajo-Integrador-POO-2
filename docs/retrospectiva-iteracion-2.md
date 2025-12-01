# Retrospectiva – Iteración 2

En esta segunda iteración logramos completar todas las historias de usuario que teníamos planificadas. A diferencia de la primera, donde quedó pendiente integrar la anulación de factura en la interfaz, esta vez pudimos terminar absolutamente todo: modificación y baja lógica de servicios, estado de cuenta del cliente, emisión de recibos, pagos totales y parciales, anulación de factura e informe del historial de facturación masiva. Fue una iteración bastante cargada, pero llegamos bien a todos los objetivos.

En lo técnico hicimos varios ajustes importantes. Actualizamos el diagrama de clases por las nuevas relaciones que necesitábamos para los recibos, agregamos una pantalla de inicio en la sección de clientes, sumamos una tabla para mostrar los clientes inactivos y también incorporamos un combobox en la facturación individual. Además, realizamos pequeños cambios de código para mantener coherencia interna, como renombrar *ClienteServicio* a *ClienteService*.

Una diferencia grande con la iteración anterior fue que ahora sí pudimos avanzar con todas las pruebas unitarias del proyecto. Hicimos test completos de los modelos y servicios, y también cubrimos los controladores y repositorios más importantes. Esto llevó bastante trabajo porque tuvimos algunos problemas con las distintas versiones de Java y varios inconvenientes con Mockito, lo que nos hizo invertir más tiempo del previsto en la parte de testing. Aun así, logramos cerrar toda esta sección sin dejar nada pendiente.

En cuanto a la organización, el grupo mantuvo la misma dinámica positiva que en la primera iteración. No tuvimos problemas de coordinación y cada integrante tomó historias relacionadas con lo que había desarrollado antes, lo cual ayudó a mantener un trabajo más ordenado. Incluso cuando una tarea era individual, hubo colaboración del resto del equipo cuando hacía falta. La comunicación fue buena y fluida durante todo el proceso.

En esta iteración también aprendimos bastante, sobre todo en lo relacionado a pruebas unitarias, mejores prácticas y el manejo de herramientas como Spring y Maven. Sentimos que avanzamos con más seguridad gracias a la base que ya habíamos construido en la primera iteración.

Como esta fue la última etapa del proyecto, no quedaron mejoras pendientes para una iteración futura. En general, la experiencia fue positiva: pudimos cumplir todo lo planificado, aprendimos mucho y el trabajo en equipo se mantuvo sólido hasta el final.
