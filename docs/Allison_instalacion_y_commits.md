# Instalación, commits y validación de Allison

## Estado de esta entrega
Base: rama main, commit 6950f1c4f24d04b7a1ca72fcda41be7de09db5a7.
La rama Allison se creó únicamente en la copia de trabajo de esta asistencia. No se publicaron cambios ni se crearon commits remotos.
El repositorio recibido ya contenía implementación parcial del módulo; estos cambios la completan. No atribuya a estos commits todo el código previo.

## Abrir e integrar en IntelliJ
1. Guarde sus cambios locales antes de incorporar esta entrega.
2. En su copia del repositorio, ejecute `git fetch origin` y `git switch -c Allison origin/main`. Si esa rama ya existe, use `git switch Allison` y revise cómo integrar main sin perder sus cambios.
3. Copie desde esta entrega únicamente los archivos indicados en los grupos de commits de abajo. No copie .git, .idea ni db.properties.
4. Abra pom.xml en IntelliJ y seleccione JDK 21 tanto en Project SDK como en Maven Runner. Recargue Maven.
5. Configure db.properties tomando db.properties.example como referencia. No suba contraseñas.
6. Pida a Brayan el esquema consolidado: en la copia recibida no había un schema.sql versionado. Se necesitan las tablas socio, membresia, plan y consulta_membresia con las columnas que esperan los repositorios existentes.
7. En MySQL Workbench seleccione la base correcta y ejecute sql/allison_historial.sql después del esquema del equipo. Confirme que socio.id_socio sea INT con el mismo signo y que use InnoDB; la FK requiere tipos compatibles.
8. Ejecute `./mvnw clean test` (en Windows: `.\mvnw.cmd clean test`) y después ` .\mvnw.cmd javafx:run` o la configuración JavaFX existente de IntelliJ.

Los registros antiguos de acceso_historial y las denegaciones antiguas de consulta_membresia no se migran automáticamente. Los nuevos intentos usan intento_acceso; las consultas informativas siguen en consulta_membresia. Coordine con Brayan si se requiere migrar el historial previo. El script complementario debe incorporarse al script final de entrega del grupo.

## Commits sugeridos
Ejecute desde la raíz del repositorio. Use su identidad habitual de Git. Revise `git diff --cached` antes de cada commit.

### 1. Persistencia y reglas
```bash
git add sql/allison_historial.sql src/main/java/cr/ac/una/est/controlmembresiasgimnasio/repository/AccesoRepository.java src/main/java/cr/ac/una/est/controlmembresiasgimnasio/repository/AccesoRepositoryJdbc.java src/main/java/cr/ac/una/est/controlmembresiasgimnasio/modelo/accesos/RegistroAcceso.java src/main/java/cr/ac/una/est/controlmembresiasgimnasio/service/ControlAcceso.java src/main/java/cr/ac/una/est/controlmembresiasgimnasio/service/HistorialAccesoService.java
git commit -m "feat: persistir intentos de acceso con resultado y motivo mediante repository JDBC"
```

### 2. Interfaz y concurrencia
```bash
git add src/main/java/cr/ac/una/est/controlmembresiasgimnasio/vista/ControladorAcceso.java src/main/java/cr/ac/una/est/controlmembresiasgimnasio/vista/HistorialAccesosController.java src/main/resources/cr/ac/una/est/controlmembresiasgimnasio/HistorialAccesosView.fxml
git commit -m "feat: consultar historial por fechas y socio con Task e indicador de carga"
```

### 3. Documentación
```bash
git add docs/Allison_parte_escrita.md docs/Allison_instalacion_y_commits.md
git commit -m "docs: documentar control de acceso historial y pruebas de Allison"
git push -u origin Allison
```

## Pruebas pendientes en el entorno del equipo
| Caso | Resultado esperado |
| --- | --- |
| Membresía activa que vence hoy | Permite e inserta motivo de vigencia |
| Membresía vencida o inactiva | Deniega y conserva el motivo correspondiente |
| Membresía activa que empieza mañana | Deniega: aún no inicia |
| Socio sin membresía | Deniega con socio asociado |
| Cédula desconocida | Deniega; id_socio nulo y cédula conservada |
| Reiniciar aplicación | El intento sigue en el historial |
| Fechas inicial y final iguales | Incluye todo ese día |
| Buscar por cédula completa o nombre | Devuelve solo coincidencias |
| Cambiar período rápidamente | La respuesta anterior no sustituye la nueva |
| MySQL apagado | Error visible, sin bloqueo de la ventana |
| Fallo al guardar intento | Advierte que el registro no pudo guardarse |
| Exportar CSV | Exporta las filas visibles con fecha, resultado y motivo |

La validación realizada aquí fue de XML FXML y revisión de diferencias. No se completó compilación: el entorno tiene un runtime Java 17, carece de javac y Maven no pudo resolver repo.maven.apache.org para descargar su distribución. Tampoco se dispone del servidor MySQL del equipo. Por ello, las pruebas de ejecución anteriores siguen pendientes y no deben presentarse como aprobadas.
