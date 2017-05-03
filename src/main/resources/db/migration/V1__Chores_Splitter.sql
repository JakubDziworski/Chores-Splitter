CREATE TABLE IF NOT EXISTS users
(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR NOT NULL,
  email VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS chores
(
  chore_id BIGINT PRIMARY KEY AUTO_INCREMENT,
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

CREATE TABLE IF NOT EXISTS PENALTIES (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL REFERENCES users(id),
  points INT NOT NULL,
  reason VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS TASKS_DISPATCHES
(
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  dispatched_at BIGINT NOT NULL,
);
--
-- INSERT INTO users(name,email) VALUES
--   ('Zbysiu','zbysiu@gmail.com'),
--   ('Mariusz','mariusz@gmail.com');
--
-- INSERT INTO chores(chore_id,src_chore_id,created_at,name,points,interval) VALUES
--   (1,1,5000000,'kurze',15,5),
--   (2,1,5000000,'kurze trudne',15,5),
--   (3,2,5000000,'szafki',25,3);
--
-- INSERT INTO tasks(user_id,chore_id,assigned_at,completed_at) VALUES
--   (1,1,5000000,null),
--   (2,2,5000000,50001000);
--
-- INSERT INTO PENALTIES(user_id, points, reason) VALUES (1,25,'did not sweep');