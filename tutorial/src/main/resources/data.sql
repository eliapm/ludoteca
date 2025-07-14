INSERT INTO category(name) VALUES ('Eurogames');
INSERT INTO category(name) VALUES ('Ameritrash');
INSERT INTO category(name) VALUES ('Familiar');

INSERT INTO author(name, nationality) VALUES ('Alan R. Moon', 'US');
INSERT INTO author(name, nationality) VALUES ('Vital Lacerda', 'PT');
INSERT INTO author(name, nationality) VALUES ('Simone Luciani', 'IT');
INSERT INTO author(name, nationality) VALUES ('Perepau Llistosella', 'ES');
INSERT INTO author(name, nationality) VALUES ('Michael Kiesling', 'DE');
INSERT INTO author(name, nationality) VALUES ('Phil Walker-Harding', 'US');

INSERT INTO game(title, age, category_id, author_id) VALUES ('On Mars', '14', 1, 2);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Aventureros al tren', '8', 3, 1);
INSERT INTO game(title, age, category_id, author_id) VALUES ('1920: Wall Street', '12', 1, 4);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Barrage', '14', 1, 3);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Los viajes de Marco Polo', '12', 1, 3);
INSERT INTO game(title, age, category_id, author_id) VALUES ('Azul', '8', 3, 5);


INSERT INTO client(name) VALUES ('Pedro');
INSERT INTO client(name) VALUES ('María');
INSERT INTO client(name) VALUES ('Juan');


INSERT INTO loan(game_id, client_id, start_date, end_date) VALUES (1, 3, '2025-10-14', '2025-10-23');
INSERT INTO loan(game_id, client_id, start_date, end_date) VALUES (3, 3, '2025-10-23', '2025-10-29');
INSERT INTO loan(game_id, client_id, start_date, end_date) VALUES (1, 1, '2025-07-13', '2025-07-23');
INSERT INTO loan(game_id, client_id, start_date, end_date) VALUES (2, 1, '2025-08-01', '2025-08-10');
INSERT INTO loan(game_id, client_id, start_date, end_date) VALUES (4, 1, '2025-09-05', '2025-09-15');
INSERT INTO loan(game_id, client_id, start_date, end_date) VALUES (5, 3, '2025-07-20', '2025-07-25');
