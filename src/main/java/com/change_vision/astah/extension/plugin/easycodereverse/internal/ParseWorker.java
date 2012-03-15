package com.change_vision.astah.extension.plugin.easycodereverse.internal;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.dialog.JProgressBarDialog;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.dictionary.Dictionary;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.dictionary.DictionaryLoader;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.exception.ParseException;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.AstahModelBuilder;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.model.ClassInfo;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.parser.SourceCodeParser;
import com.change_vision.jude.api.inf.exception.InvalidEditingException;

final class ParseWorker extends SwingWorker<ClassInfo, Object> {
	private final JProgressBarDialog dialog;
	private final String urlString;
	private final SourceCodeParser<ClassInfo> parser;
	private final Point location;
	private final List<Dictionary> dictionaries;
	private final boolean isInTransaction;
	
	private static final Logger logger = LoggerFactory.getLogger(ParseWorker.class);
	private static final Marker marker = MarkerFactory.getMarker("worker");

	ParseWorker(JProgressBarDialog dialog, SourceCodeParser<ClassInfo> parser, 
			String urlString, Point location) {
		this(dialog, parser, urlString, location, true);
	}
	
	ParseWorker(JProgressBarDialog dialog, SourceCodeParser<ClassInfo> parser, 
			String urlString, Point location, boolean isInTransaction) {
		this.dialog = dialog;
		this.dialog.setMessage(Messages.getMessage("parse_worker.message"));
		this.urlString = urlString;
		this.parser = parser;
		this.location = location;
		this.isInTransaction = isInTransaction;
		
		dictionaries = new DictionaryLoader().loadAll();
	}

	@Override
	protected ClassInfo doInBackground() throws Exception {
		if(logger.isInfoEnabled(marker)) {
			logger.trace("start to parse.");
		}

		ClassInfo model = null;
		if (isFile(urlString)) {
			model = parseCodeFromFile();
		} else {
			for (String rawUrl : lookup(urlString)) {
				model = parseCodeFromUrl(rawUrl);
				if (model != null) break;
			}
		}
		
		return model;
	}

	@Override
	protected void done() {
		try {
			ClassInfo model = get();
			if (model != null) {
				new AstahModelBuilder(model, location).build(isInTransaction);
			}
		} catch (InterruptedException e) {
			logger.warn(e.getLocalizedMessage(), e);
		} catch (ExecutionException e) {
			if (isInTransaction) {
				Throwable rootCause = e.getCause();
				dialog.showErrorMessage(e, (rootCause != null) ? rootCause.getMessage() : e.getMessage());
			}
		} catch (InvalidEditingException e) {
			if (isInTransaction) {
				dialog.showErrorMessage(e, e.getMessage());
			}
		} finally {
			dialog.setVisible(false);
		}
	}
	
	private ClassInfo parseCodeFromFile() throws IOException, ParseException {
		FileInputStream fis = null;
		File srcFile = new File(urlString);
		if (isTarget(srcFile)) {
			try {
				fis = FileUtils.openInputStream(srcFile);
				return parser.parse(fis);
			} catch (IOException e) {
				throw new IOException(Messages.getMessage("parse_worker.error.loading"), e);
			} finally {
				if (fis != null) {try {fis.close();} catch (IOException e) {}}
			}
		}
		return null;
	}

	private ClassInfo parseCodeFromUrl(String rawUrl) throws IOException, ParseException {
		try {
			HttpURLConnection conn = getConnection(rawUrl);
			conn.connect();
			
			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				return parser.parse(conn.getInputStream());
			}
		} catch (IOException e) {
			throw new IOException(Messages.getMessage("parse_worker.error.connection"), e);
		}
		return null;
	}

	private HttpURLConnection getConnection(String rawUrl) throws IOException {
		URL url = new URL(rawUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		conn.setReadTimeout(30 * 1000);
		return conn;
	}

	private boolean isFile(String urlString) {
		String schema = "file";

		try { schema = new URI(urlString).getScheme(); } catch (URISyntaxException e) {}

		return (schema == null || schema.equals("") || "file".equals(schema));
	}

	// TODO 各言語のParserに依存
	private boolean isTarget(File file) {
		String extension = FilenameUtils.getExtension(file.getName());
		return (extension.equals("java"));
	}

	// TODO 以下、別クラスに
	private List<String> lookup(String url) {
		for (Dictionary dictionary : dictionaries) {
			if (dictionary.find(url)) {
				return dictionary.lookup(url);
			}
		}
		
		List<String> ret = new ArrayList<String>();
		ret.add(url);
		return ret;
	}
}
