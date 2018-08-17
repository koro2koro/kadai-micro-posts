CREATE TABLE user_follows (
  id        BIGSERIAL     NOT NULL,
  user_id   BIGSERIAL     NOT NULL,
  follow_id BIGSERIAL     NOT NULL,
  create_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (follow_id) REFERENCES users(id),
  UNIQUE(user_id, follow_id)
);