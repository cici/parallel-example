package com.example.activities;

import io.temporal.activity.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityBImpl implements ActivityB {
    private static final Logger logger = LoggerFactory.getLogger(ActivityBImpl.class);

    @Override
    public String processTaskB(String input) {
        try {
            logger.info("Activity B processing: {}", input);
            
            // Simulate longer processing time compared to Activity A
            for (int i = 0; i < 3; i++) {
                Thread.sleep(1000);
                // Send heartbeat for longer-running activity
                Activity.getExecutionContext().heartbeat("Progress: " + (i + 1) + "/3");
            }
            
            String result = input + " - completed with intensive processing";
            logger.info("Activity B completed with result: {}", result);
            return result;
        } catch (InterruptedException e) {
            logger.error("Activity B was interrupted", e);
            Activity.getExecutionContext().heartbeat("Interrupted");
            throw Activity.wrap(e);
        }
    }
}
