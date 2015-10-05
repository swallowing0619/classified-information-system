package Other;
 
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import FileSearch.FileFrameListener;

import common.Common;

public class ClassifiedFileFrame extends JFrame  {

	public static JTextArea log;
	public JTextField inputTxt;
	public ClassifiedFileFrame ccf;

	public void init() {
		// 初始化窗体
		this.setTitle("文件分级管控");
		this.setDefaultCloseOperation(3);
		this.setSize(500, 370);
		this.setLocationRelativeTo(null);
		this.setLayout(new FlowLayout());
		log = new JTextArea(18, 40);
		JScrollPane p = new JScrollPane(log);
		this.add(p);
		ccf = this;

		JLabel label = new JLabel("请输入磁盘：");
		this.add(label);
		inputTxt = new JTextField(10);
		this.add(inputTxt);
		JButton jb = new JButton("扫描");
		this.add(jb);
		jb.addActionListener(al);

		this.setVisible(true);

	}

	// 按钮监听器
	ActionListener al = new ActionListener() {

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("扫描")) {
				if (inputTxt.getText().equals("")) {
					log.append("输入不能为空!请输入！\n");
					return;
				}
				String str = inputTxt.getText();
				char c = str.charAt(0);
				if (str.length() > 1
						|| !((c > 66 && c < 75) || (c > 98 && c < 107))) {
					log.append("输入有误!请重新输入！\n");
					return;
				}
				log.append("开始扫描" + str + "盘中文件...\n");
				Common.countFiles = 0;
				Common.countFolders = 0;
				Common.result = 0;
				Common.resultfile.clear();

				new SearchThread().run();
				String path = str + ":/";
				File folder = new File(path);// 默认目录
				File[] files = folder.listFiles();

				SearchFile sf = new SearchFile();

				for (int i = 0; i < files.length; i++) {
					String filename = files[i].getName();
					// System.out.println(files[i].getName());
					sf.call(new File(path + filename), "秘密");
				}

				log.append("在" + str + "盘下的所有子文件下");
				log.append("查找了" + Common.countFiles + " 个文件，"
						+ Common.countFolders + " 个文件夹，\n共找到 " + Common.result
						+ " 个符合条件的文件：\n");
				if (Common.result != 0) {
					// 连接数据库
					Common.DB.connection();
					for (int i = 0; i < Common.resultfile.size(); i++) {// 循环显示文件
						File file = (File) Common.resultfile.get(i);
						log.append(file.getAbsolutePath() + " \n");// 显示文件绝对路径

						// 写入数据库中

					}
					log.append("已写入数据库\n");
				}
				log.append("\n");

			}

		}
	};

	public static void main(String[] args) {
		new ClassifiedFileFrame().init();
	}


}
