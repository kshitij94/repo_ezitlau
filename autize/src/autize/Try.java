
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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Try {
	static JFileChooser fileChooser;
	static File openFile;
	static JFrame replaceFrame;
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		JFrame parentFrame = new JFrame();
		 
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
	}

}
