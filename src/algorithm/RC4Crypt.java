package algorithm;
 
public class RC4Crypt {
	public int key_Len;                   //密钥长度
	public int message_len;                  //明文长度
	public int[] keys;
	
	public int[] generateKey(byte Key[])        //生成伪随机密钥
	{
		//初始化S
	    int[] S = new int[256];
	    int[] T = new int[256];
	    for(int i=0;i<256;i++)
	    {
	    	S[i] = i;
	    	T[i] = Key[i % key_Len];
	    }
	    for(int temp,j=0,i=0;i<256;i++)
	    {
	    	j=(j+S[i]+T[i])%256;
	    	temp=S[i];S[i]=S[j];S[j]=temp;
	    }
	    
	    //与明文长度相同的生成密钥流
	    int tmp,t;
	    int k[] = new int[message_len];
//	    System.out.println("message_len="+message_len);
	    int j=0,i=0;
	    for(int count=0;i<message_len;count++)
	    {
	    	if(count >= message_len){
//	    		System.out.println("count="+count);
	    		break;
	    	}
	    	i=(i+1)%256;
	    	j=(j+S[i])%256;
	    	tmp=S[i];S[i]=S[j];S[j]=tmp;//交换
	    	t=(S[i]+S[j])%256;
	    	k[count]=S[t];
	    }
	    return k;
	}
	
	//key转换类型String->byte
	public void seedKey(String strr)                
	{
		key_Len = strr.length();
		byte[] key=new byte[key_Len];
		for(int i=0;i<key_Len;i++)
		{
			key[i] = (byte) (strr.charAt(i));
		}
		//生成密钥流
		keys = generateKey(key);         
	}
	
	//密钥流与要加密的内容异或操作
	public int[] encryption(int[] keys,int[] mingwen)    //���ܺ���(�����������)
	{		
		int[] temp = new int[message_len];
		for(int i=0;i<message_len;i++)
		{
			temp[i] = keys[i]^mingwen[i];
		}
		return temp;
	}
	
	//加密方法
	public 	byte[] encode(byte[] strr)
	{
		//类型转换string->int[]
		int[] mingwen=new int[message_len];
		for(int i=0;i<message_len;i++)
		{
			mingwen[i] = strr[i];
//			System.out.println("mingwen:"+mingwen[i]);
		}
		int[] miwen = encryption(keys, mingwen);     //异或
		byte[] miwen_bytes = new byte[message_len];
		for(int i=0;i<message_len;i++)
		{
			miwen_bytes[i] = (byte) (miwen[i]);
		}
		return miwen_bytes;
	}
	

	public static void main(String[] args) {
		RC4Crypt rc4 = new RC4Crypt();
		byte[] mingwen = new byte[270];
//		byte[] mingwen={'c','h','i','n','a',};
		rc4.message_len = mingwen.length;
		for(int i=0;i<mingwen.length;i++){
//		mingwen={'c','h','i','n','a',};
		mingwen[i] = 'c';
		}
		String key_seed="abc";
//		System.out.println(key_seed.length());
		rc4.seedKey(key_seed);
		byte[] temp = rc4.encode(mingwen);
		temp = rc4.encode(temp);
		for(int i=0;i<temp.length;i++){
			System.out.println((char)temp[i]);
		}

	}
}
