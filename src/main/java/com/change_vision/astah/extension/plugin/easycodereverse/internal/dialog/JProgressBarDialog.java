package com.change_vision.astah.extension.plugin.easycodereverse.internal.dialog;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.change_vision.jude.api.inf.ui.IMessageDialogHandler;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandlerFactory;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.AstahAPIHandler;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.Messages;

@SuppressWarnings("serial")
public class JProgressBarDialog extends JDialog implements IProgress {
	
	private JProgressBar bar;
	private ServiceTracker tracker;
	private IMessageDialogHandler dialogHandler;
	private static final Logger logger = LoggerFactory.getLogger(JProgressBarDialog.class);
	private static final Marker marker = MarkerFactory.getMarker("dialog");

	public JProgressBarDialog(JFrame parent, ServiceTracker tracker) {
		super(parent,true);
		this.tracker = tracker;
		bar = new JProgressBar();
		bar.setIndeterminate(true);
		bar.setStringPainted(true);
		bar.setString("test");
		getContentPane().add(bar);
		setLocationRelativeTo(parent);
		pack();
	}
	
	public void setMessage(String message) {
		bar.setString(message);
	}
	
	@Override
	public void showErrorMessage(Exception e, String message) {
		if(dialogHandler == null) {
			IMessageDialogHandlerFactory factory = (IMessageDialogHandlerFactory)tracker.getService();
			String edition = new AstahAPIHandler().getEdition();
			// .astah/professional/easycodereverse.log
			String logFileRelatedPath = ".astah" + File.separator + edition.toLowerCase() + File.separator + "easycodereverse.log";
			dialogHandler = factory.createMessageDialogHandler(new Messages(), File.separator + logFileRelatedPath);
		}
		if(logger.isErrorEnabled(marker)) {
			logger.error("error has occured.",e);
		}
		dialogHandler.showErrorMessage(getParent(), message);
	}
}