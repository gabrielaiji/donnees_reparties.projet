import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerObject extends UnicastRemoteObject implements ServerObject_itf{
	public EtatLockServer etat;
	public int id;
	public transient Object object;
	public Server server;

	public Lock droit_de_modif_etat;
	public ArrayList<Client_itf> clients;

	//TODO remove
	private final Boolean affiche = false;

	public ServerObject(int id, Object object, Server server) throws RemoteException{
		this.id = id;
		this.object = object;
		this.server = server;
		this.etat = EtatLockServer.NL;

		this.droit_de_modif_etat = new ReentrantLock();
		clients = new ArrayList<Client_itf>();
	}

	public synchronized Object lock_read(Client_itf client){
		droit_de_modif_etat.lock();
		Object obj = this.object;

		if(etat == EtatLockServer.WL ){
			for(Client_itf c : clients){
				try{
					obj = c.reduce_lock(id);
					this.object = obj;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}

		if(!clients.contains(client)){
			clients.add(client);
		}
		etat = EtatLockServer.RL;

		droit_de_modif_etat.unlock();
		if(affiche){
			printEtats("lock_read()");
		}
		return obj;
	}
	public synchronized Object lock_write(Client_itf client){
		droit_de_modif_etat.lock();
		Object obj = this.object;
		switch(etat){

			case NL:
				clients.add(client);
				etat = EtatLockServer.WL;
				break;
			case RL:
				for(Client_itf c : clients){
					try{
						c.invalidate_reader(id);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				//clients = new ArrayList<Client_itf>();
				clients.clear();
				clients.add(client);
				etat = EtatLockServer.WL;
				break;
			case WL:
				for(Client_itf c : clients){
					try{
						obj = c.invalidate_writer(id);
						this.object = obj;
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				//clients = new ArrayList<Client_itf>();
				clients.clear();
				clients.add(client);
				etat = EtatLockServer.WL;
				break;
		}
		droit_de_modif_etat.unlock();
		if(affiche){
			printEtats("lock_write()");
		}
		return obj;
	}

	public synchronized void unlock(Client_itf client){ //Inutile ?
		droit_de_modif_etat.lock();
		switch(etat){

			case NL:
				etat = EtatLockServer.NL;
				System.out.println("Unlock objet deja unlocked ?");
				break;
			case RL:
				clients.remove(client);
				if(clients.isEmpty()){
					etat = EtatLockServer.NL;
				}
				break;
			case WL:
				clients.remove(client);
				if(clients.isEmpty()){
					etat = EtatLockServer.NL;
				}
				else{
					System.err.println("Il y avait plusieurs redacteurs ?");
				}
				
				break;

		}
		droit_de_modif_etat.unlock();
		if(affiche){
			printEtats("unlock()");
		}
	}

	public void printEtats(String func){
		
		System.out.println("---------------------");
		System.out.println("Objet "+id+" ("+func+")");
		System.out.println("Etat : "+etat);
		System.out.print("Clients : ");
		try{
			for(Client_itf client : clients){
				System.out.print(client.getName() +";");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println();
		
	}

	// Spécialisation de la méthode Object readResolve()
	private Object readResolve() {
		ServerObject so = (ServerObject)server.id_to_Objects.get(this.id);

		if (so == null) {
			return this;
		}
		return so;
	}
}