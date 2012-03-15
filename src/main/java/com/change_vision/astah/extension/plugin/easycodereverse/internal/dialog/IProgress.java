package com.change_vision.astah.extension.plugin.easycodereverse.internal.dialog;

public interface IProgress {

	public abstract void setMessage(String message);
	
	public abstract void showErrorMessage(Exception e ,String message);

}