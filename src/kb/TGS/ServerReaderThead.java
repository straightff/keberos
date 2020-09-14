package kb.TGS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.application.Platform;
import kb.ToolClass.SqliteTool;
import kb.client.ClientApplication;
import kb.constant.KbConstants;
import kb.ToolClass.DesTool;
import kb.ToolClass.RandomKeyTool;

//读取客户端消息
public class ServerReaderThead implements Runnable {
    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private ServerInitThread server;
    private String[] clientMess;

    public ServerReaderThead(Socket socket, ServerInitThread Server) throws IOException {
        this.socket = socket;
        server = Server;
        br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        os = this.socket.getOutputStream();
    }

    @Override
    public void run() {

        //处理信息的主线程
        Platform.runLater(() ->
        {
            server.getLoglist().add("接收到新的数据包....处理中");
            try {
                String reqText = AuthAndPacked();
                server.getLoglist().add("TGS-->C" + reqText);
                writeAuthMess(reqText);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
//				server.writeToAll(mess);

    }

    public String AuthAndPacked() throws Exception {


        String mess = recvAuthMess();
        Platform.runLater(() -> server.getLoglist().add("C-->TGS:"+mess));
        System.out.println(mess);
        System.out.println("got!");
        //C-tgs无加密。解密步骤注释
//		String mess_decode = DesTool.decrypt(mess_encode,)
        String[] clientMess = unPack(mess);

        //todo 检查kdc数据库之中是否存在 Vserver的注册ID 不存在返回错误信息 继续下面的验证步骤


        //todo 从数据库获取tgs的key tgskey
        SqliteTool sql = new SqliteTool("ServerMess");

        String tgskey = sql.searchOneFromTable(TGSUI.getTgsId(),"ServerRegist","KEY");
        String Vkey = sql.searchOneFromTable(TGSUI.getVserverId(),"ServerRegist","KEY");

        //todo 从数据库获取c-tgs session-key
        System.out.println(tgskey);
        for(String a:clientMess){
            System.out.println(a);
        }

        System.out.println("client Mess:"+clientMess[2]+" "+clientMess[2].length());
        String[] ticketMess = unPack(DesTool.decrypt(clientMess[2], tgskey));
        //解密之后可获得Kctgs
        String Kctgs = clientMess[4];
        System.out.println("1111");
        System.out.println(DesTool.decrypt(clientMess[3], Kctgs));
        String[] AuthMess = unPack(DesTool.decrypt(clientMess[3], Kctgs));
        System.out.println("client Mess:"+clientMess[3]+" "+clientMess[3].length());
        for(String a:AuthMess){
            System.out.println(a);
        }
        Boolean flag = true;
        String head = " ";
		/*
		while(true) {
			//比较Auth 与kctgs之间的 IDc
			if (!ticketMess[1].equals(AuthMess[1])) {
				flag = false;
				//todo 常量类error
				head = " ";
				break;
			}
			//比较Auth 与 系统时间戳TS
			if(caculateTS(AuthMess[2],TGSUI.getTimeStamp())>30)
			{
				flag = false;
				//todo 常量类system TS error
				head = " ";
				break;
			}
			//比较lifetime
			if(caculateTS(AuthMess[2],TGSUI.getTimeStamp())>Integer.parseInt(AuthMess[2]))
			{
				flag = false;
				//todo 常量类lifetime error
				head = " ";
				break;
			}
			//比较Auth 与kctgs之间的 ADc
			if(AuthMess[1].equals(ticketMess[2]))
			{
				flag = false;
				//todo 常量类error
				head = " ";
				break;
			}
			//检查tgs数据库中是否已存在Auth
			if(isAlreadExist(clientMess[3]))
			{
				flag = false;
				//todo 常量类error
				head = " ";
				break;
			}
			else {
				flag = true;
				head = " ";
				break;
			}
		}
		*/

//        ServerKeyGenerator sk = new ServerKeyGenerator();
        //返回包
        String Kcv = RandomKeyTool.createAuth(clientMess[1], TGSUI.getVserverId());
        String ticketVContent = Kcv + KbConstants.SEP + AuthMess[0] + KbConstants.SEP + AuthMess[1] + KbConstants.SEP + TGSUI.getVserverId() +KbConstants.SEP+ TGSUI.getTimeStamp()+KbConstants.SEP + TGSUI.getLifetime();
        String ticketVEncoded = DesTool.encrypt(ticketVContent, Vkey);
        String pack = head + KbConstants.SEP + Kcv + KbConstants.SEP + TGSUI.getVserverId() + KbConstants.SEP + TGSUI.getTimeStamp() + KbConstants.SEP + ticketVEncoded;
        String packEncode = DesTool.encrypt(pack, Kctgs);

        return packEncode;
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

    //接收认证消息
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

    public void writeAuthMess(String input) throws IOException {
        os.write((input + "=" + "\n").getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    //	public void writeMessage(String input) throws IOException
//	{
//		os.write((input + "\n").getBytes(StandardCharsets.UTF_8));
//		os.flush();
//	}
    public String[] unPack(String pack) {
        String[] temp = new String[10];
        temp = pack.split("-");
        return temp;
    }

    //判断数据库中是否存在
    public boolean isAlreadExist(String input) {
        return false;
    }
}
