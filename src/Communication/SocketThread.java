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
import common.Common;


public class SocketThread extends Thread {
	public static ArrayList<SocketThread> list = new ArrayList<SocketThread>();
	public static SocketThread drawst;
	public static String keyWord;
	private Socket socket;
	private InputStream input;
	private OutputStream output;
	private FileInputStream fis;
	private FileOutputStream fos;
	private DataOutputStream dos;
	private DataInputStream dis;
	private String name;

	public SocketThread(Socket socket) {
		this.socket = socket;
		// 添加到队列
		list.add(this);
	}

	public void run() {
		try {
			// 读取客户端发送的消息
			input = socket.getInputStream();
			dis = new DataInputStream(input);
			// 向客户端发送的消息
			output = socket.getOutputStream();
			dos = new DataOutputStream(output);
			// 输入名字
			String str = "Please enter your name:\r\n";
			// 服务器向客户端发送消息
			sendMessage(str);
			// 读取客户端输入的名字
			name = readLine(input);
			String name2 = name + "(" + socket.getInetAddress() + ")";
			System.out.println("name:" + name2);
			// 提醒有人上线
			if (list.size() > 1) {
				for (int i = 0; i < list.size(); i++) {
					SocketThread st = list.get(i);
					if (st == this) {
						continue;
					}
					// 发送消息
					String msg = st.name + "上线了！可以开始聊天了！\n";
					this.sendMessage(msg);
					st.sendMessage(name + "上线了！可以开始聊天了！\n");
				}
			}

			// 从客户端读取字符串消息
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
					if ("bye\n".equals(line)) {
						System.out.println("Sever received " + name
								+ "offLine！");
						// 退出关闭
						break;
					}
					// 打印当前客户所说的话
					System.out.println("Sever received " + name + ":" + line);

					// 群发消息
					for (int i = 0; i < list.size(); i++) {
						SocketThread st = list.get(i);
						if (st == this) {
							continue;
						}
						// 向其他客户端发出消息
						String msg = name + ":" + line;
						st.sendMessage(msg);

					}
				} else if (type == 2) {// 接收文件
					// 服务端接收文件
					receiveFile();

				} else if (type == 3) {// 接收询问接收文件的结果
					int len = dis.readInt();
					byte[] bytes = new byte[len];
					dis.readFully(bytes);
					// 读取客户端的输入流字符串
					String line = new String(bytes, "UTF-8");
					System.out.println("询问结果是：" + line);
					if ("OK".equals(line)) {
						System.out.println("准备向其他客户发送文件");
						// 发送
						if (Common.sendFile_name != null) {
							this.sendFile(new File("D:/涉密信息系统/ServerTemp/"
									+ Common.sendFile_name));
						}
					}
				}else if(type == 4){
					// 服务端接收加密文件
					receivedCryptFile();
				
				}else if (type == 5) {// 接收询问接收文件的结果
					int len = dis.readInt();
					byte[] bytes = new byte[len];
					dis.readFully(bytes);
					// 读取客户端的输入流字符串
					String line = new String(bytes, "UTF-8");
//					System.out.println("询问结果是：" + line);
					if ("OK".equals(line)) {
						System.out.println("准备向其他客户发送文件");
						// 发送
						if (Common.sendFile_name != null) {
							this.sendCryptFile(new File("D:/涉密信息系统/ServerTemp/"
									+ Common.sendFile_name));
						}
					}
				}else if(type == 6){// 接收公钥和n
					int publicKey = dis.readInt();
					int n = dis.readInt();
					// 群发消息
					for (int i = 0; i < list.size(); i++) {
						SocketThread st = list.get(i);
						if (st == this) {
							continue;
						}
						// 向其他客户端发公钥
						st.sendPublicKey(publicKey, n);

					}
				}
				else {
					break;
				}

			}
			// 客户下线关闭当前端口
			socket.close();
			// 删除队列中的对象
			list.remove(this);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 读取输入流的方法
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private String readLine(InputStream input) throws IOException {
		// 新建一个字节队列
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataInputStream dis = new DataInputStream(input);
		dis.readInt();
		dis.readInt();
		while (true) {
			int n = input.read();
			// System.out.println(n);
			// 回车符
			if (n == '\r') {
				continue;
			}
			// 换行符
			if (n == '\n') {
				break;
			}
			// 把读取的字节内容先保存
			bos.write(n);
		}
		// 把字节队列中的数据取出来
		byte[] bytes = bos.toByteArray();
		String content = new String(bytes, "UTF-8");
		return content;
	}

