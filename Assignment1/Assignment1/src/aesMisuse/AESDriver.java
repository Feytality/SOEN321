package aesMisuse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Goes through process of making sure steps to break OFB works 
 * @authors Felicia Santoro-Petti, Daniel Caterson
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
		
		
		
		//OFB a User Plaintxt
		ofb.encipher();
		//Break the OFB just performed
		ofb.attack(PU);
		//Show that you would get the same result if you are an admin getting a cookie at the same time
		ofbAttack.encipher();
		String CU=ofb.getCiphertxt();
		String CA=ofbAttack.getCiphertxt();
		
		System.out.println("Base 64 encoded User Ciphertext");
		System.out.println(StringUtil.encode(StringUtil.StrToByte(CU, CU.length())));
		System.out.println("Base 64 encoded Attacker/Admin Ciphertext");
		System.out.println(StringUtil.encode(StringUtil.StrToByte(CA, CA.length())));
		
	}

}
