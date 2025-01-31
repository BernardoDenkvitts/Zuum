package com.example.zuum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.drools.core.ClockType;
import org.drools.core.time.SessionPseudoClock;
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
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.time.SessionClock;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;

import com.example.zuum.Common.utils;
import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RidePrice;
import com.example.zuum.Ride.RideStatus;
import com.example.zuum.Ride.Dto.PriceRequestDTO;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserType;

public class RuleEngineTest {

    private KieSession kieSession;
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

        kieSession = kieBase.newKieSession();
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
        kieSession.insert(ridePrice);

        LocalDateTime now = LocalDateTime.now();
        IntStream.range(0, 31).forEach(i -> {
            RideModel ride = new RideModel(i, null, null, RideStatus.ACCEPTED,
                    new BigDecimal(10.0), now.minusMinutes(i), LocalTime.now(), null, null, null);

            kieSession.insert(ride);
        });

        kieSession.fireAllRules(match -> match.getRule().getName().equals("high-number-of-rides-in-one-hour"));

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

            kieSession.insert(ride);
        });

        kieSession.insert(ridePrice);
        kieSession.insert(priceRequestDTO);

        kieSession.fireAllRules(match -> match.getRule().getName().equals("active-daily-user"));

        assertEquals(new BigDecimal(7.6).setScale(5, RoundingMode.HALF_UP),
                ridePrice.getPrice().setScale(5, RoundingMode.HALF_UP));
    }

    @Test
    void testFastStop() {
        RidePrice ridePrice = getRidePrice();
        UserModel user = getTestUser();
        PriceRequestDTO priceRequestDTO = getPriceRequestDTO();

        LocalDateTime rideTime = LocalDateTime.now().minusMinutes(10);
        RideModel ride = new RideModel(1, user, null, RideStatus.COMPLETED,
                    new BigDecimal(10.0), rideTime, LocalTime.now(), null, null, null);

        kieSession.insert(ridePrice);
        kieSession.insert(priceRequestDTO);
        kieSession.insert(ride);

        kieSession.fireAllRules(match -> match.getRule().getName().equals("fast-stop"));

        assertEquals(new BigDecimal(7.44).setScale(5, RoundingMode.HALF_UP),
                ridePrice.getPrice().setScale(5, RoundingMode.HALF_UP));
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

        kieSession.insert(ridePrice);
        kieSession.insert(priceRequestDTO);
        kieSession.insert(ride);

        kieSession.fireAllRules(match -> match.getRule().getName().equals("long-ride-last-two-hours"));

        assertEquals(new BigDecimal(7.36).setScale(5, RoundingMode.HALF_UP),
                ridePrice.getPrice().setScale(5, RoundingMode.HALF_UP));
    }

}
