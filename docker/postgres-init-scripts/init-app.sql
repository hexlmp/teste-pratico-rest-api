-- Remove se existir
SELECT 'DROP DATABASE app_bd'
WHERE EXISTS (SELECT 1 FROM pg_database WHERE datname = 'app_bd')\gexec


-- Cria o banco de dados app_bd, se ele não existir
SELECT 'CREATE DATABASE app_bd'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'app_bd')\gexec

-- Conecta ao banco de dados app_bd
\c app_bd

-- Cria o esquema app_schema, se ele não existir
-- CREATE SCHEMA IF NOT EXISTS app_schema;

-- Cria as tabelas do teste prático

CREATE TABLE public.cidade (
	cid_id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	cid_nome varchar(255) NOT NULL,
	cid_uf varchar(255) NOT NULL,
	CONSTRAINT cidade_pkey PRIMARY KEY (cid_id),
	CONSTRAINT ck_uf CHECK (((cid_uf)::text ~ '^[A-Z]{2}$'::text))
);


CREATE TABLE public.pessoa (
	pes_id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	pes_nome varchar(255) NOT NULL,
	pes_data_nascimento date NULL,
	pes_sexo varchar(255) NULL,
	pes_mae varchar(255) NULL,
	pes_pai varchar(255) NULL,
	CONSTRAINT pessoa_pes_sexo_check CHECK (((pes_sexo)::text = ANY (ARRAY[('Masculino'::character varying)::text, ('Feminino'::character varying)::text, ('Outro'::character varying)::text]))),
	CONSTRAINT pessoa_pkey PRIMARY KEY (pes_id)
);


CREATE TABLE public.unidade (
	unid_id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	unid_nome varchar(255) NOT NULL,
	unid_sigla varchar(255) NOT NULL,
	CONSTRAINT uk_unid_sigla UNIQUE (unid_sigla),
	CONSTRAINT unidade_pkey PRIMARY KEY (unid_id)
);



CREATE TABLE public.endereco (
	end_id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	end_tipo_logradouro varchar(255) NULL,
	end_logradouro varchar(255) NOT NULL,
	end_numero varchar(255) NULL,
	end_bairro varchar(255) NOT NULL,
	cid_id int8 NOT NULL,
	end_cep bpchar(8) NULL,
	CONSTRAINT ck_cep CHECK ((end_cep ~ '^\d{8}$'::text)),
	CONSTRAINT endereco_pkey PRIMARY KEY (end_id),
	CONSTRAINT endereco_cid_id_fkey FOREIGN KEY (cid_id) REFERENCES public.cidade(cid_id) ON DELETE RESTRICT
);


CREATE TABLE public.foto_pessoa (
	fp_id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	pes_id int8 NOT NULL,
	fp_data date DEFAULT CURRENT_DATE NOT NULL,
	fp_bucket varchar(255) NOT NULL,
	fp_hash varchar(255) NOT NULL,
	CONSTRAINT foto_pessoa_pkey PRIMARY KEY (fp_id),
	CONSTRAINT uk_fp_hash UNIQUE (fp_hash),
	CONSTRAINT foto_pessoa_pes_id_fkey FOREIGN KEY (pes_id) REFERENCES public.pessoa(pes_id) ON DELETE CASCADE
);


CREATE TABLE public.lotacao (
	lot_id int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	pes_id int8 NOT NULL,
	unid_id int8 NOT NULL,
	lot_data_lotacao date DEFAULT CURRENT_DATE NOT NULL,
	lot_data_remocao date NULL,
	lot_portaria varchar(255) NULL,
	CONSTRAINT ck_datas_lotacao CHECK (((lot_data_remocao IS NULL) OR (lot_data_remocao >= lot_data_lotacao))),
	CONSTRAINT lotacao_pkey PRIMARY KEY (lot_id),
	CONSTRAINT lotacao_pes_id_fkey FOREIGN KEY (pes_id) REFERENCES public.pessoa(pes_id) ON DELETE CASCADE,
	CONSTRAINT lotacao_unid_id_fkey FOREIGN KEY (unid_id) REFERENCES public.unidade(unid_id) ON DELETE RESTRICT
);


CREATE TABLE public.pessoa_endereco (
	pes_id int8 NOT NULL,
	end_id int8 NOT NULL,
	CONSTRAINT pessoa_endereco_pkey PRIMARY KEY (pes_id, end_id),
	CONSTRAINT pessoa_endereco_end_id_fkey FOREIGN KEY (end_id) REFERENCES public.endereco(end_id) ON DELETE CASCADE,
	CONSTRAINT pessoa_endereco_pes_id_fkey FOREIGN KEY (pes_id) REFERENCES public.pessoa(pes_id) ON DELETE CASCADE
);


