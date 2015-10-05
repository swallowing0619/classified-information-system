package Communication;
 
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import algorithm.Md5Hash;
import algorithm.RC4Crypt;
import algorithm.RSACrypt;
import common.MsgListener;

/**
 * 	 通信协议
 * type   传输类型
 * 1      String 
 * 2      File
 * 3      query whether you receive
 * 4      CrptedFile
 * 5 	  query whether you receive cryptedFile
 * 6      send  publicKey and key
 * @author humeng
 */
public class ClientThread extends Thread {
	private OutputStream output;
	private DataOutputStream dos;
	private DataInputStream dis;
	private FileInputStream fis;
	private FileOutputStream fos;
	private MsgListener msgListener;
	private RC4Crypt rc4;
	private int publickey;
	private int n;

	public void addListener(MsgListener l) {
		this.msgListener = l;
	}

	public void run() {
		try {
			System.out.println("Connect to server......");
			Socket socket = new Socket("127.0.0.1", 6666);
			System.out.println("Success！");
			// 读取对方发送的消息
			InputStream input = socket.getInputStream();
			dis = new DataInputStream(input);
			// 向对方发送的消息
			output = socket.getOutputStream();
			dos = new DataOutputStream(output);
			while (true) {
				// 接受数据包的类型
				int type = dis.readInt();
				if (type == 1) {
					// 接收数据包的长度
					int len = dis.readInt();
					byte[] bytes = new byte[len];
					dis.readFully(bytes);
					// 读取客户端的输入流字符串
					String line = new String(bytes, "UTF-8");
					if(line.contains("我的公钥")){
						
					}
					// System.out.println(line);
					// 從服务器接收到的消息显示到界面上
					msgListener.onRecvMsg(line);

				} else if (type == 2) {
					//客户端接收普通文件
					receiveFile();

				} else if(type == 3){
					//接收“是否接收文件”的询问
					int len = dis.readInt();
					byte[] bytes = new byte[len];
					dis.readFully(bytes);
					// 读取需要接收的文件名
					String fileName = new String(bytes, "UTF-8");
					//从客户端界面返回结果
					int res = msgListener.onQuery(fileName);
//					System.out.println("res============"+res);
					//若接收直接返回"OK"
					if(res == 0){
						queryResult("OK");
					}else{
						queryResult("NO");
					}
					
				}else if(type ==4 ){
					//接收加密文件
					receivedCryptFile();
					
					
				}else if(type == 5){
					//接收“是否接收加密文件”的询问
					int len = dis.readInt();
					byte[] bytes = new byte[len];
					dis.readFully(bytes);
					// 读取需要接收的文件名
					String fileName = new String(bytes, "UTF-8");
					//从客户端界面返回结果
					int res = msgListener.onQueryCryptAccept(fileName);
					//若接收直接返回"OK"
					if(res == 0){
						queryCryptResult("OK");
					}else{
						queryCryptResult("NO");
					}
					
				}else if(type == 6){// 接收公钥和n
					publickey = dis.readInt();
					n = dis.readInt();
				}
				else {
					continue;
				}

			}
		} catch (Exception e) {
			System.out.println("失败！");
			e.printStackTrace();
		}
	}

