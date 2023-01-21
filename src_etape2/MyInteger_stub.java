import java.rmi.RemoteException;

public class MyInteger_stub extends SharedObject implements MyInteger_itf, java.io.Serializable {

	public MyInteger_stub(Client client, int id, Object object) throws RemoteException {
		super(client, id, object);
	}
	public void incr() {
		MyInteger s = (MyInteger) obj;
		s.incr();
	}
	public int getInt() {
		MyInteger s = (MyInteger) obj;
		return s.getInt();
	}
}