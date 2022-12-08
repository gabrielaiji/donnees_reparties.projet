import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SharedObject extends UnicastRemoteObject implements Serializable, SharedObject_itf {
	public EtatLockClient etat;
	public Object obj;
	public int id;
	public Client client;

	public SharedObject(Client client, int id, Object object) throws RemoteException{
		this.obj = object;
		this.id = id;
		this.client = client;
	}

	// invoked by the user program on the client node
	public void lock_read() {
		try{
			switch(etat){
				case NL:
					Client.lock_read(id);
				case RLC:
					etat = EtatLockClient.RLT;
					break;
				case WLC:
					etat = EtatLockClient.RLT_WLC;
					break;
				default:
					//TODO
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		switch(etat){

			case NL:
			case WLC:
			case RLC:
				etat = EtatLockClient.WLT;
				break;
			default:
				//TODO

		}
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		switch(etat){

			case WLT:
			case RLT_WLC:
				etat = EtatLockClient.WLC;
				break;
			case RLT:
				etat = EtatLockClient.RLC;
				break;
			default:
				//TODO

		}
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		switch(etat){

			case WLT:
				etat = EtatLockClient.RLC;
				break;
			case RLT_WLC:
				etat = EtatLockClient.RLT;
				break;
			default:
				//TODO
		}
		return obj; //A VERIF
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		switch(etat){

			case RLC:
			case RLT:
				etat = EtatLockClient.NL;
				break;
			default:
				//TODO
		}
	}

	public synchronized Object invalidate_writer() {
		switch(etat){

			case WLC:
			case WLT:
			case RLT_WLC:
				etat = EtatLockClient.NL;
				break;
			default:
				//TODO
		}
		return obj;
	}
}
