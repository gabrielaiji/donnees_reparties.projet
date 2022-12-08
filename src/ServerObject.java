import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class ServerObject extends UnicastRemoteObject implements ServerObject_itf{
	public EtatLockServer etat;
	public int id;
	public Object object;

	public ServerObject(int id, Object object) throws RemoteException{
		this.id = id;
		this.object = object;
		this.etat = EtatLockServer.NL;
	}

	public void lock_read(){
		switch(etat){

			case NL:
				etat = EtatLockServer.RL;
				break;
			case RL:
				etat = EtatLockServer.RL;
				break;
			case WL:
				//TO DO : blocage : callback
				etat = EtatLockServer.RL;
				break;

		}
	}
	public void lock_write(){
		switch(etat){

			case NL:
				etat = EtatLockServer.WL;
				break;
			case RL:
				//TO DO : blocage : callback
				etat = EtatLockServer.WL;
				break;
			case WL:
				//TO DO : blocage : callback
				etat = EtatLockServer.WL;
				break;

		}
	}

	public void unlock(){
		switch(etat){

			case NL:
				etat = EtatLockServer.NL;
				break;
			case RL:
				//TO DO : déblocage : appeler le callback
				etat = EtatLockServer.WL;
				break;
			case WL:
				//TO DO : déblocage : appeler le callback
				etat = EtatLockServer.WL;
				break;

		}
	}
}