public class SynchroRead {

    public static void main(String argv[]) {
		
		if (argv.length != 0) {
			System.out.println("java SynchroRead");
			return;
		}
	
		// initialize the system
		Client.init();
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		MyInteger_itf myInteger = (MyInteger_itf)Client.lookup("SYNCHRO");
		if (myInteger == null) {
			myInteger = (MyInteger_itf)Client.create(new MyInteger());
			Client.register("SYNCHRO", myInteger);
		}
        myInteger.lock_read();
        int compteurFinale = myInteger.getInt();
        myInteger.unlock();

        System.out.println(compteurFinale);
        System.exit(0);
    }
    
}
