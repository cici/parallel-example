package com.example.workflows;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface ParentWorkflow {
    @WorkflowMethod
    String start();
}
