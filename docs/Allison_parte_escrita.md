# Aporte de Allison: control de acceso, historial y concurrencia

## Introducción
Este apartado describe la evolución del módulo de control de acceso del sistema de membresías del gimnasio. Se explica cómo se consultan las membresías persistidas, cómo se registran los intentos de ingreso y cómo se utiliza concurrencia para consultar MySQL sin bloquear la interfaz gráfica.

## Descripción de la evolución
El Proyecto 1 trabajaba con información en memoria. Para el Proyecto 2, la verificación utiliza PagoService y MembresiaRepositoryJdbc para recuperar la membresía desde MySQL. El resultado depende de su activación y sus fechas. Se rechazan las membresías inactivas, futuras, vencidas o sin vencimiento registrado, así como los socios sin membresía y las cédulas desconocidas. Una membresía activa que vence hoy permite ingresar.

La funcionalidad nueva es el historial de intentos de acceso. Cada verificación de entrada intenta guardar la cédula consultada, la fecha y hora, el resultado y el motivo. Se conservan tanto las autorizaciones como las denegaciones. Las consultas informativas de membresía permanecen separadas de los intentos de ingreso.

## Justificación del modelo de datos
La tabla intento_acceso tiene la llave primaria id_intento, generada por MySQL. id_socio es una llave foránea hacia socio(id_socio); puede ser nula cuando la cédula digitada no corresponde a un socio registrado. La relación es de uno a muchos: un socio puede acumular varios intentos.

cedula_consultada representa el dato que se ingresó al verificar el acceso, no una copia destinada a sustituir los datos personales del socio. Se conserva para documentar también intentos con cédulas desconocidas. El nombre se obtiene mediante LEFT JOIN, sin copiarlo en cada intento. permitido y motivo corresponden al resultado histórico: una renovación posterior no modifica lo ocurrido antes. fecha_hora se genera en MySQL.

La tabla tiene atributos atómicos y una llave primaria simple. Sus atributos describen el intento identificado por esa llave y no incluyen datos de contacto ni características del plan. Esto evita dependencias parciales y transitivas dentro de la entidad y es compatible con la tercera forma normal. La normalización de todo el esquema debe justificarse con el modelo consolidado del equipo.

ON DELETE RESTRICT evita eliminar un socio que tenga intentos relacionados. El equipo debe manejar esa restricción en la pantalla de socios. Los índices por fecha y por socio-fecha facilitan las consultas. El script es complementario al esquema del equipo; no crea las demás tablas ni migra registros antiguos.

## Arquitectura y consultas
RegistroAcceso representa una fila del historial. AccesoRepository define el contrato de persistencia y AccesoRepositoryJdbc implementa las operaciones SQL. ControlAcceso decide el resultado y solicita su registro; HistorialAccesoService coordina las consultas. Los controladores JavaFX presentan los resultados.

Se utiliza PreparedStatement para enlazar cédulas, fechas, resultados, motivos y límites. Estos valores no se incorporan como instrucciones SQL. try-with-resources cierra conexiones, sentencias y resultados incluso si ocurre un error.

## Concurrencia y manejo de errores
HistorialAccesosController crea un Task que consulta la base en call(). Los filtros se leen antes de iniciar el trabajo. setOnSucceeded actualiza la lista observable y setOnFailed informa el error desde el hilo de JavaFX. Durante la consulta se muestra un ProgressIndicator. Al iniciar otra búsqueda se cancela la anterior y se comprueba la identidad de la tarea antes de publicar un resultado, evitando que una respuesta antigua sustituya la más reciente.

La verificación y el registro del acceso también se ejecutan en un Task. Se impiden verificaciones simultáneas desde esa pantalla. Si falla la lectura, se informa un error y no se interpreta como una membresía vencida. Si la lectura funciona pero falla la escritura, se muestra que no fue posible guardar el registro. En ese caso no se puede afirmar que el intento quedó persistido.

## Uso de inteligencia artificial
Para esta implementación se utilizó ChatGPT como apoyo en la revisión del repositorio, separación del contrato Repository y su implementación JDBC, diseño de la tabla de intentos y uso de Task. La base recibida ya contenía parte del control de acceso y del historial; el aporte descrito completa y ajusta esa implementación.

No se ha verificado el uso de otras herramientas de IA, por lo que no corresponde inventar una comparación entre productos. Para completar el análisis comparativo solicitado por las profesoras, el equipo debe incorporar las herramientas que realmente probó, los mismos problemas utilizados para compararlas, los errores detectados y las adaptaciones realizadas. Este párrafo documenta únicamente el uso comprobado en esta asistencia.

## Borrador de conclusión individual para revisar después de las pruebas
La evolución del control de acceso permite comprender cómo separar las reglas del negocio de las consultas SQL. El historial conserva el resultado de cada intento, mientras que las llaves foráneas ayudan a mantener las relaciones. El uso de Task muestra por qué las consultas de base de datos deben ejecutarse fuera del hilo de interfaz y por qué la actualización de controles debe realizarse desde JavaFX. La asistencia de IA requiere revisar el código, comprobarlo contra la base de datos y poder explicar sus decisiones; por sí sola no demuestra que el sistema funcione en el entorno final.

## Actualización del manual de usuario
1. Abra Control de acceso desde el menú principal.
2. Digite una cédula y pulse el botón de entrada. Espere el resultado. La consulta informativa de membresía no representa un ingreso.
3. Abra Historial de accesos y seleccione Intentos de acceso.
4. Elija Hoy, Últimos 7 días o Todos. Puede indicar Desde y Hasta; Desde reemplaza el inicio del período y Hasta incluye el día completo.
5. Digite la cédula completa o parte del nombre del socio y pulse Actualizar o Enter.
6. Observe el indicador mientras se consulta. Use el filtro de resultado para ver los permitidos o denegados dentro del conjunto cargado.
7. La tabla muestra fecha, socio, cédula, resultado y motivo. Las cédulas desconocidas aparecen sin nombre.
8. Puede exportar las filas visibles a CSV. La consulta tiene un máximo de 5000 registros; si alcanza ese límite, reduzca el rango. El filtro de resultado se aplica a esas filas cargadas.
9. Si ocurre un error, compruebe con el equipo que MySQL esté disponible y vuelva a actualizar.

## Guion breve de defensa
Primero muestro una membresía vigente y registro una entrada. Luego verifico una cédula desconocida para demostrar que también se conserva una denegación. Abro el historial, busco ambos casos y explico sus motivos. Reinicio la aplicación para comprobar la persistencia. En código señalo la interfaz Repository, el INSERT parametrizado, el LEFT JOIN y el Task; explico la diferencia entre call() y los manejadores de éxito y error. Finalmente muestro la llave foránea y justifico por qué id_socio puede ser nulo.
