package com.change_vision.astah.extension.plugin.easycodereverse.internal.dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryBase implements Dictionary {
	protected Pattern pattern;
	protected String urlPattern = "";
	protected String[] rawUrls = new String[]{""};
	
	public DictionaryBase() {
		pattern = Pattern.compile(urlPattern);
	}
	
	@Override
	public boolean find(String url) {
		Matcher matcher = pattern.matcher(url);
		return matcher.find();
	}
	
	@Override
	public List<String> lookup(String url) {
		Matcher matcher = pattern.matcher(url);
		List<String> ret = new ArrayList<String>();
		for (String rawUrl : rawUrls) {
			ret.add(matcher.replaceAll(rawUrl));
		}
		return ret;
	}
}
