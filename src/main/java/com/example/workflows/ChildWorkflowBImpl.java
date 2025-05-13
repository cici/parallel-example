package com.example.workflows;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import com.example.activities.ActivityB;

import java.time.Duration;

public class ChildWorkflowBImpl implements ChildWorkflowB {
    private static final Logger logger = Workflow.getLogger(ChildWorkflowBImpl.class);

    // Create an activity client stub
    private final ActivityB activityB = Workflow.newActivityStub(
            ActivityB.class,
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
        logger.info("Child workflow B started with task: {}", taskName);

        // Simulate work that takes longer than Child A
        // This will make Child A finish first consistently for the example
        Workflow.sleep(Duration.ofSeconds(5));

        // Execute activity B
        String activityResult = activityB.processTaskB(taskName + " - processed by activity B");

        logger.info("Child workflow B completed with activity result: {}", activityResult);

        return "Result from Child B: " + activityResult;
    }
}
