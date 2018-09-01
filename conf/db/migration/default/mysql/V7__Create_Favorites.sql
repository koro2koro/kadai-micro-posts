CREATE TABLE `favorites` (
  `id`           BIGINT     AUTO_INCREMENT,
  `user_id`      BIGINT     NOT NULL,
  `micropost_id` BIGINT     NOT NULL,
  `create_at` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_at` TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY(`id`),
  FOREIGN KEY (`user_id`) REFERENCES users(`id`),
  FOREIGN KEY (`micropost_id`) REFERENCES micro_posts(`id`),
  UNIQUE(`user_id`, `micropost_id`)
) ENGINE=InnoDB;