package com.example.zuum.Financial;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.zuum.Financial.Dto.TotalRideAmountDTO;

@RestController
@RequestMapping("/financial")
public record FinancialController(FinancialService service) {

    @GetMapping("/{driverId}/earn")
    public ResponseEntity<TotalRideAmountDTO> getDriverEarns(
        @PathVariable Integer driverId,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        TotalRideAmountDTO dto = new TotalRideAmountDTO(service.getSumPriceBetweenDates(null, driverId, startDate, endDate));
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{passengerId}/expense")
    public ResponseEntity<TotalRideAmountDTO> getPassengerExpenses(
        @PathVariable Integer passengerId,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        TotalRideAmountDTO dto = new TotalRideAmountDTO(service.getSumPriceBetweenDates(passengerId, null, startDate, endDate));
        
        return ResponseEntity.ok(dto);
    }

}
