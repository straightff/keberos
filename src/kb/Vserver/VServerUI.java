package kb.Vserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class VServerUI extends Application
{
	private ServerSocket ssocket;
//	private Socket socket;
	private ArrayList<Thread> recvClientThread;
	private ServerInitThread initAc;
	private final static String TGS_ID = "40501";
	private final static String VSERVER_ID = "40502";
	private final static String AS_ID = "40401";
	private static String TicketV;


	@Override
	public void init() throws Exception
	{

//		ssocket = new ServerSocket(); 
//		ServerInitThread initAc = new ServerInitThread(ssocket);
//		super.init();
	}

	@Override
	public void stop() throws Exception
	{

		if (!recvClientThread.isEmpty())
			for (Thread thread : recvClientThread)
			{
				thread.interrupt();
			}
		for (Socket socket : ServerInitThread.getRecvClientSocket())
		{
			if (socket != null && !socket.isClosed())
				socket.close();
		}

		if (ssocket != null && !ssocket.isClosed())
			ssocket.close();
//		ssocket.close();
		super.stop();
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{

		ServerInitThread.logArea = new ListView<String>();
		primaryStage.setTitle("VServer");

		primaryStage.setWidth(500);
		primaryStage.setHeight(700);
		primaryStage.show();
		primaryStage.setScene(mainStage());
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(WindowEvent we)
			{
				System.exit(0);
			}
		});

		// 窗口关闭则ssocket连接关闭

//		SocketSetup ssup = new SocketSetup();

	}

	public Scene mainStage() throws NumberFormatException, IOException
	{
		HBox hb = new HBox();
		VBox vb = new VBox();
		GridPane grid = new GridPane();

//		ListView<String> logArea = new ListView<String>();
		ServerInitThread.logArea.setPrefSize(400, 500);

		Button connect = new Button("Connect");

		Label lport = new Label("port:");
		TextField port = new TextField("8888");
		lport.setPrefSize(50, 40);
		port.setPrefSize(100, 40);

		// 格式控制
		port.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				String regEx = "[^0-9]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(newValue);
				String mess = m.replaceAll("");
				if (mess.length() != newValue.length())
				{
					port.setText(oldValue);
					Tooltip tp = new Tooltip("请输入数字");
					port.setTooltip(tp);
				} else if (newValue.length() > 6)
				{
					port.setText(oldValue);
				}

			}
		});

		grid.add(ServerInitThread.logArea, 0, 0);
		grid.add(connect, 0, 1);
		grid.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(lport, port);
		hb.setAlignment(Pos.CENTER);
		vb.getChildren().add(hb);
		vb.getChildren().add(grid);

		Scene scene = new Scene(vb);

		connect.setOnAction(new EventHandler<ActionEvent>()
		{

			@Override
			public void handle(ActionEvent event)
			{
				try
				{
					recvClientThread = new ArrayList<Thread>();
					initAc = new ServerInitThread(Integer.parseInt(port.getText()));
//					logArea.setItems(initAc.loglist);
//					new ServerReaderThead(socket);
					Thread serverThread = new Thread(initAc);
					recvClientThread.add(serverThread);
					serverThread.start();

					connect.setDisable(true);
				} catch (NumberFormatException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return scene;
	}

	public static String getTicketV() {
		return TicketV;
	}

	public static void setTicketV(String ticketV) {
		TicketV = ticketV;
	}

	public static String getTgsId() {
		return TGS_ID;
	}

	public static String getVserverId() {
		return VSERVER_ID;
	}

	public static String getAsId() {
		return AS_ID;
	}

	public static String getTimeStamp()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		return sdf.format(new Date());
	}

}
