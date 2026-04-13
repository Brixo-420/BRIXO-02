-- Full schema creation matching JPA entities.

CREATE TABLE IF NOT EXISTS CLIENTE (
    id_cliente  SERIAL PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL,
    correo      VARCHAR(255) NOT NULL UNIQUE,
    contrasena  VARCHAR(255) NOT NULL,
    telefono    VARCHAR(255),
    ciudad      VARCHAR(255),
    foto_perfil VARCHAR(255),
    creado_en   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS CONTRATISTA (
    id_contratista    SERIAL PRIMARY KEY,
    nombre            VARCHAR(255) NOT NULL,
    correo            VARCHAR(255) NOT NULL UNIQUE,
    contrasena        VARCHAR(255) NOT NULL,
    telefono          VARCHAR(255),
    ciudad            VARCHAR(255),
    ubicacion_mapa    VARCHAR(255),
    foto_perfil       VARCHAR(255),
    experiencia       TEXT,
    portafolio        TEXT,
    descripcion_perfil TEXT,
    verificado        BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en         TIMESTAMP
);

CREATE TABLE IF NOT EXISTS CATEGORIA (
    id_categoria SERIAL PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    descripcion  TEXT,
    imagen_url   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS UBICACION (
    id_ubicacion SERIAL PRIMARY KEY,
    ciudad       VARCHAR(100),
    departamento VARCHAR(100),
    direccion    VARCHAR(255),
    latitud      NUMERIC(10, 8),
    longitud     NUMERIC(11, 8)
);

CREATE TABLE IF NOT EXISTS SERVICIO (
    id_servicio     SERIAL PRIMARY KEY,
    nombre          VARCHAR(255) NOT NULL,
    descripcion     TEXT,
    precio_estimado NUMERIC(12, 2),
    imagen_url      VARCHAR(255),
    id_categoria    INTEGER REFERENCES CATEGORIA(id_categoria)
);

CREATE TABLE IF NOT EXISTS SOLICITUD (
    id_solicitud  SERIAL PRIMARY KEY,
    id_cliente    INTEGER NOT NULL REFERENCES CLIENTE(id_cliente),
    id_contratista INTEGER REFERENCES CONTRATISTA(id_contratista),
    titulo        VARCHAR(150) NOT NULL,
    descripcion   TEXT NOT NULL,
    presupuesto   NUMERIC(12, 2) DEFAULT 0,
    ubicacion     VARCHAR(255),
    estado        VARCHAR(20) NOT NULL DEFAULT 'ABIERTA',
    creado_en     TIMESTAMP
);

CREATE TABLE IF NOT EXISTS CONTRATO (
    id_contrato    SERIAL PRIMARY KEY,
    fecha_inicio   DATE,
    fecha_fin      DATE,
    costo_total    NUMERIC(12, 2),
    estado         VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    id_contratista INTEGER NOT NULL REFERENCES CONTRATISTA(id_contratista),
    id_cliente     INTEGER NOT NULL REFERENCES CLIENTE(id_cliente)
);

CREATE TABLE IF NOT EXISTS RESENA (
    id_resena    SERIAL PRIMARY KEY,
    comentario   TEXT,
    fecha        DATE,
    calificacion SMALLINT NOT NULL,
    id_contrato  INTEGER NOT NULL REFERENCES CONTRATO(id_contrato),
    id_cliente   INTEGER NOT NULL REFERENCES CLIENTE(id_cliente)
);

CREATE TABLE IF NOT EXISTS CERTIFICACION (
    id_certificado  SERIAL PRIMARY KEY,
    nombre          VARCHAR(255) NOT NULL,
    entidad_emisora VARCHAR(255),
    fecha_obtenida  DATE,
    id_contratista  INTEGER NOT NULL REFERENCES CONTRATISTA(id_contratista)
);

CREATE TABLE IF NOT EXISTS MENSAJE (
    id_mensaje       SERIAL PRIMARY KEY,
    remitente_id     INTEGER NOT NULL,
    remitente_rol    VARCHAR(20) NOT NULL,
    destinatario_id  INTEGER NOT NULL,
    destinatario_rol VARCHAR(20) NOT NULL,
    contenido        TEXT NOT NULL,
    leido            BOOLEAN NOT NULL DEFAULT FALSE,
    creado_en        TIMESTAMP
);

CREATE TABLE IF NOT EXISTS COTIZACION_CONFIRMADA (
    id                SERIAL PRIMARY KEY,
    id_cliente        INTEGER,
    descripcion       TEXT NOT NULL,
    servicio_principal VARCHAR(255) NOT NULL,
    materiales_json   JSON NOT NULL,
    personal_json     JSON NOT NULL,
    complejidad       VARCHAR(20) NOT NULL DEFAULT 'medio',
    estado            VARCHAR(20) NOT NULL DEFAULT 'pendiente',
    creado_en         TIMESTAMP NOT NULL,
    confirmado_en     TIMESTAMP NOT NULL
);
