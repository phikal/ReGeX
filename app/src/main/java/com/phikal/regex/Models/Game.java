package com.phikal.regex.Models;

import com.phikal.regex.Games.TaskGenerationException;

public interface Game {
    interface ProgressCallback {
        void progress(Progress p);
    }

    void onProgress(ProgressCallback pc);
    Task nextTask() throws TaskGenerationException;
}
