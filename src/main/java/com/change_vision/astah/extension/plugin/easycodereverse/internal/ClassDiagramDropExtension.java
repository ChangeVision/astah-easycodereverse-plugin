package com.change_vision.astah.extension.plugin.easycodereverse.internal;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
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
		
		DataFlavor[] currentDataFlavors = dtde.getCurrentDataFlavors();
		for (DataFlavor df : currentDataFlavors) {
			System.out.println(df);
		}
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
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
							file.getAbsolutePath(), dtde.getLocation(), false);
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
		} else if (dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			JFrame parent = handler.getMainFrame();
			JProgressBarDialog dialog = new JProgressBarDialog(parent, tracker);
			
			ParseWorker worker = new ParseWorker(dialog, new JavaCodeParser(), 
					getURLStringFromDropContent(dtde), dtde.getLocation());
			worker.execute();
			
			dialog.setVisible(true);
			dtde.dropComplete(true);
			layout(worker);
		}
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
	
	private float getAstahAPIVersion() {
		float astahAPIVersion = 0f;
		
		try {
			String astahAPIVersionStr = handler.getProjectAccessor().getAstahAPIVersion();
			astahAPIVersion = Float.parseFloat(astahAPIVersionStr);
		} catch (NumberFormatException e) {
		}
		return astahAPIVersion;
	}
	
	private boolean canPreCheckSupportedFiles() {
		return (6.6f <= getAstahAPIVersion());
	}

	private boolean canLayout() {
		return (6.5f <= getAstahAPIVersion());
	}

	private String getURLStringFromDropContent(DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_LINK);
		Transferable target = dtde.getTransferable();

		String urlString;
		try {
			urlString = (String)target.getTransferData(DataFlavor.stringFlavor);
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