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
import kb.TGS.TGSUI;
import kb.ToolClass.SqliteTool;
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
        String pack = null;
        try {
            pack = AuthAndPacked();
            writeAuthMess(pack);


            if (mode.equals(KbConstants.C_V)) {

                String finalPack = pack;
                Platform.runLater(()->server.getLoglist().add("Vserver-->C:" + finalPack));
            }
            else {
                while (true) {
                    try {
                        final String mess = br.readLine();
                        Platform.runLater(() -> server.getLoglist().add(mess));
                        server.writeToAll(mess);
                    } catch (IOException e) {
                        Platform.runLater(() -> server.getLoglist().add("error read"));
                        e.printStackTrace();
                    }
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
    public String AuthAndPacked() throws Exception {
        //收包
        SqliteTool sql = new SqliteTool("ServerMess");

        String Vkey = sql.searchOneFromTable(VServerUI.getVserverId(), "ServerRegist", "KEY");
        String recvPackEncode = recvAuthMess();

        String[] clientMess = unPack(recvPackEncode);
        Platform.runLater(()->server.getLoglist().add("C-->Vserver：" + recvPackEncode));

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
        System.out.println("kcv:"+Kcv);
        String[] Auth = unPack(DesTool.decrypt(clientMess[2], Kcv));

        System.out.println("Auth package");
        for (String s : Auth) {
            System.out.println(s);
        }

        //获取头部，来判断VServer的应用方式
        mode = clientMess[0];

        //todo 验证过程

        //发包
        String packContent = Auth[2] + 1;
        String packEncode = DesTool.encrypt(packContent, Kcv);


        return packEncode;
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
