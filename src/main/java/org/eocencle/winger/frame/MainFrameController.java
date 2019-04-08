package org.eocencle.winger.frame;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.eocencle.winger.web.server.ServerThread;
import org.eocencle.winger.state.StateMachine;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

public class MainFrameController implements Initializable {
	// 菜单栏
	
	// 工具栏
	@FXML
	private Button startButton;
	@FXML
	private ImageView startBtnImage;
	@FXML
	private Button stopButton;
	@FXML
	private ImageView stopBtnImage;
	
	private StateMachine serverStateMachine;

	// 列表
	@FXML
	private TableView<Request> requestTable;
	@FXML
	private TableColumn<Request, String> idColumn;
	@FXML
	private TableColumn<Request, String> resultColumn;
	@FXML
	private TableColumn<Request, String> protocolColumn;
	@FXML
	private TableColumn<Request, String> hostColumn;
	@FXML
	private TableColumn<Request, String> urlColumn;
	@FXML
	private TableColumn<Request, String> bodyColumn;
	@FXML
	private TableColumn<Request, String> cachingColumn;
	@FXML
	private TableColumn<Request, String> contentTypeColumn;
	@FXML
	private TableColumn<Request, String> processColumn;
	@FXML
	private TableColumn<Request, String> commentsColumn;
	@FXML
	private TableColumn<Request, String> customColumn;
	/*@FXML
	private Label idLabel;
	@FXML
	private Label resultLabel;
	@FXML
	private Label protocolLabel;*/
	
	public static final Integer SERVER_INIT = 1;
	public static final Integer SERVER_START = 2;
	public static final Integer SERVER_STOP = 3;
	
	private ServerThread server;
	
	public void initialize(URL location, ResourceBundle resources) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startButton", this.startButton);
		params.put("startBtnImage", this.startBtnImage);
		params.put("stopButton", this.stopButton);
		params.put("stopBtnImage", this.stopBtnImage);
		this.serverStateMachine = new StateMachine(params);
		this.serverStateMachine.addEvent(SERVER_INIT, new ServerStopEvent());
		this.serverStateMachine.addEvent(SERVER_START, new ServerStartEvent());
		this.serverStateMachine.addEvent(SERVER_STOP, new ServerStopEvent());
		
		this.initToolBar();
		this.initTableView();
		
		//this.server = new ServerThread();
	}
	
	private void initToolBar() {
		this.serverStateMachine.trigger(SERVER_INIT);
	}
	
	public void startButtonClick(ActionEvent e) {
		this.serverStateMachine.trigger(SERVER_START);
		//this.server = new ServerThread();
		//this.server.start();
	}
	
	public void stopButtonClick(ActionEvent e) throws InterruptedException {
		this.serverStateMachine.trigger(SERVER_STOP);
		//KillServer.process("8081");
		this.server.stopServer();
		this.server.stop();
	}
	
	private void initTableView() {
		ObservableList<Request> list = FXCollections.observableArrayList();
		Request request = new Request();
		request.setId(5111);
		request.setResult(200);
		request.setProtocol("HTTP");
		request.setHost("192.168.180.144");
		request.setUrl("/xiaoliu66007/p/3304835.html");
		request.setBody(627384);
		request.setCaching("private");
		request.setContentType("text/plain; charset=utf-8");
		request.setProcess("");
		request.setComments("");
		request.setCustom("");
		
		this.idColumn.setCellValueFactory(new PropertyValueFactory("id"));
		this.resultColumn.setCellValueFactory(new PropertyValueFactory("result"));
		this.protocolColumn.setCellValueFactory(new PropertyValueFactory("protocol"));
		this.hostColumn.setCellValueFactory(new PropertyValueFactory("host"));
		this.urlColumn.setCellValueFactory(new PropertyValueFactory("url"));
		this.bodyColumn.setCellValueFactory(new PropertyValueFactory("body"));
		this.cachingColumn.setCellValueFactory(new PropertyValueFactory("caching"));
		this.contentTypeColumn.setCellValueFactory(new PropertyValueFactory("contentType"));
		this.processColumn.setCellValueFactory(new PropertyValueFactory("process"));
		this.commentsColumn.setCellValueFactory(new PropertyValueFactory("comments"));
		this.customColumn.setCellValueFactory(new PropertyValueFactory("custom"));
		
		list.add(request);
		list.add(request);
		list.add(request);
		this.requestTable.setItems(list);
		
		/*new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
					Random rand = new Random();
					for (int i = 5112; i < 5200; i ++) {
						Request r = new Request();
						r.setId(i);
						r.setResult(200);
						r.setProtocol("HTTP");
						r.setHost("192.168.180.144");
						r.setUrl("/xiaoliu66007/p/3304835.html");
						r.setBody(627384);
						r.setCaching("private");
						r.setContentType("text/plain; charset=utf-8");
						r.setProcess("");
						r.setComments("");
						r.setCustom("");
						list.add(r);
						Thread.sleep(rand.nextInt(1000));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();*/
	}

}
