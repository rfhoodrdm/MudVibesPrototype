package com.example.mudvibe.service.area;

import org.springframework.stereotype.Service;

import com.example.mudvibe.data.area.Room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleAreaManagerService implements AreaManagerService {
	
	/**
	 * Lookup the room by key(location id).
	 * If location is null, or 0, or the location doesn't match a room that we currently have defined, then we send the default (Nowhere).
	 */
	@Override
	public Room findRoomByLocationId(Long locationId) {
		
		//TODO: implementm an actual lookup.
		Room nowhere = new Room();
		
		String roomTitle = "Nowhere";
		String roomDescription = "You stand within a hollow dome made of apparently transparent force, you look overhead and behold a sea of stars floating against a sea of blackness, an almost incomprehensible distance from where you are. It is neither hot nor cold here, and time seems to have left this place forgotten and untouched for uncounted ages.";
		
		nowhere.setTitle(roomTitle);
		nowhere.setDescription(roomDescription);
		
		return nowhere;
	}
	
	
}
