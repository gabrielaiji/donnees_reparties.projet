import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server extends UnicastRemoteObject implements Server_itf {
	public HashMap<Integer, ServerObject> id_to_Objects;
	public HashMap<String, Integer> name_to_Id;
	public int idCompteur;

	private static final Boolean affiche = false;

	public Server() throws RemoteException{
		id_to_Objects = new HashMap<Integer, ServerObject>();
		name_to_Id = new HashMap<String, Integer>();
		idCompteur = 0;

	}

	public int lookup(String name) throws java.rmi.RemoteException{
		if(name_to_Id.containsKey(name)){
			return name_to_Id.get(name);
		}
		else{
			return  -1;
		}
	}

	public void register(String name, int id) throws java.rmi.RemoteException{
		System.out.println("registering object "+id);
		if(lookup(name) == -1){
			name_to_Id.put(name,id);
		}
	}

	public int create(Object o) throws java.rmi.RemoteException{
		int id = createId();
		ServerObject so = new ServerObject(id, o, this);
		id_to_Objects.put(id, so);
		return id;
	}

	public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException{
		if(affiche){
			System.out.println("Received lock_read request of "+id);
		}
		if(! id_to_Objects.containsKey(id)){
			System.err.println("id n'existe pas (lock_read)");
		}

		ServerObject server_object = id_to_Objects.get(id);
		Object obj =  server_object.lock_read(client);

		return obj;
	}


	public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException{
		if(affiche){
			System.out.println("Received lock_write request of "+id);
		}
		if(! id_to_Objects.containsKey(id)){
			System.err.println("id n'existe pas (lock_write)");
		}

		ServerObject server_object = id_to_Objects.get(id);
		Object obj = server_object.lock_write(client);

		return obj;
	}

	public int createId(){
		return idCompteur++;
	}

	public Object getServerVersionObject(int id){
		if(! id_to_Objects.containsKey(id)){
			System.err.println("id n'existe pas (getServerVersionObject)");
		}
		ServerObject server_object = id_to_Objects.get(id);
		return server_object.object;
	}

	public static void main(String[] args){
		try {
			System.out.println("Launch of Server");

			int port = 4000;
			Server server = new Server();
			Registry registry = LocateRegistry.createRegistry(port);
			
			Naming.rebind("//localhost:"+port+"/nameserver", server);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
