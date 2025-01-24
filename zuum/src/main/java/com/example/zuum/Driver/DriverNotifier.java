package com.example.zuum.Driver;

import java.security.Principal;
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
import com.example.zuum.Ride.Dto.RideRequestNotificationDTO;

@Component
public record DriverNotifier(
    SimpUserRegistry registry,
    SimpMessagingTemplate template,
    DriverService service
) {

    static Logger LOGGER = utils.getLogger(DriverNotifier.class);

    public void notifySpecificDriver(Principal principal, String destination, Object payload) {
        template.convertAndSendToUser(principal.getName(), destination, payload);
    }

    public void newRideRequest(RideRequestNotificationDTO dto) {
        String rideRequestQueue = "/queue/ride-request";
        Set<SimpUser> subscribedDrivers = getSubscribers("/user" + rideRequestQueue);
        List<DriverModel> nearbyDrivers = service.findDriversNearby(dto.origin().getX(), dto.origin().getY(), 6000f);
        List<SimpUser> drivesToNotify = findOnlineNearbyDrivers(subscribedDrivers, nearbyDrivers);
        
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
            template.convertAndSendToUser(driver.getName(), queue, dto);
        }
    }

}
