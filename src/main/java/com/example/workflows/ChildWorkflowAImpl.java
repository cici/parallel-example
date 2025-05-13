package com.example.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import com.example.activities.ActivityA;

import java.time.Duration;

public class ChildWorkflowAImpl implements ChildWorkflowA {
    private static final Logger logger = Workflow.getLogger(ChildWorkflowAImpl.class);

    // Create an activity client stub
    private final ActivityA activityA = Workflow.newActivityStub(
            ActivityA.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(10))
                    .setRetryOptions(RetryOptions.newBuilder()
                    .setInitialInterval(Duration.ofSeconds(1))
                    .setBackoffCoefficient(2)
                    .setMaximumInterval(Duration.ofSeconds(30))
                    .build())
                .build());
                    
    @Override
    public String execute(String taskName) {
        logger.info("Child workflow A started with task: {}", taskName);

        // Simulate work that takes some time - usually this would be 
        // a real operation, potentially calling activities
        Workflow.sleep(Duration.ofSeconds(3));

        // Execute activity A
        String activityResult = activityA.processTaskA(taskName + " - processed by activity A");

        logger.info("Child workflow A completed with activity result: {}", activityResult);

        return "Result from Child A: " + activityResult;
    }
}
