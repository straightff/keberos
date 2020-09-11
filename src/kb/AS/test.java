package kb.AS;

import kb.ToolClass.DesTool;

public class test
{

	public static void main(String[] args) throws Exception {
		String a = "JPzmV8CCxzNwsWFkDOO7ChtYnJjTTR7U+fo3Oppvu7YqI33Y28lso/Dz6oSujPuGu6VZvrlAQaIJwS8aVIwHxTPsRvcdGcFcc6qvwDaLM9c=";
		String key = "5872b92f";
		String encode = DesTool.encrypt(a,key);
		System.out.println(encode);
		System.out.println(DesTool.decrypt(encode,key));
	}

}
