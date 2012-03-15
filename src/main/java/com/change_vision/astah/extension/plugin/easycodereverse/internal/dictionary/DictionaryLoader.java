package com.change_vision.astah.extension.plugin.easycodereverse.internal.dictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.AstahAPIHandler;

public class DictionaryLoader {
	private static final Logger logger = LoggerFactory.getLogger(DictionaryLoader.class);
	private AstahAPIHandler handler = new AstahAPIHandler();
	
	private String getAstahUserHome() {
		String home = SystemUtils.getUserHome() + File.separator + ".astah" + File.separator + handler.getEdition();
		return home;
	}
	
	public List<Dictionary> loadAll() {
		List<Dictionary> dictionaries = new ArrayList<Dictionary>();
		try {
			dictionaries.addAll(loadDefault());
		} catch (Exception e) {
			logger.warn(e.getLocalizedMessage());
		}
		
		try {
			dictionaries.addAll(loadCustomForBeta());
		} catch (Exception e) {
			logger.debug(e.getLocalizedMessage());
		}
		
		try {
			dictionaries.addAll(loadCustom());
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
		}
		return dictionaries;
	}
	
	public List<Dictionary> loadDefault() throws IOException, JSONException {
		InputStream resourceAsStream = Dictionary.class.getResourceAsStream("default.json");
		return load(resourceAsStream);
	}
	
	public List<Dictionary> loadCustomForBeta() throws IOException, JSONException {
		File custom = new File("dictionary.json");
		return load(custom);
	}

	public List<Dictionary> loadCustom() throws IOException, JSONException {
		File custom = new File(getAstahUserHome(), "easycodereverse-dict.json");
		return load(custom);
	}

	public List<Dictionary> load(InputStream is) throws IOException, JSONException {
		String jsonString = convertStreamToString(is);
		return loadJsonFromString(jsonString);
	}
	
	public List<Dictionary> load(File dictionaryJson) throws IOException, JSONException {
		if (dictionaryJson == null || !dictionaryJson.exists()) {
			logger.debug("failed loading " + dictionaryJson.getAbsolutePath());
			throw new FileNotFoundException("dictionary file not found.");
		}
		
		String jsonString = FileUtils.readFileToString(dictionaryJson);
		return loadJsonFromString(jsonString);
	}

	private List<Dictionary> loadJsonFromString(String jsonString) throws JSONException {
		List<Dictionary> dictionaries = new ArrayList<Dictionary>();
		JSONObject jsonObject = new JSONObject(jsonString);
		String[] names = JSONObject.getNames(jsonObject);
		for (String name : names) {
			JSONObject dictObj = jsonObject.getJSONObject(name);
			String urlPattern = dictObj.getString("urlPattern");
			JSONArray rawUrlsJsonArray = dictObj.getJSONArray("rawUrls");
			String[] rawUrls = new String[rawUrlsJsonArray.length()];
			for (int i = 0; i < rawUrls.length; i++) {
				rawUrls[i] = rawUrlsJsonArray.getString(i);
			}
			logger.debug("added:" + urlPattern);
			dictionaries.add(new UserDictionary(urlPattern, rawUrls));
		}
		
		return dictionaries;
	}
	
	private String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
}
