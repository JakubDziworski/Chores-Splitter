CREATE TABLE IF NOT EXISTS users
(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR NOT NULL,
  email VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS chores
(
  chore_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  src_chore_id BIGINT REFERENCES chores(chore_id),
  created_at TIMESTAMP NOT NULL,
  name VARCHAR NOT NULL,
  points INTEGER NOT NULL,
  interval INTEGER
);

CREATE TABLE IF NOT EXISTS tasks
(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL REFERENCES users(id),
  chore_id BIGINT NOT NULL REFERENCES chores(chore_id),
  assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
  completed_at TIMESTAMP
);
--
-- INSERT INTO users(name,email) VALUES
--   ('Zbysiu','zbysiu@gmail.com'),
--   ('Mariusz','mariusz@gmail.com');
--
-- INSERT INTO chores(created_at,name,points,interval) VALUES
--   (CURRENT_TIMESTAMP(),'kurze',15,5),
--   (CURRENT_TIMESTAMP(),'szafki',25,3);
--
-- INSERT INTO tasks(user_id,chore_id,assigned_at,completed_at) VALUES
--   (1,1,CURRENT_TIMESTAMP(),null),
--   (2,2,CURRENT_TIMESTAMP(),CURRENT_TIMESTAMP())


