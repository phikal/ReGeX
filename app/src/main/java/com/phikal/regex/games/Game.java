package com.phikal.regex.games;

import android.content.Context;

import com.phikal.regex.R;
import com.phikal.regex.games.match.IntroMatchTask;
import com.phikal.regex.games.match.MutMatchTask;
import com.phikal.regex.games.match.SimpleMatchTask;
import com.phikal.regex.games.match.WordTask;
import com.phikal.regex.models.Progress;
import com.phikal.regex.models.Task;

import java.lang.reflect.InvocationTargetException;

public enum Game {

    SIMPLE_MATCH(SimpleMatchTask.class, R.string.simple_match),
    MUTATE_MATCH(MutMatchTask.class, R.string.mutate_match),
    WORD_MATCH(WordTask.class, R.string.word_match),
    INTRO_MATCH(IntroMatchTask.class, R.string.intro_match);

    public static final Game DEFAULT_GAME = SIMPLE_MATCH;

    public final int name;
    public final Class<? extends Task> taskClass;

    Game(Class<? extends Task> taskClass, int name) {
        this.taskClass = taskClass;
        this.name = name;
    }

    public Progress getProgress(Context ctx) {
        return new Progress(ctx, name());
    }

    public Task nextTask(Context ctx, Progress.ProgressCallback pc) {
        try {
            return taskClass.getDeclaredConstructor(Context.class, Game.class, Progress.class, Progress.ProgressCallback.class)
                    .newInstance(ctx, this, getProgress(ctx), pc);
        } catch (IllegalAccessException iea) {
            iea.printStackTrace();
            throw new RuntimeException(); // really, really shoudln't happen
        } catch (InstantiationException ie) {
            ie.printStackTrace();
            throw new RuntimeException(); // really, really shoudln't happen
        } catch (NoSuchMethodException nsme) {
            nsme.printStackTrace();
            throw new RuntimeException(); // really, really shoudln't happen
        } catch (InvocationTargetException ite) {
            ite.printStackTrace();
            throw new RuntimeException(); // really, really shoudln't happen
        }
    }
}
