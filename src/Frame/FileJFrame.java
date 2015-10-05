package Frame;
 
import java.awt.Button;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import FileSearch.FileFrameListener;
import FileSearch.FileListener;

public class FileJFrame extends JFrame implements FileFrameListener{
	public static JTextArea log;
	public JTextField inputTxt;
	public static FileJFrame fj;

	public void init() {
		this.setLayout(null);
		this.setSize(560, 370);
		this.setTitle("文件定密");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(2);
		this.setLayout(new FlowLayout());
		log = new JTextArea(18, 42);
		JScrollPane p = new JScrollPane(log);
		this.add(p);

		JLabel label = new JLabel("请选择文件：");
		this.add(label);
		inputTxt = new JTextField(20);
		this.add(inputTxt);
		JButton jb = new JButton("浏览");
		this.add(jb);
		FileListener l=new FileListener(inputTxt); 
		jb.addActionListener(l);
		this.setVisible(true);
		fj = this;
	}

	public static void main(String[] args) {
		FileJFrame f = new FileJFrame();
		f.init();
	}

	@Override
	public void onMessage(String str) {
		log.append(str+"\n");
	}

	@Override
	public void onclose() {
	}
}
