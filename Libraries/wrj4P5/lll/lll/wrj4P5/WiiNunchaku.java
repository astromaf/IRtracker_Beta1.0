package lll.wrj4P5;
import lll.Loc.*;
import wiiremotej.event.*;

/**
 * WiiNunchaku is the model of the Nunchuk containing some states. 
 */

public class WiiNunchaku {
    public Loc sensed = new Loc();	// latest sensed 3D(Loc) acceleration measured by Gravity Constant.
    public Loc senced = sensed;		// Just non-sence. typo
	public Loc acc = new Loc();		// latest sensed 3D(Loc) accelerationÅim/sec/sec)
	public Loc stick = new Loc();		// latest sensed 2D(Loc) location(%) of the stick.
//	 TODO
//	public Loc vel = new Loc();		// latest sensed 3D(Loc) velocity(m/sec)
//	public Loc pos = new Loc();		// latest sensed 3D(Loc) location(m)
//
//  <Stable pitch and roll>
    public float stablePitch() {// exact only in stable handling
    		return (float) Math.asin(sensed.y/Wrj4P5.ONE_G);
}
    public float stableRoll() {// exact only in stable handling
		return (float) Math.asin(sensed.x/Wrj4P5.ONE_G);
}
	private WRNunchukExtensionEvent nextevt;
//
	public void inputEvent(WRNunchukExtensionEvent evt) {
		nextevt = evt;
		sensed.move((float)nextevt.getAcceleration().getXAcceleration(),
				(float)nextevt.getAcceleration().getYAcceleration(),
				(float)nextevt.getAcceleration().getZAcceleration()
			);
		acc.move(sensed).scale(Wrj4P5.ONE_G);	
		stick.move((float)nextevt.getAnalogStickData().getX(),
				   (float)nextevt.getAnalogStickData().getY(),
				   0
				);
	}
}
