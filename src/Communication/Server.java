package Communication;
 
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

//服务端
public class Server {

	public void setup(int port){
		
	    try {
	    	//监听端口
			ServerSocket sers = new ServerSocket(port);
			System.out.println("服务器监听端口"+port+"成功！");
			
			while(true){
				//等待客户端访问
				Socket socket = sers.accept();
				System.out.println("有人访问！");
				
				//把客户端交给线程处理
				SocketThread st = new SocketThread(socket);
				st.start();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
	public static void main(String[] args) {
		new Server().setup(6666);

	}
	

}
