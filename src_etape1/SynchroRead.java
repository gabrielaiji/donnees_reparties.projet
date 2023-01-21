public class SynchroRead {

    public static void main(String argv[]) {
		
		if (argv.length != 0) {
			System.out.println("java SynchroRead");
			return;
		}
	
		// initialize the system
		Client.init("synchro_fin");
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		SharedObject sharedObject = Client.lookup("SYNCHRO");
		if (sharedObject == null) {
			sharedObject = Client.create(new MyInteger());
			Client.register("SYNCHRO", sharedObject);
		}
        sharedObject.lock_read();
        int compteurFinale = ((MyInteger) sharedObject.obj).getInt();
        sharedObject.unlock();

        System.out.println(compteurFinale);
        System.exit(0);
    }
    
}
