package lll.wrj4P5;

import wiiremotej.event.*;

/**
 * RimokonEvent is the information from WiiRemote's input event. 
 */

public class RimokonEvent {
	public final static int ONE = WRButtonEvent.ONE;

	public final static int TWO = WRButtonEvent.TWO;

	public final static int B = WRButtonEvent.B;

	public final static int A = WRButtonEvent.A;

	public final static int MINUS = WRButtonEvent.MINUS;

	public final static int HOME = WRButtonEvent.HOME;

	public final static int LEFT = WRButtonEvent.LEFT;

	public final static int RIGHT = WRButtonEvent.RIGHT;

	public final static int DOWN = WRButtonEvent.DOWN;

	public final static int UP = WRButtonEvent.UP;

	public final static int PLUS = WRButtonEvent.PLUS;

	//   
	private WRButtonEvent wrbevt;

	//    
	public RimokonEvent(WRButtonEvent evt) {
		wrbevt = evt;
	}

	public boolean isAnyPressed(int button) { // check the given button is pressed.
		return wrbevt.isAnyPressed(button);
	}

	public boolean isOnlyPressed(int button) { // check the given button is pressed.
		return wrbevt.isOnlyPressed(button);
	}

	public boolean isPressed(int button) { // check the given button is pressed.
		return wrbevt.isPressed(button);
	}

	public boolean wasOnlyPressed(int button) { // check the given button was pressed.
		return wrbevt.wasOnlyPressed(button);
	}

	public boolean wasPressed(int button) { // check the given button was pressed.
		return wrbevt.wasPressed(button);
	}

	public boolean wasReleased(int button) { //  check the given button was released.
		return wrbevt.wasReleased(button);
	}
}
