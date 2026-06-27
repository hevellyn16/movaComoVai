CREATE TYPE user_type AS ENUM ('ADMIN', 'COMMON');

CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       user_type user_type NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP,
                       is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE tags (
                      id UUID PRIMARY KEY,
                      tag_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE venues (
                        id UUID PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        number VARCHAR(50) NOT NULL,
                        city VARCHAR(100) NOT NULL,
                        street VARCHAR(255) NOT NULL,
                        neighborhood VARCHAR(100) NOT NULL,
                        landmark VARCHAR(255),
                        has_parking_lot BOOLEAN NOT NULL DEFAULT FALSE,
                        has_accessibility BOOLEAN NOT NULL DEFAULT FALSE,
                        has_bathroom BOOLEAN NOT NULL DEFAULT FALSE,
                        has_foods_and_drinks BOOLEAN NOT NULL DEFAULT FALSE
);