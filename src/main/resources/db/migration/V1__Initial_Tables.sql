CREATE TABLE IF NOT EXISTS area (
    area_id UUID PRIMARY KEY,
    area_name VARCHAR(255) NOT NULL,
    area_description TEXT NOT NULL,
    min_room_id BIGINT NOT NULL,
    max_room_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS rooms (
    location_id BIGINT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS directional_exit (
    exit_id UUID PRIMARY KEY,
    parent_room_id BIGINT NOT NULL,
    movement_direction VARCHAR(32) NOT NULL,
    destination_room_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS player_character_data_record (
    character_id UUID PRIMARY KEY,
    player_id UUID NOT NULL,
    character_name VARCHAR(255) NOT NULL UNIQUE,
    location_id BIGINT NOT NULL,
    created_on TIMESTAMP NOT NULL
);
