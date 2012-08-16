package lll.wrj4P5;
import wiiremotej.event.*;

public class KurakonReleasedThread extends Thread {
	WRClassicControllerExtensionEvent evt;
	int myId;
	Wrj4P5 parent;

	public KurakonReleasedThread(WRClassicControllerExtensionEvent evt, Wrj4P5 parent, int rid) {
		super();
		this.evt = evt;
		this.parent = parent;
		this.myId = rid;
	}
	public void run() {
     	try{
    				parent.kurakonReleased.invoke(
    					parent.parent, 
    					new Object[] {
    					new KurakonEvent(evt), 
    					new Integer(myId),
    					}
    				);
    		}catch (Exception e){
    			System.err.println("Disabling nunchuk() for " + (parent.parent).getName() + " because of an error.");
    			e.printStackTrace();
    		}
	}
}
