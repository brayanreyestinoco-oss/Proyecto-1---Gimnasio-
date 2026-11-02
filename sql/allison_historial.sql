-- Ejecutar en MySQL Workbench DESPUÉS del schema.sql del equipo.
-- Seleccionar primero la misma base configurada en db.properties.
-- No elimina ni transforma las tablas de historial anteriores.
CREATE TABLE IF NOT EXISTS intento_acceso (
    id_intento BIGINT NOT NULL AUTO_INCREMENT,
    id_socio INT NULL,
    cedula_consultada VARCHAR(20) NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    permitido BOOLEAN NOT NULL,
    motivo VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_intento),
    CONSTRAINT fk_intento_socio FOREIGN KEY (id_socio)
        REFERENCES socio(id_socio) ON DELETE RESTRICT,
    INDEX idx_intento_fecha (fecha_hora, id_intento),
    INDEX idx_intento_socio_fecha (id_socio, fecha_hora)
) ENGINE=InnoDB;
