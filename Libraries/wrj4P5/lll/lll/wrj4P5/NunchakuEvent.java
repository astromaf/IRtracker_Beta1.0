package lll.wrj4P5;
import lll.Loc.*;
import wiiremotej.event.*;

/**
 * NunchakuEvent is the information from Nunchuk's input event. 
 */

public class NunchakuEvent {
	public final static int C = WRNunchukExtensionEvent.C;
	public final static int Z = WRNunchukExtensionEvent.Z;
//
	private WRNunchukExtensionEvent nextevt;
//
	public NunchakuEvent(WRNunchukExtensionEvent evt) {
    		nextevt = evt;
    }
	public boolean isAnyPressed(int button) { // check the given button is
												// pressed.
		return nextevt.isAnyPressed(button);
	}

	public boolean isOnlyPressed(int button) { // check the given button is
												// pressed.
		return nextevt.isOnlyPressed(button);
	}

	public boolean isPressed(int button) { // check the given button is
											// pressed.
		return nextevt.isPressed(button);
	}

	public boolean wasOnlyPressed(int button) { // check the given button was
												// pressed.
		return nextevt.wasOnlyPressed(button);
	}

    public boolean wasPressed(int button) { // check the given button was pressed.
    		return nextevt.wasPressed(button);
    }
    public boolean wasReleased(int button) { // check the given button was released.
    		return nextevt.wasReleased(button);
    }
    public Loc getStick() { // get the current stick 2D(Loc) position. (-1 to 1)
    		return new Loc((float)nextevt.getAnalogStickData().getX(),
    					  (float)nextevt.getAnalogStickData().getY(),
    					  0);
    }
}
