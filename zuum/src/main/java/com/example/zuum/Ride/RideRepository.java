package com.example.zuum.Ride;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<RideModel, Integer> {

    @Query("SELECT COUNT(r) > 0 FROM RideModel r WHERE r.passanger.id = :userId AND r.status = 'PENDING'")
    boolean existsPendingRideRequest(Integer userId);

    List<RideModel> findByStatusAndCreatedAtBefore(RideStatus status, LocalDateTime createdAt);

    @Query(value = "SELECT * FROM ride WHERE ride.status = 'PENDING' AND ST_DWithin(ride.origin, ST_SetSRID(ST_MakePoint(:lat, :longt), 4326)::geography, :maxDistance) ORDER BY ride.id", nativeQuery = true)
    Page<RideModel> findPendingNearbyRides(double lat, double longt, float maxDistance, Pageable pageable);

    @Query("SELECT COUNT(r) > 0 FROM RideModel r WHERE r.driver.id = :driverId AND r.status = 'IN_PROGRESS'")
    boolean driverHasInProgressRide(Integer driverId);

}