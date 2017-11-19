package org.eocencle.winger.frame;

import java.io.IOException;
import java.util.Map;

import org.eocencle.winger.io.Resources;
import org.eocencle.winger.state.Event;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ServerStopEvent implements Event {

	@Override
	public void process(Map<String, Object> params) {
		try {
			Button startButton = (Button) params.get("startButton");
			startButton.setText("启动");
			startButton.setDisable(false);
			ImageView startBtnImage = (ImageView) params.get("startBtnImage");
			startBtnImage.setImage(new Image(Resources.getResourceAsStream("org/eocencle/winger/frame/resource/image/startable.png")));
			
			Button stopButton = (Button) params.get("stopButton");
			stopButton.setDisable(true);
			stopButton.setText("停止");
			ImageView stopBtnImage = (ImageView) params.get("stopBtnImage");
			stopBtnImage.setImage(new Image(Resources.getResourceAsStream("org/eocencle/winger/frame/resource/image/distop.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
