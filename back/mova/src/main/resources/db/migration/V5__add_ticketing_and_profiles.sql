ALTER TABLE users
    ADD COLUMN username VARCHAR(50) UNIQUE,
    ADD COLUMN avatar_url VARCHAR(500),
    ADD COLUMN bio TEXT,
    ADD COLUMN location VARCHAR(100),
    ADD COLUMN is_private BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN push_notifications BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN email_notifications BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE event_schedules (
                                 id UUID PRIMARY KEY,
                                 event_id UUID NOT NULL,
                                 schedule_time TIMESTAMP NOT NULL,
                                 title VARCHAR(100) NOT NULL,
                                 description TEXT,
                                 CONSTRAINT fk_event_schedules_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TYPE ticket_status AS ENUM ('PENDING', 'PAID', 'CANCELLED');
CREATE TYPE payment_method AS ENUM ('CREDIT_CARD', 'PIX', 'BOLETO');

CREATE TABLE tickets (
                         id UUID PRIMARY KEY,
                         user_id UUID NOT NULL,
                         event_id UUID NOT NULL,

                         ticket_number VARCHAR(50) NOT NULL UNIQUE,
                         status ticket_status NOT NULL DEFAULT 'PENDING',
                         payment_method payment_method NOT NULL,

                         total_price DECIMAL(10,2) NOT NULL,
                         service_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,

                         qr_code_hash VARCHAR(255) UNIQUE,

                         purchased_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP,

                         CONSTRAINT fk_tickets_user FOREIGN KEY (user_id) REFERENCES users(id),
                         CONSTRAINT fk_tickets_event FOREIGN KEY (event_id) REFERENCES events(id)
);

