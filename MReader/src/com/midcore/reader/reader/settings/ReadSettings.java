package com.midcore.reader.reader.settings;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.midcore.reader.db.Settings;

public class ReadSettings {

	public final static String FONT_SIZE = "font_size";
	private final static int DEFAULT_FONT_SIZE = 38;
	private final static int MAX_FONT_SIZE = 67;
	private final static int MIN_FONT_SIZE = 20;
	
	public final static String THEME = "theme";
	private final static Theme DEFAULT_THEME = Theme.PARCHMENT;
	
	public final static String PAGE_STYLE = "page_style";
	private final static PageStyle DEFAULT_PAGE_STYLE = PageStyle.LAYOVER;
	
	public final static String EYE_PROTECTION = "eye_protection";
	private final static boolean DEFAULT_EYE_PROTECTION = false;
	
	public final static String BRIGHT_SYSTEM = "bright_system";
	private final static boolean DEFAULT_BRIGHT_SYSTEM = true;
	
	public final static String BRIGHT = "bright";
	private final static int DEFAULT_BRIGHT = 100;
	private final static int MAX_BRIGHT = 255;
	private final static int MIN_BRIGHT = 0;
	
	private static ArrayList<SettingsObserver> sObservers = new ArrayList<SettingsObserver>();
	
	public static void addSettingsObserver(SettingsObserver observer) {
		if (!sObservers.contains(observer)) {
			sObservers.add(observer);
		}
	}
	
	public static void removeSettingsObserver(SettingsObserver observer) {
		sObservers.remove(observer);
	}
	
	public static int getFontSize(Context context) {
		int fontSize = Settings.getInt(context, FONT_SIZE, DEFAULT_FONT_SIZE);
		if (fontSize > MAX_FONT_SIZE) {
			fontSize = MAX_FONT_SIZE;
		} else if (fontSize < MIN_FONT_SIZE) {
			fontSize = MIN_FONT_SIZE;
		}
		return fontSize;
	}
	
	public static int setFontSize(Context context, int fontSize) {
		if (fontSize > MAX_FONT_SIZE) {
			fontSize = MAX_FONT_SIZE;
		} else if (fontSize < MIN_FONT_SIZE) {
			fontSize = MIN_FONT_SIZE;
		}
		int oldValue = getFontSize(context);
		if (oldValue != fontSize) {
			Settings.putInt(context, FONT_SIZE, fontSize);
			dispatchOnSettingsChange(FONT_SIZE, oldValue, fontSize);
		}
		return fontSize;
	}
	
	public static int checkFontSize(int fontSize) {
		if (fontSize > MAX_FONT_SIZE) {
			fontSize = MAX_FONT_SIZE;
		} else if (fontSize < MIN_FONT_SIZE) {
			fontSize = MIN_FONT_SIZE;
		}
		return fontSize;
	}
	
	public static Theme getTheme(Context context) {
		String v = Settings.getString(context, THEME, DEFAULT_THEME.key);
		Theme theme = Theme.valueOf(v);
		if (theme == null) {
			theme = DEFAULT_THEME;
		}
		return theme;
	}
	
	public static void setTheme(Context context, Theme theme) {
		Theme oldValue = getTheme(context);
		if (oldValue != theme) {
			Settings.putString(context, THEME, theme.key);
			dispatchOnSettingsChange(THEME, oldValue, theme);
		}
	}
	
	public static PageStyle getPageStyle(Context context) {
		String v = Settings.getString(context, PAGE_STYLE, DEFAULT_PAGE_STYLE.key);
		PageStyle style = PageStyle.valueOf(v);
		if (style == null) {
			style = DEFAULT_PAGE_STYLE;
		}
		return style;
	}
	
	public static void setPageStyle(Context context, PageStyle style) {
		PageStyle oldValue = getPageStyle(context);
		if (oldValue != style) {
			Settings.putString(context, PAGE_STYLE, style.key);
			dispatchOnSettingsChange(PAGE_STYLE, oldValue, style);
		}
	}
	
	public static boolean getEyeProtection(Context context) {
		return Settings.getBoolean(context, EYE_PROTECTION, DEFAULT_EYE_PROTECTION);
	}
	
	public static void setEyeProtection(Context context, boolean eyeProtection) {
		boolean oldValue = getEyeProtection(context);
		if (oldValue != eyeProtection) {
			Settings.putBoolean(context, EYE_PROTECTION, eyeProtection);
			dispatchOnSettingsChange(EYE_PROTECTION, oldValue, eyeProtection);
		}
	}
	
	public static boolean getBrightSystem(Context context) {
		return Settings.getBoolean(context, BRIGHT_SYSTEM, DEFAULT_BRIGHT_SYSTEM);
	}
	
	public static void setBrightSystem(Context context, boolean brightSystem) {
		boolean oldValue = getBrightSystem(context);
		if (oldValue != brightSystem) {
			Settings.putBoolean(context, BRIGHT_SYSTEM, brightSystem);
			dispatchOnSettingsChange(BRIGHT_SYSTEM, oldValue, brightSystem);
		}
	}
	
	public static int getBright(Context context) {
		int bright = Settings.getInt(context, BRIGHT, DEFAULT_BRIGHT);
		if (bright > MAX_BRIGHT) {
			bright = MAX_BRIGHT;
		} else if (bright < MIN_BRIGHT) {
			bright = MIN_BRIGHT;
		}
		return bright;
	}
	
	public static int setBright(Context context, int bright) {
		if (bright > MAX_BRIGHT) {
			bright = MAX_BRIGHT;
		} else if (bright < MIN_BRIGHT) {
			bright = MIN_BRIGHT;
		}
		int oldValue = getBright(context);
		if (oldValue != bright) {
			Settings.putInt(context, BRIGHT, bright);
			dispatchOnSettingsChange(BRIGHT, oldValue, bright);
		}
		return bright;
	}
	
	public static int checkBright(int bright) {
		if (bright > MAX_BRIGHT) {
			bright = MAX_BRIGHT;
		} else if (bright < MIN_BRIGHT) {
			bright = MIN_BRIGHT;
		}
		return bright;
	}
	
	static void dispatchOnSettingsChange(String key, Object oldValue, Object newValue) {
		@SuppressWarnings("unchecked")
		ArrayList<SettingsObserver> observersCopy = (ArrayList<SettingsObserver>) sObservers.clone();
		int numListeners = observersCopy.size();
        for (int i = 0; i < numListeners; ++i) {
        	observersCopy.get(i).onChange(key, oldValue, newValue);
        }
	}
	
}
