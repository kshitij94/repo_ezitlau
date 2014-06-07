import java.applet.Applet;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;



public class Externalclient extends Applet{
	
	private CloseableHttpClient httpclient = null; 
	String loginLink = null;								/*value of action in submit form.This page corresponds to the password requirement when the external link is opened.*/
	String loginPassword = null;						/*Required only for debug purposes.*/
	String uploadLink = null;							/*variable to store upload link.Required for deployment. */
	String filename = null;
	InputStream in = null;
	File saveFile = null;
	JFrame confirmSaveFrame = null;
	String FILENAME_PATTERN = "filename=[\"](.*)[\"]";
	int bufferSize = 65536;
	int BLOCK_SIZE = 16 * 1024;
	long contentLength;
	static int ENC_KEY_LEN = 32;
	static int ENC_BLOCK_LEN = 16;
	static String FILE_ID_PATTERN = "[\\w\\d]+-[\\w\\d]+-[\\w\\d]+-[\\w\\d]+";
	int KEY_SIZE = 32;
	
	private static volatile boolean IS_WRITE = false;
	
	/*
	 * disableSSL		:	Method to disable the SSL.
	 * Pre				:	HttpClient client does not connect to a secure server.If the server certificate is not certified.
	 * Post				:	HttpClient connects to all servers event without a authorized certificate.
	 * Note				:	USE ONLY FOR DEBUGGING AND TESTING PURPOSES.
	 * Return Value 	:	String is null if every thing is fine.Otherwise returns non null string.	  
	 */
    String disableSSL()
	{
		String retVal = null;
		try 
		{	
			SSLContextBuilder builder = new SSLContextBuilder();
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
			this.httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} 
		catch (NoSuchAlgorithmException e2) 
		{
			e2.printStackTrace();
			retVal ="Error in disableSSL:"+ e2.getMessage();
			
		} 
		catch (KeyStoreException e2) 
		{
			e2.printStackTrace();
			
			retVal = "Error in disableSSL:"+e2.getMessage();
		} 
		catch (KeyManagementException e) 
		{
			e.printStackTrace();
			retVal ="Error in disableSSL:"+ e.getMessage();
		}
		return retVal;
	}
    private String initialiseApplet(String loginLink, String password, String uploadLink) 
    {
    	String retVal = null;
    	
    	this.loginLink = loginLink;
    	this.loginPassword = password;
    	this.uploadLink = uploadLink;
    	
    	retVal = disableSSL();
    	if(retVal == null)
    	{
    		retVal = externalClientAuthenticate();
    	}    	
    	return retVal;
    }
    
