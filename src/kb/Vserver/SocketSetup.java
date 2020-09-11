/*
 * package kb.Vserver;
 * 
 * import java.io.BufferedReader; import java.io.IOException; import
 * java.io.InputStream; import java.io.InputStreamReader; import
 * java.net.ServerSocket; import java.net.Socket;
 * 
 * public class SocketSetup { private BufferedReader br; private ServerSocket
 * ssocket; private ClientBean[] Client; private Socket socket; private static
 * final int MaxConnect = 100; private Thread RecvMess; private Thread
 * CatchConnection; private InputStream is; private int num; private boolean
 * ready;
 * 
 * class CatchConnection extends Thread {
 * 
 * @Override public void run() { try {
 * 
 * ClientBean cli = new ClientBean(ssocket.accept()); Client[num++] = cli; is =
 * cli.getSocket().getInputStream(); br = new BufferedReader(new
 * InputStreamReader(is)); notifyInit(); } catch (IOException e) {
 * e.printStackTrace(); notifyInit(); }
 * 
 * } }
 * 
 * class RecvMess extends Thread {
 * 
 * @Override public void run() {
 * 
 * String mess = null; try { waitForInit(); while ((mess = br.readLine()) !=
 * null) {
 * 
 * // logArea.appendText("Client says:" + mess + "\n"); } } catch (IOException
 * e) { e.printStackTrace(); } catch (InterruptedException e) {
 * e.printStackTrace(); } } }
 * 
 * private synchronized void waitForInit() throws InterruptedException { while
 * (!ready) { wait(); } }
 * 
 * private synchronized void notifyInit() { ready = true; notifyAll(); } }
 */