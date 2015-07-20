package com.midcore.reader.reader.settings;

public interface SettingsObserver {

	public void onChange(String key, Object oldValue, Object newValue);
}
