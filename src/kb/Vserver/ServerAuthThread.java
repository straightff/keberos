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
import kb.ToolClass.SqliteTool;
import kb.client.ClientApplication;
import kb.constant.KbConstants;
import kb.ToolClass.DesTool;

//读取客户端消息
public class ServerAuthThread implements Runnable {
    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private ServerInitThread server;
    private static String mode;

    public ServerAuthThread(Socket socket, ServerInitThread Server) throws IOException {
        mode = "111";
        this.socket = socket;
        server = Server;
        br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        os = this.socket.getOutputStream();
    }

    @Override
    public void run() {

        System.out.println("begin");
        while (true) {
            try {
                AuthAndPacked();
            } catch (Exception e) {
                Platform.runLater(() -> server.getLoglist().add("error read"));
                e.printStackTrace();
            }
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
    public void AuthAndPacked() throws Exception {
        //收包
        String recvPackEncode = recvAuthMess();
        String[] clientMess = unPack(recvPackEncode);
        //获取头部，来判断VServer的应用方式
        mode = clientMess[0];
        String packEncode; //要返回的包
//            System.out.println("mode is "+mode);
        if (mode.equals(KbConstants.C_V)) {
            Platform.runLater(() -> ClientApplication.getLoglist().add("认证开始"));
            SqliteTool sql = new SqliteTool("ServerMess");

            String Vkey = sql.searchOneFromTable(VServerUI.getVserverId(), "ServerRegist", "KEY");

            Platform.runLater(() -> server.getLoglist().add("C-->Vserver：" + recvPackEncode));

            System.out.println("clientMess:");
            for (String mess : clientMess) {
                System.out.println(mess);
            }

            String[] ticket = unPack(DesTool.decrypt(clientMess[1], Vkey));
            System.out.println("ticket package");
            for (String s : ticket) {
                System.out.println(s);
            }

            String Kcv = ticket[0];
            System.out.println("kcv:" + Kcv);
            String[] Auth = unPack(DesTool.decrypt(clientMess[2], Kcv));

            System.out.println("Auth package");
            for (String s : Auth) {
                System.out.println(s);
            }
            //发包
            String packContent = Auth[2] + 1;
            packEncode = DesTool.encrypt(packContent, Kcv);
            writeAuthMess(packEncode);
            String finalPack = packEncode;
            Platform.runLater(() -> server.getLoglist().add("Vserver-->C:" + finalPack));
            Platform.runLater(() -> ClientApplication.getLoglist().add("认证完成"));
            //todo 验证过程
        }
        //mode = KbContains.C_V_chat
        else {
            ClientBean cli = new ClientBean(clientMess[1], clientMess[2], clientMess[3], socket);
            String ts = VServerUI.getTimeStamp();
            cli.getMessMap().put(ts, clientMess[4]);
            // packEncode = clientMess[1];
//              final String mess = br.readLine();
            Platform.runLater(() -> {
                server.getLoglist().add("clientID:" + cli.getClientId() + " Addr:" + cli.getAddr() + " user:" + cli.getName()+" mess:"+ts+" "+clientMess[4]);
            });
            server.writeToAll("clientID:" + cli.getClientId() + " Addr:" + cli.getAddr() + " user:" + cli.getName()+" ts:"+ts+" mess:"+clientMess[4]);

//            return packEncode;
        }
    }

    //写认证消息
    public void writeAuthMess(String input) throws IOException {
        os.write((input + "=" + "\n").getBytes());
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
