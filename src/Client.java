import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.rmi.registry.*;
import java.rmi.Naming;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {

	public static HashMap<Integer, SharedObject> id_to_Objects;
	//public static HashMap<String, SharedObject> name_to_Objects;
	public static Server_itf server;
	public static Client client;
	//TODO :remove
	public static String name;
	private static final Boolean affiche = false;

	public Client() throws RemoteException {
		super();
	}

	public String getName() throws java.rmi.RemoteException{
		return name;
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init(String n) {
		try{
			client = new Client();
			connectToServer();
			id_to_Objects = new HashMap<Integer, SharedObject>();
			name = n;
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {

		try{
			int id = server.lookup(name);
			if (id != -1){
				Object o = server.lock_read(id, client);
				SharedObject so = new SharedObject(client, id, o);
				so.unlock();
				
				id_to_Objects.put(id, so);
				return so;
			}
			else{
				if(affiche){
					System.out.println(name + " does not exits");
				}
				return null;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(affiche){
			System.out.println("lookup failed !");
		}
		return null;

	}		
	
	// binding in the name server
	public static void register(String name, SharedObject so) {// Pk le SharedObject et pas l'obj directement ?
		try{
			//int id = server.lookup(name);
			//SharedObject sharedObj = so;
			int id = so.id;

			server.register(name, id);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		try{
			int id = server.create(o);
			SharedObject so = new SharedObject(client, id, o);
			id_to_Objects.put(id, so);
			//TODO descripteur : en fait non ?
			return so;
		}catch(Exception e){
			e.printStackTrace();
		}
		//TODO exceptions
		return null;
	}

	public static void connectToServer(){
		int port = 4000;
		try {
			server = (Server_itf) Naming.lookup("//localhost:"+port+"/nameserver");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		SharedObject sharedObj = id_to_Objects.get(id);
		Object obj = sharedObj.obj;
		if(affiche){
			System.out.println("\nRequesting lock_read for object " +id);
		}
		try{
			obj = server.lock_read(id, client);
			if(affiche){
				System.out.println("Request finished correctly\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return obj;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		if(affiche){
			System.out.println("\nRequesting lock_write for object " +id);
		}
		SharedObject sharedObj = id_to_Objects.get(id);
		Object obj = sharedObj.obj;
		try{
			obj = server.lock_write(id, client);
			if(affiche){
				System.out.println("Request finished correctly\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return obj;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		if(affiche){
			System.out.println("Received lock_reduction for object " +id);
		}
		SharedObject sharedObj = id_to_Objects.get(id);
		sharedObj.reduce_lock();
		return sharedObj.obj;
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		if(affiche){
			System.out.println("Received invalidate_reader for object " +id);
		}
		SharedObject sharedObj = id_to_Objects.get(id);
		sharedObj.invalidate_reader();
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		if(affiche){
			System.out.println("Received invalidate_writer for object " +id);
		}
		SharedObject sharedObj = id_to_Objects.get(id);
		sharedObj.invalidate_writer();
		return sharedObj.obj;
	}
}
