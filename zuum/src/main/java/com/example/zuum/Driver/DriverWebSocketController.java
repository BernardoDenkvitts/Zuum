package com.example.zuum.Driver;

import java.security.Principal;

import org.slf4j.Logger;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.zuum.Common.utils;
import com.example.zuum.Driver.Dto.LocationDTO;
import com.example.zuum.Notification.WsNotifier;
import com.example.zuum.Notification.Dto.WsMessageDTO;
import com.example.zuum.Notification.Dto.WsMessageType;

@Controller
public record DriverWebSocketController(DriverService service, WsNotifier notifier) {

    static Logger LOGGER = utils.getLogger(DriverWebSocketController.class);

    private static final String queue = "/queue/driver/reply"; 

    @MessageMapping("/drivers/location")
    public void updateCurrentLocation(LocationDTO dto, Principal principal) {
        try {
            LOGGER.info("Updating driver {} location", dto.driverId());
            service.updateLocation(dto);
            notifier.notifyUser(principal.getName(), queue, new WsMessageDTO(WsMessageType.DRIVER_LOCATION_UPDATE, "Updated"));
        } catch(Exception e) {
            notifier.notifyUser(principal.getName(), queue, new WsMessageDTO(WsMessageType.ERROR, "Error: " + e.getMessage()));
        }
    }

}
