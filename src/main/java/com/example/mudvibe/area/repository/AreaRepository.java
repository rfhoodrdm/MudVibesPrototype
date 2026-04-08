package com.example.mudvibe.area.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mudvibe.data.area.AreaRecord;

public interface AreaRepository extends JpaRepository<AreaRecord, UUID> {

}
