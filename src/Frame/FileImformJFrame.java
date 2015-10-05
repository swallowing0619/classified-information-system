package Frame;
 
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import FileSearch.FileFrameListener;
import FileSearch.ScanListener;

import common.Common;

public class FileImformJFrame extends JFrame implements FileFrameListener{
	// 输入文件信息

	private JButton jb = new JButton("扫描入库");// 开始定密
	private JLabel jl1 = new JLabel();
	private JTextField jtx1 = new JTextField(18);// 文件名
	private JLabel jl2 = new JLabel();
	private JTextField jtx2 = new JTextField(18);// 创建人
	private JLabel jl3 = new JLabel();
	private JTextField jtx3 = new JTextField(18);// 范畴
	public static FileImformJFrame fij;

	public void init() {
		this.setTitle("请输入文件信息");
		this.setLayout(new FlowLayout());
		this.setSize(280, 150);// 设置窗口大小
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(3);
		jl1.setText("文件名");
		jtx1.setText(Common.classifiedfile.getName());
		jl2.setText("创建人");
		jl3.setText("范     畴");
		this.add(jl1);
		this.add(jtx1);
		this.add(jl2);
		this.add(jtx2);
		this.add(jl3);
		this.add(jtx3);
		this.add(jb);
		this.setVisible(true);
		fij = this;
		// 为按钮添加事件监听器
		ScanListener l = new ScanListener(jtx1, jtx2, jtx3);
		jb.addActionListener(l);
	}

	public static void main(String[] args) {
		FileImformJFrame f1 = new FileImformJFrame();
		f1.init();
	}

	@Override
	public void onMessage(String str) {
	}

	@Override
	public void onclose() {
		this.dispose();
	}
}
