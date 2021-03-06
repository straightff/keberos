package kb.AS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javafx.application.Platform;
import kb.TGS.TGSUI;
import kb.ToolClass.DesTool;
import kb.ToolClass.RsaTool;
import kb.ToolClass.SqliteTool;
import kb.constant.KbConstants;
import kb.ToolClass.RandomKeyTool;

//读取客户端消息
public class ServerReaderThead implements Runnable {
    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private ServerInitThread server;
    private String[] clientMess;

    public ServerReaderThead(Socket socket, ServerInitThread Server) throws IOException {
        this.clientMess = new String[12];
        this.socket = socket;
        server = Server;
        br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        os = this.socket.getOutputStream();
    }

    @Override
    public void run() {

        String mess = null;
        try {
            mess = recvAuthMess();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clientMess = unPack(mess);
        String finalMess = mess;
        Platform.runLater(() ->
        {
//            server.getPackList().add(finalMess); // 将接受的包先存入list中暂存
            server.getLoglist().add("接收到新的请求>>>>>");

            if (clientMess[0].equals(KbConstants.C_AS)) {
                server.getLoglist().add("接收到一条来自Client的请求信息，内容如下");

//                clientMess = unPack(finalMess);
                for (String string : clientMess) {
                    server.getLoglist().add(string);
                }
                server.getLoglist().add("解包完成");

                //开始对client返回信息
                String AS_req_encoded = null;
                try {
                    AS_req_encoded = getPacked();
                    System.out.println(AS_req_encoded);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    //向客户端返回信息
                    writeAuthMess(AS_req_encoded);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (clientMess[0].equals(KbConstants.C_AS_ERROR)) {
                server.getLoglist().add("Client 异常断开!");
            }
        });

        // server.writeToAll(mess);


    }

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


    //封包
    public String getPacked() throws Exception {

        SqliteTool sql = new SqliteTool("ServerMess");

        String tgskey = sql.searchOneFromTable(TGSUI.getTgsId(),"ServerRegist","KEY");
        //生成 c-tgs session-key
        ASUI.setKctgs(RandomKeyTool.createAuth(clientMess[1], ASUI.getTgsId())); // c-tgs session-key

        String tgsContent = ASUI.getKctgs() + KbConstants.SEP + clientMess[1] + KbConstants.SEP
                + socket.getLocalSocketAddress().toString() + KbConstants.SEP + ASUI.getTgsId()+KbConstants.SEP
                + ASUI.getTimeStamp()+KbConstants.SEP + ASUI.getLifetime();

        String ticket = DesTool.encrypt(tgsContent, tgskey);
        String AS_req = KbConstants.AS_C + KbConstants.SEP + ASUI.getKctgs() + KbConstants.SEP + clientMess[1]
                + KbConstants.SEP + ASUI.getTgsId() + KbConstants.SEP + ASUI.getTimeStamp()
                + KbConstants.SEP + ASUI.getLifetime() + KbConstants.SEP + ticket;
        String[] keys = unPack(KbConstants.C_PubKEY);
        String AS_req_encoded = RsaTool.enCode(keys[0],keys[1],AS_req);
        return AS_req_encoded;
    }

    public String[] unPack(String pack) {
        String[] temp = new String[10];
        temp = pack.split("-");
        return temp;
    }

    //添加'='号以做密文结尾，readline 方法读取结束的标志
    public void writeAuthMess(String input) throws IOException {
        os.write((input + "=" + "\n").getBytes(StandardCharsets.UTF_8));
        os.flush();
    }
}
