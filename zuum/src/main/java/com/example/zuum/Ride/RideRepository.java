package com.example.zuum.Ride;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<RideModel, Integer> {

    List<RideModel> findByStatusAndCreatedAtBefore(RideStatus status, LocalDateTime createdAt);

    @Query(value = "SELECT * FROM ride WHERE ride.status = 'PENDING' AND ST_DWithin(ride.origin, ST_SetSRID(ST_MakePoint(:longt, :lat), 4326)::geography, :maxDistance) ORDER BY ride.id", nativeQuery = true)
    Page<RideModel> findPendingNearbyRides(double longt, double lat, float maxDistance, Pageable pageable);

    @Query("SELECT r FROM RideModel r WHERE ((:isDriver = true AND r.driver.id = :userId) OR (:isDriver = false AND r.passenger.id = :userId)) AND r.status != 'COMPLETED'")
    Optional<RideModel> findActiveRideByUser(Integer userId, boolean isDriver);
}