	/**
	 * 服务器接收文件的方法
	 * 
	 * @param msg
	 */
	public void receiveFile() throws IOException {
		try {
			try {
				// 接收数据包的长度
				int len = dis.readInt();
				byte[] bytes = new byte[len];
				dis.readFully(bytes);
				// 文件名
				String fileName = new String(bytes, "UTF-8");
				// 查询服务器临时文件夹中是否含有该文件
				// boolean isRepeat = GetRepeat.isRepeat(fileName);
				// 文件长度
				long l = dis.readLong();

				// 服务器端创建一个文件夹存放接收的文件
				File f = new File("D:/涉密信息系统/ServerTemp");
				if (!f.exists()) {
					f.mkdir();
				}
				// 服务器存储临时文件路径
				String filePath = "D:/涉密信息系统/ServerTemp/" + fileName;

				fos = new FileOutputStream(new File(filePath));
				byte[] inputByte = new byte[1024];
				System.out.println("服务器开始接收文件...");
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
				System.out.println("服务器完成接收：" + filePath);
				// 给其他客户端询问是否接收文件
				for (int i = 0; i < list.size(); i++) {
					SocketThread st = list.get(i);
					if (st == this) {
						continue;
					}
					// 询问
					Common.sendFile_name = fileName;
					st.query(fileName);

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
	 * 向客户端发送文件的方法
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void sendFile(File file) throws IOException {
		System.out.println("服务器发送文件给客户端");

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
			dos.writeLong(l);// 文件长度

			byte[] sendBytes = new byte[1024];
			int length = 0;
			double sumL = 0;
			while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
				sumL += length;
				// System.out.println("已传输：" + ((sumL / l) * 100) + "%");
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
	 * 从服务器接收加密文件的方法
	 * 
	 */
	private void receivedCryptFile()throws IOException{
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
//				String enCrypted_keyString = new String(keybytes, "UTF-8");
				Common.enCrypted_keyString = keybytes;
				// 读取文件长度
				long l = dis.readLong();

				// 服务器端创建一个文件夹存放接收的文件
				File f = new File("D:/涉密信息系统/ServerTemp");
				if (!f.exists()) {
					f.mkdir();
				}
				// 服务器存储临时文件路径
				String filePath = "D:/涉密信息系统/ServerTemp/" + fileName;

				fos = new FileOutputStream(new File(filePath));
				byte[] inputByte = new byte[1024];
				System.out.println("服务器开始接收加密文件...");
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
				System.out.println("服务器完成接收：" + filePath);
				// 给其他客户端询问是否接收文件
				for (int i = 0; i < list.size(); i++) {
					SocketThread st = list.get(i);
					if (st == this) {
						continue;
					}
					// 询问
					Common.sendFile_name = fileName;
					st.queryCryptAccept(fileName);

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
	 * 向客户端发送加密文件
	 * @throws IOException 
	 */
	public void sendCryptFile(File file) throws IOException{
		System.out.println("服务器发送加密文件给客户端");
		boolean bool = false;
		try {
			// 服务器输出流写入字节
			dos.writeInt(4);
			String filename = file.getName();
			byte[] bytes = filename.getBytes();
			int len = bytes.length;
			dos.writeInt(len);
			dos.write(bytes);
			System.out.println("文件名:"+filename+"写入成功...");
			
			//将已收到的文件密钥与文件摘要密文写入IO流
			byte[] enCrypt_byte = Common.enCrypted_keyString;
			dos.writeInt(enCrypt_byte.length);
			dos.write(enCrypt_byte);

			fis = new FileInputStream(file);
			long l = file.length();
			dos.writeLong(l);//文件长度
			
			byte[] sendBytes = new byte[1024];
			int length = 0;//每次加密的长度
			double sumL = 0;//已传输的长度
			//将文件加密后写入流
			while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
				sumL += length;
//				System.out.println("已传输：" + ((sumL / l) * 100) + "%");
				//写入IO流
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
	 * 向客户端发送消息
	 * 
	 * @param msg
	 */
	public void sendMessage(String msg) {
		try {
			dos.writeInt(1);
			// 服务器输出流写入字节
			byte[] bytes = msg.getBytes();
			int len = bytes.length;
			dos.writeInt(len);
			dos.write(bytes);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 向客户端询问是否接收文件的方法
	 */

	private void query(String filename) {
		try {
			dos.writeInt(3);
			// 服务器输出流写入字节
			byte[] bytes = filename.getBytes();
			int len = bytes.length;
			dos.writeInt(len);
			dos.write(bytes);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 向客户端询问是否接收加密文件的方法
	 */
	private void queryCryptAccept(String fileName) {
		try {
			dos.writeInt(5);
			// 服务器输出流写入字节
			byte[] bytes = fileName.getBytes();
			int len = bytes.length;
			dos.writeInt(len);
			dos.write(bytes);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 向客户端发送公钥和n
	 */
	private void sendPublicKey(int publicKey,int n){
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
}
