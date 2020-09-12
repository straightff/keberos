package kb.ToolClass;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
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
	private static BigInteger x,y;

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

	public RsaTool()
	{
		prime1 = bigPrimeCreate();
		prime2 = bigPrimeCreate();
		n = prime1.multiply(prime2);
		f = prime1.subtract(BigInteger.ONE).multiply(prime2.subtract(BigInteger.ONE));
		e = BigInteger.valueOf(publicExponent);
		d = exgcd(f,e);
//		d = e.modInverse(f);
	}

	public String[] createPubKey() throws UnsupportedEncodingException {
		String[] Gp = new String[3];

		Gp[0] = Base64.getEncoder().encodeToString(n.toByteArray());
		Gp[1] = Base64.getEncoder().encodeToString(e.toByteArray());
		return Gp;
	}

	public String[] createPriKey() throws UnsupportedEncodingException {
		String[] Gp = new String[3];
		Gp[0] = Base64.getEncoder().encodeToString(n.toByteArray());
		Gp[1] = Base64.getEncoder().encodeToString(d.toByteArray());

		return Gp;
	}

	public static String enCode(String n, String de, String pt) throws IOException {

		BigInteger bign = new BigInteger(Base64.getDecoder().decode(n));
		BigInteger bigde = new BigInteger(Base64.getDecoder().decode(de));
		BigInteger bigpt = new BigInteger(pt.getBytes("UTF-8"));
		BigInteger re = quickp(bigpt, bigde, bign);

//		BigInteger re = bigpt.modPow(bigde, bign);
		return Base64.getEncoder().encodeToString(re.toByteArray());
	}

	public static String deCode(String n, String de, String pt) throws IOException
	{
		BigInteger bign = new BigInteger(Base64.getDecoder().decode(n));

		BigInteger bigde = new BigInteger(Base64.getDecoder().decode(de));
		BigInteger bigpt = new BigInteger(Base64.getDecoder().decode(pt.getBytes("UTF-8")));
//		BigInteger a = new BigInteger(pt);
//		BigInteger re = bigpt.modPow(bigde, bign);
		BigInteger re = quickp(bigpt, bigde, bign);
		return new String(re.toByteArray(),"UTF-8");


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

	public static BigInteger GCD(BigInteger a, BigInteger b) {
		if(b.equals(BigInteger.ZERO))
			return a;
		else
			return GCD(b,a.mod(b));
	}

	private static BigInteger exgcd(BigInteger a,BigInteger b)
	{
		if(b.equals(BigInteger.ZERO))
		{
			x = new BigInteger("1");
			y=new BigInteger("0");
			return y;
		}else {
			BigInteger r=exgcd(b,a.mod(b));
			BigInteger t=x;

			x=y;
			y=t.subtract((a.divide(b)).multiply(y));

			return y;
		}
	}

//	public static void main(String[] args) throws IOException {
//		RsaTool rsa = new RsaTool();
//		String[] pri = rsa.createPriKey();
//		String[] pub = rsa.createPubKey();
//		String han = "hhhhasdas哈师大飒飒的daAsD撒士大夫sd";
//
//		String textEncode = rsa.enCode(pub[0],pub[1],han);
//		System.out.println(textEncode);
//		System.out.println(rsa.deCode(pri[0],pri[1],textEncode));
//		System.out.println(exgcd(new BigInteger("79"),new BigInteger("3220")).toString());
//	}
//
//

}
