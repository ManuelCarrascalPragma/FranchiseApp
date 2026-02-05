CREATE SCHEMA IF NOT EXISTS franchise_management;

SET search_path TO franchise_management;

CREATE TABLE IF NOT EXISTS franchises (
                                          id BIGSERIAL PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS branches (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    franchise_id BIGINT NOT NULL,
    CONSTRAINT fk_franchise
    FOREIGN KEY(franchise_id)
    REFERENCES franchises(id)
    ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS products (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
    stock BIGINT DEFAULT 0,
    branch_id BIGINT NOT NULL,
    CONSTRAINT fk_branch
    FOREIGN KEY(branch_id)
    REFERENCES branches(id)
    ON DELETE CASCADE
    );