/*
 * This repository interface provides database access operations for the
 * SensorMetadata entity.
 *
 * It extends JpaRepository, which provides built-in CRUD functionality for
 * managing sensor metadata records, including creating, retrieving,
 * updating, and deleting sensor information.
 *
 * The repository uses String as the ID type because the sensorId field in
 * the SensorMetadata entity is used as the primary key.
 *
 * This interface allows the service layer to interact with the sensor
 * metadata database table without requiring manual SQL queries.
 */

package com.weatherapi.repository;

import com.weatherapi.model.SensorMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorMetadataRepository extends JpaRepository<SensorMetadata, String> {
}
