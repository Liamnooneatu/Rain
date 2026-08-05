package com.weatherapi.repository;

import com.weatherapi.model.SensorMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorMetadataRepository extends JpaRepository<SensorMetadata, String> {
}
