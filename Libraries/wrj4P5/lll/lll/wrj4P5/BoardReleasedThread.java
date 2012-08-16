package lll.wrj4P5;

import wiiremotej.event.*;

public class BoardReleasedThread extends Thread {
	BBButtonEvent evt;
	int myId;
	Wrj4P5 parent;

	public BoardReleasedThread(BBButtonEvent evt, Wrj4P5 parent, int rid) {
		super();
		this.evt = evt;
		this.parent = parent;
		this.myId = rid;
	}
	public void run() {
     	try{
    				parent.boardReleased.invoke(
    					parent.parent, 
    					new Object[] {
    					new Integer(myId),
    					}
    				);
    		}catch (Exception e){
    			System.err.println("Disabling board() for " + (parent.parent).getName() + " because of an error.");
    			e.printStackTrace();
    		}
	}
}
