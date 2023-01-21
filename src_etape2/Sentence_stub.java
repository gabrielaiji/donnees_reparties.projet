import java.rmi.RemoteException;

public class Sentence_stub extends SharedObject implements Sentence_itf, java.io.Serializable {
	
	public Sentence_stub(Client client, int id, Object object) throws RemoteException {
		super(client, id, object);
	}
	public void write(String text) {
		Sentence s = (Sentence)obj;
		s.write(text);
	}
	public String read() {
		Sentence s = (Sentence)obj;
		return s.read();	
	}
	
}