package common;
 
import java.awt.Color;

public interface MsgListener {
	//消息内容显示到log上
	public void onRecvMsg(String str);
	
	//传输的文件显示到log上
	public void onReceivedFile(String fileName);

	
	//询问是否接收文件
	public int onQuery(String fileName);
	
	//询问是否接收加密文件
	public int onQueryCryptAccept(String fileName);
	
	//询问是否验证加密文件
	public int onQueryProved(String fileName);
	
	//验证并解密文件成功的提示框
	public void onSuccess(String fileName);
	
	public void onFailed(String fileName);
}
