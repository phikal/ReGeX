package com.phikal.regex.models;

public interface Input {

    enum Response { OK, ERROR }

    interface StatusCallback {
        void status(Response r, boolean max, String msg);
    }

    void setText(String text);
    void onEdit(StatusCallback sc);

}
