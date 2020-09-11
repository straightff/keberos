package kb.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kb.constant.KbConstants;
import kb.ToolClass.RandomKeyTool;
import kb.ToolClass.DesTool;

public class ClientThread implements Runnable {
    private Socket socket;
    //	private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    //	private BufferedWriter bw;
    private String name;
    public ObservableList<String> messList;
    private int mode;

    // 读消息
    @Override
    public void run() {

        if (mode == KbConstants.Auth_MODE) {
            // AS
            try {
                AS_auth();
                TGS_auth();
                VServer_auth();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // TGS

        } else {
            System.out.println("cut");
            while (true) {
                try {
                    final String mess = br.readLine();

                    Platform.runLater(() -> messList.add(mess));

                } catch (SocketException e) {
                    Platform.runLater(() -> messList.add("ERROR!"));
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    // 初始化建立连接
    public ClientThread(String hostName, int port, String name, int mode) throws UnknownHostException, IOException {

        socket = new Socket(hostName, port);

        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        os = socket.getOutputStream();
        messList = FXCollections.observableArrayList();
        this.name = name;
    }

    public ClientThread(String hostName, int port, int mode) throws UnknownHostException, IOException {

        socket = new Socket(hostName, port);

        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        os = socket.getOutputStream();
        messList = FXCollections.observableArrayList();
        this.mode = mode;
    }

    // AS 认证过程，请求得到tgsticket保存
    private void AS_auth() throws IOException {
        //发包
        String send_pack = KbConstants.C_AS_regist + KbConstants.SEP + ClientApplication.getClient_ID() + KbConstants.SEP + ClientApplication.getTgsId()
                + KbConstants.SEP + ClientApplication.getTimeStamp() + KbConstants.SEP + ClientApplication.getHashKey(); // 将要发送的数据包
        writeAuthMess(send_pack,os);
        try {
            String recvPackEncode = recvAuthMess(br);
            String recvPack = DesTool.decrypt(recvPackEncode, ClientApplication.getHashKey());
            System.out.println("recvPack is"+recvPack);

            String[] clientMess = recvPack.split("-");
            for (String a:clientMess) {
                System.out.println(a);
            }
            //存储tgsTicket
            ClientApplication.setTgsTicket(clientMess[6]);
            ClientApplication.getLoglist().add("Tgs TGT:"+clientMess[6]);

            //存储c_tgs
            ClientApplication.setKctgs(clientMess[1]);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // TGS 认证过程 得到ticketV
    private void TGS_auth() throws Exception {

        Socket TGSsocket = new Socket("localhost",7777);
        OutputStream TGSos = TGSsocket.getOutputStream();

        BufferedReader TGSbr = new BufferedReader(new InputStreamReader(TGSsocket.getInputStream()));

        Platform.runLater(() -> ClientApplication.getLoglist().add("TGS auth begin!"));
        //发包
        String Auth = ClientApplication.getClient_ID() + KbConstants.SEP + TGSsocket.getLocalAddress().toString()
                + KbConstants.SEP + ClientApplication.getTimeStamp();
//        String[] recv = ClientApplication.getClientMess();
        System.out.println(ClientApplication.getKctgs());
        String Auth_encoded = DesTool.encrypt(Auth, ClientApplication.getKctgs());
        String pack = KbConstants.C_TGS + KbConstants.SEP + ClientApplication.getVserverId() + KbConstants.SEP
                + ClientApplication.getTgsTicket() + KbConstants.SEP + Auth_encoded+KbConstants.SEP+ClientApplication.getKctgs();

        //由于加密函数没有在后面添加结束标志位，因此，在这里添加 ‘=’ 作为包结束的标志
        writeAuthMess(pack,TGSos);
        Platform.runLater(() -> ClientApplication.getLoglist().add("C-->TGS:"+pack));




        //收包
        String recvPackEncode = recvAuthMess(TGSbr);
        Platform.runLater(() ->ClientApplication.getLoglist().add("收到来自TGS的包"));


        //解密
        String recvPack = DesTool.decrypt(recvPackEncode,ClientApplication.getKctgs());

        //解包
        String[] clientMess = unPack(recvPack);

        String Kcv = clientMess[1];
        String ticketV = clientMess[4];

        //存储ticketV
        ClientApplication.setVTicket(ticketV);
        ClientApplication.setKcv(Kcv);
        //todo 添加到client的loglist
        Platform.runLater(() -> ClientApplication.getLoglist().add("ticketV:"+ticketV));
        TGSos.close();
        TGSbr.close();

    }
    //VServer认证过程

    private void VServer_auth() throws Exception {

        Socket Vsocket = new Socket("localhost",8888);
        OutputStream VSos = Vsocket.getOutputStream();
        BufferedReader Vbr = new BufferedReader(new InputStreamReader(Vsocket.getInputStream()));
        //发包

        String Kcv = ClientApplication.getKcv();

        String TS = ClientApplication.getTimeStamp();
        String AuthContent = ClientApplication.getClient_ID()+KbConstants.SEP+socket.getLocalAddress().toString()+KbConstants.SEP+TS;
        String AuthEncode = DesTool.encrypt(AuthContent,Kcv);
        String pack = KbConstants.C_V+KbConstants.SEP+ClientApplication.getVTicket()+KbConstants.SEP+AuthEncode;
        writeAuthMess(pack,VSos);



        //收包
        String recvPackEncode = recvAuthMess(Vbr);
        Platform.runLater(() -> ClientApplication.getLoglist().add("V-->C:"+recvPackEncode));


        String recvPack = DesTool.decrypt(recvPackEncode,ClientApplication.getKcv());
        String[] clientMess = unPack(recvPack);

        if(clientMess[1].equals(TS+1))
        {

        }
        VSos.close();
        Vbr.close();
//        Vsocket.close();
    }


    public String recvAuthMess(BufferedReader br1) throws IOException {
        StringBuilder req = new StringBuilder();
        String mess = "";
        while (!(mess = br1.readLine()).endsWith("=")) {
            req.append(mess);
        }
        req.append(mess).deleteCharAt(req.length() - 1);
        return req.toString();
    }

    public void writeAuthMess(String input,OutputStream os1) throws IOException {
        os1.write((input + "=" + "\n").getBytes(StandardCharsets.UTF_8));
        os1.flush();
    }

    // 写聊天消息
    public void writeMessage(String input) throws IOException {
        os.write(("client-" + name + "says-" + input + "\n").getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    public ObservableList<String> getMessList() {
        return messList;
    }

    public String[] unPack(String pack)
    {
        String[] temp = new String[10];
        temp = pack.split("-");
        return temp;
    }
    //判断数据库中是否存在
    public boolean isAlreadExist(String input)
    {
        return false;
    }
}
