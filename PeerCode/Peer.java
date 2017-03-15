/*@author Vaishnavi Thangapalam
 * Peer.java is run by the peer
 * The main() initiates a thread for client and another thread for the peer to act as Server
 * You need to choose a property file which has the index server's IP
 * */

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Peer {
	static {

		final String propFile = choosePropertyFile();
		System.setProperty("PropertyFile", propFile);
	}
	public static void main(String[] args) throws IOException {

		startServer();
		startClient();
	}

	//method to start a thread for Peer to act as a client
	public static void startClient() {
		(new Thread() {
			@Override
			public void run() {
				try
				{

					Peer cli = new Peer();
					String serverIp= getvalueFromProperty("serverIP");
					int serverPort = Integer.parseInt(getvalueFromProperty("serverPort").trim());
					Socket s1= null;
					try
					{
						s1 = new Socket(serverIp,serverPort);
					}
					catch(Exception ex)
					{
						System.out.println("exp "+ex.getMessage());
					}
					DataInputStream din = new DataInputStream(s1.getInputStream());
					DataOutputStream dos = new DataOutputStream(s1.getOutputStream());
					ObjectInputStream objin = new ObjectInputStream(s1.getInputStream());

					//used for exit condition of client peer
					int exitcode=0;
					while(exitcode==0)
					{

						System.out.println(din.readUTF());
						System.out.println(din.readUTF());
						Scanner sc = new Scanner(System.in);
						int choice = sc.nextInt();
						dos.writeInt(choice);
						long startTime = System.nanoTime();

						switch(choice)
						{
						// case 1 registers the files in the given shared location
						case 1:
							System.out.println(din.readUTF(din));
							String sharedFilesLoc = getvalueFromProperty("sharedFilesLocation");
							File[] fileList = cli.getListOfFiles(sharedFilesLoc);
							cli.sendIp(s1,dos);
							int clientPort = Integer.parseInt(getvalueFromProperty("clientPortNum").trim());
							cli.sendPortNumberOfPeer(dos,clientPort);
							ObjectOutputStream outToClient = new ObjectOutputStream(s1.getOutputStream());
							outToClient.writeObject(fileList);
							//writng file location
							dos.writeUTF(sharedFilesLoc);

							System.out.println(din.readUTF(din));


							break;

							//case 2 Downloads the file which the user has entered note: exact file name is to be entered

						case 2:

							System.out.println("Please enter the file name you want to download");
							String filetoDownload = sc.next();
							dos.writeUTF(filetoDownload);
							int res_temp = din.readInt();

							if(res_temp == -1)
							{
								System.out.println(" Sorry the file "+filetoDownload+" is not found!!!");
								break;
							}

							LinkedHashSet<String> fileLoc = (LinkedHashSet<String>) objin.readObject();
							Iterator itr = fileLoc.iterator();

							while (itr.hasNext()){
								System.out.println(itr.next());
							}
							cli.downloadFile(fileLoc,filetoDownload);
							break;

							//case 3 the client code exited when 3 is entered by user		
						case 3:
							exitcode=1;
							break;

						default:
							System.out.println(din.readUTF(din));
							System.out.println();
							break;

						}	
						long endTime = System.nanoTime();
						long duration = (endTime - startTime);
						System.out.println("response time for the request!!!" + duration);

					}
					din.close();
					dos.close();
					s1.close();
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
		}).start();
	}

	// method to start thread for peer to act as Server
	public static void startServer() {
		(new Thread() {
			@Override
			public void run() {

				try{
					int clientPort = Integer.parseInt(getvalueFromProperty("clientPortNum"));
					ServerSocket s = new ServerSocket(clientPort);
					Socket s1 ;
					while(true){
						s1 = s.accept();
						PeerServer conn_c= new PeerServer(s1);
						Thread t = new Thread(conn_c);
						t.start();


					}

				} 
				catch (IOException ioe) {
					System.out.println("IOException on socket listen: " + ioe);
					ioe.printStackTrace();
				}

			}
		}).start();
	}

	/**sends he peers ip to server
	 * @param s1
	 * @param dout
	 * @return
	 * @throws Exception
	 */
	String sendIp(Socket s1, DataOutputStream dout) throws Exception
	{	
		InetAddress Ip;
		Ip =s1.getLocalAddress();
		String clientIp = Ip.getHostAddress();

		System.out.println("Ip address of the client to be registered "+ Ip);
		dout.writeUTF(clientIp);
		return clientIp;
	}

	/**gets the list of files in the shared folder location
	 * @param path
	 * @return
	 */
	File[] getListOfFiles(String path)
	{

		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println("File " + listOfFiles[i].getName());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}
		return listOfFiles;
	}



	/**sends peers port number to server
	 * @param dout
	 * @param portNum
	 * @throws Exception
	 */
	void sendPortNumberOfPeer(DataOutputStream dout, int portNum) throws Exception
	{


		dout.writeInt(portNum);


	}


	/**Sends he list of file names in the shared folder location of peer
	 * @param s
	 * @param filenames
	 * @throws Exception
	 */
	void sendFileNames(Socket s,File[] filenames) throws Exception

	{
		ObjectOutputStream obj = new ObjectOutputStream(s.getOutputStream());
		obj.writeObject(filenames);
		obj.flush();

	}


	/**
	 * @param soc
	 * @param regClient
	 * @throws Exception
	 */
	void sendKeyToServer(Socket soc, HashMap<ArrayList ,String> regClient) throws Exception

	{

		ObjectOutputStream objout = new ObjectOutputStream(soc.getOutputStream());
		objout.writeObject(regClient);

	}

	/**function downloads the file from the peer , id of which was sent by the index server
	 * @param fileLoc
	 * @param file_name
	 */
	void downloadFile(LinkedHashSet<String> fileLoc, String file_name)
	{
		DataInputStream din =null;
		DataOutputStream dos = null;
		InputStream is= null;
		FileOutputStream fos=null;
		BufferedOutputStream bos=null;

		try{
			//location of download
			System.out.println("Into download function");
			String fileDownloadLoc = getvalueFromProperty("fileDownloadLocation");
			String  fileloc = fileDownloadLoc+file_name;
			String[] strArr = new String[fileLoc.size()];
			fileLoc.toArray(strArr);

			System.out.println("Enter index number the link from where you want to download the file");
			for(int i =0;i<strArr.length;i++)
			{
				System.out.println("Index:"+i+". "+strArr[i]);
			}
			Scanner sc_obj = new Scanner(System.in);
			int index_fileLink = sc_obj.nextInt();

			if(strArr[index_fileLink]==null)
			{                                                                                                                                                                                                                                     
				//invalid condition

			}
			String loc = strArr[index_fileLink];
			String delims = "[|]";
			String[] splitStrings = loc.split(delims);
			String ip = splitStrings[0];
			int port = Integer.parseInt(splitStrings[1]);
			String filepath = splitStrings[2];

			System.out.println("IP chosen "+ip);
			System.out.println("Port "+port);
			Socket socket = new Socket(ip, port);
			System.out.println(socket.isConnected());
			try {

				System.out.println("starting download of file "+file_name);

				din = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(file_name);
				dos.writeUTF(filepath);

				//download
				byte[] aByte = new byte[1];
				int bytesRead;

				is = socket.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				if (is != null) {


					fos = new FileOutputStream(fileloc);
					bos = new BufferedOutputStream(fos);
					bytesRead = is.read(aByte, 0, aByte.length);

					do {
						baos.write(aByte);
						bytesRead = is.read(aByte);
					} while (bytesRead != -1);

					bos.write(baos.toByteArray());

					bos.flush();
					File filedownload = new File(fileloc);
					long len = filedownload.length();
					System.out.println("File "+file_name+ " "+len+"bytes download complete");
				} 

			}catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();

			}
			finally
			{
				din.close();
				dos.close();
				is.close();
				fos.close();
				bos.close();
			}

			socket.close();  
		}
		catch(Exception ex)
		{
			System.out.println("Exception when downloading file");
			System.out.println(ex);
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
