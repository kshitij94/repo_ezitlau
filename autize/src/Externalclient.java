import java.applet.Applet;
import java.awt.GridLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.CookieManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;



public class Externalclient extends Applet{
	
	private CloseableHttpClient httpclient = null; 
	String debugMode = null;							/*debugMode is ON if debug and testing is on. Is OFF otherwise*/
	String loginLink = null;								/*value of action in submit form.This page corresponds to the password requirement when the external link is opened.*/
	String loginPassword = null;						/*Required only for debug purposes.*/
	String uploadLink = null;							/*variable to store upload link.Required for deployment. */
	
	
	String CLIENT_NOT_INIT = "CLIENT_NOT_INITIALISED";
	String FILENAME_PATTERN = "filename=[\"](.*)[\"]";
	String	 CONTENT_DISPOSITION_NOT_PRESENT = "CONTENT_DISPOSITION_NOT PRESENT";
	String FILE_NAME_NOT_FOUND = "FILE_NAME_NOT_FOUND_IN_DOWNLOAD";
	
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
			httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} 
		catch (NoSuchAlgorithmException e2) 
		{
			retVal = e2.getMessage();
		} 
		catch (KeyStoreException e2) 
		{
			retVal = e2.getMessage();
		} 
		catch (KeyManagementException e) 
		{
			retVal = e.getMessage();
		}
		return retVal;
	}
	
    /*
     * establishSecureConnection		:	Method to do handshake with the server to generate secrete session key.
     * Pre								:	Only one layer of encryption was provided by the HTTPS.
     * Post								:	Double layer excryption provided.One from HTTPS and one from the secrete session key.
     * 
     */
    String establishSecureConnection()
    {
    	String retVal = null;
    	return retVal;
    }
    
    /*
     * initDebugmode		:	method to initialize loginLink, **.
     * Uses					:	Only for debug mode. 
     */
    void initDebugmode(String loginLink, String loginPassword)
    {
    	this.loginLink = loginLink;
    	this.loginPassword = loginPassword;
    }
    
    /*
     * 
     */
    void setUploadLink(String uploadLink)
    {
    	this.uploadLink = uploadLink;
    }
    
    private String initialiseApplet(String loginLink, String password, String uploadLink)
    {
    	String retVal = null;
    	
    	this.loginLink = loginLink;
    	this.loginPassword = password;
    	this.uploadLink = uploadLink;
    	
    	disableSSL();
    	
    	System.out.println("RETURN VALUE OF AUTHENTICATE : "+authenticate());
    	return retVal;
    }
    
    /*
     * (non-Javadoc)
     * @see java.applet.Applet#init()
     * 
     */
	public void init(String debugMode)
	{
				
		authenticate();
		initialiseApplet("");

	
	}	  
	public void start()
	{
		
	}
	public void stop()
	{
		
	}
	
	private void printEntityContent(CloseableHttpResponse response)
	{
		
		
		if(response != null)
		{
			String line;
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
			line = "";
			
			while ((line = rd.readLine()) != null) 
			{
				System.out.println(line);
			}
			      
		}
	}
	
	/*
	 * authenticate			:	method to login into the external client link.
	 * Pre					:	External client has not logged in the external link.
	 * Post					:	External client has been successfully logged into the external link.ie cookies are initialised
	 * String 				:	Null if every thing is fine.Else a non null error is returned.
	 * NOTE					:	METHOD USED FOR ONLY DEBUGGING PURPOSES.THIS METHOD IS NOT REQUIRED 
	 * 								FOR DEPLOYMENT PURPOSES. 			
	 */
	public String authenticate()
	{

		if(debugMode == "ON")
		{
			System.out.println("INSIDE AUTHENTICATION...");
		}
		
		
		String retVal = null;
		if(httpclient != null)
		{
			if(!(loginLink == null || loginPassword == null))
			{
				CloseableHttpResponse response2 = null; 
			
				HttpPost httpPost = null;
				List <NameValuePair> list = null;
				
				list = new ArrayList<NameValuePair>();
				httpPost = new HttpPost(loginLink);
				
				
				try 
				{
					httpPost.setEntity(new UrlEncodedFormEntity(list));
					list.add(new BasicNameValuePair("password",loginPassword));
					response2 = httpclient.execute(httpPost);
					
					if(debugMode == "ON")
					{
						printEntityContent(response2);
						System.out.println("END OF AUTHENTICATION...");
					}
					
					response2.close();
				} 
				catch (UnsupportedEncodingException e) 
				{
					retVal = e.getMessage();
				} 
				catch (ClientProtocolException e) 
				{
					retVal = e.getMessage();
				} 
				catch (IOException e) 
				{
					retVal = e.getMessage();
				}
			}
		
		}
		else
		{
			retVal = CLIENT_NOT_INIT;
		}
		
		return retVal;
	}
	
	
	/*
	 * downloadForClient		:	method to download file for the external client.
	 */
	public String downloadForClient(String downloadUrl)
	{
		
		String retVal = null;
		CloseableHttpResponse response = null;
		      
		HttpGet httpget = new HttpGet(downloadUrl);
		
		try 
		{
			response = httpclient.execute(httpget);
			Header[] headers = response.getHeaders("Content-Disposition");
			if(headers.length == 0)
			{
				retVal = CONTENT_DISPOSITION_NOT_PRESENT;
			}
			else
			{
				
				//extracting the name of the file.
				Pattern r = Pattern.compile(FILENAME_PATTERN);
			    String filename = null;
			    Matcher m = r.matcher(headers[0].toString());
			    
			    if(m.find())
			    {
			    	//filename found 
			    	filename = m.group(1);
			    	if(debugMode == "ON")
			    	{
			    		System.out.println("The filename of downloaded file is:"+filename);
			    	}
			    	
			    }
			    else
			    {
			    	retVal = FILE_NAME_NOT_FOUND;
			    }
			    
				InputStream in = response.getEntity().getContent();
				
				File path = null;//new File();
				
				path.mkdirs();
			}
		} 
		catch (ClientProtocolException e) 
		{
			retVal = e.getMessage();
		} 
		catch (IOException e) 
		{
			retVal = e.getMessage();
		}
		
		        	        
		
	    
	  /*
	    
	    if (m.find( )) 
	    {
	    	
	        filename = m.group(1);
	    }
	    else 
	    {
	         filename = "myfile.txt";
	    }
	              
	              System.out.println(filename);
	              
	              
	              File file = new File(path, filename);
	              FileOutputStream fos = new FileOutputStream(file);

	              byte[] buffer = new byte[1024];
	              int len1 = 0;
	              while ((len1 = in.read(buffer)) != -1) {
	                      fos.write(buffer, 0, len1);
	              }

	              fos.close();
		
	         
				
			 // httpPost = new HttpPost("");
			 */ 
			
	              return null;

	}
	

	public void uploadForClient() throws IOException
	{
		
		System.out.println("INSIDE UPLOAD METHOD");
		
		JFileChooser chooser = new JFileChooser();
		JFrame parent = new JFrame();
		
		//FileNameExtensionFilter filter = new FileNameExtensionFilter( "JPG & GIF Images", "jpg", "gif");
		//chooser.setFileFilter(filter);
		
		int status = chooser.showOpenDialog(parent);
		if(status == JFileChooser.APPROVE_OPTION)
		{
			File file = chooser.getSelectedFile();
			FileBody fileBody = new FileBody(file);
			HttpPost post = new HttpPost(this.uploadLink);
			post.setHeader("enctype", "multipart/form-data");
			MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
		    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		    multipartEntity.addPart("uploadfile", fileBody);
		    post.setEntity(multipartEntity.build());
		    HttpResponse response = httpclient.execute(post);
		    System.out.println("upload status : " + response.getStatusLine());
		}
		/*
		File image = new File(filePath);
	    FileBody fileBody = new FileBody(image);

	   
	    HttpPost post = new HttpPost("https://192.168.9.163/shareupload/YWRzcXdAZXdmZXcuY29t");
	    post.setHeader("enctype", "multipart/form-data");

	    MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
	    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    multipartEntity.addPart("uploadfile", fileBody);
	    post.setEntity(multipartEntity.build());
	    
	    System.out.println("content type = :"+multipartEntity.build().getContentType());
		System.out.println("centen length = :"+multipartEntity.build().getContentLength());
		
	    HttpResponse response = httpclient.execute(post);
	    
	    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    String line = "";
	    
	    while ((line = rd.readLine()) != null) 
	    {
	      System.out.println(line);
	    }
	    */
	}
}



