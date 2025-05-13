package com.example.activities;

import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityAImpl implements ActivityA {
    private static final Logger logger = LoggerFactory.getLogger(ActivityAImpl.class);

    @Override
    public String processTaskA(String input) {
        try {
            logger.info("Activity A processing: {}", input);
            
            // Simulate processing that takes some time
            Thread.sleep(1000);
            
            String result = input + " - completed quickly";
            logger.info("Activity A completed with result: {}", result);
            return result;
        } catch (InterruptedException e) {
            logger.error("Activity A was interrupted", e);
            Activity.getExecutionContext().heartbeat("Interrupted");
            throw Activity.wrap(e);
        }
    }
}

