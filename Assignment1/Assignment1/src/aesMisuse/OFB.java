package aesMisuse;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
/**
 * Simulates an Output Feedback Block cipher
 * @authors Felicia Santoro-Petti, Daniel Caterson
 *
 */
public class OFB {
	private byte[] key;
	private byte[] iV;
	private byte[][] PU;
	private byte[][] CU;
	private int blocks;
	private int BLOCK_SIZE = 16;
	private byte[] encryption;
	private byte[][] endENC;
	private String ciphertext;
	/**
	 * sets up object to begin OFB
	 * @param bit128Key
	 * @param bit128IV
	 * @param userPlainTxt
	 */
	public OFB(String bit128Key, String bit128IV, String userPlainTxt) {
		encryption = new byte[16];
		key = StringUtil.StrToByte(bit128Key, 16);
		iV = StringUtil.StrToByte(bit128IV, 16);
		
		setPlainTxt(StringUtil.StrToByte(userPlainTxt, userPlainTxt.length()));
		CU = new byte[blocks][BLOCK_SIZE];//
		endENC = new byte[blocks][BLOCK_SIZE];//
		encryptKeyIV();

	
	}
/**
 * Prepares plain text to be used in OFB
 * @param userPlainTxt
 */
	private void setPlainTxt(byte[] userPlainTxt) {
		blocks = userPlainTxt.length / BLOCK_SIZE;
		int extra = userPlainTxt.length % BLOCK_SIZE;
		int copyLength = BLOCK_SIZE;
		if (extra != 0) {
			blocks++;
		}
		PU = new byte[blocks][BLOCK_SIZE];
		int k = 0;
		for (int i = 0; i < blocks; i++) {
			if (i == blocks - 1) {// if last block check to see if padded
				copyLength = extra;
			}
			for (int j = 0; j < copyLength; j++) {
				PU[i][j] = userPlainTxt[k++];
			}

		}

	}
/**
 * ecrypts iv and key for use in OFB
 */
	public void encryptKeyIV() {
		byte[] enc = new byte[16];

		for (int i = 0; i < 16; i++) {
			enc[i] = iV[15 - i];
		}
		encryption = StringUtil.XOR(key, enc);

	}

	
	/**
	 * Perfroms full pass of OFB
	 */
	public void encipher() {
		for (int i = 0; i < blocks; i++) {
			CU[i] = StringUtil.XOR(encryption, PU[i]);
			ciphertext+=StringUtil.toString(CU[i]);
			endENC[i]=encryption;
			iV = encryption;
			//encryptKeyIV();
			
			
		}
	}
	/**
	 * performs a sequence of xors to get the encrypted IV KEY
	 * @param attackPlainTxt
	 */
	public void attack(String attackPlainTxt){
		byte[][] PA = setAttackTxt(StringUtil.StrToByte(attackPlainTxt, attackPlainTxt.length()));
		byte[][] PAPrime=new byte[blocks][BLOCK_SIZE];
		byte[][] ENC=new byte[blocks][BLOCK_SIZE];
		//begin by xor PA wi CU
		for(int i = 0;i <blocks;i++){
			PAPrime[i]=StringUtil.XOR(PA[i], CU[i]);
			
		}
		for(int j = 0;j <blocks;j++){
			ENC[j]=StringUtil.XOR(PAPrime[j], PU[j]);			
		}		
		
		
	}
	/**
	 * Prepares plain text attack to be xored
	 * @param attackPlainTxt
	 * @return
	 */
	private byte[][] setAttackTxt(byte[] attackPlainTxt) {
		blocks = attackPlainTxt.length / BLOCK_SIZE;
		int extra = attackPlainTxt.length % BLOCK_SIZE;
		int copyLength = BLOCK_SIZE;
		if (extra != 0) {
			blocks++;
		}
		byte[][] PA= new byte[blocks][BLOCK_SIZE];
		int k = 0;
		for (int i = 0; i < blocks; i++) {
			if (i == blocks - 1) {// if last block check to see if padded
				copyLength = extra;
			}
			for (int j = 0; j < copyLength; j++) {
				PA[i][j] = attackPlainTxt[k++];
			}
			
		}
		
return PA;
	}
	
	/**
	 * Prints the encryption string that corresponds to a full pass of OFB that have been retrieve throught unsavoury means
	 *	
	 * @param ENC
	 */
	public void printHackedEncryption(byte[][] ENC){		
		for(int i = 0;i <blocks;i++){
			StringUtil.printbytearr(ENC[i]);			
		}
	}

	/**
	 * Prints the encryption string that corresponds to a full pass of OFB
	 */
	public void printOriginalEncryption(){		
		for(int i = 0;i <blocks;i++){
			StringUtil.printbytearr(endENC[i]);			
		}
	}
	
	public String getCiphertxt(){		
		return ciphertext;
	}
	
	
}
