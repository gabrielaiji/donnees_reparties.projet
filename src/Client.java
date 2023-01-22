import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.rmi.Naming;
import java.lang.reflect.Constructor;

public class Client extends UnicastRemoteObject implements Client_itf {

	public static HashMap<Integer, SharedObject_itf> id_to_Objects;
	public static Server_itf server;
	public static Client client;

	private static final Boolean affiche = false;

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
			id_to_Objects = new HashMap<Integer, SharedObject_itf>();
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
				SharedObject so = create_stub(client, id, o);
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
	public static void register(String name, SharedObject_itf so) {// Pk le SharedObject et pas l'obj directement ?
		try{
			int id = ((SharedObject) so).id;

			server.register(name, id);

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		try{
			int id = server.create(o);
			SharedObject so = create_stub(client, id, o);
			id_to_Objects.put(id, so);

			return so;
		}catch(Exception e){
			e.printStackTrace();
		}

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
		if(affiche){
			System.out.println("\nRequesting lock_read for object " +id);
		}
		try{
			Object obj = server.lock_read(id, client);
			if(affiche){
				System.out.println("Request finished correctly\n");
			}
			return obj;
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Request lock_read for object " +id+" failed\n");
		return null;
	}

	// request a write lock from the server
	public static Object lock_write (int id) {
		if(affiche){
			System.out.println("\nRequesting lock_write for object " +id);
		}
		try{
			Object obj = server.lock_write(id, client);
			if(affiche){
				System.out.println("Request finished correctly\n");
			}
			return obj;
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("Request lock_write for object " +id+" failed\n");
		return null;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		if(affiche){
			System.out.println("Received lock_reduction for object " +id);
		}
		SharedObject sharedObj = (SharedObject) id_to_Objects.get(id);
		sharedObj.reduce_lock();
		return sharedObj.obj;
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		if(affiche){
			System.out.println("Received invalidate_reader for object " +id);
		}

		if (id_to_Objects.containsKey(id)){
			SharedObject sharedObj = (SharedObject) id_to_Objects.get(id);
			sharedObj.invalidate_reader();
		}
		else if (affiche){
			System.out.println("object "+id+" still not created. invalidate_reader validated.");
		}
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		if(affiche){
			System.out.println("Received invalidate_writer for object " +id);
		}
		SharedObject sharedObj = (SharedObject) id_to_Objects.get(id);
		sharedObj.invalidate_writer();
		return sharedObj.obj;
	}

	// create stub
	public static SharedObject create_stub(Client client, int id, Object o){
		try{
			Class<?> classe = o.getClass();
			String nomStub = classe.getName() + "_stub";
			Constructor<?> constructeur = Class.forName(nomStub).getConstructors()[0];
			SharedObject so = (SharedObject) constructeur.newInstance(client, id, o);
			return so;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}
