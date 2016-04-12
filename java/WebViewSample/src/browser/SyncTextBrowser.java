package browser;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class SyncTextBrowser extends Region {

	final WebView browser = new WebView();
	final WebEngine webEngine = browser.getEngine();

	public SyncTextBrowser() {
		//apply the styles
		getStyleClass().add("browser");
		// load the web page
		webEngine.load("http://localhost:9000/sync-text.html");

		// listen do events
		webEngine.getLoadWorker().stateProperty().addListener(
				new ChangeListener<State>() {
					@Override
					public void changed(ObservableValue ov, State oldState, State newState) {
						if(newState == Worker.State.SUCCEEDED){
							System.out.println("Success");
						}
					}
				});
		//add the web view to the scene
		getChildren().add(browser);
	}

	@Override protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(browser,0,0,w,h,0, HPos.CENTER, VPos.CENTER);
	}

	@Override protected double computePrefWidth(double height) {
		return 750;
	}

	@Override protected double computePrefHeight(double width) {
		return 500;
	}
}