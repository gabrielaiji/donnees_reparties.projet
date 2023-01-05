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

	public Client() throws RemoteException {
		super();
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		try{
			client = new Client();
			connectToServer();
			id_to_Objects = new HashMap<Integer, SharedObject>();
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
				return id_to_Objects.get(id);
			}
			else{
				return null;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;

	}		
	
	// binding in the name server
	public static void register(String name, SharedObject so) {
		try{
			int id = server.lookup(name);
			SharedObject sharedObj = so;
			if(id == -1){
				id = server.create(sharedObj.obj);
			}

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
			//TODO descripteur
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
		try{
			server.lock_read(id, client);
		}catch(Exception e){
			e.printStackTrace();
		}
		SharedObject sharedObj = id_to_Objects.get(id);
		return sharedObj.etat;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		try{
			server.lock_write(id, client);
		}catch(Exception e){
			e.printStackTrace();
		}
		SharedObject sharedObj = id_to_Objects.get(id);
		return sharedObj.etat;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		SharedObject sharedObj = id_to_Objects.get(id);
		sharedObj.reduce_lock();
		return sharedObj.etat;
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		SharedObject sharedObj = id_to_Objects.get(id);
		sharedObj.invalidate_reader();
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		SharedObject sharedObj = id_to_Objects.get(id);
		sharedObj.invalidate_writer();
		return sharedObj.etat;
	}
}
