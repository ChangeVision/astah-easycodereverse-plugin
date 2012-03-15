package com.change_vision.astah.extension.plugin.easycodereverse.internal.parser;

import java.io.IOException;
import java.io.InputStream;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.exception.ParseException;

public interface SourceCodeParser<T> {
	public T parse(InputStream is) throws IOException, ParseException;
}

