package com.phikal.regex.games.extract;

import com.phikal.regex.games.Games;
import com.phikal.regex.models.Game;
import com.phikal.regex.models.Collumn;
import com.phikal.regex.models.Input;
import com.phikal.regex.models.Task;

import java.util.List;

public class SimpleMatchExtractGame implements Game {

    @Override
    public com.phikal.regex.models.Task nextTask() {
        return new Task() {
            @Override
            public List<Collumn> getCollumns() {
                return null;
            }

            @Override
            public List<Input> getInputs() {
                return null;
            }
        };
    }

    @Override
    public Games getGame() {
        return null;
    }

    @Override
    public void onProgress(ProgressCallback pc) {

    }
}
