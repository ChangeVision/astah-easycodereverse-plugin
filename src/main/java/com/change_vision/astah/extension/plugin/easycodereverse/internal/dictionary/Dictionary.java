package com.change_vision.astah.extension.plugin.easycodereverse.internal.dictionary;

import java.util.List;

public interface Dictionary {
	boolean find(String url);
	
	List<String> lookup(String url);
}
