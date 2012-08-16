package lll.wrj4P5;

import lll.Loc.*;
import wiiremotej.event.*;

public class GuitarEvent {
	public final static int BLUE = WRGuitarExtensionEvent.BLUE;

	public final static int GREEN = WRGuitarExtensionEvent.GREEN;

	public final static int ORANGE = WRGuitarExtensionEvent.ORANGE;

	public final static int RED = WRGuitarExtensionEvent.RED;

	public final static int STRUM_DOWN = WRGuitarExtensionEvent.STRUM_DOWN;

	public final static int STRUM_UP = WRGuitarExtensionEvent.STRUM_UP;

	public final static int YELLOW = WRGuitarExtensionEvent.YELLOW;

	public final static int MINUS = WRGuitarExtensionEvent.MINUS;

	public final static int PLUS = WRGuitarExtensionEvent.PLUS;

	//
	private WRGuitarExtensionEvent cextevt;

	//
	public GuitarEvent(WRGuitarExtensionEvent evt) {
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

	public Loc getStick() { // get the current left-stick 2D(Loc) position. (-1 to 1)
		return new Loc((float) cextevt.getAnalogStickData().getX(),
				(float) cextevt.getAnalogStickData().getY(), 0);
	}

	public float getWhammyBar() { // get the current left-trigger button position. (0 to 1)
		return (float) cextevt.getWhammyBar();
	}
}
