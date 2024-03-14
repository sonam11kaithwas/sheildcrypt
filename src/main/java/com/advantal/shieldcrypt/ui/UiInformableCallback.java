package com.advantal.shieldcrypt.ui;

public interface UiInformableCallback<T> extends UiCallback<T> {
    void inform(String text);
}
