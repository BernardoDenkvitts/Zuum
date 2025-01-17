package com.example.zuum.Driver;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.example.zuum.Driver.Dto.LocationDTO;
import com.example.zuum.Trip.Dto.TripRequestNotificationDTO;

@Controller
@MessageMapping("/ws/drivers")
public record DriverWebSocketController(DriverService service) {

    @MessageMapping("/request")
    @SendTo("/topic/trip-request")
    public TripRequestNotificationDTO handleNewTripRequest(TripRequestNotificationDTO notificationDTO) {
        return notificationDTO;
    }

    @MessageMapping("/location")
    public void updateCurrentLocation(@Validated LocationDTO dto) {
        service.updateLocation(dto);
    }

}
