package com.example.mudvibe.area.service;

import com.example.mudvibe.data.area.RoomData;

public interface AreaManager {

	RoomData findRoomByLocationId(Long locationId);

	Long getStartingLocationId();

}
