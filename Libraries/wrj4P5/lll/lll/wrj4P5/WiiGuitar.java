package lll.wrj4P5;

import lll.Loc.*;
import wiiremotej.event.*;

/**
 * WiiKurakon is the model of the ClassicController containing some states. 
 */

public class WiiGuitar {
	public Loc stick = new Loc();	// latest sensed 2D(Loc) location(%) of the stick.
	public float whammy = 0;		// latest sensed pressure(%) of the WhammyBar.
//
	private WRGuitarExtensionEvent cextevt;
//
	public void inputEvent(WRGuitarExtensionEvent evt) {
		cextevt = evt;
		stick.move((float)cextevt.getAnalogStickData().getX(),
				   (float)cextevt.getAnalogStickData().getY(),
				   0
				);
		whammy = (float)cextevt.getWhammyBar();
	}
}
