package com.example.zuum.Drools;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.example.zuum.Common.utils;
import com.example.zuum.Ride.RideModel;

@Component
public class DroolsService {

    private final KieSession kieSession;
    static final Logger LOGGER = utils.getLogger(DroolsService.class);

    public DroolsService(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    public FactHandle insert(Object object) {
        LOGGER.info("Inserting {} into Drools", object.getClass());

        return kieSession.insert(object);
    }

    public int fireAllRules() {
        int fired = kieSession.fireAllRules();
        LOGGER.info("Number of rules fired -> {}", fired);
        
        return fired;
    }
    
    public int fireSpecificRule(String ruleName) {
        int fired = kieSession.fireAllRules(match -> match.getRule().getName().equals(ruleName));
        LOGGER.info("Number of rules fired -> {}", fired);
        
        return fired;
    }
    
    public void updateRideModel(RideModel newObject) {
        LOGGER.info("Updating Drools' data");
        kieSession.insert(newObject.getId());
        kieSession.insert(newObject.getStatus());
        fireSpecificRule("update-ride-status");
    }

    public void showObjectsInsideMemory() {
        var objects = kieSession.getObjects();
        for (Object object : objects) {
            LOGGER.info("OBJECT -> {}", object.getClass());
        }
    }

    /**
     *   Avoid to keep FACTS inside the memory
     *   We want only EVENTS (RideModel)
     */
    public void deleteFacts() {
        var objects = kieSession.getObjects();
        for (Object object : objects) {
            if ((object instanceof RideModel) == false) {
                LOGGER.info("OBJECT TO BE DELETED -> {}", object.getClass());
                kieSession.delete(kieSession.getFactHandle(object));
            }
        }
    }

}
