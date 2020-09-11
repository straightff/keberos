package kb.ToolClass;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public final class test2
{
	private static final String DES = "DES";
//	private static final String KEY = "asdasdasdasdadsaaaaa";

	private static byte[] encrypt(byte[] src, byte[] key) throws Exception
	{
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey secretKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(DES);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
		return cipher.doFinal(src);
	}

	private static byte[] decrypt(byte[] src, byte[] key) throws Exception
	{
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey secretKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(DES);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);
		return cipher.doFinal(src);
	}

	private static String byte2hex(byte[] b)
	{
		String hs = "";
		String temp = "";
		for (int n = 0; n < b.length; n++)
		{
			temp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (temp.length() == 1)
				hs = hs + "0" + temp;
			else
				hs = hs + temp;
		}
		return hs.toUpperCase();

	}

	private static byte[] hex2byte(byte[] b)
	{
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("length not even");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2)
		{
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	public static String decode(String src, String key)
	{
		String decryptStr = "";
		try
		{
			byte[] decrypt = decrypt(hex2byte(src.getBytes(StandardCharsets.UTF_8)), key.getBytes(StandardCharsets.UTF_8));
			decryptStr = new String(decrypt);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return decryptStr;
	}

	public static String encode(String src, String key)
	{
		byte[] bytes = null;
		String encryptStr = "";
		try
		{
			bytes = encrypt(src.getBytes(StandardCharsets.UTF_8), key.getBytes(StandardCharsets.UTF_8));
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		if (bytes != null)
			encryptStr = byte2hex(bytes);
		return encryptStr;
	}

//	/**
//	 * 解密
//	 */
//	public static String decode(String src)
//	{
//		return decode(src, KEY);
//	}
//
//	/**
//	 * 加密
//	 */
//	public static String encode(String src)
//	{
//		return encode(src, KEY);
//	}
//
//	public static void main(String[] args)
//	{
//		String te = encode("asdaaaaaaaaaaaaaaaaaaaaaaaaaaaa".trim());
//		System.out.println(te);
////		System.out.println(ASUI.getAS_key());
//		System.out.println(decode(te));
//		System.out.println(String.valueOf(System.currentTimeMillis()).substring(0, 5));
//	}
}