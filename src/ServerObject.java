import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerObject extends UnicastRemoteObject implements ServerObject_itf{
	public EtatLockServer etat;
	public int id;
	public Object object;

	public ArrayList<Client_itf> clients;

	public ServerObject(int id, Object object) throws RemoteException{
		this.id = id;
		this.object = object;
		this.etat = EtatLockServer.NL;

		clients = new ArrayList<Client_itf>();
	}

	public Object lock_read(Client_itf client){
		Object obj = this.object;
		switch(etat){

			case NL:
				clients.add(client);
				etat = EtatLockServer.RL;
				break;
			case RL:
				clients.add(client);
				etat = EtatLockServer.RL;
				break;
			case WL:
				for(Client_itf c : clients){
					try{
						obj = c.reduce_lock(id);
						this.object = obj;
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				clients.add(client);
				etat = EtatLockServer.RL;
				break;
		}
		printEtats("lock_read()");
		return obj;
	}
	public Object lock_write(Client_itf client){
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
				clients = new ArrayList<Client_itf>();
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
				clients = new ArrayList<Client_itf>();
				clients.add(client);
				etat = EtatLockServer.WL;
				break;
		}
		printEtats("lock_write()");
		return obj;
	}

	public void unlock(Client_itf client){ //Inutile ?
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
		printEtats("unlock()");
		return ;
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
}