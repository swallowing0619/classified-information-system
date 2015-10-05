package common;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import Database.DBConnector;
import algorithm.Md5Hash;
import algorithm.RC4Crypt;
import algorithm.RSACrypt;

public class Common {
	public static final int CLIENT_NUM = 2 ;
	public static String  sendFile_name = null;
	public static byte[] enCrypted_keyString = null;//加密后的密文字节流
	
	//扫描文件
	public static int countFiles = 0;
	public static int countFolders = 0;
	public static int result = 0;
	public static ArrayList resultfile = new ArrayList();
	//数据库对象
	public static DBConnector DB = new DBConnector();
	//要扫描的文件
	public static File classifiedfile;
	
			
	
	

}
