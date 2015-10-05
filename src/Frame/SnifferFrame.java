package Frame;
 
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import FileSearch.FileFrameListener;
import Sniffer.Sniffer;

public class SnifferFrame extends JFrame implements FileFrameListener{
	public  JTextArea log;
	public JTextField inputTxt;
	public Sniffer sniffer;

	public void init() {
		// 初始化窗体
		this.setTitle("端口监听");
		this.setDefaultCloseOperation(2);
		this.setSize(550, 420);
		this.setLocationRelativeTo(null);
		this.setLayout(new FlowLayout());
		log = new JTextArea(20, 38);
		JScrollPane p = new JScrollPane(log);
		this.add(p);

		JLabel label = new JLabel("请选择上面所列网卡对应的数字：");
		this.add(label);
		inputTxt = new JTextField(10);
		this.add(inputTxt);
		JButton jb = new JButton("确定");
		this.add(jb);
		jb.addActionListener(al);

		this.setVisible(true);
		
		onMessage("<select a number from the following>:");
	    sniffer =  new Sniffer(this);
	   
		sniffer.init();
	}
	
	ActionListener al = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			sniffer.s = inputTxt.getText();
			new Thread(sniffer).start();
			inputTxt.setText("");
			onMessage("\n网卡【"+sniffer.s+"】监听如下：");
			
		}
	};

	public static void main(String[] args) {
		new SnifferFrame().init();
	}
	
	@Override
	public void onMessage(String str) {
		System.out.println(str);
		log.append(str+"\n");
		
	}

	@Override
	public void onclose() {
		
	}
}
