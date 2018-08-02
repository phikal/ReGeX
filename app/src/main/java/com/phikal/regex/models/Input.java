package com.phikal.regex.models;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class Input implements TextWatcher {

    private StatusCallback statusCallback;

    @Override
    public abstract void afterTextChanged(Editable text);

    public void setStatusCallback(StatusCallback sc) {
        this.statusCallback = sc;
    }

    protected void updateStatus(Response resp, String msg) {
        if (statusCallback != null)
            statusCallback.status(resp, msg);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    public enum Response {OK, ERROR}

    public interface StatusCallback {
        void status(Response resp, String msg);
    }
}
