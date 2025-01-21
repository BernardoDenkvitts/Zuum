package com.example.zuum.Driver;


import java.security.Principal;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.example.zuum.Trip.Dto.TripRequestNotificationDTO;

@Component
public record DriverNotifier(SimpMessagingTemplate template) {

    public void newTripRequest(TripRequestNotificationDTO dto) {
        template.convertAndSend("/topic/trip-request", dto);
    }

    public void notifySpecificDriver(Principal principal, String destination, Object payload) {
        template.convertAndSendToUser(principal.getName(), destination, payload);
    }

}
