package org.eocencle.winger.frame;
	
import java.io.IOException;

import org.eocencle.winger.io.Resources;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;


public class MainFrame extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(Resources.getResourceURL("org/eocencle/winger/frame/MainFrame.fxml"));

			primaryStage.setTitle("Winger");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		launch(args);
	}
}
