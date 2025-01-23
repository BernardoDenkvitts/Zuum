package com.example.zuum.Ride;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<RideModel, Integer> {

    @Query("SELECT COUNT(t) > 0 FROM RideModel t WHERE t.passanger.id = :userId AND t.status = 'PENDING'")
    boolean existsPendingRideRequest(Integer userId);

    List<RideModel> findByStatusAndCreatedAtBefore(RideStatus status, LocalDateTime createdAt);

}