	/**
	 * 向服务器发送消息的方法
	 */
	public void sendMessage(String msg) {
		try {
			// 服务器输出流写入字节
			byte[] bytes = msg.getBytes();
			int len = bytes.length;
			dos.writeInt(1);
			dos.writeInt(len);
			dos.write(bytes);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 向服务器发送普通文件的方法
	 */
	public void sendFile(File file) throws IOException {
		System.out.println("客户端发送普通文件");
		boolean bool = false;
		try {
			// 服务器输出流写入字节
			dos.writeInt(2);
			byte[] bytes = file.getName().getBytes();
			int len = bytes.length;
			dos.writeInt(len);
			dos.write(bytes);
			System.out.println("文件名写入成功...");

			fis = new FileInputStream(file);
			long l = file.length();
			dos.writeLong(l);//文件长度
			
			byte[] sendBytes = new byte[1024];
			int length = 0;
			double sumL = 0;
			while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
				sumL += length;
//				System.out.println("已传输：" + ((sumL / l) * 100) + "%");
				dos.write(sendBytes, 0, length);
				dos.flush();
			}
			// 虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较
			if (sumL == l) {
				bool = true;
			}
			System.out.println("文件字节流写入结束!");

		} catch (IOException e) {
			System.out.println("客户端文件传输异常");  
            bool = false;  
            e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
		}
		System.out.println(bool ? "成功" : "失败");
	}

	/**
	 * 从服务器接收普通文件的方法
	 * @param msg
	 */
	 public  void receiveFile() throws IOException {  
	        try {  
	            try {  
	            	// 接收数据包的长度
					int len = dis.readInt();
					byte[] bytes = new byte[len];
					dis.readFully(bytes);
					// 文件名
					String fileName = new String(bytes, "UTF-8");	
					//文件长度	
					long l = dis.readLong();
					
					//服务器端创建一个文件夹存放接收的文件
					File f = new File("D:/涉密信息系统/ClientTemp");  
	                if(!f.exists()){  
	                    f.mkdir();    
	                }
	                //服务器存储临时文件路径
	                String filePath = "D:/涉密信息系统/ClientTemp/"+fileName;
					
					fos = new FileOutputStream(new File(filePath));
	                byte[] inputByte = new byte[1024];
	                System.out.println("客户端开始接收文件...");
	                int length = 0;
	                double sumL = 0;
	                while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {  
	                    fos.write(inputByte, 0, length);  
	                    fos.flush();
	                    sumL += length;
	                    //全部接收
	                    if (sumL == l) {
	        				break;
	        			}
	                }  
	                System.out.println("客户端完成接收："+filePath);
	                msgListener.onReceivedFile(fileName);
	               
	            }finally {  
	                    if (fos != null)  
	                        fos.close();  
	            }     
	            
	        } catch (Exception e) {  
	            e.printStackTrace();  
	    }  
	}  
	 
	/**
	 * 向服务器发送加密文件的方法
	 * 
	 */
	public void sendCryptedFile(File file,String Filekey,int privateKey ,int N) throws IOException{
		System.out.println("客户端发送加密文件");
		boolean bool = false;
		try {
			// 服务器输出流写入字节
			dos.writeInt(4);
			String filename = "(加密)"+file.getName();
			byte[] bytes = filename.getBytes();
			int len = bytes.length;
			dos.writeInt(len);
			dos.write(bytes);
			System.out.println("文件名:"+filename+"写入成功...");
			
			//将密钥与文件摘要加密写入IO流
			//文件摘要
			String fileMD5 = Md5Hash.getMD5(file);
			String key_string = Filekey+"&"+fileMD5;
			//私钥加密文件加密密钥与文件摘要值
			RSACrypt rsa = new RSACrypt();
			rsa.privateKey = privateKey;
			rsa.n = N;
			byte[] enCrypt_byte = rsa.encrypt(key_string);//私钥加密
			//加密结果写入IO流
			System.out.println("要加密的明文："+key_string);
			System.out.println("已加密的密文：len="+enCrypt_byte.length);
			for(int i=0;i<enCrypt_byte.length;i++){
				System.out.print(enCrypt_byte[i]+" ");
			}
			System.out.println();
			dos.writeInt(enCrypt_byte.length);
			dos.write(enCrypt_byte);

			fis = new FileInputStream(file);
			long l = file.length();
			dos.writeLong(l);//文件长度
			
			byte[] sendBytes = new byte[1024];
			byte[] decode = null;
			int length = 0;//每次加密的长度
			double sumL = 0;//已传输的长度
			//实例化RC4对象
			if(rc4 == null )rc4 = new RC4Crypt();
			//将文件加密后写入流
			while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
				sumL += length;
//				System.out.println("已传输：" + ((sumL / l) * 100) + "%");
				//加密字节流
				rc4.message_len = length;
				rc4.seedKey(Filekey);
				decode = new byte[length];
				decode = rc4.encode(sendBytes);
				//写入IO流
				dos.write(decode, 0, length);
				dos.flush();
			}
			// 虽然数据类型不同，但JAVA会自动转换成相同数据类型后在做比较
			if (sumL == l) {
				bool = true;
			}
			System.out.println("文件字节流写入结束!");

		} catch (IOException e) {
			System.out.println("客户端文件传输异常");  
            bool = false;  
            e.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();
		}
		System.out.println(bool ? "成功" : "失败");
		
	}
	
