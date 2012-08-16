package lll.wrj4P5;

import lll.Loc.*;
import wiiremotej.event.*;

/**
 * WiiKurakon is the model of the ClassicController containing some states. 
 */

public class WiiKurakon {
	public Loc lStick = new Loc();	// latest sensed 2D(Loc) location(%) of the left stick.
	public Loc rStick = new Loc();	// latest sensed 2D(Loc) location(%) of the right stick.
	public float lTrigger = 0;		// latest sensed pressure(%) of the left trigger button.
	public float rTrigger = 0;		// latest sensed pressure(%) of the right trigger button.
//
	private WRClassicControllerExtensionEvent cextevt;
//
	public void inputEvent(WRClassicControllerExtensionEvent evt) {
		cextevt = evt;
		lStick.move((float)cextevt.getLeftAnalogStickData().getX(),
				   (float)cextevt.getLeftAnalogStickData().getY(),
				   0
				);
		rStick.move((float)cextevt.getRightAnalogStickData().getX(),
				   (float)cextevt.getRightAnalogStickData().getY(),
				   0
				);
		lTrigger = (float)cextevt.getLeftTrigger();
		rTrigger = (float)cextevt.getRightTrigger();
	}
}
