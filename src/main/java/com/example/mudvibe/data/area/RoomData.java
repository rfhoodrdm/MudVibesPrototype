package com.example.mudvibe.data.area;

import java.util.List;

public interface RoomData {

	long getLocationId();
	String getTitle();
	String getDescription();
	List<DirectionalExitData> getDirectionalExits();
}
