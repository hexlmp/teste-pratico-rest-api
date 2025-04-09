-- Cria o banco de dados app_bd, se ele não existir
SELECT 'CREATE DATABASE app_bd'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'app_bd')\gexec

-- Conecta ao banco de dados app_bd
\c app_bd

-- Cria o esquema app_schema, se ele não existir
-- CREATE SCHEMA IF NOT EXISTS app_schema;

-- Cria as tabelas do teste prático
CREATE TABLE cidade (
    cid_id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cid_nome VARCHAR(200) NOT NULL,
    cid_uf CHAR(2) NOT NULL,
    CONSTRAINT ck_uf CHECK (cid_uf ~ '^[A-Z]{2}$')
);

CREATE TABLE unidade (
    unid_id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    unid_nome VARCHAR(200) NOT NULL,
    unid_sigla VARCHAR(20) NOT NULL,
    CONSTRAINT uk_unid_sigla UNIQUE (unid_sigla)
);

CREATE TABLE pessoa (
    pes_id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pes_nome VARCHAR(200) NOT NULL,
    pes_data_nascimento DATE,
    pes_sexo VARCHAR(9) CHECK (pes_sexo IN ('Masculino', 'Feminino', 'Outro')),
    pes_mae VARCHAR(200),
    pes_pai VARCHAR(200)
);

CREATE TABLE foto_pessoa (
    fp_id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pes_id INT8 NOT NULL,
    fp_data DATE NOT NULL DEFAULT CURRENT_DATE,
    fp_bucket VARCHAR(50) NOT NULL,
    fp_hash VARCHAR(64) NOT NULL, -- Aumentado para 64 caracteres (SHA-256)
    CONSTRAINT uk_fp_hash UNIQUE (fp_hash),
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE
);

CREATE TABLE servidor_temporario (
    pes_id INT8 PRIMARY KEY,
    si_data_admissao DATE NOT NULL,
    si_data_demissao DATE,
    CONSTRAINT ck_datas CHECK (si_data_demissao IS NULL OR si_data_demissao >= si_data_admissao),
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE
);

CREATE TABLE servidor_efetivo (
    pes_id INT8 PRIMARY KEY,
    se_matricula VARCHAR(20) NOT NULL,
    CONSTRAINT uk_se_matricula UNIQUE (se_matricula),
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE
);

CREATE TABLE lotacao (
    lot_id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pes_id INT8 NOT NULL,
    unid_id INT8 NOT NULL,
    lot_data_lotacao DATE NOT NULL DEFAULT CURRENT_DATE,
    lot_data_remocao DATE,
    lot_portaria VARCHAR(100),
    CONSTRAINT ck_datas_lotacao CHECK (lot_data_remocao IS NULL OR lot_data_remocao >= lot_data_lotacao),
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE,
    FOREIGN KEY (unid_id) REFERENCES unidade(unid_id) ON DELETE RESTRICT
);

CREATE TABLE endereco (
    end_id INT8 GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    end_tipo_logradouro VARCHAR(50),
    end_logradouro VARCHAR(200) NOT NULL,
    end_numero VARCHAR(20), -- Alterado para VARCHAR para permitir números como "S/N"
    end_bairro VARCHAR(100) NOT NULL,
    cid_id INT8 NOT NULL,
    end_cep CHAR(8), -- Adicionado campo CEP
    CONSTRAINT ck_cep CHECK (end_cep ~ '^\d{8}$'),
    FOREIGN KEY (cid_id) REFERENCES cidade(cid_id) ON DELETE RESTRICT
);

CREATE TABLE pessoa_endereco (
    pes_id INT8 NOT NULL,
    end_id INT8 NOT NULL,
    pe_principal BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (pes_id, end_id),
    FOREIGN KEY (pes_id) REFERENCES pessoa(pes_id) ON DELETE CASCADE,
    FOREIGN KEY (end_id) REFERENCES endereco(end_id) ON DELETE CASCADE
);

CREATE TABLE unidade_endereco (
    unid_id INT8 NOT NULL,
    end_id INT8 NOT NULL,
    ue_principal BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (unid_id, end_id),
    FOREIGN KEY (unid_id) REFERENCES unidade(unid_id) ON DELETE CASCADE,
    FOREIGN KEY (end_id) REFERENCES endereco(end_id) ON DELETE CASCADE
);

