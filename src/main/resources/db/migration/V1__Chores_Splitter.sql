CREATE TABLE IF NOT EXISTS users
(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR NOT NULL,
  email VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS chores
(
  chore_id BIGINT PRIMARY KEY,
  src_chore_id BIGINT NOT NULL REFERENCES chores(chore_id),
  created_at BIGINT NOT NULL,
  name VARCHAR NOT NULL,
  points INTEGER NOT NULL,
  interval INTEGER
);

CREATE TABLE IF NOT EXISTS tasks
(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL REFERENCES users(id),
  chore_id BIGINT NOT NULL REFERENCES chores(chore_id),
  assigned_at BIGINT NOT NULL,
  completed_at BIGINT
);

CREATE TABLE IF NOT EXISTS tasks_dispatches
(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dispatched_at BIGINT NOT NULL,
);
