package kb.Vserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

//接受客户端连接并存储进 recvClientMess
public class ServerInitThread implements Runnable
{
	private static ArrayList<Socket> recvClientSocket;// 存储接受客户端消息的socket表
	private static ArrayList<ServerAuthThread> clientThread;// 存储处理客户端信息读取线程的Thread表
	private ObservableList<String> loglist;// 存储服务器收到的信息
	private ObservableList<String> clientName;// 存储客户端名称

	private ServerSocket ss;
	private Socket socket;
	public static ListView<String> logArea;
	private static final int MaxConnect = 10;
	private static int i = 0;

	public ServerInitThread(int port) throws IOException
	{
		super();

		recvClientSocket = new ArrayList<Socket>();
		clientThread = new ArrayList<ServerAuthThread>();
		loglist = FXCollections.observableArrayList();
		clientName = FXCollections.observableArrayList();

		this.ss = new ServerSocket(port);

	}

	@Override
	public void run()
	{
		while (i < MaxConnect)
		{

			try
			{
				Platform.runLater(() ->
				{
					loglist.add("Listenning!!");
				});
				socket = ss.accept();

				Platform.runLater(() ->
				{
					if (socket.isConnected())
					{
						loglist.add("connect");
					}
				});
				recvClientSocket.add(socket);
				ServerAuthThread sat = new ServerAuthThread(socket, this);
				Thread rThread = new Thread(sat);
				clientThread.add(sat);
//			rThread.setDaemon(true);
				rThread.start();

//				ServerReaderThread srt = new ServerReaderThread(socket,this);
//				Thread thread =new Thread(srt);
//				clientThread.add(thread);

				logArea.setItems(loglist);
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}

	}

	public void writeToAll(String input) throws IOException
	{
		for (ServerAuthThread srt : clientThread)
		{
			srt.writeMessage(input);
		}
	}

	public static ArrayList<ServerAuthThread> getClientThread()
	{
		return clientThread;
	}

	public ObservableList<String> getLoglist()
	{
		return loglist;
	}

	public static ArrayList<Socket> getRecvClientSocket()
	{
		return recvClientSocket;
	}

	public ObservableList<String> getClientName()
	{
		return clientName;
	}
}
