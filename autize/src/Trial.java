import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class Trial extends Applet {

	public void writeToFile(String data, String filename) throws IOException
	{
		
	}
	public void test()
	{
			Graphics g = getGraphics();
		  int width = getSize().width;
	      int height = getSize().height;
	      setBackground( Color.black );
	      
	      
	      
	      g.setColor( Color.green );
	      for ( int i = 0; i < 10; ++i ) {
	         g.drawLine( width, height, i * width / 10, 0 );
	      }
	}
	
	public void makefile1(String filename) 
	{	
			List<String> l = null;
			Iterator<String> it = l.iterator();
	
			try
			{
				System.out.println("in ***************** end");
				System.out.println("creating file  : " + "C:\\Users\\kshitij\\git\\qwer\\autize\\bin" + File.separator +filename);
				File file = new File("C:\\Users\\kshitij\\git\\qwer\\autize\\bin" + File.separator +filename);
				file.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.out.println("ERROR : " + e.getMessage());
			}
			catch(SecurityException e)
			{
				System.out.println("ERROR : "+ e.getMessage());
			}
	}
}