CREATE TABLE public.servidor_efetivo (
	pes_id int8 NOT NULL,
	se_matricula varchar(20) NOT NULL,
	CONSTRAINT servidor_efetivo_matricula_unique UNIQUE (se_matricula),
	CONSTRAINT servidor_efetivo_pkey PRIMARY KEY (pes_id, se_matricula),
	CONSTRAINT servidor_efetivo_pes_id_fkey FOREIGN KEY (pes_id) REFERENCES public.pessoa(pes_id) ON DELETE CASCADE
);


CREATE TABLE public.servidor_temporario (
	pes_id int8 NOT NULL,
	st_data_admissao date NOT NULL,
	st_data_demissao date NULL,
	CONSTRAINT servidor_temporario_pkey PRIMARY KEY (pes_id, st_data_admissao),
	CONSTRAINT servidor_temporario_pes_id_fkey FOREIGN KEY (pes_id) REFERENCES public.pessoa(pes_id) ON DELETE CASCADE
);


CREATE TABLE public.unidade_endereco (
	unid_id int8 NOT NULL,
	end_id int8 NOT NULL,
	CONSTRAINT unidade_endereco_pkey PRIMARY KEY (unid_id, end_id),
	CONSTRAINT unidade_endereco_end_id_fkey FOREIGN KEY (end_id) REFERENCES public.endereco(end_id) ON DELETE CASCADE,
	CONSTRAINT unidade_endereco_unid_id_fkey FOREIGN KEY (unid_id) REFERENCES public.unidade(unid_id) ON DELETE CASCADE
);


-- Inserindo cidades brasileiras (com UF no formato 'XX' conforme constraint)
INSERT INTO cidade (cid_nome, cid_uf) VALUES
('São Paulo', 'SP'),
('Rio de Janeiro', 'RJ'),
('Brasília', 'DF'),
('Salvador', 'BA'),
('Fortaleza', 'CE'),
('Belo Horizonte', 'MG'),
('Manaus', 'AM'),
('Curitiba', 'PR'),
('Cuiabá', 'MT'),
('Recife', 'PE'),
('Goiânia', 'GO'),
('Porto Alegre', 'RS'),
('Belém', 'PA'),
('Florianópolis', 'SC'),
('Vitória', 'ES'),
('Natal', 'RN');


-- Inserindo pessoas teste
INSERT INTO pessoa (pes_nome, pes_data_nascimento, pes_sexo, pes_mae, pes_pai) VALUES
('Albert Einstein', '1879-03-14', 'Masculino', 'Paulina Koch', 'Hermann Einstein'),
('Marie Curie', '1867-11-07', 'Feminino', 'Bronisława Boguska', 'Wladyslaw Sklodowski'),
('Leonardo da Vinci', '1452-04-15', 'Masculino', 'Caterina di Meo Lippi', 'Ser Piero da Vinci'),
('Frida Kahlo', '1907-07-06', 'Feminino', 'Matilde Calderón y González', 'Guillermo Kahlo'),
('Pablo Picasso', '1881-10-25', 'Masculino', 'Maria Picasso y López', 'José Ruiz y Blasco'),
('Isaac Newton', '1643-01-04', 'Masculino', 'Hannah Ayscough', 'Isaac Newton Sr.'),
('Vincent van Gogh', '1853-03-30', 'Masculino', 'Anna Cornelia Carbentus', 'Theodorus van Gogh'),
('Claude Monet', '1840-11-14', 'Masculino', 'Louise-Justine Aubrée', 'Claude-Adolphe Monet'),
('Nikola Tesla', '1856-07-10', 'Masculino', 'Duka MandiC', 'Milutin Tesla'),
('Jane Goodall', '1934-04-03', 'Feminino', 'Margaret Myfanwe Joseph', 'Mortimer Herbert Morris-Goodall'),
('Galileu Galilei', '1564-02-15', 'Masculino', 'Giulia Ammannati', 'Vincenzo Galilei'),
('Maya Angelou', '1928-04-04', 'Feminino', 'Vivian Baxter', 'Bailey Johnson'),
('Stephen Hawking', '1942-01-08', 'Masculino', 'Isobel Eileen Hawking', 'Frank Hawking'),
('Andy Warhol', '1928-08-06', 'Masculino', 'Julia Warhola', 'Andrej Warhola'),
('Rosalind Franklin', '1920-07-25', 'Feminino', 'Muriel Frances Waley', 'Ellis Arthur Franklin');


