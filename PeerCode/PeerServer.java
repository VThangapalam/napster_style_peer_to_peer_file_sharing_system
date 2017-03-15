/*
 * @author vaishnavi Thangapalam
 * This is fike implements the server side code for the peer
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;



class PeerServer implements Runnable {
	private Socket peerserver;


	PeerServer(Socket server) {
		this.peerserver=server;
	}

	public void run () {
		DataOutputStream dos=null;
		DataInputStream din=null;
		try{
			dos = new DataOutputStream(peerserver.getOutputStream());
			din = new DataInputStream(peerserver.getInputStream());
			BufferedOutputStream outToClient = new BufferedOutputStream(peerserver.getOutputStream());

			String fileName = din.readUTF();
			String filepath = din.readUTF();
			System.out.println("fileName sent in req by client " + fileName+" in the location " + filepath );
			
			String sourceFilePath = filepath+fileName;



			if (outToClient != null) {
				File myFile = new File (sourceFilePath);

				byte[] mybytearray = new byte[(int) myFile.length()];

				FileInputStream fis = null;

				try {
					fis = new FileInputStream(myFile);
				} catch (FileNotFoundException ex) {
					// Do exception handling
				}
				BufferedInputStream bis = new BufferedInputStream(fis);

				try {
					bis.read(mybytearray, 0, mybytearray.length);
					outToClient.write(mybytearray, 0, mybytearray.length);
					outToClient.flush();
					outToClient.close();
					peerserver.close();

					// File sent, exit the main method
					return;
				} catch (IOException ex) {
					// Do exception handling
				}
			}




		} catch (Exception e) {
			e.printStackTrace();
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

