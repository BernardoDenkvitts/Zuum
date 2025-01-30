package com.example.zuum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;

import com.example.zuum.Ride.RideModel;
import com.example.zuum.Ride.RidePrice;
import com.example.zuum.Ride.RideStatus;
import com.example.zuum.Ride.Dto.PriceRequestDTO;
import com.example.zuum.User.UserModel;
import com.example.zuum.User.UserType;

public class RuleEngineTest {

    private KieSession kieSession;


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

    @Test
    void testHighNumberOfRidesInOneHour() {
        RidePrice ridePrice = new RidePrice(10.0);
        kieSession.insert(ridePrice);

        LocalDateTime now = LocalDateTime.now();
        IntStream.range(0, 31).forEach(i -> {
            RideModel ride = new RideModel(i, null, null, RideStatus.ACCEPTED,
            new BigDecimal(10.0), now.minusMinutes(i), null, null, null);
            
            kieSession.insert(ride);
        });
        
        kieSession.fireAllRules(match -> match.getRule().getName().equals("high-number-of-rides-in-one-hour"));

        assertEquals(new BigDecimal(8.8).setScale(5, RoundingMode.HALF_UP), ridePrice.getPrice().setScale(5, RoundingMode.HALF_UP));
    }

    @Test
    void testActiveDailyUser() {
        Integer userId = 1;
        
        RidePrice ridePrice = new RidePrice(10.0);
        UserModel user = new UserModel(userId, "Test User Drools", "testdrools@gmail.com", UserType.PASSANGER, null, null);
        
        PriceRequestDTO priceRequestDTO = new PriceRequestDTO(userId, null, null);

        IntStream.range(0, 3).forEach(i -> {
            LocalDateTime rideTime = LocalDateTime.now().minusHours(i * 5);
            RideModel ride = new RideModel(i, user, null, RideStatus.COMPLETED,
            new BigDecimal(10.0), rideTime, null, null, null);
            
            kieSession.insert(ride);
        });
        
        kieSession.insert(ridePrice);
        kieSession.insert(priceRequestDTO);

        kieSession.fireAllRules(match -> match.getRule().getName().equals("active-daily-user"));

        assertEquals(new BigDecimal(7.6).setScale(5, RoundingMode.HALF_UP), ridePrice.getPrice().setScale(5, RoundingMode.HALF_UP));
    }

}
