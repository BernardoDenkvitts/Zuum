package com.example.zuum.Driver;

import java.security.Principal;

import org.slf4j.Logger;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.zuum.Common.utils;
import com.example.zuum.Driver.Dto.LocationDTO;
import com.example.zuum.Notification.WsNotifier;

@Controller
@MessageMapping("/drivers")
public record DriverWebSocketController(DriverService service, WsNotifier notifier) {

    static Logger LOGGER = utils.getLogger(DriverWebSocketController.class);

    @MessageMapping("/location")
    public void updateCurrentLocation(LocationDTO dto, Principal principal) {
        LOGGER.info("Updating driver {} location", dto.driverId());
        service.updateLocation(dto);
        notifier.notifyUser(principal.getName(), "/queue/reply", "Location updated sucessfully");
    }

}
