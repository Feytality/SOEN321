package aesMisuse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Goes through process of making sure process works 
 * @author Cat
 *
 */
public class AESDriver {

	public static void main(String[] args) {
				
		String key ="ffffffffaaaaaaaa";
		String IV = "abcabcabcabcabca";		
		String PU = "user=Andie,tmstmp=1443657660";
		String PA = "user=Admin,tmstmp=1443657660";
		OFB ofb = new OFB(key,IV,PU);		
		OFB ofbAttack = new OFB(key,IV,PA);
		
		
		
		
		System.out.println("Original ecription");
		ofb.encipher();
		
		ofb.printOriginalEncryption();		
		System.out.println("Retrieve ecription");
		ofb.attack(PA);
		
		System.out.println("Verify ecription");
		ofbAttack.encipher();
		ofbAttack.printOriginalEncryption();
		
	}

}
