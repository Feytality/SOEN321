package question13c;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class AESDriver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		byte[] s2b=new byte[1];
		String plaintxt = "user=anonymous,tmstmp=1443657660";
		try {
			s2b=plaintxt.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < s2b.length; i++) {
			//System.out.println(s2b[i]);
			
		}
		System.out.println(s2b.length);
		System.out.println(plaintxt.length());
		String binary = new BigInteger("Z".getBytes()).toString(2);
		int z =Integer.parseInt( new BigInteger("z".getBytes()).toString(2));
		int zero =Integer.parseInt( new BigInteger("0".getBytes()).toString(2));
		
		System.out.println(s2b[0]^s2b[3]);
		
	}

}
