package com.advantal.shieldcrypt.ui;

import android.app.PendingIntent;

public interface UiCallback<T> {
	void success(T object);

	void error(int errorCode, T object);

	void userInputRequired(PendingIntent pi, T object);
}
