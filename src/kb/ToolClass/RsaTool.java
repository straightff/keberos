package kb.ToolClass;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;

public class RsaTool
{
	private static BigInteger prime1;
	private static BigInteger prime2;
	public static final int publicExponent = 65537; // e值
	private static BigInteger d;
	private static BigInteger f;
	private static BigInteger n;
	private static BigInteger e;

	public static BigInteger getPrime1()
	{
		return prime1;
	}

	public static BigInteger getPrime2()
	{
		return prime2;
	}

	public static BigInteger getD()
	{
		return d;
	}

	public static String numToString(String input)
	{
		String temp;
		String result = "";
		for (int i = 0; i < input.length(); i += 5)
		{
			temp = input.substring(i, i + 5);
			result += (char) Integer.parseInt(temp);
		}
		return result;
	}

	public static String stringToNum(String input)
	{
		char[] mess2 = input.toCharArray();
		String result = "";
		for (int i = 0; i < mess2.length; i++)
		{
			if ((int) mess2[i] < 10000 && (int) mess2[i] >= 100)
			{
				result += "00";
			} else if (((int) mess2[i] < 100))
			{
				result += "000";
			}

			result += (int) mess2[i];
		}
		return result;
	}

	public RsaTool()
	{
		prime1 = bigPrimeCreate();
		prime2 = bigPrimeCreate();
		n = prime1.multiply(prime2);
		f = prime1.subtract(BigInteger.ONE).multiply(prime2.subtract(BigInteger.ONE));
		e = BigInteger.valueOf(publicExponent);
		d = e.modInverse(f);
	}

	public BigInteger[] createPriKey()
	{
		BigInteger[] bintGp = new BigInteger[3];
		bintGp[0] = n;
		bintGp[1] = e;
		return bintGp;
	}

	public BigInteger[] createPubKey()
	{
		BigInteger[] bintGp = new BigInteger[3];
		bintGp[0] = n;
		bintGp[1] = d;

		return bintGp;
	}

	public BigInteger enCode(BigInteger n, BigInteger de, BigInteger pt) throws UnsupportedEncodingException
	{

//		BigInteger re = pt.modPow(de, n);
		return quickp(pt, de, n);

	}

	public BigInteger deCode(BigInteger n, BigInteger de, BigInteger pt) throws UnsupportedEncodingException
	{

//		BigInteger a = new BigInteger(pt);
//		BigInteger re = pt.modPow(de, n);

		return quickp(pt, de, n);

	}

	// 快速幂 C++ 改大数类
	static BigInteger quickp(BigInteger a, BigInteger d, BigInteger n)
	{
		BigInteger ans = new BigInteger("1");
		a = a.mod(n);
		while (!d.equals(BigInteger.ZERO))
		{
			if (d.mod(BigInteger.valueOf(2)).equals(BigInteger.ONE))
				ans = (ans.multiply(a)).mod(n);
			d = d.shiftRight(1);
			a = (a.multiply(a)).mod(n);
//			System.out.println(a + " " + b + " " + mo);
		}
		return ans;
	}

	// 生成大素数精度 2^-256
	public static BigInteger bigPrimeCreate()
	{
		Random r = new Random();
		BigInteger bigInteger = BigInteger.probablePrime(2048, r);
		while (!bigInteger.isProbablePrime(256))
		{
			bigInteger = BigInteger.probablePrime(2048, r);
		}
		return bigInteger;
	}
}
