package com.change_vision.astah.extension.plugin.easycodereverse.internal;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.change_vision.astah.extension.plugin.easycodereverse.internal.dialog.JProgressBarDialog;
import com.change_vision.astah.extension.plugin.easycodereverse.internal.parser.JavaCodeParser;
import com.change_vision.jude.api.inf.editor.ITransactionManager;
import com.change_vision.jude.api.inf.model.IClassDiagram;
import com.change_vision.jude.api.inf.view.DiagramDropTargetListener;
import com.change_vision.jude.api.inf.view.IDiagramViewManager;

public final class ClassDiagramDropExtension extends DiagramDropTargetListener {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ClassDiagramDropExtension.class);
	@SuppressWarnings("unused")
	private static final Marker marker = MarkerFactory.getMarker("extension");

	private AstahAPIHandler handler = new AstahAPIHandler();

	private ServiceTracker tracker;

	public ClassDiagramDropExtension(ServiceTracker tracker) {
		super(IClassDiagram.class);
		this.tracker = tracker;
	}

	@Override
	public void dropExternalData(DropTargetDropEvent dtde) {
		if(dtde.isLocalTransfer()) return;
		
		if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			JFrame parent = handler.getMainFrame();
			JProgressBarDialog dialog = new JProgressBarDialog(parent, tracker);
			
			ParseWorker worker = new ParseWorker(dialog, new JavaCodeParser(), 
					getURLStringFromDropContent(dtde), getLocation(dtde));
			worker.execute();
			
			dialog.setVisible(true);
			dtde.dropComplete(true);
			layout(worker);
		} else if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			List<File> files = null;
			if (canPreCheckSupportedFiles()) {
				files = getFilesFromDropContent(dtde);
				if (!isSupportedFiles(files)) {
					return;
				}
			}
			
			if(JOptionPane.showConfirmDialog(handler.getMainFrame(),
					Messages.getMessage("confirm_code_parse.message"),
					Messages.getMessage("title"), JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			
			if (!canPreCheckSupportedFiles()) {
				files = getFilesFromDropContent(dtde);
				if (!isSupportedFiles(files)) {
					return;
				}
			}
			
			JFrame parent = handler.getMainFrame();
			JProgressBarDialog dialog = new JProgressBarDialog(parent, tracker);

			List<ParseWorker> workers = new ArrayList<ParseWorker>();
			ITransactionManager transactionManager = handler.getProjectAccessor().getTransactionManager();
			try {
				transactionManager.beginTransaction();
				for (File file : files) {
					ParseWorker worker = new ParseWorker(dialog, new JavaCodeParser(), 
							file.getAbsolutePath(), getLocation(dtde), false);
					worker.execute();
					workers.add(worker);
					dialog.setVisible(true);
				}
				transactionManager.endTransaction();
			} catch (Exception e) {
				transactionManager.abortTransaction();
				dialog.showErrorMessage(e, Messages.getMessage("parse_worker.error.parse"));
			}
			
			layout(workers);
			dtde.dropComplete(true);
		}
	}
	
	private Point getLocation(DropTargetDropEvent dtde) {
		Point location = dtde.getLocation();
		if (canUseToWorldCoordAPI()) {
			IDiagramViewManager viewManager = handler.getDiagramViewManager();
			Point2D worldCoordLocation = viewManager.toWorldCoord(location.x, location.y);
			location = new Point((int) worldCoordLocation.getX(), (int) worldCoordLocation.getY());
		}
		
		return location;
	}

	private void layout(ParseWorker worker) {
		if (!canLayout()) return;
		
		try {worker.get();} catch (Exception e) {}
		
		layout();
	}

	private void layout(List<ParseWorker> workers) {
		if (!canLayout()) return;
		
		for (ParseWorker worker : workers) {
			try {worker.get();} catch (Exception e) {}
		}
		
		layout();
	}

	private void layout() {
		ITransactionManager transactionManager = handler.getProjectAccessor().getTransactionManager();
		try {
			transactionManager.beginTransaction();
			handler.getDiagramViewManager().layoutSelected();
			transactionManager.endTransaction();
		} catch (Exception e) {
			transactionManager.abortTransaction();
		}
	}
	
	private int compareAstahAPIVersionTo(String target) {
		String astahAPIVersionStr = handler.getProjectAccessor().getAstahAPIVersion();
		DefaultArtifactVersion astahAPIVersion = new DefaultArtifactVersion(astahAPIVersionStr);
		DefaultArtifactVersion targetVersion = new DefaultArtifactVersion(target);
		return astahAPIVersion.compareTo(targetVersion);
	}
	
	private boolean canPreCheckSupportedFiles() {
		return compareAstahAPIVersionTo("6.6") >= 0;
	}

	private boolean canLayout() {
		return compareAstahAPIVersionTo("6.5") >= 0;
	}
	
	private boolean canUseToWorldCoordAPI() {
		return compareAstahAPIVersionTo("6.6.4") >= 0;
	}

	private String getURLStringFromDropContent(DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_LINK);
		Transferable target = dtde.getTransferable();

		String urlString;
		try {
            urlString = String.class.cast(target.getTransferData(DataFlavor.stringFlavor)).trim();
		} catch (Exception e) {
			urlString = "";
		}
		return urlString;
	}
	
	private boolean isSupportedFiles(List<File> files) {
		if (files == null || files.size() == 0) {
			return false;
		}
		
		boolean supported = true;
		String[] supportedExtensions = {"java"};
		for (File file : files) {
			if (!FilenameUtils.isExtension(file.getName(), supportedExtensions)) {
				return false;
			}
		}
		return supported;
	}
	
	@SuppressWarnings("unchecked")
	private List<File> getFilesFromDropContent(DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_COPY);
		List<File> list;
		try {
			list = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		} catch (Exception e) {
			list = new ArrayList<File>();
		}
		return list;
	}
	
	@Override
	public void dropModels(DropTargetDropEvent dtde, Set<?> models) {
	}

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}
}