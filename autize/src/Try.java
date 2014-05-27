
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;


public class Try {
	static JFileChooser fileChooser;
	static File openFile;
	static JFrame replaceFrame;
	static JFrame parentFrame;
	static String uploadLink;
	static CloseableHttpClient httpclient;
	public static void uploadForClient() throws IOException
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
			System.out.println("File selected :"+ file.getName());
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
	public static void uploadForClient1() throws IOException
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
			HttpPost post = new HttpPost("https://192.168.9.163/shareupload/YWRzcXdAZXdmZXcuY29t");
			post.setHeader("enctype", "multipart/form-data");
			MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
		    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		    multipartEntity.addPart("uploadfile", fileBody);
		    post.setEntity(multipartEntity.build());
		    HttpResponse response = httpclient.execute(post);
		    System.out.println("upload status : " + response.getStatusLine());
		    
		    System.out.println("content type = :"+multipartEntity.build().getContentType());
			System.out.println("centen length = :"+multipartEntity.build().getContentLength());
		    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    String line = "";
		    
		    while ((line = rd.readLine()) != null) 
		    {
		      System.out.println(line);
		    }
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
	public static void main(String[] args) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, ClientProtocolException, IOException
	{
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
		httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		
		HttpGet httpget = new HttpGet("https://192.168.9.163/share/a-1-xIIhd4rXMYjBwJeqNu2_TQoEY6m9d+YWRzcXdAZXdmZXcuY29t");
		HttpResponse response1 = httpclient.execute(httpget);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
		String line = "";
		    
		while ((line = rd.readLine()) != null) 
		{
			System.out.println(line);
		}
		
		HttpPost post = new HttpPost("https://192.168.9.163/share/password/a-1-xIIhd4rXMYjBwJeqNu2_TQoEY6m9d+YWRzcXdAZXdmZXcuY29t");
		List <NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("password","88888888"));
		post.setEntity(new UrlEncodedFormEntity(list));
		response1 = httpclient.execute(post);
		 rd = new BufferedReader(new InputStreamReader(response1.getEntity().getContent()));
			line = "";
			    
			while ((line = rd.readLine()) != null) 
			{
				System.out.println(line);
			}
			
			//upload
			File image = new File("C:\\Users\\kshitij\\Desktop\\codes.txt");
		    FileBody fileBody = new FileBody(image);
		    post = new HttpPost("https://192.168.9.163/shareupload/YWRzcXdAZXdmZXcuY29t");
		    post.setHeader("enctype", "multipart/form-data");

		    MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
		    multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		    
		    multipartEntity.addPart("uploadfile", fileBody);
		    
		    post.setEntity(multipartEntity.build());
		    
		    System.out.println("content type = :"+multipartEntity.build().getContentType());
			
		    System.out.println("centen length = :"+multipartEntity.build().getContentLength());
			
		    HttpResponse response = httpclient.execute(post);
		    
		    rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		    line = "";
		    
		    while ((line = rd.readLine()) != null) 
		    {
		      System.out.println(line);
		    }
		    

		    if(image.exists())
		    {
		    	System.out.println("file exist");
		    }

		    httpclient.close();
		System.out.println("wefwfwfwf");
	}
	public static void main1(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException 
	{
		
		SSLContextBuilder builder = new SSLContextBuilder();
		builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER );
		httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		
		CloseableHttpResponse response2 = null; 
		
			HttpPost httpPost = null;
			List <NameValuePair> list = null;
			
			list = new ArrayList<NameValuePair>();
			httpPost = new HttpPost("https://192.168.9.163/share/password/a-1-xIIhd4rXMYjBwJeqNu2_TQoEY6m9d+YWRzcXdAZXdmZXcuY29t");
			
			
			try 
			{
				list.add(new BasicNameValuePair("password","88888888"));
				httpPost.setEntity(new UrlEncodedFormEntity(list));
				
				response2 = httpclient.execute(httpPost);
				
				BufferedReader rd = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
				String line = "";
				    
				while ((line = rd.readLine()) != null) 
				{
					System.out.println(line);
				}
				
				response2.close();
			} 
			catch (UnsupportedEncodingException e) 
			{
				e.printStackTrace();
			} 
			catch (ClientProtocolException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
	
		
		/*
		// TODO Auto-generated method stub
		 parentFrame = new JFrame();
		 
		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Specify a file to open");   
	
		
	
		
		int userSelection = 	fileChooser.showOpenDialog(parentFrame);
		
		if (userSelection == JFileChooser.APPROVE_OPTION) 
		{
			openFile = fileChooser.getSelectedFile();
			
			System.out.println("open file  " + openFile.getAbsolutePath());
		   
			fileChooser.setDialogTitle("Save as");
			
			
			
			fileChooser.setSelectedFile(new File(openFile.getName()));
			
			userSelection = fileChooser.showSaveDialog(parentFrame);
			
			if(userSelection == JFileChooser.APPROVE_OPTION)
			{
				File saveFile = new File(fileChooser.getCurrentDirectory() + File.separator + fileChooser.getSelectedFile().getName());
				if(saveFile.exists())
				{
					System.out.println("File already exists change the name");
					replaceFrame = new JFrame("Confirm Save As");
					replaceFrame.setSize(400,400);
					replaceFrame.setLayout(new GridLayout(3,1));
					
					replaceFrame.add(new JLabel(fileChooser.getSelectedFile().getName() + " already present.Do you want to replace it?"));
					JButton submit = new JButton("Ok");
					JButton cancel = new JButton("Cancel");
					
					replaceFrame.add(submit);
					replaceFrame.add(cancel);
					
					replaceFrame.setVisible(true);
					
					
					submit.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e) 
						{
							File file = new File(fileChooser.getCurrentDirectory()+File.separator + fileChooser.getSelectedFile().getName());
							file.delete();
							FileOutputStream fos = null;
							try {
								fos = new FileOutputStream(new File(fileChooser.getCurrentDirectory()+File.separator + fileChooser.getSelectedFile().getName()));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							 
							//BufferedReader reader = 
							BufferedReader in = null;
							try {
								in = new BufferedReader(new FileReader(openFile));
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
				              byte[] buffer = new byte[1024];
				              int len1 = 0;
				              try {
								while ((len1 = in.read()) != -1) {
								          try {
											fos.write(buffer, 0, len1);
										} catch (IOException e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
								  }
								
								
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

				              try {
								fos.close();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
				              
				            replaceFrame.dispose();
						}		
						
						
					});
					cancel.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent arg0) {
							replaceFrame.dispose();
							
						}});
					
					
					
				}
				else
				{
					System.out.println("File can be added");
					BufferedReader in = new BufferedReader(new FileReader(fileChooser.getCurrentDirectory()+File.separator + fileChooser.getSelectedFile().getName()));
					
				}
			}
						
			
		
		 }
		 */
	}

}
