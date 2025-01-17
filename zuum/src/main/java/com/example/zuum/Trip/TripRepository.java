package com.example.zuum.Trip;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<TripModel, Integer> {
    
    @Query("SELECT COUNT(t) > 0 FROM TripModel t WHERE t.userId = :userId AND t.status = 'WAITING'")
    boolean existsPendingTripRequest(Integer userId);
}