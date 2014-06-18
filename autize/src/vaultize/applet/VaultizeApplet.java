package vaultize.applet;

import java.applet.Applet;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class VaultizeApplet extends Applet {

		final int BITLENGTH = 512;
		byte[] aesKey = null;
		SSLSocketFactory sslSocketFactory = null;
		String ServerResponseA = "public_key";
		String serverResponseP = "prime";
		String serverResponseG = "generator";
		
		public void disableSSL() throws NoSuchAlgorithmException, KeyManagementException
		{
			HostnameVerifier myhostnameverifier = new HostnameVerifier() {
				@Override
					public boolean verify(String arg0, SSLSession arg1) {
						// TODO Auto-generated method stub
						return true;
					}
				};
				HttpsURLConnection.setDefaultHostnameVerifier(myhostnameverifier);
				
				// A TRUSTMANAGER THATS TRUSTS EVERY CERTIFICATES
				class MyTrustManager implements X509TrustManager {

					@Override
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] arg0, String arg1)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] arg0, String arg1)
							throws CertificateException {
						// TODO Auto-generated method stub
						
					}

					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						// TODO Auto-generated method stub
						return null;
					}

					
				}
				//CREATE AN SSLSocketFactory USING THE MOCKED TRUSTMANAGER
				SSLContext ctx = SSLContext.getInstance("TLS");
				javax.net.ssl.TrustManager mtm = new MyTrustManager();
				javax.net.ssl.TrustManager[] tm = { mtm };
				ctx.init(null, tm, null);
				sslSocketFactory = ctx.getSocketFactory();
	
			
		}
		
		public void start()
		{
			
			try 
			{
				disableSSL();
				
				System.out.println(executeHandshake("https://192.168.9.57/share/publickey", "https://192.168.9.57/share/token"));
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/*
			try {
				disableSSL();
				URL url = new URL("https://192.168.9.90/");
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				
				((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
				
				connection.setRequestMethod("GET");
				
				connection.connect();
				
				
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String decodedString;
				while ((decodedString = in.readLine()) != null) 
				j
					System.out.println(decodedString);
				}
				in.close();
			} catch (KeyManagementException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
		}
		
		/*
		 * executeHandshake	:	Method to do handshake with the server using Deffie HellMan algorithm. Using handshake client 
		 * 									establish a shared secrete key. Which is used encrypt the AES 256 key. AES 256 key is used to 
		 * 									encrypt the data.
		 * publicKeyLink			:	Link which when called with Sessionid returns the public part of the Deffie Hellman handshaking.
		 * shareToken 			:	Link to send the the public key in the Deffie Hellman handshaking.Server returns the encrypted token.
		 * 									Then use the shared Deffie Hellman key to decrypt the token. 
		 * retVal						:	return value of the method. is null if every thing is fine other wise a non null string with error message.
		 */
		String executeHandshake(String publicKeyLink,String shareToken) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException
		{
			URL url;
			String retVal = null;
			String charset = "UTF-8";
			try 
			{
				

					CookieHandler.setDefault( new CookieManager( null, CookiePolicy.ACCEPT_ALL ) );
				
					url = new URL(publicKeyLink);
					HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					
					System.out.println("1");
					
					//code to disable SSL
					((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
					
					System.out.println("2");
					connection.setRequestMethod("POST");
					
					connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
					connection.setRequestProperty("Cache-Contro","no-cache");
					connection.setRequestProperty("Accept-Charset", charset);
					
					
					connection.setDoOutput(true);
					OutputStream out = (connection.getOutputStream());
					String query = String.format("sessionid=%s",URLEncoder.encode("324234",charset));
					out.write(query.getBytes(charset));
					out.close();
					
					
					System.out.println("3");
					
					connection.connect();
					
					
						//printing ALL  the headers
						System.out.println("4");
						for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
						    System.out.println(header.getKey() + "=" + header.getValue());
						}
						
					
						
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String decodedString = "";
						String tmp = null;
						while ((tmp = in.readLine()) != null) 
						{
							decodedString += decodedString + tmp;
						}
						in.close();
						System.out.println(decodedString);
						JSONObject jsonObj = new JSONObject(decodedString);
						System.out.println("*************response code : "+connection.getResponseCode());
						
						System.out.println("*************a :"+ jsonObj.getString(ServerResponseA));
						System.out.println("*************g :"+ jsonObj.getInt(serverResponseG));
						System.out.println("*************p :"+ jsonObj.getString(serverResponseP));
						
						BigInteger A =new BigInteger(jsonObj.getString(ServerResponseA));
						BigInteger p =new BigInteger(jsonObj.getString(serverResponseP));
						BigInteger g =new BigInteger(String.valueOf(jsonObj.getInt(serverResponseG)));
						
						
							SecureRandom rand = new SecureRandom();
							BigInteger b = BigInteger.probablePrime(this.BITLENGTH, rand);
							
							
							
							System.out.println("*************random prime number generated b:"+b);
							
							BigInteger s = g.modPow(b, p);
							
							//Generating the client's public key B
							BigInteger B = g.modPow(b, p);
							
							byte[] sBytes = s.toByteArray();
							
							byte[] aesKey = new byte[32];
							
							for(int i = 0 ; i < 32; i++)
							{
								aesKey[i] = sBytes[i];
							}
							
							url = new URL(shareToken);
							
							connection = (HttpURLConnection)url.openConnection();
							
							connection.setRequestMethod("POST");
							
							/*
							for (String cookie : cookies) 
							{
								System.out.println(cookie.split(";", 2)[0]+";");
								connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
								
							}
							*/
							
							connection.setRequestProperty("Accept-Charset", charset);
							connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
							connection.setDoOutput(true);
							connection.setDoInput(true);
							//code to disable SSL
							((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
							
							out = (connection.getOutputStream());
							query = String.format("sessionid=%s&data=%s",URLEncoder.encode("324234",charset),URLEncoder.encode(B.toString(),charset));
							System.out.println(query);
							
							out.write(query.getBytes(charset));
							
							
							out.close();
							connection.connect();
							System.out.println("123");
							
							
							System.out.println("4");
							for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
							    System.out.println(header.getKey() + "=" + header.getValue());
							}
							
							InputStream tokenInStream = connection.getInputStream();
							byte[] encryptedToken = new byte[tokenInStream.available()];
							tokenInStream.read(encryptedToken);
							
							System.out.println("bytes to be read  : " + tokenInStream.available());
							System.out.println("1234");
							
							String aeskey = "";
							tmp = "";
							
							
							//System.out.println(in.);
							
							/*
							while ((tmp = in.readLine()) != null) 
							{
								//aeskey += aeskey + tmp;
								System.out.println(tmp);
							}
							
							in.close();
							System.out.println("***********aes key received :" + aeskey);
							*/
							//System.out.println("decoded token :  " +Arrays.toString(decrypt(encryptedToken,aesKey )));
							System.out.write(decrypt(encryptedToken,aesKey ));
							
							
							/*
							
							if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
							{
								in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
								aeskey = "";
								tmp = null;
								while ((tmp = in.readLine()) != null) 
								{
									//aeskey += aeskey + tmp;
								}
								in.close();
								System.out.println("***********aes key received :" + aeskey);
							}
							else
							{
								retVal = "ERROR in executeHandshake  : failed request for token";
							}
							
							*/
					}
				
			catch (MalformedURLException e) 
			{
				retVal = "ERROR IN executeHandshake : "+e.getMessage();
			} 
			catch (IOException e) 
			{
				retVal = "ERROR IN executeHandshake : "+e.getMessage();
			}
			catch(JSONException e)
			{
				retVal = retVal = "ERROR in executeHandshake : server response does not contains p or A or g";
				e.printStackTrace();
			}
				
			return retVal;
		}
		
		String download(String downloadLink)
		{
			String retVal = null;
			if(downloadLink == null)
			{
				retVal = "ERROR in download() : downloadLink input parameter is null";
				
				
			}
			else
			{
				
				if(aesKey == null)
				{
					retVal = "ERROR in download() : aesKey not initialised. call executeHandshake first.";
				}
				else
				{
					
					try 
					{
						URL url = new URL(downloadLink);
						HttpURLConnection connection = (HttpURLConnection)url.openConnection();
						connection.setRequestMethod("GET");
						connection.connect();
						if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
						{
							InputStream rd = connection.getInputStream();
							
						}
						else
						{
							
						}
					} 
					catch (IOException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			return null;
			
		}
		private byte[] decrypt(byte[] buffer, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
		{
			byte decrypted[];
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		    Cipher cipher = Cipher.getInstance("AES/ECB/NOPADDING");
		    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
		    decrypted = cipher.doFinal(buffer);
			return decrypted;
		}
}
