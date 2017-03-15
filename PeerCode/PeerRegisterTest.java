import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class PeerRegisterTest {
	static {
        
	       final String propFile = choosePropertyFile();
	       System.setProperty("PropertyFile", propFile);
	    }
	
	public static void main(String[] args)
	{
		
		PeerRegisterTest peerTest = new PeerRegisterTest();
		peerTest.createTextFiles();
		Peer peer=new Peer();
		peer.startClient();
		peer.startServer();
	}
	
	void createTextFiles()
	{
	BufferedWriter output = null;
	String dirLoc= getvalueFromProperty("FileCreationLoc");
	String filename=dirLoc+"test";
	int start = Integer.parseInt(getvalueFromProperty("FileCreationStartNuminName").trim());
	int end = Integer.parseInt(getvalueFromProperty("FileCreationEndNuminName").trim());
	  
	for(int i=start;i<end;i++)
	try {
		String t = String.valueOf(i);
		String text ="Ubuntu This is the first line of  file test"+i;
        File file = new File(filename+t+".txt");
        output = new BufferedWriter(new FileWriter(file));
        output.write(text);
    } catch ( IOException e ) {
        e.printStackTrace();
    } finally {
        if ( output != null ) 
        	try
        {
        	output.close();
        }
        catch(Exception ex)
        {
        	System.out.println(ex.getMessage());
        }
    }
	
	}
	

	  static  String getvalueFromProperty(String parameter)
	  {
		try
		{
			String proploc  = System.getProperty("PropertyFile"); 
		Properties prop = new Properties();
		FileInputStream input= new FileInputStream(proploc);
		  
		prop.load(input);
	  	String value =  prop.getProperty(parameter);
		return value;
		}
		catch(Exception ex)
		{
			System.out.println("Error in retrieving value from Property File!!");
		}
	    return null;
	  }
 
	  public static String choosePropertyFile()
  	{
		try{
			
		
  	JFrame parentFrame = new JFrame();
  	 
  	JFileChooser fileChooser = new JFileChooser();
  	fileChooser.setDialogTitle("Choose Property file for the client");   
  	 
  	int userSelection = fileChooser.showSaveDialog(parentFrame);
  	 
  	if (userSelection == JFileChooser.APPROVE_OPTION) {
  	    File fileToSave = fileChooser.getSelectedFile();
  	    System.out.println("property file chosen is " + fileToSave.getAbsolutePath());
  	   return(fileToSave.getAbsolutePath()); 
  	   
  	   
  	}
  	}
	  catch(Exception ex)
	  {
		 
  	System.out.println("Exception occured while choosing property file !! please upload the correct");
  	
	  }
		return null;
  	}
	
	}

	

