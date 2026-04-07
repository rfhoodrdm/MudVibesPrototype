package com.example.mudvibe.area;

import java.util.Optional;

import com.example.mudvibe.data.area.Room;

public interface AreaManager {

	Room findRoomByLocationId(Optional<Long> locationIdMaybe);

	Long getStartingLocationId();

}
