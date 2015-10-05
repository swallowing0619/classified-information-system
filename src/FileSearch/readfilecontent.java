package FileSearch;
 
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.JTextField;

import common.Common;

import Database.DBConnector;
import Frame.FileImformJFrame;
import Frame.FileJFrame;

public class readfilecontent {
	// 读取文件内容，并提取出文件的关键词
	static String text = "";
	DBConnector da = new DBConnector();
	String[] keywords = { "secret", "security", "China", "Xijinping",
			"economy" };
	private JTextField jtx1;
	private JTextField jtx2;
	private JTextField jtx3;
	String name;
	String Security_Class;
	String Create_Time;
	String Validate_Time;
	int Creater_ID;
	int Validate_Domain;
	

	public readfilecontent(JTextField jtx1, JTextField jtx2, JTextField jtx3) {
		this.jtx1 = jtx1;
		this.jtx2 = jtx2;
		this.jtx3 = jtx3;
		name = jtx1.getText();
		Creater_ID = Integer.parseInt(jtx2.getText());
		Validate_Domain = Integer.parseInt(jtx3.getText());
	}

	public void writeText(String filePath) {
		try {
			// JFileChooser chooser = new JFileChooser();
			// chooser.showOpenDialog(this);
//			System.out.println("filePath:" + filePath);
			File file = new File(filePath);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s;
			while ((s = br.readLine()) != null) {
				// text.append(s + "\n");
				text = text + s + "\n";
				// System.out.println(text);
			}
			// text.setText(text.getText().trim());
			// System.out.println(text);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}

	public void keywords() {
		String[] start = text.trim().split("\n");// 按行数来分割文章
//		System.out.println("start: " + start.length);
		int n = 0;// 出现关键字的次数
		for (int i = 0; i < start.length; i++) {
			StringTokenizer st = new StringTokenizer(start[i]);
			// int n = 0;//出现关键字的次数
			while (st.hasMoreTokens()) {
				String comp = st.nextToken();
				for (String s : keywords) {
					if (s.equals(comp)) {
						n++;
					}
				}
			}
		}
		if (n == 0) {
			Security_Class = "公开";
			Validate_Time = "0Y";
		}
		if (n > 0 && n < 5) {
			Security_Class = "秘密级";
			Validate_Time = "10Y";
		}
		if (n >= 5 && n < 10) {
			// 文章为机密级
			Security_Class = "机密级";
			Validate_Time = "20Y";
		}
		if (n >= 10) {
			// 文章为绝密级
			Security_Class = "绝密级";
			Validate_Time = "30Y";
		}
		Date dt = new Date();// 如果不需要格式,可直接用dt,dt就是当前系统时间
		DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");// 设置显示格式
		Create_Time = "";
		Create_Time = df.format(dt);// 用DateFormat的format()方法在dt中获取并以yyyy/MM/dd
		// HH:mm:ss格式显示
		da.add(name, Security_Class, Create_Time, Validate_Time, Creater_ID,
				Validate_Domain);
		
		//关闭窗口
		FileImformJFrame.fij.onclose();
		
		//日志显示
		FileJFrame.fj.onMessage("文件："+Common.classifiedfile.getAbsolutePath()+" 已定密入库。");
	}
}
