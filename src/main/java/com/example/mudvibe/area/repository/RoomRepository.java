package com.example.mudvibe.area.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mudvibe.data.area.RoomRecord;

public interface RoomRepository extends JpaRepository<RoomRecord, Long> {

}