    /*
     * (non-Javadoc)
     * @see java.applet.Applet#init()
     * 
     */
	public void init() 
	{
		
	
	}	  
	public void start()
	{
		String result = null;
		
		
		//result = 	initialiseApplet("https://192.168.9.163/share/password/a-1-xIIhd4rXMYjBwJeqNu2_TQoEY6m9d+YWRzcXdAZXdmZXcuY29t","88888888","https://192.168.9.163/shareupload/YWRzcXdAZXdmZXcuY29t");
		
		if(result != null)
		{
			System.out.println(result);
		}
		else
		{
			disableSSL();
			
			
			/*
				try {
					deviceAuthenticate("https://192.168.9.90/login","intern1@vaultize.com", "88888888", "{'mac': 'V0StJ5', 'plat': 'Windows 7', 'nm': 'VAULTIZE-PC'}");
				} catch (InvalidKeyException | NoSuchAlgorithmException
						| NoSuchProviderException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			*/
			
			
			try
			{
				tetsingEnDownload("https://192.168.9.90/login","intern1@vaultize.com", "88888888","{'mac': 'V0StJ5', 'plat': 'Windows 7', 'nm': 'VAULTIZE-PC'}", "https://192.168.9.90/download/l-1-2-2");
			} 
			catch (InvalidKeyException | NoSuchAlgorithmException
					| NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException | JSONException | IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			/*
			try {
				uploadEncryptedFile("https://192.168.9.90/login","intern1@vaultize.com", "88888888","{'mac': 'V0StJ5', 'plat': 'Windows 7', 'nm': 'VAULTIZE-PC'}","https://192.168.9.90/upload/");
				
			
			} catch (InvalidKeyException | NoSuchAlgorithmException
					| NoSuchPaddingException | IllegalBlockSizeException
					| BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
	
			//upload 
			//uploadForClient();
			//download
			//showSaveDialog("https://192.168.9.163/download/get/a-1-1-8");
		}
		
	}
	public void stop()
	{
		
	}
	private int read(InputStream in, byte[] buffer) throws IOException
	{
		
		int inputRead = 0;
		int readRetVal = in.read(buffer);
		if(readRetVal != -1)
		{
			while(inputRead < buffer.length && readRetVal != -1)
			{
				inputRead += readRetVal;
				readRetVal = in.read(buffer, inputRead, buffer.length - (inputRead));
				//System.out.print(readRetVal + " ");
			}
		}
		else
		{
			inputRead = -1;
		}
		
		
		return inputRead;
	}
	
	public void uploadEncryptedFile(String loginLink, String username,String password, String device, String encryUpLink) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		System.out.println("**************UPLOADING ENCRYPTED FILE FROM DEVICE***************** ");
		System.out.println("UPLOAD LINK :"+encryUpLink);
		String retVal = null;
		StringEntity entity;
		JSONObject jsonObj;
		try 
		{
			jsonObj = new JSONObject(device);
			

	
			//entity.setContentType("application/json");
			
			HttpPost post = new HttpPost(loginLink);
			//post.setEntity(entity);
			
			List <NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("password",password));
			list.add(new BasicNameValuePair("user",username));
			list.add(new BasicNameValuePair("device", jsonObj.toString()));
			
			post.setEntity(new UrlEncodedFormEntity(list));
			
			CloseableHttpResponse response = httpclient.execute(post);
						
			jsonObj = new JSONObject(IOUtils.toString(response.getEntity().getContent()));
			System.out.println("SECRET KEY :" + jsonObj.getString("sec"));
			System.out.println("DEVICE ID :"+jsonObj.getString("dev"));
			
			JFileChooser upChooser = new JFileChooser();
			JFrame parent = new JFrame();
			int status = upChooser.showOpenDialog(upChooser);
			if(status == JFileChooser.APPROVE_OPTION)
			{
				File inputFile = upChooser.getSelectedFile();
				
				
				//File encrypted = File.createTempFile(upChooser.getSelectedFile().getName(),null);
				System.out.println("FILE SELECTED FOR UPLOAD:"+inputFile.getAbsolutePath());
				
				File encryptedTmpFile = new File("C:\\Users\\kshitij\\Desktop\\debug" + File.separator +"(encrypted)" +upChooser.getSelectedFile().getName());
				
				encryptedTmpFile.createNewFile();
			    if(encryptedTmpFile.exists())
			    {
			    	encryptedTmpFile.delete();
			    }
			    OutputStream outputStream = null;
			    InputStream inputStream = null;
			    int blockNum = 0;
			    String id = jsonObj.getString("dev");
			    try 
			    {
			    		outputStream = new FileOutputStream(encryptedTmpFile);
			    		inputStream = new FileInputStream(inputFile);

			            byte[] buffer = new byte[BLOCK_SIZE];
			            int readNumBytes;
			            int blockNumber = 0 ;
			            
			            while((readNumBytes = read(inputStream, buffer)) != -1)
			            {
			            	byte[] toEncryptBuffer = null;
			            	int toEncryptBufferSize;
			            	int paddingLength;
			            	if(readNumBytes == BLOCK_SIZE)
			            	{
			            		toEncryptBufferSize = BLOCK_SIZE + ENC_BLOCK_LEN;
			            		paddingLength  = ENC_BLOCK_LEN;
			            	
			            	}
			            	else
			            	{
			            		paddingLength = ENC_BLOCK_LEN - readNumBytes%ENC_BLOCK_LEN;
			            		toEncryptBufferSize = readNumBytes + paddingLength;
			            		
			            	}
			            	
			            	
			            	toEncryptBuffer = new byte[toEncryptBufferSize];
			            	
			            	//copying the original data read into the toEncryptBuffer
			            	for(int j = 0 ; j < readNumBytes ; j++)
			            	{
			            		toEncryptBuffer[j] = buffer[j];
			            	}
			            	
			            	//we need to add padding to the toEncryptBuffer to make it multiple of 16.
			            	for(int j = readNumBytes ; j < readNumBytes + paddingLength ; j++)
			            	{
			            		toEncryptBuffer[j] = (byte)paddingLength;
			            	}
			            	
			            	byte[] encryptedBytes = encrypt(toEncryptBuffer, getKey(jsonObj.getString("dev") ,blockNumber , jsonObj.getString("sec")));
			            	//writing the encrypted bytes to a local file.
			            	//outputStream.write(Base64.encodeBase64(encryptedBytes));
			            	outputStream.write((encryptedBytes));
			            	blockNumber++;
			            }
			            outputStream.close();
			            //code to decrypt file on th local drive.
			   
			            System.out.println("SELECTED FILE IS ENCRYPTED AND SAVED AS "+encryptedTmpFile.getAbsolutePath());
			            
			            //code to upload file 
			            
			            System.out.println();
			            System.out.println("***********UPLOADING ENCRYPTED FILE TO SERVER********************");
			            
			            String urlEncodeFileName = URLEncoder.encode(encryptedTmpFile.getName().substring(11), "UTF-8");
			            System.out.println("plain filename :"+encryptedTmpFile.getName());
		                
			            System.out.println("url encodeing :"+urlEncodeFileName);
			            
			            String base64FileName = Base64.encodeBase64String(urlEncodeFileName.getBytes());
			            System.out.println("base64 encoded :"+base64FileName);
			            
			            FileBody fileBody = new FileBody(encryptedTmpFile,ContentType.APPLICATION_OCTET_STREAM, jsonObj.getString("dev")+":"+base64FileName);
			            
			            	System.out.println("upload filename:"+jsonObj.getString("dev")+":"+base64FileName);
			               
			                 post = new HttpPost(encryUpLink);
					         
					         //post = new HttpPost("http://httpbin.org/post");
					         
					         post.setHeader("enctype", "multipart/form-data");
					         
					         MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
							 
					         multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
					         
							 multipartEntity.addPart("uploadfile", fileBody);
							 
							 //multipartEntity.addPart("filename", new StringBody( Base64.encodeBase64String( encryptedTmpFile.getName().getBytes() ),ContentType.DEFAULT_BINARY));
							 
							 post.setEntity(multipartEntity.build());
							 
							 response = httpclient.execute(post);
							 
							 BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
							    String line = "";
							    retVal = response.getStatusLine().toString();
							    while ((line = rd.readLine()) != null) 
							    {
							      System.out.println(line);
							    }
							  
			    } 
			    finally
			    {
			    	
			    }
			        
			}
		} 
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	public void testing(String loginLink, String username,String password, String device) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		String retVal = null;
		StringEntity entity;
		JSONObject jsonObj;
		try 
		{
			jsonObj = new JSONObject(device);
			

			System.out.println(jsonObj.toString());
			//entity.setContentType("application/json");
			
			HttpPost post = new HttpPost(loginLink);
			//post.setEntity(entity);
			
			List <NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("password",password));
			list.add(new BasicNameValuePair("user",username));
			list.add(new BasicNameValuePair("device", jsonObj.toString()));
			System.out.println(list);
			
			post.setEntity(new UrlEncodedFormEntity(list));
			
			CloseableHttpResponse response = httpclient.execute(post);
						
			jsonObj = new JSONObject(IOUtils.toString(response.getEntity().getContent()));
			System.out.println("secrete key :" + jsonObj.getString("sec"));
			System.out.println("status = "+response.getStatusLine());
			
			HttpGet getDownload = new HttpGet("https://192.168.9.90/download/l-1-3-2&-1");
			response = httpclient.execute(getDownload);
			
			InputStream rd = response.getEntity().getContent();
			
			rd = new FileInputStream(new File("C:\\Users\\kshitij\\Desktop\\debug\\(encrypted)20130814_23821.jpg"));
			
			int SIZE = 16 * 1024 + 16;
			FileOutputStream feos = new FileOutputStream(new File("C:\\Users\\kshitij\\Desktop\\debug"+File.separator +"(decrypted).jpg" ));
		    //System.out.println("file path to save:"+"C:\\Users\\kshitij\\Desktop"+File.separator + getDownloadFileName(response.getHeaders("Content-Disposition")[0]));
			
			byte[] buffer = new byte[SIZE];
			int readRetVal ;
			int blockNumber = 0;
			
			while((readRetVal = read(rd, buffer)) != -1)
			{
				System.out.println("read length:"+readRetVal);
				byte[] longKey = (jsonObj.getString("dev")+ blockNumber + jsonObj.getString("sec")).getBytes("UTF-8");
				
				byte[] shortKey = new byte[32];
				for(int i = 0 ; i < 32; i++)
				{
					shortKey[i] = longKey[i];
				}
				
		    	Cipher cipher = Cipher.getInstance("AES/ECB/NOPADDING");
		    	SecretKeySpec secKey = new SecretKeySpec(shortKey, "AES");
		    	cipher.init(Cipher.DECRYPT_MODE, secKey);
		    	byte[] decoded = new byte[readRetVal];
		    	
		    	decoded = cipher.doFinal(buffer);
		    	System.out.println("padding length:"+ ((decoded[readRetVal-1])));
		    	feos.write(decoded, 0, readRetVal - (decoded[readRetVal-1]));
		    	blockNumber++;
			}
		    feos.close();
			
			
			
		} 
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static byte[] encrypt(byte[] data, byte[] key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
	   {
	        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
	        Cipher cipher = Cipher.getInstance("AES/ECB/NOPADDING");
	       
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	        byte[] encrypted = cipher.doFinal(data);
	        return encrypted;
	    }
	
	
	public void tetsingEnDownload(String loginLink, String username,String password, String device, String downloadUrl) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, JSONException, ClientProtocolException, IOException
	{
		String retVal = null;
		StringEntity entity;
		JSONObject jsonObj;
		
			jsonObj = new JSONObject(device);
			
			
			HttpPost post = new HttpPost(loginLink);
			
			List <NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("password",password));
			list.add(new BasicNameValuePair("user",username));
			list.add(new BasicNameValuePair("device", jsonObj.toString()));
			
			post.setEntity(new UrlEncodedFormEntity(list));
			
			CloseableHttpResponse response = httpclient.execute(post);
						
			jsonObj = new JSONObject(IOUtils.toString(response.getEntity().getContent()));

			System.out.println("secrete key :" + jsonObj.getString("sec"));
			System.out.println("status = "+response.getStatusLine());
			
			HttpGet getDownload = new HttpGet(downloadUrl);
			
			
			
			String objectId = getDownloadObjectId(downloadUrl);
			if(objectId != null)
			{
				response = httpclient.execute(getDownload);
				
				InputStream rd = response.getEntity().getContent();
				
				
				//pritinh the headers
				Header[] headers  = response.getAllHeaders();
				for(int i = 0 ; i < headers.length; i++)
				{
					System.out.println("vdfgdfgvdfs************* value:"+headers[i].getName()+ "value :" +headers[i].getValue());
					
				}
				
				 File decryptedFile = new File("C:\\Users\\kshitij\\Desktop\\debug"+File.separator + getDownloadFileName(response.getHeaders("Content-Disposition")[0]));
				 
			     if(decryptedFile.exists())
			     {
			    	 decryptedFile.delete();
			     }
			     
			     
			     OutputStream decryptedStream = null;
			     InputStream  encryptedStream = null;
			     int blockNum = 0;
			     
			    	 decryptedStream = new FileOutputStream(decryptedFile);
			    	 encryptedStream = rd;
			    	 
			         byte[] buffer = new byte[BLOCK_SIZE+ENC_BLOCK_LEN];
			         int byteRead;
			         int blockNumber = 0; 
			         while((byteRead = read(encryptedStream, buffer)) != -1)
			         {
			        	 byte[] decryptedBytes;
			        	 byte[] key = getKey(objectId ,blockNumber,jsonObj.getString("sec"));
			        	 
			        	 if(byteRead == BLOCK_SIZE+ENC_BLOCK_LEN)
			        	 {
			        		 decryptedBytes = decrypt(buffer, key);
			        	 }
			        	 else
			        	 {
			        		 byte[] tmpEncryptedByte = cutShortByteArray(buffer, byteRead);
			        		 decryptedBytes = decrypt(tmpEncryptedByte, key);
			        	 }
			        	 
			        	 
			        	 //decryption of the blocks is done.
			        	 //now we need to remove padding
			        	 //padding is detected by reading the last byte of the decryptedBytes.The value represent the size of padding.
			        	 int  paddingLength = decryptedBytes[decryptedBytes.length-1];
			        	 
			        	 blockNumber++;
			        	// System.out.println("BLOCK LENGTH :"+decryptedBytes.length + " Padding length:"+paddingLength + " Total size downloaded:"+ (blockNumber *16400 + byteRead) /1024  + " KB");
				        System.out.println("Total bytes:"+(blockNumber *16400 + byteRead) + " Bytes");
			        	 //writing to the file. We need to exclude the padding from the last.
			        	 decryptedStream.write(decryptedBytes, 0, decryptedBytes.length - paddingLength);
			        	 
			         }
			         decryptedStream.close();
			         encryptedStream.close();

			}
			else
			{
				System.out.println("download link"+downloadUrl + " INVALID. It must have [\\w\\d]+-[\\w\\d]+-[\\w\\d]+-[\\w\\d]+ form");
			}
	}

	
	private String getDownloadObjectId(String downloadUrl) 
	{
		String retVal = null;
		Pattern pattern = Pattern.compile(FILE_ID_PATTERN);
		Matcher m = pattern.matcher(downloadUrl);
		if(m.find())
		{
			retVal = (String) downloadUrl.subSequence(m.start(), m.end());
		}
		return retVal;
		
	}
	private byte[] cutShortByteArray(byte[] buffer, int byteRead) {
		byte[] retVal = new byte[byteRead];
		for(int i = 0 ; i < byteRead; i++)
		{
			retVal[i] = buffer[i];
		}
		return retVal;
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
	private byte[] getKey(String fileId, int blockNumber, String orKey) 
	{
		
		byte[] longKey = (fileId + blockNumber + orKey).getBytes(); 
		byte[] retVal = new byte[KEY_SIZE];
		for(int i = 0 ; i < KEY_SIZE; i++)
		{
			retVal[i] = longKey[i];
		}
		return retVal ;
	}
	public void clientAu(String loginLink, String username,String password, String device) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		String retVal = null;
		StringEntity entity;
		JSONObject jsonObj;
		try 
		{
			jsonObj = new JSONObject(device);
			

			System.out.println(jsonObj.toString());
			//entity.setContentType("application/json");
			
			HttpPost post = new HttpPost(loginLink);
			//post.setEntity(entity);
			
			List <NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("password",password));
			list.add(new BasicNameValuePair("user",username));
			list.add(new BasicNameValuePair("device", jsonObj.toString()));
			System.out.println(list);
			
			post.setEntity(new UrlEncodedFormEntity(list));
			
			CloseableHttpResponse response = httpclient.execute(post);
						
			jsonObj = new JSONObject(IOUtils.toString(response.getEntity().getContent()));
			System.out.println("secrete key :" + jsonObj.getString("sec"));
			System.out.println("status = "+response.getStatusLine());
			
			HttpGet getDownload = new HttpGet("https://192.168.9.90/download/l-1-3-2&-1");
			response = httpclient.execute(getDownload);
			
			InputStream rd = response.getEntity().getContent();
			
			
			
			int SIZE = 16 * 1024 + 16;
			FileOutputStream feos = new FileOutputStream(new File("C:\\Users\\kshitij\\Desktop"+File.separator + getDownloadFileName(response.getHeaders("Content-Disposition")[0])));
		    //System.out.println("file path to save:"+"C:\\Users\\kshitij\\Desktop"+File.separator + getDownloadFileName(response.getHeaders("Content-Disposition")[0]));
			
			byte[] buffer = new byte[SIZE];
			int readRetVal ;
			int blockNumber = 0;
			
			while((readRetVal = read(rd, buffer)) != -1)
			{
				System.out.println("read length:"+readRetVal);
				byte[] longKey = ("l-1-3-2" + blockNumber + jsonObj.getString("sec")).getBytes("UTF-8");
				
				byte[] shortKey = new byte[32];
				for(int i = 0 ; i < 32; i++)
				{
					shortKey[i] = longKey[i];
				}
		    	Cipher cipher = Cipher.getInstance("AES/ECB/NOPADDING");
		    	SecretKeySpec secKey = new SecretKeySpec(shortKey, "AES");
		    	cipher.init(Cipher.DECRYPT_MODE, secKey);
		    	byte[] decoded = new byte[readRetVal];
		    	
		    	decoded = cipher.doFinal(buffer);
		    	System.out.println("padding length:"+ ((decoded[readRetVal-1])));
		    	feos.write(decoded, 0, readRetVal - (decoded[readRetVal-1]));
		    	blockNumber++;
			}
		    feos.close();
			
			
			
		} 
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String getDownloadFileName(Header header) 
	{
		Pattern r = Pattern.compile(FILENAME_PATTERN);
	    String  retVal = null;
	    Matcher m = r.matcher(header.getValue());
	    
	    if(m.find())
	    {
	    	//filename found 
	    	retVal = m.group(1);
	    	System.out.println("filename ="+retVal);
	   	}
	    else
	    {
	    	System.out.println("can not find file name");
	    }
		return retVal;
	}
	public String deviceAuthenticate(String loginLink, String username, String password, String device) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException 
	{
		String retVal = null;
		StringEntity entity;
		JSONObject jsonObj;
		try 
		{
			jsonObj = new JSONObject(device);
			

			System.out.println(jsonObj.toString());
			//entity.setContentType("application/json");
			
			HttpPost post = new HttpPost(loginLink);
			//post.setEntity(entity);
			
			List <NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("password",password));
			list.add(new BasicNameValuePair("user",username));
			list.add(new BasicNameValuePair("device", jsonObj.toString()));
			System.out.println(list);
			
			post.setEntity(new UrlEncodedFormEntity(list));
			
			CloseableHttpResponse response = httpclient.execute(post);
			jsonObj = new JSONObject(IOUtils.toString(response.getEntity().getContent()));
			System.out.println("secrete key :" + jsonObj.getString("sec"));
			System.out.println("status = "+response.getStatusLine());
			
			HttpGet getDownload = new HttpGet("https://192.168.9.90/download/1w-1-3-5-1");
			response = httpclient.execute(getDownload);
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    int line;
		    retVal = response.getStatusLine().toString();
		    long blockNumber = 0;
		    
		    char[] cbuf = new char[16 * 1024];
		    while ((line = rd.read(cbuf)) != -1) 
		    {
		    	
		    	System.out.println(line);
		    	String key = "1w-1-3-5-1" + blockNumber + jsonObj.getString("sec");
		  
		    	byte[] byteKey = key.getBytes();
		    	key = "";
		    	for(int i = 0 ; i < 32; i++)
		    	{
		    		key += byteKey[i];
		    	}
		    	
		    	 Cipher cipher = Cipher.getInstance("AES");
		    	System.out.println("Key:"+key);
				String encryptionKey = key;
				SecretKeySpec secKey = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
		    	
				cipher.init(Cipher.DECRYPT_MODE, secKey); 
				
				 String decoded = new String(cipher.doFinal(cbuf.toString().getBytes()),"UTF-8");
		    	System.out.println(decoded);
		    }
		    
			
			
			
			
		} 
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retVal;
	}
	/*
	 * authenticate			:	method to login into the external client link.
	 * Pre					:	External client has not logged in the external link.
	 * Post					:	External client has been successfully logged into the external link.ie cookies are initialised
	 * String 				:	Null if every thing is fine.Else a non null error is returned.
	 * NOTE					:	METHOD USED FOR ONLY DEBUGGING PURPOSES.THIS METHOD IS NOT REQUIRED 
	 * 								FOR DEPLOYMENT PURPOSES. 			
	 */
	public String externalClientAuthenticate()  
	{
	
		String retVal = null;
		if(httpclient != null)
		{
			if(!(loginLink == null || loginPassword == null))
			{
			
				HttpGet httpget = null;
				HttpResponse response1 = null;
				BufferedReader rd = null;
				String line = null;
				
				HttpPost post = new HttpPost(this.loginLink);
				
				List <NameValuePair> list = new ArrayList<NameValuePair>();
				
				list.add(new BasicNameValuePair("password",this.loginPassword));
				
				try 
				{
					post.setEntity(new UrlEncodedFormEntity(list));
					response1 = httpclient.execute(post);
				} 
				catch (UnsupportedEncodingException e) 
				{
					e.printStackTrace();
					retVal ="Error in authenticate"+ e.getMessage();
				} 
				catch (ClientProtocolException e) 
				{
					e.printStackTrace();
					retVal ="Error in authenticate"+ e.getMessage();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
					retVal ="Error in authenticate"+ e.getMessage();
				}
				
				
				
				try 
				{
					rd = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
					line = "";
				    
					while ((line = rd.readLine()) != null) 
					{
						System.out.println(line);
					}
				} 
				catch (IllegalStateException e) 
				{
					e.printStackTrace();
					retVal ="Error in authenticate: "+ e.getMessage();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
					retVal ="Error in authenticate: "+ e.getMessage();
				}
				
			}
			else
			{
				retVal = "Error in authenticate: login link or login password not initialised";
			}
		
		}
		else
		{
			retVal = "Error in authenticate: httpclient not initialised";
		}
		
		return retVal;
	}
	public String setInputStream(String downloadUrl)
	{
		String retVal = null;
		CloseableHttpResponse response = null;
		
		HttpGet httpget1 = new HttpGet(downloadUrl);
		
		try 
		{
			response = httpclient.execute(httpget1);
			Header[] headers = response.getHeaders("Content-Disposition");
			if(headers.length == 0)
			{
				retVal = "CONTENT_DISPOSITION_NOT_PRESENT";
				System.out.println("content disposition not preset");
			}
			else
			{
				
				//extracting the name of the file.
				Pattern r = Pattern.compile(FILENAME_PATTERN);
			    filename = null;
			    Matcher m = r.matcher(headers[0].toString());
			    
			    if(m.find())
			    {
			    	//filename found 
			    	filename = m.group(1);
			    	System.out.println("filename ="+filename);
			    	
			    	//assign in proper value which will be used by ConformSaveCancel and ConfirmSaveOk to get the data.
			    	this.in = (response.getEntity().getContent());
			    	
			    	//setting up the content length 
			    	contentLength = Long.valueOf(response.getHeaders("Content-Length")[0].getValue());
			   	}
			    else
			    {
			    	retVal = "Error in downloadForClient :Matcher cannot find the pattern. FILE PATTER = '"+FILENAME_PATTERN+"' and HEADER got = "+ headers[0].toString();
			    }
			}
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			retVal = "Error in downloadForClient:"+e.getMessage();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			retVal = "Error in downloadForClient:"+e.getMessage();
		}
		return retVal;

	}
	public String showSaveDialog(String downloadUrl)
	{
		String retVal = null;
		if(in == null)
		{
			retVal = setInputStream(downloadUrl);
		}
		
		if(retVal == null)
		{
			JFrame parentFrame = new JFrame();
	    	
	    	JFileChooser fileChooser = new JFileChooser();
			if(saveFile == null)
			{
				fileChooser .setSelectedFile(new File(filename));
			}
			else
			{
				fileChooser .setSelectedFile(saveFile);
			}
	    	
	    	
	    	int userSelection = fileChooser.showSaveDialog(parentFrame);
	    	
	    	if(userSelection == JFileChooser.APPROVE_OPTION)
	    	{
	    		this.saveFile = new File(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName());
	    		
	    		if(!saveFile.exists())
	    		{
	    			writeFileOnDisk(saveFile	,in);
						
					saveFile = null;
					in = null;
					filename = null;
					
	    		}
	    		else
	    		{
	    			confirmSaveFrame = new JFrame("Confirm Save As");
	    			confirmSaveFrame.setSize(400,400);
	    			confirmSaveFrame.setLayout(new GridLayout(3,1));
	    			confirmSaveFrame.add(new JLabel(fileChooser.getSelectedFile().getName() + " already present. Do you want to replace it?"));
	    			JButton submit = new JButton("Ok");
					JButton cancel = new JButton("Cancel");
					confirmSaveFrame.add(submit);
					confirmSaveFrame.add(cancel);
					confirmSaveFrame.setVisible(true);
					
					submit.addActionListener(new ConfirmSaveOk());
					cancel.addActionListener(new ConformSaveCancel());
	    		}
	    	}
		}
		return retVal;
	}
	
	public String uploadForClient()  
	{
		String retVal = null;
		
		//upload file
		JFileChooser chooser = new JFileChooser();
		
		
		JFrame parent =  new JFrame();
		int status = chooser.showOpenDialog(parent);
		if(status == JFileChooser.APPROVE_OPTION)
		{
			FileBody fileBody = new FileBody(chooser.getSelectedFile());
		    HttpPost post = new HttpPost(this.uploadLink);
		    post.setHeader("enctype", "multipart/form-data");
		    MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
		    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		    
		    multipartEntity.addPart("uploadfile", fileBody);
		    
		    post.setEntity(multipartEntity.build());
		    
		    HttpResponse response;
			try 
			{
				JFrame uploadFrame = new JFrame("Uploading...");
				uploadFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				uploadFrame.setBounds(0, 0, 300, 200);
				
				JProgressBar uploadProgressBar;
				uploadProgressBar = new JProgressBar(0,100);
				uploadProgressBar.setBounds(20, 20, 200, 30);
				uploadProgressBar.setValue(0);
				uploadProgressBar.setStringPainted(true);
				
				ProgressBarListener listener = new ProgressBarListener(uploadProgressBar, chooser.getSelectedFile().length());
			    FileEntityWithProgressBar fileEntity = new FileEntityWithProgressBar(chooser.getSelectedFile(), "binary/octet-stream", listener,this.bufferSize);
			    post.setEntity(fileEntity);
			    uploadFrame.add(uploadProgressBar);
			    uploadFrame.setVisible(true);
			    
				response = httpclient.execute(post);
				
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			    String line = "";
			    retVal = response.getStatusLine().toString();
			    while ((line = rd.readLine()) != null) 
			    {
			      System.out.println(line);
			    }
			    System.out.println("Uploaded");
			}
			catch (ClientProtocolException e) 
			{
				e.printStackTrace();
				retVal = "Error in uploadForClient" +e.getMessage();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				retVal = "Error in uploadForClient" +e.getMessage();
			}
		    
		    

		}

		return retVal;
	}
	
	public void writeFileOnDisk(File file, InputStream inStream)
	{
		FileOutputStream fos;
		try 
		{
			
			JFrame downloadFrame = new JFrame("Downloading...");
			downloadFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			downloadFrame.setBounds(0, 0, 300, 200);
			final JProgressBar progressBar = new JProgressBar(0,100);
			progressBar.setBounds(20, 20, 200, 30);
			progressBar.setValue(0);
		    progressBar.setStringPainted(true);
			IS_WRITE = true;
			
			downloadFrame.add(progressBar);
			downloadFrame.setVisible(true);
			System.out.println("confirm save ok.. writing now..");
			fos = new FileOutputStream(file);
			byte[] buffer = new byte[bufferSize];
	        int len1 = 0;
	        int i = 1;
	        System.out.println("content length :" + contentLength + " :Writing to disk now...");
	        
	        
	        int lenSum = 0;
	        
	        while ((len1 = in.read(buffer)) != -1) 
	        {
	                  fos.write(buffer, 0, len1);
	                  
	                  lenSum += len1;
	                  
	                  System.out.println("data read "+lenSum);
	                  
	                  final int tempLenSum = lenSum; 
	                  SwingUtilities.invokeLater(new Runnable() 
	                  {
	                      public void run() 
	                      {
	                         progressBar.setValue((int) (100 * tempLenSum/contentLength));
	                      }
	                   });
	        }
	        System.out.println("written done...");
	        fos.close();
	        
	        downloadFrame.dispose();
	        
	        
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}
	
	public class ConfirmSaveOk implements ActionListener 
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			saveFile.delete();
			
			writeFileOnDisk(saveFile, in);
			in = null;
		    saveFile = null;
		    filename = null;
		    confirmSaveFrame.dispose();
		}

	}
	
	public class ConformSaveCancel implements ActionListener 
	{

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			confirmSaveFrame.dispose();
			showSaveDialog(null);
		}

	}
}



