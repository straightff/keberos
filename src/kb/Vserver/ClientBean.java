package kb.Vserver;

import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;

public class ClientBean
{
	private String name; //userName
	private String addr; //client ip 地址
	private Socket socket; //client连接的socket
	private String clientId; //client端的唯一ID
	private Map<String,String> messMap;//存储message;<timeStamp,mess>

	public ClientBean(String clientId, String name,String addr,Socket socket) {
		messMap = new Hashtable<String,String>();
		this.name = name;
		this.addr = addr;
		this.socket = socket;
		this.clientId = clientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public String getClientId() {
		return clientId;
	}

	public Map<String, String> getMessMap() {
		return messMap;
	}

	public void setMessMap(Map<String, String> messMap) {
		this.messMap = messMap;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
