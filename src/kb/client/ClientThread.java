
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
import kb.ToolClass.RsaTool;
import kb.Vserver.VServerUI;
import kb.constant.KbConstants;
import kb.ToolClass.RandomKeyTool;
import kb.ToolClass.DesTool;

public class ClientThread implements Runnable {
    private Socket socket;
    //	private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    //	private BufferedWriter
    private String name; //username
    public ObservableList<String> messList;
    public ObservableList<String> loglist;
    private int mode;
    private ClientApplication cli;

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

        }
        if(mode==KbConstants.VSERVER_MODE)
        {
            while (true) {
                try {
                    final String mess = recvAuthMess(br);
                    String[] messages = unPack(mess);
                    Platform.runLater(() -> {
                        for (String message : messages) {
                            System.out.println(message);
                        }

//                        String packDesDecode = null;
                        try {
//                            packDesDecode = DesTool.decrypt(messages[1], ClientApplication.getVTicket());
//                            System.out.println(packDesDecode);
                            String[] pri = unPack(KbConstants.C_PriKEY);
                            String packRsaDecode = RsaTool.deCode(pri[0],pri[1],messages[1]);
                            System.out.println("packRSaDecode:"+packRsaDecode);
                            String[] Mess = unPack(packRsaDecode);
                            Platform.runLater(()->
                            {
                                messList.add("收到的加密包为:\n"+mess);
                                messList.add("clientID:" + Mess[0] + "\tAddr:" + Mess[2] + "\ttime:"+Mess[4]);
                                messList.add("user: "+Mess[1]+"\tMessage: "+Mess[3]);
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//            packDesDecode.replaceAll("\r|\n","");

                    });
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
    public ClientThread(String hostName, int port, String name, int mode,ClientApplication cli) throws UnknownHostException, IOException {

        socket = new Socket(hostName, port);
        this.cli = cli;
//        this.cli = new ClientApplication();
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        os = socket.getOutputStream();
        messList = FXCollections.observableArrayList();//消息列表
        loglist = FXCollections.observableArrayList();//log列表
        this.name = name;
        this.mode = mode;
    }

    public ClientThread(String hostName, int port, int mode,ClientApplication cli) throws UnknownHostException, IOException {

        socket = new Socket(hostName, port);
        this.cli = cli;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        os = socket.getOutputStream();
        messList = FXCollections.observableArrayList();//消息列表
        loglist = FXCollections.observableArrayList();//log列表
        this.mode = mode;
    }

    // AS 认证过程，请求得到tgsticket保存
    private void AS_auth() throws IOException {
        //发包
        Platform.runLater(()->cli.getLoglist().add("AS认证开始!"));
        String send_pack = KbConstants.C_AS + KbConstants.SEP + ClientApplication.getClient_ID() + KbConstants.SEP + ClientApplication.getTgsId()
                + KbConstants.SEP + ClientApplication.getTimeStamp() ; // 将要发送的数据包
        Platform.runLater(()->cli.getLoglist().add("C-->AS发送的包为：\n"+send_pack));

        writeAuthMess(send_pack, os);
        try {
            String recvPackEncode = recvAuthMess(br);

            //rsa
            String[] keys = unPack(KbConstants.C_PriKEY);
            String recvPack = RsaTool.deCode(keys[0],keys[1],recvPackEncode);
            System.out.println("recvPack is" + recvPack);
            Platform.runLater(()->
            {
                cli.getLoglist().add("AS-->C收到的包为：\n"+recvPack);

            });

            String[] clientMess = recvPack.split("-");
            Platform.runLater(()->{
                cli.getLoglist().add("解包后的信息为:");
                for (String mess : clientMess) {
                    cli.getLoglist().add(mess);
                }
            });
            //存储tgsTicket
            cli.setTgsTicket(clientMess[6]);
//            ClientApplication.getLoglist().add("Tgs TGT:" + clientMess[6]);

            //存储c_tgs
            cli.setKctgs(clientMess[1]);

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

        //建立与TGS的socket连接
        Socket TGSsocket = new Socket(KbConstants.TgsAddr, 7777);
        OutputStream TGSos = TGSsocket.getOutputStream();

        BufferedReader TGSbr = new BufferedReader(new InputStreamReader(TGSsocket.getInputStream()));

        Platform.runLater(() -> cli.getLoglist().add("TGS 认证开始!"));
        //发包
        String Auth = ClientApplication.getClient_ID() + KbConstants.SEP + TGSsocket.getLocalAddress().toString()
                + KbConstants.SEP + ClientApplication.getTimeStamp();
//        String[] recv = ClientApplication.getClientMess();
        System.out.println(cli.getKctgs());
        String Auth_encoded = DesTool.encrypt(Auth, cli.getKctgs());
        String pack = KbConstants.C_TGS + KbConstants.SEP + ClientApplication.getVserverId() + KbConstants.SEP
                + cli.getTgsTicket() + KbConstants.SEP + Auth_encoded + KbConstants.SEP + cli.getKctgs();

        //由于加密函数没有在后面添加结束标志位，因此，在这里添加 ‘=’ 作为包结束的标志
        writeAuthMess(pack, TGSos);
        Platform.runLater(() -> cli.getLoglist().add("C-->TGS发的包为" + pack));


        //收包
        String recvPackEncode = recvAuthMess(TGSbr);
        Platform.runLater(() -> cli.getLoglist().add("收到来自TGS的包"));


        //解密
        String recvPack = DesTool.decrypt(recvPackEncode, cli.getKctgs());
        Platform.runLater(()->cli.getLoglist().add("TGS-->C收到的包为:"+recvPack));
        //解包
        String[] clientMess = unPack(recvPack);
        Platform.runLater(()->{
            cli.getLoglist().add("解包后的信息为:");
            for (String mess : clientMess) {
                cli.getLoglist().add(mess);
            }
        });
        String Kcv = clientMess[1];
        String ticketV = clientMess[4];

        //存储ticketV
        cli.setVTicket(ticketV);
        cli.setKcv(Kcv);

//        Platform.runLater(() -> ClientApplication.getLoglist().add("ticketV:" + ticketV));？
        TGSos.close();
        TGSbr.close();

    }
    //VServer认证过程

    private void VServer_auth() throws Exception {

        Platform.runLater(() -> cli.getLoglist().add("Vserver 认证开始!"));
        //建立与server的socket连接
        Socket Vsocket = new Socket(KbConstants.VsAddr, 8888);
        OutputStream VSos = Vsocket.getOutputStream();
        BufferedReader Vbr = new BufferedReader(new InputStreamReader(Vsocket.getInputStream()));

        //发包
        String Kcv = cli.getKcv();

        String TS = ClientApplication.getTimeStamp();
        String AuthContent = ClientApplication.getClient_ID() + KbConstants.SEP + socket.getLocalAddress().toString() + KbConstants.SEP + TS;
        String AuthEncode = DesTool.encrypt(AuthContent, Kcv);
        String pack = KbConstants.C_V + KbConstants.SEP + cli.getVTicket() + KbConstants.SEP + AuthEncode;
        Platform.runLater(()->cli.getLoglist().add("C-->V 发的包为:\n"+pack));
        writeAuthMess(pack, VSos);


        //收包
        String recvPackEncode = recvAuthMess(Vbr);
        Platform.runLater(() -> cli.getLoglist().add("V-->C 收的包为:\n" + recvPackEncode));


        String recvPack = DesTool.decrypt(recvPackEncode, cli.getKcv());
        String[] clientMess = unPack(recvPack);
        for (String mess : clientMess) {
            System.out.println(mess);
        }

        if (clientMess[1].equals(TS + 1)) {
            Platform.runLater(() -> cli.getLoglist().add("认证成功"));
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

    public void writeAuthMess(String input, OutputStream os1) throws IOException {
        os1.write((input + "=" + "\n").getBytes(StandardCharsets.UTF_8));
        os1.flush();
    }

    // 写聊天消息
    public void writeMessage(String input,String clientid,String userName) throws Exception {
//        os.write((KbConstants.C_V_CHAT+"client-" + name + "-says-" + input + "\n").getBytes(StandardCharsets.UTF_8));
        // head-clientid-userName-Addr-mess=

        String pack = clientid +KbConstants.SEP+userName+KbConstants.SEP+socket.getLocalAddress()+KbConstants.SEP+ClientApplication.getTimeStamp()+KbConstants.SEP+ input;
        String[] key = unPack(KbConstants.S_PubKEY);
        //rsa加密
        String mess = RsaTool.enCode(key[0],key[1],pack);
        Platform.runLater(()->{
            loglist.add("未加密的pack为:\n"+pack);
            loglist.add("消息经过Vserver的公钥加密:"+mess);
        });
        //再套外面的des加密
//        System.out.println("tV::"+ClientApplication.getVTicket());
//        String packDesEncode = DesTool.encrypt(mess,ClientApplication.getVTicket());
//        Platform.runLater(()->{
//            loglist.add("消息经过ticketV des加密:"+packDesEncode);
//        });
        os.write((KbConstants.C_V_CHAT + KbConstants.SEP +mess+"="+ "\n").getBytes(StandardCharsets.UTF_8));
        os.flush();

    }

    public ObservableList<String> getMessList() {
        return messList;
    }

    public String[] unPack(String pack) {
        String[] temp = new String[10];
        temp = pack.split("-");
        return temp;
    }

    //判断数据库中是否存在
    public boolean isAlreadExist(String input) {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
