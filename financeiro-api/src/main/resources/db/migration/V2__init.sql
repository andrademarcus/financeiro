-- V2__init.sql

CREATE TABLE user_info (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(255),
    username VARCHAR(255)
);


CREATE TABLE conta (
    id BIGSERIAL PRIMARY KEY,
    data_atualizacao TIMESTAMP,
    data_cadastro TIMESTAMP,
    data_pagamento DATE,
    data_vencimento DATE,
    descricao VARCHAR(255),
    situacao VARCHAR(255) NOT NULL,
    usuario_atualizacao VARCHAR(255),
    usuario_cadastro VARCHAR(255),
    valor NUMERIC(38, 4) NOT NULL
);


INSERT INTO user_info
("name", "password", "role", username)
VALUES('Administrator', '$2a$10$gM6sacfuZRnIs6LBCnzor.nNuimP/IYMwb/NAgxnu2Mvo9GT8E7We', 'ROLE_ADMIN', 'admin');

INSERT INTO user_info
("name", "password", "role", username)
VALUES('Regular User', '$2a$10$vQr2ZDDWzsZur2X3rjuwKODpRz5o290WsHJKz9FHwanOJ6dFitfke', 'ROLE_USER', 'user');