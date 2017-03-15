/*
 * @author Vaishnavi
 * This class creates multi threaded server .
 * A new thread is created for each socket request from peers
 */
import java.net.*;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import java.io.*;


/**
 * @author vthangap
 *
 */
public class IndexServer{
	
	static {
        
	       final String propFile = choosePropertyFile();
	       System.setProperty("PropertyFile", propFile);
	        }
	
	
	
	public static void main (String args[])
	{
			
	
		 try{
		     
			int  serverPort = Integer.parseInt(getvalueFromProperty("serverPort").trim());	
			 ServerSocket s = new ServerSocket(serverPort);
			 Socket s1 ;
		      while(true){
		       
		    	  s1 = s.accept();
		    	
		        IndexRegisterOperate conn_c= new IndexRegisterOperate(s1);
		        Thread t = new Thread(conn_c);
		        t.start();
		        
		      }
		      
		    } 
		     catch (Exception exp) {
		      System.out.println(exp);
		    
		    }
	
		  

	}


	  /**Gets value from property file
	 * @param parameter
	 * @return
	 */
	static  String getvalueFromProperty(String parameter)
	  {
		try
		{
			String proploc  = System.getProperty("PropertyFile"); 
		Properties prop = new Properties();
		FileInputStream input= new FileInputStream(proploc);
		  
		prop.load(input);
	  	String value =  prop.getProperty(parameter);
	  	if(value.equals(null))
	  	{
	  		Exception ex1 = new Exception("Parameter value is null or not present in property file");
	  		throw ex1;
	  	}
	  	System.out.println("The value of "+parameter+ " is "+ value);
		return value;
		}
		catch(Exception ex)
		{
			 System.out.println("Error in retrieving "+ parameter+ " value from Property File!");
			 
			 //exiting program if right server port number not retrieved from property file
			 System.exit(0);
		}
	   return null;
	  }
 
	  /**
	   * File chooser to choose property file
	 * @return
	 */
	public static String choosePropertyFile()
  	{
				
	try{
  	JFrame parentFrame = new JFrame();
  	 
  	JFileChooser fileChooser = new JFileChooser();
  	fileChooser.setDialogTitle("Choose Property file for the Server");   
  	 
  	int userSelection = fileChooser.showSaveDialog(parentFrame);
  	 
  	if (userSelection == JFileChooser.APPROVE_OPTION) {
  	    File fileToSave = fileChooser.getSelectedFile();
  	    System.out.println("property file chosen is " + fileToSave.getAbsolutePath());
  	   return(fileToSave.getAbsolutePath()); 
  	   
  	}
	}
  	catch(Exception ex)
  	{
  		System.out.println("Propery file not chosen correctly!!!");
  		System.exit(0);
  	}
  	
  	return null;
  	}
	

	 
}