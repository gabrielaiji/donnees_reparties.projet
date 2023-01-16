import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
// utiliser en plus  java.util.concurrent.locks.Condition; ??

public class SharedObject extends UnicastRemoteObject implements Serializable, SharedObject_itf {
	public EtatLockClient etat;
	public Object obj;
	public int id;
	public Client client;
	
	public Lock moniteurWrite;
	public Lock moniteurRead;

	public SharedObject(Client client, int id, Object object) throws RemoteException{
		this.obj = object;
		this.id = id;
		this.client = client;
		this.etat = EtatLockClient.NL;

		this.moniteurWrite = new ReentrantLock();
		this.moniteurRead = new ReentrantLock();
	}

	public SharedObject(Client client, int id) throws RemoteException{
		this.id = id;
		this.client = client;
		this.etat = EtatLockClient.NL;

		this.moniteurWrite = new ReentrantLock();
		this.moniteurRead = new ReentrantLock();
	}

	// invoked by the user program on the client node
	public void lock_read() {
		
		try{
			switch(etat){
				case NL:
					this.obj = Client.lock_read(id);
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
				this.obj = Client.lock_write(id);
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
				this.notify();
				break;
			case RLT:
				etat = EtatLockClient.RLC;
				this.notify();
				break;
			default:
				System.out.println("unlock etat illogique : " +etat.toString());

		}
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		switch(etat){

			case WLC:
				etat = EtatLockClient.RLC;
				break;
			case WLT:
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				etat = EtatLockClient.RLC;
				break;
			case RLT_WLC:
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				etat = EtatLockClient.RLT;
				break;
			default:
				System.out.println("reduce_lock etat illogique : " +etat.toString());
		}
		return obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		switch(etat){

			case RLC:
				etat = EtatLockClient.NL;
				break;
			case RLT:
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				etat = EtatLockClient.NL;
				break;
			default:
				System.out.println("invalidate_reader etat illogique : " +etat.toString());
		}
	}

	public synchronized Object invalidate_writer() {
		switch(etat){

			case WLC:
				etat = EtatLockClient.NL;
				break;
			case WLT:
			case RLT_WLC:
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				etat = EtatLockClient.NL;
				break;
			default :
				System.out.println("invalidate_writer etat illogique : " +etat.toString());
				
		}
		return obj;
	}
}
