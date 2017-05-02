INSERT INTO users(name,email) VALUES
  ('Zbysiu','zbysiu@gmail.com'),
  ('Mariusz','mariusz@gmail.com');

INSERT INTO chores(chore_id,src_chore_id,created_at,name,points,interval) VALUES
  (1,1,5000000,'kurze',15,5),
  (2,1,5000000,'kurze trudne',15,5),
  (3,2,5000000,'szafki',25,3);

INSERT INTO tasks(user_id,chore_id,assigned_at,completed_at) VALUES
  (1,1,5000000,null),
  (2,2,5000000,50001000)


