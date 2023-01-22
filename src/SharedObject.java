import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SharedObject extends UnicastRemoteObject implements Serializable, SharedObject_itf {
	public EtatLockClient etat;
	public transient Object obj;	// Etape 3 : ajout du mot-clé "transient"
	public int id;
	public Client client;


	//TODO : remove
	public final Boolean affiche = false;

	public SharedObject(Client client, int id, Object object) throws RemoteException{
		this.obj = object;
		this.id = id;
		this.client = client;
		this.etat = EtatLockClient.NL;
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
					etat = EtatLockClient.RLT;
					System.out.println("lock_read etat illogique : " +etat.toString());
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		if(affiche){
			System.out.println("lock_read fini, etat = " +etat.toString());
		}
	}

	// invoked by the user program on the client node
	public void lock_write() {
		switch(etat){

			case NL:
			case RLC:
				this.obj = Client.lock_write(id);
			case WLC:
				etat = EtatLockClient.WLT;
				break;
			default:
				etat = EtatLockClient.WLT;
				System.out.println("lock_write etat illogique : " +etat.toString());

		}
		if(affiche){
			System.out.println("lock_write fini, etat = " +etat.toString());
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
				System.out.println("unlock etat illogique : " +etat.toString());

		}
		this.notify();
		if(affiche){
			System.out.println("unlock fini, etat = " +etat.toString());
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
				etat = EtatLockClient.RLT;
				break;
			case NL:
				break;
			default:
			System.out.println("reduce_lock etat illogique : " +etat.toString());
		}
		if(affiche){
			System.out.println("reduce_lock fini, etat = " +etat.toString());
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
			case NL:
				break;
			default:
				System.out.println("invalidate_reader etat illogique : " +etat.toString());
		}
		if(affiche){
			System.out.println("invalidate_reader fini, etat = " +etat.toString());
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
		if(affiche){
			System.out.println("invalidate_writer fini, etat = " +etat.toString());
		}
		return obj;
	}

	// Spécialisation de la méthode Object readResolve()
	private Object readResolve() {
		SharedObject so = (SharedObject)client.id_to_Objects.get(this.id);

		if (so == null) {
			return this;
		}
		return so;
	}
}
