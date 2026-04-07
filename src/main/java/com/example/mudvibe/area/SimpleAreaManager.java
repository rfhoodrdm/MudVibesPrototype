package com.example.mudvibe.area;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.mudvibe.data.area.Room;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimpleAreaManager implements AreaManager {
	
	public static final long NOWHERE_LOCATION_ID = 0;
	
	
    /* ********************************************************
     * 					    Public Methods
     * ********************************************************/
	
	@Override
	public Room findRoomByLocationId(Optional<Long> locationIdMaybe) {
		return noWhere();	//TODO implement actual lookup.
	}
	
	@Override
	public Long getStartingLocationId() {
		return noWhere().getLocationId();	//stub value for now. defaults to nowhere.
	}
     
    /* ********************************************************
     * 					    Helper Methods
     * ********************************************************/
	
	private Room noWhere() {
		Room nowhere = new Room();
		
		String roomTitle = "Nowhere";
		String roomDescription = "You stand within a hollow dome made of apparently transparent force, you look overhead and behold a sea of stars floating against a sea of blackness, an almost incomprehensible distance from where you are. It is neither hot nor cold here, and time seems to have left this place forgotten and untouched for uncounted ages.";
		
		nowhere.setTitle(roomTitle);
		nowhere.setDescription(roomDescription);
		nowhere.setLocationId(NOWHERE_LOCATION_ID);
		
		return nowhere;
	}

    
    /* ********************************************************
     * 	                    Deferred Methods
     * ********************************************************/
     
    /* ********************************************************
     * 	               Static Methods and Members
     * ********************************************************/
}
