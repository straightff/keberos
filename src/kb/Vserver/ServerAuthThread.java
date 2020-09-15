package kb.Vserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import kb.TGS.TGSUI;
import kb.ToolClass.RsaTool;
import kb.ToolClass.SqliteTool;
import kb.client.ClientApplication;
import kb.constant.KbConstants;
import kb.ToolClass.DesTool;
import sun.security.krb5.internal.crypto.Des;

//读取客户端消息
public class ServerAuthThread implements Runnable {
    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private ServerInitThread server;
    private static String mode;
    private static boolean isAuth = false;

    public ServerAuthThread(Socket socket, ServerInitThread Server) throws IOException {
        mode = "111";
        this.socket = socket;
        server = Server;
        br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        os = this.socket.getOutputStream();
    }

    @Override
    public void run() {

//        while (true) {
//            try {
//                String recv = recvAuthMess();
//                String[] cmess = unPack(recv);
//                if (mode.equals(KbConstants.C_V)) {
//                    AuthAndPacked(recv);
//                   break;
//                } else {
//                    chatAndPack(recv);
//
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            System.out.println("验证线程结束");
//        }
        String pack = null;

        try {
            String recv = recvAuthMess();
            String mess1[] = unPack(recv);
            for (String s : mess1) {
                System.out.println(s);
            }

            if (mess1[0].equals(KbConstants.C_V)) {
                pack = AuthAndPacked(recv);
                writeAuthMess(pack);
                String finalPack = pack;
//                Platform.runLater(() -> server.getLoglist().add("C-->Vserver 收到的包为:\n" + finalPack));

            } else {
                ServerInitThread.getClientThread().add(this);
                System.out.println("liaotian");
                Platform.runLater(() -> server.getLoglist().add("聊天线程启动"));
                try {
                    chatAndPack(recv);
//                        server.writeToAll(mess);
                    String mes = "";

                    while ((mes = br.readLine()) != null) {
                        StringBuilder sb = new StringBuilder();
                        System.out.println("recvmes" + mes);
                        sb.append(mes).deleteCharAt(sb.length() - 1);
                        chatAndPack(sb.toString());
                    }
                    String finalMes = mes;
                    Platform.runLater(() -> server.getLoglist().add("收到的加密聊天包:\n" + finalMes));
                } catch (IOException e) {
                    Platform.runLater(() -> server.getLoglist().add("error read"));
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //计算时间戳的时间差
    public static int caculateTS(String TS1, String TS2) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date TSDATE1 = null;
        Date TSDATE2 = null;
        try {
            TSDATE1 = df.parse(TS1);
            TSDATE2 = df.parse(TS2);
        } catch (Exception e) {
            // 日期型字符串格式错误
        }

        int nDay = (int) ((TSDATE2.getTime() - TSDATE1.getTime()));
        return nDay;
    }


    //验证及打包
    public String AuthAndPacked(String recvPackEncode) throws Exception {
        //收包
//        String recvPackEncode = recvAuthMess();
        String[] clientMess = unPack(recvPackEncode);

        //获取头部，来判断VServer的应用方式
        //mode = clientMess[0];
        String packEncode; //要返回的包
//            System.out.println("mode is "+mode);
        mode = KbConstants.C_V;
        Platform.runLater(() -> server.getLoglist().add("Vserver认证开始"));
        SqliteTool sql = new SqliteTool("ServerMess");
        VServerUI.setTicketV(clientMess[1]);
        String Vkey = sql.searchOneFromTable(VServerUI.getVserverId(), "ServerRegist", "KEY");

        Platform.runLater(() -> server.getLoglist().add("C-->Vserver 收到的包为:\n" + recvPackEncode));

        System.out.println("clientMess:");
        Platform.runLater(() -> {
            server.getLoglist().add("解包:");
            for (String mess : clientMess) {
                server.getLoglist().add(mess);
                System.out.println(mess);
            }
            server.getLoglist().add("解包完成");
        });

        String[] ticket = unPack(DesTool.decrypt(clientMess[1], Vkey));
        System.out.println("ticket package");
        Platform.runLater(() -> {
            server.getLoglist().add("ticket解包:");
            for (String s : ticket) {
                server.getLoglist().add(s);
                System.out.println(s);
            }
            server.getLoglist().add("解包完成");
        });


        String Kcv = ticket[0];
        System.out.println("kcv:" + Kcv);
        String[] Auth = unPack(DesTool.decrypt(clientMess[2], Kcv));

        System.out.println("Auth package");
        Platform.runLater(() -> {
            server.getLoglist().add("Auth 认证器解包:");
            for (String s : Auth) {
                server.getLoglist().add(s);
                System.out.println(s);
            }
            server.getLoglist().add("解包完成");
        });

//        for (String s : Auth) {
//            System.out.println(s);
//        }

        //认证
        String head = "";
        if (ticket[1].equals(Auth[0]))
        {
            head = KbConstants.V_C;
        }else {
            head = KbConstants.V_C_ERROR;
        }
        //发包
        String packContent = head + KbConstants.SEP + Auth[2] + 1;
        packEncode = DesTool.encrypt(packContent, Kcv);
        writeAuthMess(packEncode);
        String finalPack = packEncode;

        Platform.runLater(() -> {
            server.getLoglist().add("打包后的pack:\n"+packContent);
            server.getLoglist().add("加密后的pack:\n"+packEncode);
            server.getLoglist().add("Vserver-->C 发送的包为: \n" + finalPack);
            Platform.runLater(() -> server.getLoglist().add("认证完成"));
        });
        return packEncode;

    }

    public void chatAndPack(String mess) throws Exception {
        System.out.println(VServerUI.getTicketV());
//        ServerInitThread.getClientThread().add(this);
        String[] messages = unPack(mess);
//        String packDesDecode = DesTool.decrypt(messages[1], VServerUI.getTicketV());
////            packDesDecode.replaceAll("\r|\n","");
//        System.out.println(packDesDecode);
        String[] pri = unPack(KbConstants.S_PriKEY);
        String packRsaDecode = RsaTool.deCode(pri[0], pri[1], messages[1].trim());
        String[] Mess = unPack(packRsaDecode);
        ClientBean cli = new ClientBean(Mess[0], Mess[1], Mess[2], socket);

        cli.getMessMap().put(Mess[3], Mess[4]);
        // packEncode = clientMess[1];
//              final String mess = br.readLine();
        Platform.runLater(() -> {
            server.getLoglist().add("clientID:" + cli.getClientId() + " Addr:" + Mess[2] + " user:" + Mess[1] + " time:" + Mess[3] + " Message:" + Mess[4]);

        });

        try {
            server.writeToAll(Mess[3], cli.getClientId(), cli.getName(), Mess[4]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeMessage(String input, String clientid, String userName, String ts) throws Exception {
//        os.write((KbConstants.C_V_CHAT+"client-" + name + "-says-" + input + "\n").getBytes(StandardCharsets.UTF_8));
        // head-clientid-userName-Addr-mess=

//        String pack = KbConstants.V_C_CHAT + KbConstants.SEP + clientid + KbConstants.SEP + userName + KbConstants.SEP + socket.getInetAddress() + KbConstants.SEP + ts + KbConstants.SEP + input;
        String pack = clientid + KbConstants.SEP + userName + KbConstants.SEP + socket.getInetAddress() + KbConstants.SEP + ts + KbConstants.SEP + input;

        String[] key = unPack(KbConstants.C_PubKEY);
        //rsa加密
        String mess = RsaTool.enCode(key[0], key[1], pack);
        Platform.runLater(() -> {
            server.getLoglist().add("将要广播的未加密聊天包:\n"+pack);
            server.getLoglist().add("经过Client的公钥加密后的聊天包:\n" + mess);
        });
//        //再套外面的des加密
//        System.out.println("tV::" + VServerUI.getTicketV());
//        String packDesEncode = DesTool.encrypt(mess, VServerUI.getTicketV());
//        Platform.runLater(() -> {
//            server.getLoglist().add("消息经过ticketV des加密:" + packDesEncode);
//        });
        os.write((KbConstants.V_C_CHAT + KbConstants.SEP + mess + "=" + "\n").getBytes(StandardCharsets.UTF_8));
        os.flush();
        os.flush();

    }

    //写认证消息
    public void writeAuthMess(String input) throws IOException {
        os.write((input + "=" + "\n").getBytes("UTF-8"));
        os.flush();
    }

    //接收验证消息
    public String recvAuthMess() throws IOException {
        StringBuilder req = new StringBuilder();
        String mess = "";
        while (!(mess = br.readLine()).endsWith("=")) {
            req.append(mess);
        }
//		System.out.println("get there!");
        req.append(mess).deleteCharAt(req.length() - 1);
        return req.toString();
    }

    //写聊天消息
    public void writeMessage(String input) throws IOException {
        os.write((input + "\n").getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    public String[] unPack(String pack) {
        String[] temp = new String[10];
        temp = pack.split("-");
        return temp;
    }
}


