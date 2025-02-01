package com.example.zuum.Notification;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import com.example.zuum.Common.utils;
import com.example.zuum.Driver.DriverModel;
import com.example.zuum.Notification.Dto.WsMessageDTO;
import com.example.zuum.Notification.Dto.WsMessageType;
import com.example.zuum.Notification.Exception.UserIsNotConnectedException;
import com.example.zuum.Ride.RideStatus;
import com.example.zuum.Ride.Dto.RideRequestNotificationDTO;
import com.example.zuum.Ride.Dto.RideResponseDTO;

@Component
public record WsNotifier(
    SimpUserRegistry registry,
    SimpMessagingTemplate template
) {

    static Logger LOGGER = utils.getLogger(WsNotifier.class);

    public void notifyUser(String userIdentifier, String destination, Object payload) {
        
        if (registry.getUser(userIdentifier) == null) {
            // Throw this error only if the driver is accepting the Ride but the 
            // passanger is not connected
            if (payload instanceof RideResponseDTO && ((RideResponseDTO)payload).status() == RideStatus.ACCEPTED) {
                LOGGER.info("User {} is not connected", userIdentifier);
                throw new UserIsNotConnectedException("The user to be notified is not connected");
            }
        }

        LOGGER.info("Sending payload to User {}", userIdentifier);
        template.convertAndSendToUser(userIdentifier, destination, payload);
    }

    public void newRideRequest(RideRequestNotificationDTO dto, List<DriverModel> drivers) {
        String rideRequestQueue = "/queue/driver/ride-request";
        Set<SimpUser> subscribedDrivers = getSubscribers("/user" + rideRequestQueue);
        List<SimpUser> drivesToNotify = findOnlineNearbyDrivers(subscribedDrivers, drivers);

        notifyDrivers(drivesToNotify, rideRequestQueue, dto);
    }

    // Get drivers subscribed in destination (queue/topic)
    private Set<SimpUser> getSubscribers(String destination) {
        Set<SimpUser> subscribers = new HashSet<>();
        Set<SimpSubscription> subscriptions = registry
                .findSubscriptions(sub -> sub.getDestination().equals(destination));
        for (SimpSubscription sub : subscriptions) {
            subscribers.add(sub.getSession().getUser());
        }

        return subscribers;
    }

    private List<SimpUser> findOnlineNearbyDrivers(Set<SimpUser> subscribedDrivers, List<DriverModel> nearbyDrivers) {
        return subscribedDrivers.stream()
                .filter(sub -> nearbyDrivers.stream()
                        .anyMatch(driver -> String.valueOf(driver.getUser().getId()).equals(sub.getName())))
                .toList();
    }

    private void notifyDrivers(List<SimpUser> driversToNotify, String queue, RideRequestNotificationDTO dto) {
        for (SimpUser driver : driversToNotify) {
            LOGGER.info("Sending new ride request to driver {}", driver.getName());
            template.convertAndSendToUser(driver.getName(), queue, new WsMessageDTO(WsMessageType.RIDE_REQUEST, dto));
        }
    }

}
