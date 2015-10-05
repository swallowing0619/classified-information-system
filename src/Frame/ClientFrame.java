package Frame;
 
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import algorithm.RSACrypt;

import Communication.ClientThread;

import common.MsgListener;

public class ClientFrame extends JFrame implements MsgListener {
	public JTextArea log;
	public Color color;
	private OutputStream output;
	private JTextField inputTxt;
	private ClientThread ct;
	private ClientFrame cf;
	public String selectPath;
	private File file;
	private int[] RSA_keys;

	public void onRecvMsg(String str) {
		log.append(str);
	}

	public void onReceivedFile(String fileName) {
		log.append("成功接收文件："+fileName+"(D:/涉密信息系统/ClientTemp)\n");
		JOptionPane.showMessageDialog(cf.getContentPane(),
				"成功接收文件!", "提示信息", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public int onQuery(String fileName){
		int r = JOptionPane.showConfirmDialog(cf.getContentPane(), "对方发来文件："+fileName+",是否接收？",
				"请选择", JOptionPane.YES_NO_OPTION);
		return r;
	}
	
	public int onQueryCryptAccept(String fileName){
		int r = JOptionPane.showConfirmDialog(cf.getContentPane(), "对方发来加密文件："+fileName+",是否接收？",
				"请选择", JOptionPane.YES_NO_OPTION);
		return r;
	}
	
	public int onQueryProved(String fileName){
		int r = JOptionPane.showConfirmDialog(cf.getContentPane(), "成功接收，是否验证并解密文件："+fileName+"？",
				"请选择", JOptionPane.YES_NO_OPTION);
		return r;
	}
	
	public void onSuccess(String fileName){
		JOptionPane.showMessageDialog(cf.getContentPane(),
				"文件解密并验证成功!", "提示信息", JOptionPane.INFORMATION_MESSAGE);
		log.append("我：文件解密并验证成功："+fileName+"(D:/涉密信息系统/ClientTemp)\n");
	}
	
	public void onFailed(String fileName){
		JOptionPane.showMessageDialog(cf.getContentPane(),
				"文件解密并验证失败!", "提示信息", JOptionPane.INFORMATION_MESSAGE);
		log.append("我：文件解密验证失败："+fileName+"(D:/涉密信息系统/ClientTemp)\n");
	}

	private void unit() {
		// 初始化窗体
		this.setTitle("涉密信息系统");
		this.setDefaultCloseOperation(3);
		this.setSize(600, 410);
		this.setLocationRelativeTo(null);
		this.setLayout(new FlowLayout());
		cf = this;
		log = new JTextArea(18, 42);
		JScrollPane p = new JScrollPane(log);
		this.add(p);

		JPanel jp = new JPanel();
		jp.setPreferredSize(new Dimension(600, 38));

		JButton jb_transfer = new JButton("文件传输");
		jp.add(jb_transfer);
		jb_transfer.addActionListener(al);
		JButton jb_port = new JButton("端口监听");
		jp.add(jb_port);
		jb_port.addActionListener(al);
		JButton jb_manage = new JButton("文件管控");
		jp.add(jb_manage);
		this.add(jp);
		jb_manage.addActionListener(al);

		inputTxt = new JTextField(33);
		this.add(inputTxt);
		inputTxt.addKeyListener(kl);
		JButton jb = new JButton("Send");
		jb.setBackground(Color.LIGHT_GRAY);
		this.add(jb);
		jb.addActionListener(al);

		this.setVisible(true);
		// 开启线程
		ct = new ClientThread();
		ct.addListener(this);
		ct.start();

	}

	// 按钮监听器
	ActionListener al = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Send")) {
				String msg = inputTxt.getText();

				if (file != null) {
					// 传输文件
					int r = JOptionPane.showConfirmDialog(cf.getContentPane(), "是否加密传输该文件？",
							"choose one", JOptionPane.YES_NO_OPTION);
					if (r == 0) {
						inputTxt.setText("");
						msg += "\n";
						//初始化公私钥
						RSA_keys = creat_RSA_keys();
						log.append("我：初始化密钥对，公钥："+RSA_keys[0]+"；私钥(仅自己可见)："+RSA_keys[1]+"\n");
						ct.sendMessage("我的公钥是："+RSA_keys[0]+"(n:"+RSA_keys[2]+")\n");
						ct.sendPublicKey(RSA_keys[0], RSA_keys[2]);
						String input_key = JOptionPane.showInputDialog("Please input a key(1<length<256)"); 
//						System.out.println("input_key:"+input_key);
						if(input_key == null){
							return ;
						}
						log.append("我：（成功安全传输文件）" + msg);
						try {
							ct.sendCryptedFile(file,input_key,RSA_keys[1],RSA_keys[2]);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						file = null;
						
					} else if(r == 1){//普通发送
						inputTxt.setText("");
						msg += "\n";
						log.append("我:（发送文件）" + msg);
						try {
							ct.sendFile(file);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						file = null;
					}else{
						inputTxt.setText("");
						file = null;
						return;
					}

				} else {
					inputTxt.setText("");
					msg += "\n";
					log.append("我:" + msg);
					// 发送消息
					ct.sendMessage(msg);
				}
				

			} else if (e.getActionCommand().equals("文件传输")) {
				// 初始化文件选择框
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);// 设置选择目录和文件
				int returnVal = chooser.showOpenDialog(cf);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					selectPath = chooser.getSelectedFile().getPath();
					System.out.println("选择的目录是：" + selectPath);
					chooser.hide();
					
					file = chooser.getSelectedFile();
					inputTxt.setText(file.getName());
				}

			} else if (e.getActionCommand().equals("端口监听")) {
				//新窗体
				new SnifferFrame().init();
			} else if (e.getActionCommand().equals("文件管控")) {
				//新窗体
				new FileJFrame().init(); 
			}
		}

	};

	// 键盘Enter监听器
	KeyListener kl = new KeyListener() {

		public void keyTyped(KeyEvent e) {
		}

		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_ENTER) {
				String msg = inputTxt.getText();
				if(msg == ""){
					JOptionPane.showConfirmDialog(null, "发送内容不能为空");
					return ;
				}

				if (file != null) {
					// 传输文件
					JOptionPane jop = new JOptionPane();
					jop.setNextFocusableComponent(cf);
					int r = jop.showConfirmDialog(null, "是否加密传输该文件？",
							"choose one", JOptionPane.YES_NO_OPTION);
					if (r == 0) {
						inputTxt.setText("");
						msg += "\n";
						//初始化公私钥
						RSA_keys = creat_RSA_keys();
						log.append("我：初始化密钥对，公钥："+RSA_keys[0]+"；私钥(仅自己可见)："+RSA_keys[1]+"\n");
						ct.sendMessage("我的公钥是："+RSA_keys[0]+"(n:"+RSA_keys[2]+")\n");
						ct.sendPublicKey(RSA_keys[0], RSA_keys[2]);
						String input_key = JOptionPane.showInputDialog("Please input a key(1<length<256)"); 
						System.out.println("input_key:"+input_key);
						if(input_key == null){
							return ;
						}
						log.append("我：（成功安全传输文件）" + msg);
						try {
							ct.sendCryptedFile(file,input_key,RSA_keys[1],RSA_keys[2]);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						file = null;
					} else if(r == 1){
						inputTxt.setText("");
						msg += "\n";
						log.append("我:（发送文件）" + msg);
						try {
							ct.sendFile(file);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						file = null;
					}else{
						inputTxt.setText("");
						file = null;
						return;
					}
						

				} else {
					inputTxt.setText("");
					msg += "\n";
					log.append("我:" + msg);
					// 发送消息
					ct.sendMessage(msg);
				}
				

			}
		}

		public void keyReleased(KeyEvent e) {
		}

	};
	
	public int[] creat_RSA_keys(){
		RSACrypt rsa = new RSACrypt();
		int[] keys = new int[3];
		keys[0] = rsa.publicKey;
		keys[1] = rsa.privateKey;
		keys[2] = rsa.n;
		return keys;
	}

	public static void main(String[] args) {
		ClientFrame client = new ClientFrame();
		client.unit();
	}


}
