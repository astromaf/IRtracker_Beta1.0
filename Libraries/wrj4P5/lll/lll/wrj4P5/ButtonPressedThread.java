package lll.wrj4P5;

import wiiremotej.event.*;

public class ButtonPressedThread extends Thread {
	WRButtonEvent evt;
	int myId;
	Wrj4P5 parent;

	public ButtonPressedThread(WRButtonEvent evt, Wrj4P5 parent, int rid) {
		super();
		this.evt = evt;
		this.parent = parent;
		this.myId = rid;
	}
	public void run() {
     	try{
    				parent.buttonPressed.invoke(
    					parent.parent, 
    					new Object[] {
    					new RimokonEvent(evt), 
    					new Integer(myId),
    					}
    				);
    		}catch (Exception e){
    			System.err.println("Disabling wiiremote() for " + (parent.parent).getName() + " because of an error.");
    			e.printStackTrace();
    		}
	}
}
