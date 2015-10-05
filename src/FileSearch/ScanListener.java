package FileSearch;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import common.Common;

import Database.DBConnector;

public class ScanListener implements ActionListener {
	private JTextField jtx1;
	private JTextField jtx2;
	private JTextField jtx3;

	public ScanListener(JTextField jtx1, JTextField jtx2, JTextField jtx3) {
		this.jtx1 = jtx1;
		this.jtx2 = jtx2;
		this.jtx3 = jtx3;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stubt
		// 点击扫描按钮，将文件信息存入数据库
		// 获取文本框内容
		DBConnector d = new DBConnector();
		// d.insert_1(name, Creater_ID, Validate_Domain);
		// System.out.println( Validate_Domain);
		// 开始读取扫描文件
		readfilecontent read = new readfilecontent(jtx1, jtx2, jtx3);
		read.writeText(Common.classifiedfile.getAbsolutePath());
		read.keywords();
	}

}
