package com.example.workflows;

import io.temporal.workflow.Async;
import io.temporal.workflow.ChildWorkflowOptions;
import io.temporal.workflow.Promise;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

public class ParentWorkflowImpl implements ParentWorkflow {
    private static final Logger logger = Workflow.getLogger(ParentWorkflowImpl.class);

    @Override
    public String start() {
        logger.info("Parent workflow started");

        // Create child workflow A with options
        ChildWorkflowA childA = Workflow.newChildWorkflowStub(
                ChildWorkflowA.class,
                ChildWorkflowOptions.newBuilder()
                        .setWorkflowId("child-workflow-a-" + Workflow.getInfo().getWorkflowId())
                        .build());

        // Create child workflow B with options
        ChildWorkflowB childB = Workflow.newChildWorkflowStub(
                ChildWorkflowB.class,
                ChildWorkflowOptions.newBuilder()
                        .setWorkflowId("child-workflow-b-" + Workflow.getInfo().getWorkflowId())
                        .build());

        // Start both child workflows asynchronously
        Promise<String> childAResult = Async.function(childA::execute, "Task A");
        Promise<String> childBResult = Async.function(childB::execute, "Task B");

        // Create a Promise that will be ready when either of the child workflows completes
        Promise<Object> firstResult = Promise.anyOf(childAResult, childBResult);

        // Wait for the first result
        String result = (String) firstResult.get();
        logger.info("First child workflow completed with result: {}", result);

        // Note: The other child workflow will continue running in the background
        // You could also implement additional logic here to either:
        // 1. Wait for the other workflow to complete as well
        // 2. Cancel the other workflow if needed
        // 3. Do something with both results when they are ready

        return "Completed with first result: " + result;
    }
}
