package com.example.zuum.Driver;

import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.zuum.Common.utils;
import com.example.zuum.Driver.Dto.LocationDTO;
import com.example.zuum.Trip.Dto.TripRequestNotificationDTO;

@Controller
@MessageMapping("/ws/drivers")
public record DriverWebSocketController(DriverService service, SimpMessagingTemplate template) {
    
    static Logger LOGGER = utils.getLogger(DriverWebSocketController.class);
    
    @MessageMapping("/request")
    @SendTo("/topic/trip-request")
    public TripRequestNotificationDTO handleNewTripRequest(TripRequestNotificationDTO notificationDTO) {
        LOGGER.info("Notifying new trip request");

        return notificationDTO;
    }

    @MessageMapping("/location")
    public void updateCurrentLocation(LocationDTO dto, @Header("simpSessionId") String sessionId) {
        LOGGER.info("Updating driver {} location", dto.driverId());
        service.updateLocation(dto);
    }

}
