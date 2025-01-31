package com.example.zuum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;

import com.example.zuum.Common.utils;
import com.example.zuum.Drools.DroolsService;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RidePrice;
import com.example.zuum.Ride.RideStatus;
import com.example.zuum.Ride.Dto.PriceRequestDTO;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserType;

public class RuleEngineTest {

    private DroolsService droolsService;
    static Logger log = utils.getLogger(RuleEngineTest.class);
    private Integer userId = 1;

    @BeforeEach
    void setup() {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/rules.drl"));
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();

        KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

        KieBaseConfiguration kieBaseConfiguration = kieServices.newKieBaseConfiguration();
        kieBaseConfiguration.setOption(EventProcessingOption.STREAM);

        KieBase kieBase = kieContainer.newKieBase(kieBaseConfiguration);

        droolsService = new DroolsService(kieBase.newKieSession());
    }

    private UserModel getTestUser() {
        return new UserModel(userId, "Test User Drools", "testdrools@gmail.com", UserType.PASSANGER, null,
        null);
    }

    private RidePrice getRidePrice() {
        return new RidePrice(10.0);
    }

    private PriceRequestDTO getPriceRequestDTO() {
        return new PriceRequestDTO(userId, null, null);
    }

    @Test
    void testHighNumberOfRidesInOneHour() {
        RidePrice ridePrice = getRidePrice();
        droolsService.insert(ridePrice);

        LocalDateTime now = LocalDateTime.now();
        IntStream.range(0, 31).forEach(i -> {
            RideModel ride = new RideModel(i, null, null, RideStatus.ACCEPTED,
                    new BigDecimal(10.0), now.minusMinutes(i), LocalTime.now(), null, null, null);

            droolsService.insert(ride);
        });

        droolsService.fireSpecificRule("high-number-of-rides-in-one-hour");

        assertEquals(new BigDecimal(8.8).setScale(5, RoundingMode.HALF_UP),
                ridePrice.getPrice().setScale(5, RoundingMode.HALF_UP));
    }

    @Test
    void testActiveDailyUser() {
        RidePrice ridePrice = getRidePrice();
        UserModel user = getTestUser();
        PriceRequestDTO priceRequestDTO = getPriceRequestDTO();

        IntStream.range(0, 3).forEach(i -> {
            LocalDateTime rideTime = LocalDateTime.now().minusHours(5 * i);
            RideModel ride = new RideModel(1, user, null, RideStatus.COMPLETED, new BigDecimal(10.0), rideTime, LocalTime.now(), null,
                    null, null);

            droolsService.insert(ride);
        });

        droolsService.insert(ridePrice);
        droolsService.insert(priceRequestDTO);

        droolsService.fireSpecificRule("active-daily-user");

        assertEquals(new BigDecimal(7.6).setScale(2, RoundingMode.HALF_UP),
                ridePrice.getPrice().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testFastStop() {
        RidePrice ridePrice = getRidePrice();
        UserModel user = getTestUser();
        PriceRequestDTO priceRequestDTO = getPriceRequestDTO();

        LocalDateTime rideTime = LocalDateTime.now().minusMinutes(10);
        RideModel ride = new RideModel(1, user, null, RideStatus.COMPLETED,
                    new BigDecimal(10.0), rideTime, LocalTime.now(), null, null, null);

        droolsService.insert(ridePrice);
        droolsService.insert(priceRequestDTO);
        droolsService.insert(ride);

        droolsService.fireSpecificRule("fast-stop");

        assertEquals(new BigDecimal(7.44).setScale(2, RoundingMode.HALF_UP),
                ridePrice.getPrice().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void longRideLastTwoHours() {
        RidePrice ridePrice = getRidePrice();
        UserModel user = getTestUser();
        PriceRequestDTO priceRequestDTO = getPriceRequestDTO();

        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(34);
        LocalTime startTime = LocalTime.now().minusMinutes(31);
        LocalTime endTime = LocalTime.now();
        RideModel ride = new RideModel(1, user, null, RideStatus.COMPLETED,
                    new BigDecimal(10.0), createdAt, startTime, endTime, null, null);

        droolsService.insert(ridePrice);
        droolsService.insert(priceRequestDTO);
        droolsService.insert(ride);

        droolsService.fireSpecificRule("long-ride-last-two-hours");

        assertEquals(new BigDecimal(7.36).setScale(2, RoundingMode.HALF_UP),
                ridePrice.getPrice().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void shouldFireTwoRules() {
        RidePrice ridePrice = getRidePrice();
        UserModel user = getTestUser();
        PriceRequestDTO priceRequestDTO = getPriceRequestDTO();

        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(5);
        RideModel ride = new RideModel(1, user, null, RideStatus.COMPLETED,
                    new BigDecimal(10.0), createdAt, null, null, null, null);
        
        droolsService.insert(ride);
        
        LocalDateTime createdAt2 = LocalDateTime.now().minusMinutes(3);
        RideModel ride2 = new RideModel(2, user, null, RideStatus.COMPLETED,
        new BigDecimal(10.0), createdAt2, null, null, null, null);
        
        droolsService.insert(ride2);

        droolsService.insert(priceRequestDTO);
        droolsService.insert(ridePrice);

        int fired = droolsService.fireAllRules();
        assertEquals(2, fired);
        
        assertEquals(new BigDecimal(6.84).setScale(2, RoundingMode.HALF_UP), ridePrice.getPrice().setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void shouldFireThreeRules() {
        RidePrice ridePrice = getRidePrice();
        UserModel user = getTestUser();
        PriceRequestDTO priceRequestDTO = getPriceRequestDTO();

        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(5);
        RideModel ride = new RideModel(1, user, null, RideStatus.COMPLETED,
                    new BigDecimal(10.0), createdAt, null, null, null, null);
        
        droolsService.insert(ride);
        
        LocalDateTime createdAt2 = LocalDateTime.now().minusMinutes(3);
        RideModel ride2 = new RideModel(2, user, null, RideStatus.COMPLETED,
        new BigDecimal(10.0), createdAt2, null, null, null, null);
        
        droolsService.insert(ride2);
        
        LocalDateTime createdAt3 = LocalDateTime.now().minusHours(24);
        RideModel ride3 = new RideModel(2, user, null, RideStatus.COMPLETED,
        new BigDecimal(10.0), createdAt3, null, null, null, null);
        
        droolsService.insert(ride3);
        
        droolsService.insert(priceRequestDTO);
        droolsService.insert(ridePrice);

        int fired = droolsService.fireAllRules();
        assertEquals(3, fired);
        
        assertEquals(new BigDecimal(6.5).setScale(2, RoundingMode.HALF_UP), ridePrice.getPrice().setScale(2, RoundingMode.HALF_UP));
    }

}
