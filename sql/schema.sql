-- =====================================================================
-- schema.sql  |  Sistema de Control de Membresías del Gimnasio
-- Base de datos: MySQL 8 o superior
--
-- Crea las 6 tablas del sistema (en tercera forma normal) y los planes
-- iniciales. Antes de ejecutarlo, cree y seleccione la base de datos:
--     CREATE DATABASE IF NOT EXISTS gimnasio CHARACTER SET utf8mb4;
--     USE gimnasio;
-- (use el nombre de base de datos que tenga en db.properties)
--
-- Se puede ejecutar más de una vez: no borra ni modifica datos existentes.
--
-- Relaciones:
--   socio 1 --- 0..1 membresia      (un socio tiene como máximo una membresía)
--   plan  1 --- N    membresia      (un plan lo usan muchas membresías)
--   membresia 1 --- N pago          (una membresía acumula sus pagos)
--   socio 1 --- N intento_acceso    (entradas permitidas o denegadas)
--   socio 1 --- N consulta_membresia (consultas de estado en recepción)
-- =====================================================================

-- ---------------------------------------------------------------------
-- socio: datos personales, contacto de emergencia y condiciones médicas
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS socio (
    id_socio            INT           NOT NULL AUTO_INCREMENT,
    cedula              VARCHAR(20)   NOT NULL,
    nombre              VARCHAR(100)  NOT NULL,
    telefono            VARCHAR(20)   NOT NULL,
    correo              VARCHAR(100)  NOT NULL,
    contacto_emergencia VARCHAR(100)  NOT NULL,
    telefono_emergencia VARCHAR(20)   NOT NULL,
    condiciones_medicas VARCHAR(255)  NOT NULL DEFAULT '',
    PRIMARY KEY (id_socio),
    UNIQUE KEY uq_socio_cedula (cedula)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- plan: catálogo de planes (MENSUAL, ANUAL o VIP)
--   descuento_pct aplica al plan ANUAL; recargo aplica al plan VIP.
--   Valen 0 cuando no aplican, para no usar valores nulos.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS plan (
    id_plan        INT            NOT NULL AUTO_INCREMENT,
    tipo           VARCHAR(10)    NOT NULL,
    nombre         VARCHAR(50)    NOT NULL,
    precio_base    DECIMAL(12,2)  NOT NULL,
    descuento_pct  DECIMAL(5,2)   NOT NULL DEFAULT 0.00,
    recargo        DECIMAL(12,2)  NOT NULL DEFAULT 0.00,
    beneficios     VARCHAR(255)   NOT NULL,
    PRIMARY KEY (id_plan),
    UNIQUE KEY uq_plan_nombre (nombre),
    CONSTRAINT ck_plan_tipo      CHECK (tipo IN ('MENSUAL', 'ANUAL', 'VIP')),
    CONSTRAINT ck_plan_precio    CHECK (precio_base > 0),
    CONSTRAINT ck_plan_descuento CHECK (descuento_pct >= 0 AND descuento_pct <= 100),
    CONSTRAINT ck_plan_recargo   CHECK (recargo >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- membresia: vigencia de un socio (un socio, una membresía)
--   Al renovar se actualiza fecha_vencimiento; los cobros quedan en pago.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS membresia (
    id_membresia       INT      NOT NULL AUTO_INCREMENT,
    id_socio           INT      NOT NULL,
    id_plan            INT      NOT NULL,
    fecha_inicio       DATE     NOT NULL,
    fecha_vencimiento  DATE     DEFAULT NULL,
    activa             TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id_membresia),
    UNIQUE KEY uq_membresia_socio (id_socio),
    KEY fk_membresia_plan (id_plan),
    KEY idx_membresia_vencimiento (fecha_vencimiento),
    CONSTRAINT fk_membresia_socio FOREIGN KEY (id_socio)
        REFERENCES socio (id_socio) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_membresia_plan FOREIGN KEY (id_plan)
        REFERENCES plan (id_plan) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- pago: cada cobro de una membresía (monto realmente cobrado)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pago (
    id_pago       INT            NOT NULL AUTO_INCREMENT,
    id_membresia  INT            NOT NULL,
    monto         DECIMAL(12,2)  NOT NULL,
    fecha_pago    DATE           NOT NULL,
    metodo_pago   VARCHAR(30)    NOT NULL,
    PRIMARY KEY (id_pago),
    KEY idx_pago_membresia (id_membresia),
    CONSTRAINT fk_pago_membresia FOREIGN KEY (id_membresia)
        REFERENCES membresia (id_membresia) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT ck_pago_monto CHECK (monto > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- intento_acceso: cada intento de entrada, permitido o denegado
--   id_socio es nulo cuando la cédula escrita no está registrada.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS intento_acceso (
    id_intento         BIGINT       NOT NULL AUTO_INCREMENT,
    id_socio           INT          DEFAULT NULL,
    cedula_consultada  VARCHAR(20)  NOT NULL,
    fecha_hora         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    permitido          TINYINT(1)   NOT NULL,
    motivo             VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_intento),
    KEY idx_intento_fecha (fecha_hora, id_intento),
    KEY idx_intento_socio_fecha (id_socio, fecha_hora),
    CONSTRAINT fk_intento_socio FOREIGN KEY (id_socio)
        REFERENCES socio (id_socio) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- consulta_membresia: consultas de estado hechas en recepción
--   (ver la membresía sin registrar una entrada)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS consulta_membresia (
    id_consulta        INT          NOT NULL AUTO_INCREMENT,
    id_socio           INT          DEFAULT NULL,
    cedula_consultada  VARCHAR(20)  NOT NULL,
    fecha_hora         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    vigente            TINYINT(1)   NOT NULL,
    estado             VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_consulta),
    KEY idx_consulta_fecha (fecha_hora),
    KEY idx_consulta_socio (id_socio),
    CONSTRAINT fk_consulta_socio FOREIGN KEY (id_socio)
        REFERENCES socio (id_socio) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ---------------------------------------------------------------------
-- Datos iniciales: los tres planes base del gimnasio
--   INSERT IGNORE no repite un plan si su nombre ya existe.
-- ---------------------------------------------------------------------
INSERT IGNORE INTO plan (tipo, nombre, precio_base, descuento_pct, recargo, beneficios) VALUES
    ('MENSUAL', 'Mensual',     15000.00,  0.00,     0.00, 'Acceso al gimnasio en horario regular'),
    ('ANUAL',   'Anual',      180000.00, 15.00,     0.00, 'Acceso al gimnasio + 15% de descuento por pago anual'),
    ('VIP',     'VIP Mensual', 15000.00,  0.00, 10000.00, 'Acceso al gimnasio + clases especiales + casillero personal');
