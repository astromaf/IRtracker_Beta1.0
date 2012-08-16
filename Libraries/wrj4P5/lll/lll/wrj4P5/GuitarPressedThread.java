package lll.wrj4P5;
import wiiremotej.event.*;

public class GuitarPressedThread extends Thread {
	WRGuitarExtensionEvent evt;
	int myId;
	Wrj4P5 parent;

	public GuitarPressedThread(WRGuitarExtensionEvent evt, Wrj4P5 parent, int rid) {
		super();
		this.evt = evt;
		this.parent = parent;
		this.myId = rid;
	}
	public void run() {
     	try{
    				parent.guitarPressed.invoke(
    					parent.parent, 
    					new Object[] {
    					new GuitarEvent(evt), 
    					new Integer(myId),
    					}
    				);
    		}catch (Exception e){
    			System.err.println("Disabling guitar() for " + (parent.parent).getName() + " because of an error.");
    			e.printStackTrace();
    		}
	}
}
