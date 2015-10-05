package Communication;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import algorithm.Md5Hash;
import algorithm.RC4Crypt;
import algorithm.RSACrypt;

public class DecodeFile {

	// 验证解密的方法
	public static boolean provedFile(byte[] keybytes, int publicKey, int N,
			String filename) throws IOException {
		// 用公钥解密密钥和摘要的密文
		RSACrypt rsa = new RSACrypt();
		rsa.otherspublicKey = publicKey;
		rsa.n = N;
		byte[] output = rsa.decrypt(keybytes);
		System.out.println("未解密的密文：");
		for (int i = 0; i < keybytes.length; i++) {
			System.out.print(keybytes[i] + " ");
		}
		System.out.println();
		System.out.println("已解密的明文：");
		for (int i = 0; i < output.length; i++) {
			System.out.print((char) output[i]);
		}

		String str = new String(output);
		String[] strr = str.split("&");
		// for(int i=0;i<strr.length;i++){
		// System.out.println(strr[i]);
		// }

		String filekey = strr[0];
		String fileMd5 = strr[1];

		// 解密文件
		byte[] sendBytes = new byte[1024];
		byte[] decode = null;
		int length = 0;// 每次加密的长度
		double sumL = 0;// 已传输的长度
		// 实例化RC4对象
		RC4Crypt rc4 = new RC4Crypt();
		FileInputStream fis = new FileInputStream("D:/涉密信息系统/ClientTemp/"
				+ filename);
		FileOutputStream fos = new FileOutputStream("D:/涉密信息系统/ClientTemp/"
				+ "(已解密)" + filename);
		// 将文件解密后写入新文件
		while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
			sumL += length;
			// System.out.println("已传输：" + ((sumL / l) * 100) + "%");
			// 加密字节流
			rc4.message_len = length;
			rc4.seedKey(filekey);
			decode = new byte[length];
			decode = rc4.encode(sendBytes);
			// 写入生成文件IO流
			fos.write(decode, 0, length);
			fos.flush();

		}

		if (fis != null)
			fis.close();
		if (fos != null)
			fos.close();

		String fileMd5_ = Md5Hash.getMD5(new File("D:/涉密信息系统/ClientTemp/"
				+ "(已解密)" + filename));
		if (!fileMd5.equals(fileMd5_)) {
			return false;
		}

		return true;
	}

}
