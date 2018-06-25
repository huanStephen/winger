package org.eocencle.winger.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.eocencle.winger.builder.ServerBuilder;
import org.eocencle.winger.io.Resources;
import org.eocencle.winger.parsing.XPathParser;
import org.eocencle.winger.session.Configuration;
import org.eocencle.winger.web.server.ServerThread;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WingerRun extends Application {

	public static void main(String[] args) throws IOException {
		/*if ("-s".equals(args[0])) {
			if (StringUtils.isNotBlank(args[1])) {
				startServer(args[1]);
			}
		} else if ("-f".equals(args[0])) {
			launch();
		} else {
			System.out.println("参数错误！");
		}*/
		startServer("E:/mine/git/winger-feature/winger/src/main/resource/config.xml");
	}
	
	private static void startServer(String xmlPath) throws IOException {
		ServerBuilder builder = new ServerBuilder(new Configuration(), new XPathParser(new FileInputStream(new File(xmlPath))).evalNode("/project"));
		ServerThread thread = new ServerThread(builder.parse());
		thread.start();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			Parent root = FXMLLoader.load(Resources.getResourceURL("org/eocencle/winger1/frame/MainFrame.fxml"));

			primaryStage.setTitle("Winger");
			primaryStage.setScene(new Scene(root));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
