CREATE TABLE event_tags (
                            event_id UUID NOT NULL,
                            tag_id UUID NOT NULL,
                            PRIMARY KEY (event_id, tag_id),
                            CONSTRAINT fk_event_tags_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                            CONSTRAINT fk_event_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

CREATE TABLE event_pictures (
                                id UUID PRIMARY KEY,
                                event_id UUID NOT NULL,
                                picture_url VARCHAR(500) NOT NULL,
                                CONSTRAINT fk_event_pictures_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE event_likes (
                             event_id UUID NOT NULL,
                             user_id UUID NOT NULL,
                             PRIMARY KEY (event_id, user_id),
                             CONSTRAINT fk_event_likes_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                             CONSTRAINT fk_event_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE favorites (
                           event_id UUID NOT NULL,
                           user_id UUID NOT NULL,
                           PRIMARY KEY (event_id, user_id),
                           CONSTRAINT fk_favorites_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                           CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE comments (
                          id UUID PRIMARY KEY,
                          user_id UUID NOT NULL,
                          event_id UUID NOT NULL,
                          comment TEXT NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP,
                          CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          CONSTRAINT fk_comments_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);