package com.example.mudvibe.util;

import java.time.Instant;

import org.springframework.stereotype.Component;

/**
 *  Wrapper class to provide current time.
 */
@Component
public class SystemClockUtil {

	public Instant getNow() {
		return Instant.now();
	}
}
