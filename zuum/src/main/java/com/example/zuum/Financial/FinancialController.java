package com.example.zuum.Financial;

import java.time.LocalDate;

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

    @GetMapping("/{userId}/earn")
    public ResponseEntity<TotalRideAmountDTO> getDriverEarns(
        @PathVariable Integer userId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        TotalRideAmountDTO dto = new TotalRideAmountDTO(service.getSumPriceBetweenDates(userId, startDate, endDate, true));
        
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{userId}/expense")
    public ResponseEntity<TotalRideAmountDTO> getUserExpenses(
        @PathVariable Integer userId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        TotalRideAmountDTO dto = new TotalRideAmountDTO(service.getSumPriceBetweenDates(userId, startDate, endDate, false));
        
        return ResponseEntity.ok(dto);
    }

}
