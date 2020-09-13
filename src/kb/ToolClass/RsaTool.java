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

		Gp[0] = Base64.getEncoder().encodeToString(n.toString().getBytes("UTF-8"));
		Gp[1] = Base64.getEncoder().encodeToString(e.toString().getBytes("UTF-8"));
		return Gp;
	}

	public String[] createPriKey() throws UnsupportedEncodingException {
		String[] Gp = new String[3];
		Gp[0] = Base64.getEncoder().encodeToString(n.toString().getBytes("UTF-8"));
		Gp[1] = Base64.getEncoder().encodeToString(d.toString().getBytes("UTF-8"));

		return Gp;
	}

	public static BigInteger convertTo (String n) throws UnsupportedEncodingException {
		String a =new String(Base64.getDecoder().decode(n),"UTF-8");
		BigInteger re =new BigInteger(a);
		return re;
	}
	public static String enCode(String n, String de, String pt) throws IOException {

		BigInteger bign = convertTo(n);
		BigInteger bigde =convertTo(de);
		BigInteger bigpt = new BigInteger(pt.getBytes("UTF-8"));
		BigInteger re = quickp(bigpt, bigde, bign);
//		BigInteger re = bigpt.modPow(bigde, bign);
		return Base64.getEncoder().encodeToString(re.toByteArray());
	}



	public static String deCode(String n, String de, String pt) throws IOException
	{
		BigInteger bign = convertTo(n);

		BigInteger bigde = convertTo(de);
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

//		String han = "hhhhasdas哈师大飒飒的daAsD撒士大夫sd";
//
//		System.out.println(pub[1]);
//		String textEncode = rsa.enCode(pub[0],pub[1],han);
//		System.out.println(textEncode);
//		System.out.println(rsa.deCode(pri[0],pri[1],textEncode));
//		System.out.println(exgcd(new BigInteger("79"),new BigInteger("3220")).toString());
//
//		BigInteger bi = new BigInteger("123");
//		String a =new String(bi.toString().getBytes("UTF-8"));
//		String ac = Base64.getEncoder().encodeToString(a.getBytes("UTF-8"));
//		System.out.println(ac);
//		String ae = new String(Base64.getDecoder().decode(ac),"UTF-8");
//		System.out.println(ae);
//
//		System.out.println(a);
//		String b = new String(bi.toByteArray());
//		System.out.println(b);



}
