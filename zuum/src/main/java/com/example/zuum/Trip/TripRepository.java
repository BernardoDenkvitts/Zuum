package com.example.zuum.Trip;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<TripModel, Integer> {
    
    @Query("SELECT COUNT(t) > 0 FROM TripModel t WHERE t.passanger.id = :userId AND t.status = 'PENDING'")
    boolean existsPendingTripRequest(Integer userId);

    List<TripModel> findByStatusAndCreatedAtBefore(TripStatus status, LocalDateTime createdAt);

}