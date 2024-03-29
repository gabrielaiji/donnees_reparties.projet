import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;


public class Synchro{
	//SharedObject sharedObject;
	static String myName;
    static int compteurLocal;

	public static void main(String argv[]) {
		
		if (argv.length != 1) {
			System.out.println("java Synchro <name>");
			return;
		}
		myName = argv[0];
	
		// initialize the system
		Client.init(myName);
		
		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		SharedObject sharedObject = Client.lookup("SYNCHRO");
		if (sharedObject == null) {
			sharedObject = Client.create(new MyInteger());
			Client.register("SYNCHRO", sharedObject);
		}
		
        Random ran = new Random();
        compteurLocal = 0;
		for(int i = 0; i<10; i++){
            int choix = ran.nextInt(2);

            if(choix == 0){
                sharedObject.lock_write();
                ((MyInteger) sharedObject.obj).incr();
                sharedObject.unlock();
                compteurLocal ++;
            }
            else{
                sharedObject.lock_read();
                ((MyInteger) sharedObject.obj).getInt();
                sharedObject.unlock();
            }
        }
        System.out.println(compteurLocal);
	}

}
