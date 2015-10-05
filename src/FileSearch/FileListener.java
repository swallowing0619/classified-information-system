package FileSearch;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import common.Common;

import Frame.FileImformJFrame;

public class FileListener implements ActionListener {
	private JTextField jt;
	public String path;

	public FileListener(JTextField jt) {
		this.jt = jt;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// 点击按钮，
		// 弹出本地文件夹
		JFileChooser jfc = new JFileChooser();
		if (jfc.showOpenDialog(jt) == JFileChooser.APPROVE_OPTION) {
			jt.setText(jfc.getSelectedFile().getPath());
		}
		// 弹出对话框，确定选择文件或者取消
		int n = JOptionPane.showConfirmDialog(null, "确定选择此文件？", "标题",
				JOptionPane.YES_NO_OPTION);
		// System.out.println(n);
		if (n == 0) {// 点击确定按钮，弹出新界面
			path = jt.getText();
			Common.classifiedfile = new File(path);
			FileImformJFrame fi = new FileImformJFrame();
			fi.init();// 弹出输入文件界面
		}
		if (n == 1) {// 清空文本框
			jt.setText("");
			Common.classifiedfile = null;
		}
		

	}

}
