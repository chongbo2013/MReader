package com.midcore.reader.reader.settings;

import java.util.ArrayList;
import java.util.List;

import com.midcore.reader.R;

public class Theme {
		
		private final static List<Theme> values = new ArrayList<Theme>();
		
		/**
		 * 夜间
		 */
		public final static Theme EYE = new Theme("EYE", R.string.theme_name_eye, -13421773, -3348273);
		/**
		 * 羊皮纸
		 */
		public final static Theme PARCHMENT = new Theme("PARCHMENT", R.string.theme_name_parchment, -12962773, R.drawable.books_bg_parchment, false);
		/**
		 * 夜之魅
		 */
		public final static Theme CHARM = new Theme("CHARM", R.string.theme_name_charm, -6974577, R.drawable.books_bg_charm, true);
		/**
		 * 夜间
		 */
		public final static Theme SPIRIT = new Theme("SPIRIT", R.string.theme_name_spirit, -12962773, R.drawable.books_bg_spirit, true);
		/**
		 * 夜间
		 */
		public final static Theme SEA = new Theme("SEA", R.string.theme_name_sea, -10260359, R.drawable.books_bg_sea, true);
		/**
		 * 夜间
		 */
		public final static Theme BOOKFLAVOR = new Theme("BOOKFLAVOR", R.string.theme_name_bookflavor, -12962773, R.drawable.books_bg_bookflavor, true);
		/**
		 * 夜间
		 */
		public final static Theme FINE = new Theme("FINE", R.string.theme_name_fine, -13092550, R.drawable.books_bg_fine, true);
		/**
		 * 夜间
		 */
		public final static Theme NIGHT1 = new Theme("NIGHT1", R.string.theme_name_night1, -11710382, -15393761);
		/**
		 * 夜间
		 */
		public final static Theme NIGHT2 = new Theme("NIGHT2", R.string.theme_name_night2, -13158601, -16777216);
		/**
		 * 夜间
		 */
		public final static Theme NIGHT3 = new Theme("NIGHT3", R.string.theme_name_night3, -10526881, -16777216);
		/**
		 * 省电
		 */
		public final static Theme SAVE_POWER = new Theme("SAVE_POWER", R.string.theme_name_save_power, -14540254, -16777216);
		
		public String key;
		public int name;
		public int textColor;
		public int background;
		public boolean tile;
		public int backgroundColor;
		
		private Theme(String key, int name, int textColor, int background, boolean tile) {
			this.key = key;
			this.name = name;
			this.textColor = textColor;
			this.background = background;
			this.tile = tile;
			values.add(this);
		}
		
		private Theme(String key, int name, int textColor, int backgroundColor) {
			this.key = key;
			this.name = name;
			this.textColor = textColor;
			this.backgroundColor = backgroundColor;
			values.add(this);
		}
		
		public static Theme valueOf(String key) {
			for (Theme theme : values) {
				if (theme.key.equals(key)) {
					return theme;
				}
			}
			return null;
		}
		
		public static List<Theme> values() {
			return values;
		}
		
	}