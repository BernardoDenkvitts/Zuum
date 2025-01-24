package com.example.zuum.Driver;

import java.security.Principal;

import org.slf4j.Logger;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.example.zuum.Common.utils;
import com.example.zuum.Driver.Dto.LocationDTO;

@Controller
@MessageMapping("/drivers")
public record DriverWebSocketController(DriverService service, DriverNotifier notifier) {
    
    static Logger LOGGER = utils.getLogger(DriverWebSocketController.class);

    @MessageMapping("/location")
    public void updateCurrentLocation(LocationDTO dto, Principal principal) {
        LOGGER.info("Updating driver {} location", dto.driverId());
        service.updateLocation(dto);
        notifier.notifySpecificDriver(principal, "/queue/reply", "Location updated sucessfully");
    }

}
