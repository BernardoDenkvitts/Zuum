package com.example.zuum.Driver;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<DriverModel, Integer> {

    @Query(
        value = "SELECT * FROM driver WHERE ST_DWithin(driver.curr_location, ST_SetSRID(ST_MakePoint(:lat, :longt), 4326)::geography, :distance)",
         nativeQuery = true
    )
    List<DriverModel> getDriverWithinDistance(double lat, double longt, float distance);

    Optional<DriverModel> findByUserId(Integer userId);

}
