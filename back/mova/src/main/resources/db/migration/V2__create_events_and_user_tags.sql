CREATE TABLE users_tags (
                            user_id UUID NOT NULL,
                            tag_id UUID NOT NULL,
                            PRIMARY KEY (tag_id, user_id),
                            CONSTRAINT fk_users_tags_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            CONSTRAINT fk_users_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE events (
                        id UUID PRIMARY KEY,
                        user_id UUID NOT NULL,
                        venue_id UUID,
                        event_name VARCHAR(100) NOT NULL,
                        description TEXT,
                        content_rating VARCHAR(50) NOT NULL,
                        price DECIMAL(10,2) NOT NULL,
                        starts_at TIMESTAMP NOT NULL,
                        ends_at TIMESTAMP NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP,
                        CONSTRAINT fk_events_user FOREIGN KEY (user_id) REFERENCES users(id),
                        CONSTRAINT fk_events_venue FOREIGN KEY (venue_id) REFERENCES venues(id)
);