CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO rooms (location_id, title, description)
VALUES 
(1, 'Middle of Nowhere', 'You find yourself in the middle of nowhere.'),
(2, 'Middle of Nowhere', 'You find yourself in the middle of nowhere.'),
(3, 'Middle of Nowhere', 'You find yourself in the middle of nowhere.'),
(4, 'Middle of Nowhere', 'You find yourself in the middle of nowhere.'),
(5, 'Middle of Nowhere', 'You find yourself in the middle of nowhere.'),
(6, 'Middle of Nowhere', 'You find yourself in the middle of nowhere.');

INSERT INTO directional_exit (exit_id, parent_room_id, movement_direction, destination_room_id)
VALUES
    (gen_random_uuid(), 1, 'NORTH',  2),
    (gen_random_uuid(), 1, 'EAST',   3),
    (gen_random_uuid(), 1, 'SOUTH',  4),
    (gen_random_uuid(), 1, 'WEST',   5),
    (gen_random_uuid(), 1, 'DOWN',   6),
    (gen_random_uuid(), 2, 'SOUTH',  1),
    (gen_random_uuid(), 3, 'WEST',   1),
    (gen_random_uuid(), 4, 'NORTH',  1),
    (gen_random_uuid(), 5, 'EAST',   1),
    (gen_random_uuid(), 6, 'UP',     1);
