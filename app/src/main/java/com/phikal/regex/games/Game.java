package com.phikal.regex.games;

import android.content.Context;

import com.phikal.regex.R;
import com.phikal.regex.games.match.MutMatchTask;
import com.phikal.regex.games.match.SimpleMatchTask;
import com.phikal.regex.games.match.WordTask;
import com.phikal.regex.models.Progress;
import com.phikal.regex.models.Task;

import java.lang.reflect.InvocationTargetException;

public enum Game {

    SIMPLE_MATCH(SimpleMatchTask.class, R.string.simple_match),
    MUTATE_MATCH(MutMatchTask.class, R.string.mutate_match),
    WORD_MATCH(WordTask.class, R.string.word_match);

    public static final Game DEFAULT_GAME = SIMPLE_MATCH;

    public final int name;
    public final Class<? extends Task> taskClass;

    Game(Class<? extends Task> taskClass, int name) {
        this.taskClass = taskClass;
        this.name = name;
    }

    public static Game getGame(Class<? extends Task> gen) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].taskClass.equals(gen))
                return values()[i];
        }
        return null;
    }

    public Task nextTask(Context ctx, Progress.ProgressCallback pc) {
        try {
            return taskClass.getDeclaredConstructor(Context.class, Progress.class, Progress.ProgressCallback.class)
                    .newInstance(ctx, getProgress(ctx, pc), pc);
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
        } catch (NoSuchMethodException nsme) {
            nsme.printStackTrace();
        } catch (InstantiationException ie) {
            ie.printStackTrace();
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
        return null;
    }

    public Progress getProgress(Context ctx, Progress.ProgressCallback pc) {
        return new Progress(ctx, name());
    }
}
