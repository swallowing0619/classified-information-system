package algorithm;
 
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

public class RSACrypt {

	public int otherspublicKey = 0;

	public int n = 0;

	public  int publicKey = 0;

	public  int privateKey = 0;
	private Random ran = new Random();

	public RSACrypt() {
		// create publicKey and privateKey

		int p = 0;

		int q = 0;

		int d = 0;
		int e = 0;
		// int p = 11;
		// int q = 23;
		// int d = 19;
		// int e = 139;
		int[] prime = new int[] { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37,
				41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103,
				107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167,
				173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233,
				239, 241, 251 };

		// �����ļ���ѡȡ����С��256��������Ϊp��q
		int prandom = ran.nextInt(54);
		int qrandom = ran.nextInt(54);

		p = prime[prandom];
		q = prime[qrandom];

		n = p * q;
		// ѡȡ����һ��d��(p-1)(q-1)���ʣ��Ҵ���min(p,q)
		d = (int) (Math.min(p, q) + Math.random()
				* ((p - 1) * (q - 1) - Math.min(p, q)));
		int temp = (p - 1) * (q - 1);
		BigInteger bigtemp = BigInteger.valueOf(temp);
		BigInteger bigd = BigInteger.valueOf(d);
		while (gcd(temp, d) != 1) {
			d = (int) (Math.min(p, q) + Math.random()
					* ((p - 1) * (q - 1) - Math.min(p, q)));
			bigd = BigInteger.valueOf(d);
		}
		// ͨ��ģ�������d��ģ(p-1)(q-1)�µ���Ԫe
		BigInteger bige = bigd.modInverse(bigtemp);
		e = bige.intValue();

		//生成密钥对
		publicKey = e;
		privateKey = d;//0~65536
		// System.out.println(p);
		// System.out.println(q);
		// System.out.println(temp);
		// System.out.println(d);
		// System.out.println(e);

	}

	public void getPublicKey(int othersPublicKey) {
		this.otherspublicKey = othersPublicKey;
	}

	public void getn(int n) {
		this.n = n;
	}

	public byte[] decrypt(byte[] message) {
		// TODO Auto-generated method stub
		String result = null;
		byte[] byteresult = new byte[message.length / 4];
		// ��message��stringת��Ϊbyte[]����
		// byte[] bytemessage = message.getBytes();
		// ���byte[]���͵�bytemessage�����int[]���͵�intresult[]
		for (int i = 0; i < message.length / 4; i++) {
			int a = bytesToInt(message, 4 * i);
			int temp = pow(a, otherspublicKey, n) % n;
			byteresult[i] = (byte) (temp);
		}
		// return byteresult;
		// ��byteresultת��Ϊstring����
		// System.out.println(byteresult);
//		result = new String(byteresult);
		return byteresult;
	}

	public byte[] encrypt(String message) {
		// TODO Auto-generated method stub
		// String result = null;
		byte[] byteresult = new byte[message.length() * 4];

		// ��message��string转byte[]
		byte[] bytemessage = message.getBytes();
		// ���byte[]���͵�bytemessage�����int[]���͵�intresult[]
		for (int i = 0; i < message.length(); i++) {
			int temp = pow(bytemessage[i], privateKey, n) % n;
			byte[] bytetemp = intToByteArray(temp);
			byteresult[4 * i + 0] = bytetemp[0];
			byteresult[4 * i + 1] = bytetemp[1];
			byteresult[4 * i + 2] = bytetemp[2];
			byteresult[4 * i + 3] = bytetemp[3];
		}
		// ��byteresultת��Ϊstring����
		// result = new String(byteresult);
		
		//String line = new String(byteresult,"GBK");
//		System.out.println(byteresult);
		return byteresult;
	}

	private static int gcd(int a, int b) {
		if (b == 0)
			return a;
		return gcd(b, a % b);
	}

//	public static long toLong(byte[] b)
//
//	{
//		long l = 0;
//
//		l = b[0];
//
//		l |= ((long) b[1] << 8);
//
//		l |= ((long) b[2] << 16);
//
//		l |= ((long) b[3] << 24);
//
//		l |= ((long) b[4] << 32);
//
//		l |= ((long) b[5] << 40);
//
//		l |= ((long) b[6] << 48);
//
//		l |= ((long) b[7] << 56);
//
//		return l;
//	}
//
//	public static byte[] toByteArray(long number) {
//
//		long temp = number;
//
//		byte[] b = new byte[8];
//
//		for (int i = b.length - 1; i > -1; i--) {
//
//			b[i] = new Integer((int) (temp & 0xff)).byteValue();
//
//			temp = temp >> 8;
//
//		}
//
//		return b;
//	}

	public static int pow(int x, int y, int n) // we define the power method
												// with base x and power y
												// (i.e., x^y)
	{
		int z = x;
		for (int i = 1; i < y; i++) {
			z = z * x % n;

		}

		return z;
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}

	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (int) (((src[offset] & 0xFF) << 24)
				| ((src[offset + 1] & 0xFF) << 16)
				| ((src[offset + 2] & 0xFF) << 8) | (src[offset + 3] & 0xFF));
		return value;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
//		int a = 0;
		int pub = 61 ,pri=31,n =254;
		RSACrypt temp = new RSACrypt();
		RSACrypt temp2 = new RSACrypt();

//		rsa.privateKey = temp.privateKey;
//		rsa.n = temp.n;
//		byte[] res = rsa.decrypt("Hjkgkgjhkjhji".getBytes());
//		for(int i=0;i<res.length;i++){
//			System.out.print((char)res[i]);
//		}
//		System.out.println();
//		
//		rsa.publicKey = temp.publicKey;
//		rsa.n = temp.n;
//		byte[] byteres = temp.encrypt(res.toString());
//		for(int i=0;i<byteres.length;i++){
//			System.out.print((char)byteres[i]);
//		}
//		a = temp.publicKey;
//		temp2.getPublicKey(a);
//		temp2.getn(temp.n);
		temp.privateKey = pri ;
		temp.n = n;
		byte[] res = temp.encrypt("Hjkgkgjhkjhji");
		for(int i=0;i<res.length;i++){
			System.out.print((char)res[i]);
		}
		
		System.out.println();
		temp2.otherspublicKey = pub;
		temp2.n = n;
		byte[] byteres = temp2.decrypt(res);
		for(int i=0;i<byteres.length;i++){
			System.out.print((char)byteres[i]);
		}

	}

}
