package lll.wrj4P5;

import lll.Loc.*;
import wiiremotej.event.*;

/**
 * KurakonEvent is the information from Classic Controller's input event. 
 */

public class KurakonEvent {
	public final static int A = WRClassicControllerExtensionEvent.A;

	public final static int B = WRClassicControllerExtensionEvent.B;

	public final static int DPAD_DOWN = WRClassicControllerExtensionEvent.DPAD_DOWN;

	public final static int DPAD_RIGHT = WRClassicControllerExtensionEvent.DPAD_RIGHT;

	public final static int DPAD_LEFT = WRClassicControllerExtensionEvent.DPAD_LEFT;

	public final static int DPAD_UP = WRClassicControllerExtensionEvent.DPAD_UP;

	public final static int HOME = WRClassicControllerExtensionEvent.HOME;

	public final static int MINUS = WRClassicControllerExtensionEvent.MINUS;

	public final static int PLUS = WRClassicControllerExtensionEvent.PLUS;

	public final static int LEFT_TRIGGER = WRClassicControllerExtensionEvent.LEFT_TRIGGER;

	public final static int RIGHT_TRIGGER = WRClassicControllerExtensionEvent.RIGHT_TRIGGER;

	public final static int LEFT_Z = WRClassicControllerExtensionEvent.LEFT_Z;

	public final static int RIGHT_Z = WRClassicControllerExtensionEvent.RIGHT_Z;

	public final static int X = WRClassicControllerExtensionEvent.X;

	public final static int Y = WRClassicControllerExtensionEvent.Y;

	//
	private WRClassicControllerExtensionEvent cextevt;

	//
	public KurakonEvent(WRClassicControllerExtensionEvent evt) {
		cextevt = evt;
	}

	public boolean isAnyPressed(int button) { // check the given button is pressed.
		return cextevt.isAnyPressed(button);
	}

	public boolean isOnlyPressed(int button) { // check the given button is pressed.
		return cextevt.isOnlyPressed(button);
	}

	public boolean isPressed(int button) { // check the given button is pressed.
		return cextevt.isPressed(button);
	}

	public boolean wasOnlyPressed(int button) { // check the given button was pressed.
		return cextevt.wasOnlyPressed(button);
	}

	public boolean wasPressed(int button) { // check the given button was pressed.
		return cextevt.wasPressed(button);
	}

	public boolean wasReleased(int button) { // check the given button was released.
		return cextevt.wasReleased(button);
	}

	public Loc getLStick() { // get the current left-stick 2D(Loc) position. (-1 to 1)
		return new Loc((float) cextevt.getLeftAnalogStickData().getX(),
				(float) cextevt.getLeftAnalogStickData().getY(), 0);
	}

	public Loc getRStick() { // get the current right-stick 2D(Loc) position. (-1 to 1)
		return new Loc((float) cextevt.getRightAnalogStickData().getX(),
				(float) cextevt.getRightAnalogStickData().getY(), 0);
	}

	public float getLTrigger() { // get the current left-trigger button position. (0 to 1)
		return (float) cextevt.getLeftTrigger();
	}

	public float getRTrigger() { // get the current right-trigger button position. (0 to 1)
		return (float) cextevt.getRightTrigger();
	}
}
