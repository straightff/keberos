package kb.Vserver;

import java.net.Socket;

public class ClientBean
{
	private String name;
	private String addr;
	private Socket socket;

//	public ClientBean(String name, String addr, Socket socket)
//	{
//		super();
//		this.name = name;
//		this.addr = addr;
//		this.socket = socket;
//	}

	// 测试
	public ClientBean(Socket socket)
	{
		super();
		this.socket = socket;
	}

	public ClientBean(String addr, Socket socket)
	{
		super();
		this.addr = addr;
		this.socket = socket;
	}

	public String getAddr()
	{
		return addr;
	}

	public String getName()
	{
		return name;
	}

	public Socket getSocket()
	{
		return socket;
	}

	public void setAddr(String addr)
	{
		this.addr = addr;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setSocket(Socket socket)
	{
		this.socket = socket;
	}
}
