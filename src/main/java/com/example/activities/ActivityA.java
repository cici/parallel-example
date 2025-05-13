package com.example.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ActivityA {
    @ActivityMethod
    String processTaskA(String input);
}
