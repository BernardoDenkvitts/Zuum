package com.example.zuum.Driver;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<DriverModel, Integer> {

    @Query(
        value = "SELECT * FROM driver WHERE ST_Distance(driver.curr_location, ST_MakePoint(:lat, :longt)) <= 1000",
        nativeQuery = true
    )
    List<DriverModel> getDriverByDistance(double lat, double longt);

    Optional<DriverModel> findByUserId(Integer userId);

}
