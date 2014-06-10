package vaultize.applet;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.SecureRandom;

public class VaultizeApplet {

		final int BITLENGTH = 512;
		byte[] aesKey = null;
		public void init()
		{
			System.out.println(executeHandshake("https://192.168.9.57/share/publickey", "https://192.168.9.57/share/token"));
		}
		/*
		 * executeHandshake	:	Method to do handshake with the server using Deffie HellMan algorithm. Using handshake client 
		 * 									establish a shared secrete key. Which is used encrypt the AES 256 key. AES 256 key is used to 
		 * 									encrypt the data.
		 * publicKeyLink			:	Link which when called with Sessionid returns the public part of the Deffie Hellman handshaking.
		 * shareToken 			:	Link to send the the public key in the Deffie Hellman handshaking.Server returns the encrypted token.
		 * 									Then use the shared Deffie Hellman key to decrypt the token. 
		 */
		String executeHandshake(String publicKeyLink,String shareToken)
		{
			URL url;
			String retVal = null;
			
			try 
			{
					url = new URL(publicKeyLink);
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Sessionid ","1111111111");
					connection.connect();
					if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
					{
						
						if((connection.getHeaderField("A") == null) ||(connection.getHeaderField("g") == null) || (connection.getHeaderField("p") == null))
						{
							retVal = "ERROR in executeHandshake : value of A :"+connection.getHeaderField("A")+" value of g : "+connection.getHeaderField("g") + "value of p :" + connection.getHeaderField("p");
						}
						else
						{
							BigInteger A =new BigInteger( connection.getHeaderField("A"));
							BigInteger p =new BigInteger( connection.getHeaderField("p"));
							BigInteger g =new BigInteger(connection.getHeaderField("g"));
							
							SecureRandom rand = new SecureRandom();
							BigInteger b = BigInteger.probablePrime(this.BITLENGTH, rand);
							
							System.out.println("*************response code : "+connection.getResponseCode());
							System.out.println("*************A :"+ connection.getHeaderField("A"));
							System.out.println("*************a :"+ connection.getHeaderField("a"));
							System.out.println("*************p :"+ connection.getHeaderField("p"));
							System.out.println("*************g :"+ connection.getHeaderField("g"));
							System.out.println("*************ntent :");
							System.out.println("*************random prime number generated b:"+b);
							
							BigInteger s = g.modPow(b, p);
							
							BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							String decodedString;
							while ((decodedString = in.readLine()) != null) 
							{
								System.out.println(decodedString);
							}
							in.close();
							
							//Generating the client's public key B
							BigInteger B = g.modPow(b, p);
							
							url = new URL(shareToken);
							connection = (HttpURLConnection)url.openConnection();
							connection.setRequestMethod("POST");
							connection.setRequestProperty("Sessionid", "");
							connection.connect();
							if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
							{
								aesKey = connection.getRequestProperty("Data").getBytes();
								System.out.println("***********aes key received :" + connection.getRequestProperty("Data"));
							}
							else
							{
								retVal = "ERROR in executeHandshake  : failed request for token";
							}
						}
							
						
					}
					else
					{
						retVal = "ERROR in executeHandshake : Response code : "+String.valueOf(connection.getResponseCode());
					}
					
				
			} 
			catch (MalformedURLException e) 
			{
				retVal = "ERROR IN executeHandshake : "+e.getMessage();
			} 
			catch (IOException e) 
			{
				retVal = "ERROR IN executeHandshake : "+e.getMessage();
			}
					
		
			return retVal;
		}
}
