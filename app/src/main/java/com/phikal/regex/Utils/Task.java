package com.phikal.regex.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Task {

    public static final String
            ID = "id",
            MAX = "max",
            RIGHT = "right",
            WRONG = "wrong";
    public static final String
            GS = "\0x1D", // Group separator
            RS = "\0x1E", // Record separator
            US = "\0x1F"; // Unit separator
    private List<Word> right, wrong;
    private int max;
    private String id;

    public Task(List<Word> right, List<Word> wrong, int max) {
        this.right = right;
        this.wrong = wrong;
        this.max = max;
    }

    public Task(List<Word> right, List<Word> wrong, int max, String id) {
        this.right = right;
        this.wrong = wrong;
        this.max = max;
        this.id = id;
    }

    public static Task parseTask(String s) {
        String[] parts = s.split(GS, -1);
        if (parts.length == 4) {
            List<Word> right = splitList(parts[2]),
                    wrong = splitList(parts[3]);
            int max = Integer.parseInt(parts[1]);
            return new Task(
                    right,
                    wrong,
                    max,
                    parts[0]
            );
        } else return null;
    }

    public static String joinList(List<Word> l) {
        String r = "";
        for (Word w : l)
            r += w.toString() + RS;
        return r.substring(0, r.length() - 2);
    }

    public static List<Word> splitList(String s) {
        String[] l = s.split(RS, -1);
        List<Word> res = new ArrayList<>();
        for (String p : l)
            res.add(Word.parse(p));
        return res;
    }

    public static Task fromJSON(JSONObject o) {
        List<Word> right = new ArrayList<>(),
                wrong = new ArrayList<>();
        try {
            JSONArray rightj = o.getJSONArray(RIGHT),
                    wrongj = o.getJSONArray(WRONG);
            for (int i = 0; i < rightj.length(); i++)
                right.add(Word.fromJSON(rightj.getJSONArray(i)));
            for (int i = 0; i < wrongj.length(); i++)
                wrong.add(Word.fromJSON(wrongj.getJSONArray(i)));
            return new Task(right, wrong, o.getInt(MAX), o.has(ID) ? o.getString(ID) : null);
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    public static Task fromJSON(String json) {
        try {
            return fromJSON(new JSONObject(json));
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    public static JSONObject toJSON(Task task) {
        JSONObject object = new JSONObject();
        JSONArray right = new JSONArray(),
                wrong = new JSONArray();
        try {
            object.put(ID, task.id);
            object.put(MAX, task.max);
            for (Word w : task.right)
                right.put(w.toJSON());
            object.put(RIGHT, right);
            for (Word w : task.wrong)
                wrong.put(w.toJSON());
            object.put(WRONG, wrong);
            return object;
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public List<Word> getRight() {
        return right;
    }

    public List<Word> getWrong() {
        return wrong;
    }

    public int getMax() {
        return max;
    }

    public String getId() {
        return id;
    }

    @Override  // Format: [ID]/[MAX]/RIGHT/WRONG
    public String toString() {
        String s = "";
        s += id + GS;
        s += max + GS;
        s += joinList(right) + GS;
        s += joinList(wrong);
        return s;
    }

    public JSONObject toJSON() {
        return toJSON(this);
    }

}