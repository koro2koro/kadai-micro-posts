CREATE TABLE micro_posts (
  id        BIGSERIAL    NOT NULL,
  user_id   BIGSERIAL    NOT NULL,
  content   VARCHAR(140) NOT NULL,
  create_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
);

-- user_idで検索するためインデックスを割り当てておく
CREATE INDEX micro_posts_user_id_idx ON users (id);