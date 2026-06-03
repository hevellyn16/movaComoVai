CREATE TABLE comment_likes (
                               comment_id UUID NOT NULL,
                               user_id UUID NOT NULL,
                               PRIMARY KEY (comment_id, user_id),
                               CONSTRAINT fk_comment_likes_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
                               CONSTRAINT fk_comment_likes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE comment_pictures (
                                  id UUID PRIMARY KEY,
                                  comment_id UUID NOT NULL,
                                  picture_url VARCHAR(500) NOT NULL,
                                  CONSTRAINT fk_comment_pictures_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TABLE answers (
                         id UUID PRIMARY KEY,
                         comment_id UUID NOT NULL,
                         user_id UUID NOT NULL,
                         answer TEXT NOT NULL,
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP,
                         CONSTRAINT fk_answers_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
                         CONSTRAINT fk_answers_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);