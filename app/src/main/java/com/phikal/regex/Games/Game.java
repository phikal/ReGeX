package com.phikal.regex.Games;

import com.phikal.regex.Models.Task;

public interface Game {
    Task nextTask() throws TaskGenerationException;
}