	/**
	 * 从服务器接收加密文件的方法
	 * 
	 */
	private void receivedCryptFile(){
		try {
			try {
				// 接收数据包的长度
				int len = dis.readInt();
				byte[] bytes = new byte[len];
				dis.readFully(bytes);
				// 读取文件名
				String fileName = new String(bytes, "UTF-8");
				
				//读取文件密钥与文件摘要
				int keylen = dis.readInt();
				byte[] keybytes = new byte[keylen];
				dis.readFully(keybytes);
//				String keyString = new String(keybytes, "UTF-8");
//				System.out.println("keyString"+keyString);
				
				// 读取文件长度
				long l = dis.readLong();

				// 服务器端创建一个文件夹存放接收的文件
				File f = new File("D:/涉密信息系统/ClientTemp");
				if (!f.exists()) {
					f.mkdir();
				}
				// 服务器存储临时文件路径
				String filePath = "D:/涉密信息系统/ClientTemp/" + fileName;
				System.out.println("文件存储路径："+filePath);
				fos = new FileOutputStream(new File(filePath));
				byte[] inputByte = new byte[1024];
				System.out.println("开始接收加密文件...");
				int length = 0;
				double sumL = 0;
				while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
					fos.write(inputByte, 0, length);
					fos.flush();
					sumL += length;
					// 全部接收
					if (sumL == l) {
						break;
					}
				}
				System.out.println("完成接收：" + filePath);
				int res = msgListener.onQueryProved(fileName);
				if(res == 0){
					//验证解密
					System.out.println("公钥："+publickey+"n:"+n);
					boolean result = DecodeFile.provedFile(keybytes, publickey, n, fileName);
					//验证后弹出提示框
					if(result){
						msgListener.onSuccess(fileName);
					}else{
						msgListener.onFailed(fileName);
					}
				}else{
					msgListener.onReceivedFile(fileName);
				}
			} finally {
				if (fos != null)
					fos.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * 向服务器返回询问结果的方法
	 */
	
	private void queryResult(String Result) {
		try {
			dos.writeInt(3);
			// 服务器输出流写入字节
			byte[] bytes = Result.getBytes();
			int len = bytes.length;
			dos.writeInt(len);
			dos.write(bytes);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 向服务器返回询问接收加密文件结果的方法
	 */
	
	private void queryCryptResult(String Result) {
		try {
			dos.writeInt(5);
			// 服务器输出流写入字节
			byte[] bytes = Result.getBytes();
			int len = bytes.length;
			dos.writeInt(len);
			dos.write(bytes);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 向服务器发送公钥和n
	 */
	public void sendPublicKey(int publicKey,int n){
		try {
			// 服务器输出流写入字节
			dos.writeInt(6);
			dos.writeInt(publicKey);
			dos.writeInt(n);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从输入流中读取字符串
	 */
	public String readLine(InputStream input) throws IOException {
		// 创建一个字节队列
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// 从输入流中循环读取字节
		while (true) {
			// 读取一个字节
			int n = input.read();
			// 回车符
			if (n == '\r')
				continue;
			// 换行符则结束
			if (n == '\n') {
				break;
			}
			// 保存
			bos.write(n);
		}
		// 把字节队列中的数据取出
		byte[] bytes = bos.toByteArray();
		// 转成字符串
		String content = new String(bytes, "UTF-8");
		return content;

	}

}
