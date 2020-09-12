package kb.Vserver;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerReaderThread implements Runnable{
    private Socket socket;
    private BufferedReader br;
    private OutputStream os;
    private ServerInitThread server;
    private static String mode;

    public ServerReaderThread(Socket socket, ServerInitThread Server) throws IOException {

        this.socket = socket;
        server = Server;
        br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        os = this.socket.getOutputStream();
    }

    @Override
    public void run() {
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

    //写聊天消息
    public void writeMessage(String input) throws IOException {
        os.write((input + "\n").getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

}
