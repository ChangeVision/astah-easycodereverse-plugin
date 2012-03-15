package com.change_vision.astah.extension.plugin.easycodereverse.internal.dictionary;

import java.util.regex.Pattern;

public class UserDictionary extends DictionaryBase {	
	public UserDictionary(String urlPattern, String[] rawUrls) {
		this.urlPattern = urlPattern;
		this.rawUrls = rawUrls;
		
		pattern = Pattern.compile(urlPattern);
	}
}
