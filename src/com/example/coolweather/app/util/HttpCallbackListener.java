package com.example.coolweather.app.util;

import java.io.InputStream;

public interface HttpCallbackListener {
	void onFinish(InputStream response);
	void onError(Exception e);
}
