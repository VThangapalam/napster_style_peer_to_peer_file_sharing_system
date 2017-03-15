/*
 * @author Vaishnavi Thangapalam
 * This class has run method which keeps the server socket open for the peers.
 *  
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;


/**
 * @author vthangap
 *
 */
class IndexRegisterOperate implements Runnable {
	private Socket server;




	static HashMap<String,LinkedHashSet<String>> map1 = new HashMap<>();


	IndexRegisterOperate (Socket server) {
		this.server=server;
	}

	public void run () {


		try {



			DataOutputStream dos = new DataOutputStream(server.getOutputStream());
			DataInputStream din = new DataInputStream(server.getInputStream());
			ObjectOutputStream objout = new ObjectOutputStream(server.getOutputStream());

			int exitcode=0;
			while(exitcode==0)
			{
				dos.writeUTF("hello welcome to Registry server");
				dos.writeUTF("Please enter 1. to register the peer 2.to download a file 3. To quit");
				int userChoice =  din.readInt();
				System.out.println(userChoice+" userChoice!");


				switch (userChoice)
				{
                // Accept the list of filenames from peer to register and stores it in a HashMap
				case 1:
					dos.writeUTF("You want to register the peer ...please wait");
					String Ip = din.readUTF();
					System.out.println(Ip);
					int port = getPortNumber(din);      
					File[] files = getFileNameList(server);
					String sharedLocPath = din.readUTF();

					for(int j=0;j<files.length;j++)
					{

						if(map1.containsKey(files[j].getName()))
						{
							System.out.println(files[j].length());
							LinkedHashSet<String> temp = map1.get(files[j].getName());
							temp.add(Ip+"|"+port+"|"+sharedLocPath);
							map1.put(files[j].getName(), temp);

						}
						else
						{

							LinkedHashSet<String> ip = new LinkedHashSet<>();
							ip.add(Ip+"|"+port+"|"+sharedLocPath);
							map1.put(files[j].getName(), ip);

						}
					}



					for (Map.Entry<String, LinkedHashSet<String>> entry : map1.entrySet())
					{
						String key = entry.getKey().toString();;
						LinkedHashSet<String> value = entry.getValue();
						System.out.println("key, " + key + " value " + value );
					}


					dos.writeUTF("registration complete");


					break;

				case 2:
                    //accepts requests from peer to lookup for a file and send the peer id which have the file to the peer which requested it
					String searchName = din.readUTF();
					System.out.println("name to be searched "+searchName);
					System.out.println(searchName); 
					if(!map1.containsKey(searchName))
					{
						dos.writeInt(-1);
						break;
					}
					else
					{
						dos.writeInt(2);

						LinkedHashSet<String> searchResult = map1.get(searchName);
						objout.writeObject(searchResult);

					}
					break;
                //user wants to terminate the transaction
				case 3:
					exitcode=1;
					return;
					//break;

				default :
					dos.writeUTF("invalid number entered");
					break;
				}

			}
			dos.close();
			din.close();     

			server.close();
		} catch (Exception ioe) {
			System.out.println("IOException on socket listen: " + ioe);
			ioe.printStackTrace();
		}



	}


	/**
	 * Gets the list of file names 
	 * @param server
	 * @return File[] 
	 * @throws Exception
	 */
	File[] getFileNameList(Socket server)throws Exception
	{
		System.out.println("into get file function");

		ObjectInputStream objin = new ObjectInputStream(server.getInputStream());
		File[] fi = (File[]) objin.readObject();

		System.out.println(fi.length);
		for (int i =0;i<fi.length;i++)
		{
			System.out.println(fi[i].getName());
		}
		System.out.println("end of get file function");
		return fi;


	}
	

	/**Returns the port number received from client 
	 * @param ds
	 * @return
	 * @throws Exception
	 */
	int getPortNumber(DataInputStream ds) throws Exception
	{
		int portcli = ds.readInt();
		System.out.println("The port number that the client has sent to server for future request "+portcli);
		return portcli;


	}


}