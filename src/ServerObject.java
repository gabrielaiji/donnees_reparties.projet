public enum EtatLockServer = {NL, RL, WL}

public class ServerObject extends UnicastRemoteObject implements SharedObject{
	public EtatLockServer etat;
	public int id;
	public Object object;

	public ServerObject(){
		etat = NL;
	}

	public ServerObject(Object o){
		etat = NL;
		object = o;
	}

	public void lock_read(){
		switch(etat){

			case NL:
				etat = RL;
				break;
			case RL:
				etat = RL;
				break
			case WL:
				//TO DO : blocage : callback
				etat = RL;
				break;

		}
	}
	public void lock_write(){
		switch(etat){

			case NL:
				etat = WL;
				break;
			case RL:
				//TO DO : blocage : callback
				etat = WL;
				break
			case WL:
				//TO DO : blocage : callback
				etat = WL;
				break;

		}
	}

	public void unlock(){
		switch(etat){

			case NL:
				etat = NL;
				break;
			case RL:
				//TO DO : déblocage : appeler le callback
				etat = WL;
				break
			case WL:
				//TO DO : déblocage : appeler le callback
				etat = WL;
				break;

		}
	}
}