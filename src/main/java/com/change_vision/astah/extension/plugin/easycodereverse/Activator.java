package com.change_vision.astah.extension.plugin.easycodereverse;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.AstahAPIHandler;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.ClassDiagramDropExtension;
import com.change_vision.jude.api.inf.ui.IMessageDialogHandlerFactory;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;

public class Activator implements BundleActivator {
	
	private static Activator activator;
	private AstahAPIHandler handler = new AstahAPIHandler();
	
	public Activator() {
		activator = this;
	}

	public void start(BundleContext context) {
		EditionChecker checker = new EditionChecker();
		if (checker.hasError()) {
			return;
		}
		
		ServiceTracker tracker = new ServiceTracker(context, IMessageDialogHandlerFactory.class.getName(), null);
		tracker.open();
		
		ClassDiagramDropExtension diagramDropTargetListener = new ClassDiagramDropExtension(tracker);
		IDiagramViewManager diagramViewManager = handler.getDiagramViewManager();
		diagramViewManager.addDropTargetListener(diagramDropTargetListener);
	}

	public void stop(BundleContext context) {
	}
	
	public static Activator getActivator() {
		return Activator.activator;
	}
}
