public class Server extends UnicastRemoteObject implements Server_itf {
	HashMap<Integer, SharedObject_itf> objectIdRegister;
	HashMap<String, Integer> idNameRegister;

	public Server(){
		objectIdRegister = new HashMap<Integer, SharedObject_itf>();
		idNameRegister = new HashMap<String, Integer>();

	}


	public int lookup(String name) throws java.rmi.RemoteException{
		return idNameRegister.get(name);
	}

	public void register(String name, int id) throws java.rmi.RemoteException{
		idNameRegister.put(name,id);
		
		
	}

	public int create(Object o) throws java.rmi.RemoteException{
		ServerObject so = new ServerObject();
		int id = createId();
		objectIdRegister.put(id, so);
		return id;

	}

	public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException;
	public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException;

	public int createId(){//TO DO
		return 0;
	}
}