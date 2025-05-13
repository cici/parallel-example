package com.example.worker;

import com.example.activities.ActivityAImpl;
import com.example.activities.ActivityBImpl;
import com.example.workflows.ChildWorkflowAImpl;
import com.example.workflows.ChildWorkflowBImpl;
import com.example.workflows.ParentWorkflow;
import com.example.workflows.ParentWorkflowImpl;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

/**
 * Example that demonstrates how to start two child workflows in parallel
 * and continue with the result of the one that finishes first.
 */
public class ParallelChildWorkflowsExample {
    public static final String TASK_QUEUE = "ParallelChildWorkflowsTaskQueue";

    public static void main(String[] args) {
        // Create the Temporal service client
        String address = getEnv("TEMPORAL_ADDRESS", "127.0.0.1:7233");
        String namespace = getEnv("TEMPORAL_NAMESPACE", "default");

        WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
        WorkflowClient client = WorkflowClient.newInstance(service);

        // Create a worker factory that can be used to create workers that poll specific task queues
        WorkerFactory factory = WorkerFactory.newInstance(client);

        // Create a worker that polls the task queue
        Worker worker = factory.newWorker(TASK_QUEUE);

        // Register workflow and activity implementations with the worker
        worker.registerWorkflowImplementationTypes(
                ParentWorkflowImpl.class,
                ChildWorkflowAImpl.class,
                ChildWorkflowBImpl.class);
        worker.registerActivitiesImplementations(new ActivityAImpl(), new ActivityBImpl());

        // Start the worker
        factory.start();

        // Start the parent workflow
        ParentWorkflow parentWorkflow = client.newWorkflowStub(
                ParentWorkflow.class,
                WorkflowOptions.newBuilder()
                        .setTaskQueue(TASK_QUEUE)
                        .setWorkflowId("parent-workflow-id")
                        .build());

        // Execute the parent workflow and get the result
        String result = parentWorkflow.start();
        System.out.println("Parent workflow completed with result: " + result);
    }

    private static String getEnv(String key, String fallback) {
        return System.getenv().getOrDefault(key, fallback);
    }
}

