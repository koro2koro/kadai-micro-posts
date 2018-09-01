CREATE TABLE favorites (
  id           BIGSERIAL  NOT NULL,
  user_id      BIGSERIAL  NOT NULL,
  micropost_id BIGSERIAL  NOT NULL,
  create_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (micropost_id) REFERENCES micro_posts(id),
  UNIQUE(user_id, micropost_id)
);