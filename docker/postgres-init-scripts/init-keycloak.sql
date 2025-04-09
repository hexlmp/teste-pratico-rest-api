-- Cria o banco de dados keycloak_bd, se ele não existir
SELECT 'CREATE DATABASE keycloak_bd'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak_bd')\gexec

-- Conecta ao banco de dados keycloak_bd
\c keycloak_bd

-- Cria o esquema keycloak_schema, se ele não existir
-- CREATE SCHEMA IF NOT EXISTS keycloak_schema;
