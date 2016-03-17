package transfer;

import org.jpedal.examples.viewer.gui.javafx.FXViewerTransitions.TransitionType;

import main.BaseViewerFX.FitToPage;

public class TransferWrapper {
	
	private String PDFfile;
	private float scale;
	private int currentScaling;
	private int currentPage;
	private FitToPage zoomMode;
	private TransitionType transitionType;
	private long fileSize;
	
	public TransferWrapper(String file, float scale, int crtScaling, int crtPage,
			FitToPage zMode, TransitionType transType, long fSize) {
		PDFfile = file;
		this.scale = scale;
		currentScaling = crtScaling;
		currentPage = crtPage;
		zoomMode = zMode;
		transitionType = transType;
		fileSize = fSize;
	}
	
	public String getPDFfile() { return PDFfile; }
	public void setPDFfile(String fileName) { PDFfile = fileName; }
	
	public float getScale() { return scale; }
	
	public int getCurrentScaling() { return currentScaling; }
	
	public int getCurrentPage() { return currentPage; }
	
	public FitToPage getZoomMode() { return zoomMode; }
	
	public TransitionType getTransitionType() { return transitionType; }
	
	public long getFileSize() { return fileSize; }
}
