package lll.wrj4P5;

import wiiremotej.event.*;

public class BoardPressedThread extends Thread {
	BBButtonEvent evt;
	int myId;
	Wrj4P5 parent;

	public BoardPressedThread(BBButtonEvent evt, Wrj4P5 parent, int rid) {
		super();
		this.evt = evt;
		this.parent = parent;
		this.myId = rid;
	}
	public void run() {
     	try{
    				parent.boardPressed.invoke(
    					parent.parent, 
    					new Object[] {
    					new Integer(myId),
    					}
    				);
    		}catch (Exception e){
    			System.err.println("Disabling Board() for " + (parent.parent).getName() + " because of an error.");
    			e.printStackTrace();
    		}
	}
}
