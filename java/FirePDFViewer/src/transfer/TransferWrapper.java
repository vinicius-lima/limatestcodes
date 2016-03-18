package transfer;

import org.jpedal.examples.viewer.gui.javafx.FXViewerTransitions.TransitionType;

import main.BaseViewerFX.FitToPage;

public class TransferWrapper {
	
	private String pdffile;
	private float scale;
	private int currentScaling;
	private int currentPage;
	private FitToPage zoomMode;
	private TransitionType transitionType;
	private String fileContent;
	//private long fileSize;
	
	public TransferWrapper() {
		
	}
	
	public TransferWrapper(String file, float scale, int crtScaling, int crtPage,
			FitToPage zMode, TransitionType transType, String fContent) {
		pdffile = file;
		this.scale = scale;
		currentScaling = crtScaling;
		currentPage = crtPage;
		zoomMode = zMode;
		transitionType = transType;
		fileContent = fContent;
		//fileSize = fSize;
	}
	
	public String getPDFfile() { return pdffile; }
	public void setPDFfile(String fileName) { pdffile = fileName; }
	
	public float getScale() { return scale; }
	
	public int getCurrentScaling() { return currentScaling; }
	
	public int getCurrentPage() { return currentPage; }
	
	public FitToPage getZoomMode() { return zoomMode; }
	
	public TransitionType getTransitionType() { return transitionType; }
	
	public String getFileContent() { return fileContent; }
	public void setFileContent(String fContent) { fileContent = fContent; }
	//public long getFileSize() { return fileSize; }
}
