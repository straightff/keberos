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
public class ServerInitThread implements Runnable {
    private static ArrayList<Socket> recvClientSocket;// 存储接受客户端消息的socket表
    private static ArrayList<ServerAuthAndReadThread> clientThread;// 存储处理客户端信息读取线程的Thread表
    private ObservableList<String> loglist;// 存储服务器收到的信息
    private ObservableList<String> clientName;// 存储客户端名称
    private static ArrayList<ClientBean> client; //存储客户端的信息Bean

    private ServerSocket ss;
    private Socket socket;
    public static ListView<String> logArea;
    private static final int MaxConnect = 10;
    private static int i = 0;

    public ServerInitThread(int port) throws IOException {
        super();
        client = new ArrayList<>();
        recvClientSocket = new ArrayList<Socket>();
        clientThread = new ArrayList<ServerAuthAndReadThread>();
        loglist = FXCollections.observableArrayList();
        clientName = FXCollections.observableArrayList();


        this.ss = new ServerSocket(port);

    }

    @Override
    public void run() {
        while (i < MaxConnect) {

            try {
                Platform.runLater(() ->
                {
                    loglist.add("Listenning!!");
                });
                socket = ss.accept();

                Platform.runLater(() ->
                {
                    if (socket.isConnected()) {
                        loglist.add("connect");
                    }
                });
                recvClientSocket.add(socket);
                ServerAuthAndReadThread sat = new ServerAuthAndReadThread(socket, this);
                Thread rThread = new Thread(sat);
                System.out.println("第"+i+"个线程");
//                ServerInitThread.getClientThread().add(sat);
//			rThread.setDaemon(true);
                rThread.start();

//				ServerReaderThread srt = new ServerReaderThread(socket,this);
//				Thread thread =new Thread(srt);
//				clientThread.add(thread);
                ++i;
                logArea.setItems(loglist);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    public void writeToAll(String input, String clientid, String userName, String ts) throws Exception {
        System.out.println("Thread 连接数量：" + clientThread.size());

//        for (int i = 0; i < recvClientSocket.size(); i++) {
//            clientThread.get(i).writeMessage(input, clientid, userName, ts);
//        }
        int i=0;
        for (ServerAuthAndReadThread srt : clientThread) {
//            if(i%2==0)
//            {
//                ++i;
//                continue;
//            }
//            ++i;
            System.out.println("i="+i);
            srt.writeMessage(input, clientid, userName, ts);
        }
    }
//    public void writeToAll(String input) throws Exception {
//        System.out.println("Thread 连接数量：" + clientThread.size());
//        int i=0;
//        for (ServerAuthThread srt : clientThread) {
//            if(i%2==0){
//                ++i;
//                continue;
//            }
//            srt.writeMessage(input);
//        }
//    }

    public static void setClientThread(ArrayList<ServerAuthAndReadThread> clientThread) {
        ServerInitThread.clientThread = clientThread;
    }

    public static ArrayList<ServerAuthAndReadThread> getClientThread() {
        return clientThread;
    }

    public ObservableList<String> getLoglist() {
        return loglist;
    }

    public static ArrayList<Socket> getRecvClientSocket() {
        return recvClientSocket;
    }

    public ObservableList<String> getClientName() {
        return clientName;
    }
}
