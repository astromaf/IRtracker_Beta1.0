package lll.wrj4P5;

import lll.Loc.Loc;
import wiiremotej.*;
import wiiremotej.event.*;

/*
[members]
  float sensed[2][2];	: latest sensed by pressure sensors at each corner.(mesured by Kg)
  float psensed[2][2];	// previous sensed value.
  float weight;		// = average of 4 sensor.(mesured by Kg)
  float pweight;		// = previous average of 4 sensor   
  float topRight;	// = sensed[0][0], Top-Right
  float bottomLeft;	// = sensed[1][1], Bottom-Left
  float topLeft;		// = sensed[0][1], Top-Left
  float bottomRight;	// = sensed[1][0], Bottom-Right
  boolean isIlluminated; //status of the LED on the board

[connection/communication]
:<status>
  void requestStatus()	 : command to start getting the newest status report
  boolean isReadingData()	: check if getting status
  boolean isConnected() 	: has 'me' connected ?
:<Battery>
  float getBatteryLevel()	: get the latest battery level, 0(empty) to 1(full)
:<LED Lights>
  boolean isLED()	: check the LED illumination.
  void setLED(boolean illumination)	: set the LED illumination.
:<utils>
  Loc guessCoG() : the estimation of the center of gravity
  boolean isleftZone() : is the CoG on the left side of the board 
  boolean isTopZone() : is the CoG on tne top side of the board
  boolean isCentralZone(float tol) : is the CoG on the central zone, gibven torelance
  boolean isCentralZone() : same as above, with torelance is 0.4. 
*/


public class WiiBoard extends BalanceBoardAdapter {
    public long tNow = 0;			// current sensed cycle.
    public float sensed[][] = new float[2][2];	//4 sensors
	public float psensed[][];	// previous sensed value.
    public float weight;		// = average of 4 sensor
    public float pweight;		// = previous average of 4 sensor   
    public float topRight;	// = sensed[0][0], Top-Right
    public float bottomLeft;	// = sensed[1][1], Bottom-Left
    public float topLeft;		// = sensed[0][1], Top-Left
    public float bottomRight;	// = sensed[1][0], Bottom-Right
    public boolean isIlluminated; //status of LED
//	public Loc irLight = new Loc();
	private Wrj4P5 parent;
	private BalanceBoard talker;
	private int myId;
	private float batteryLevel;

	public WiiBoard(Wrj4P5 wii)
    {
 	   super();
 	   this.parent = wii;
    }

	public BalanceBoard addTalker(BalanceBoard talker, int id)
	{
	   myId = id;
	   this.talker = talker;
 	   return talker;
	}
 
	public void disconnected()
	{
		System.out.println("Board disconnected... Please Wii again.");
		parent.disconnected(myId);
     }
 
	public void statusReported(BBStatusEvent evt)
	{
		batteryLevel = (float) evt.getBatteryLevel()/200;
		isIlluminated = evt.isLEDIlluminated();
	}

	public void buttonInputReceived(BBButtonEvent evt) {
		if (parent.boardPressed != null && evt.wasPressed()) {
			BoardPressedThread t = new BoardPressedThread(
 				(BBButtonEvent) evt,
 				parent,
 				myId
 			);
 		t.run();
		}
		if (parent.boardReleased != null && evt.wasReleased()) {
			BoardReleasedThread t = new BoardReleasedThread(
     				(BBButtonEvent) evt,
     				parent,
     				myId
     		);
		t.run();
		}
	}

	public void massInputReceived(BBMassEvent evt) {
		try {
	        psensed = (float[][]) sensed.clone();
	        pweight = weight;
	        weight = (float) evt.getTotalMass();
			sensed[0][0] = topLeft = (float) evt.getMass(MassConstants.TOP,MassConstants.LEFT);
			sensed[1][1] = bottomRight = (float) evt.getMass(MassConstants.BOTTOM,MassConstants.RIGHT);
			sensed[0][1] = topRight = (float) evt.getMass(MassConstants.TOP,MassConstants.RIGHT);
			sensed[1][0] = bottomLeft = (float) evt.getMass(MassConstants.BOTTOM,MassConstants.LEFT);
		} catch (Exception e) {
			System.out.println(e);
		}
        tNow++;            
	}

//	public void combinedInputReceived(BBCombinedEvent arg0) {
		// TODO Auto-generated method stub
//	}
	 // <connection/communication>
    public void disconnect() { //disconnect the WiiRemote device
    		if (talker != null) {talker.disconnect();talker=null;}
    		parent.disconnected(myId);
    }
	
//
    public boolean isConnected() {
		return talker.isConnected();
	}

	public void requestStatus() { // start getting the newest status report
		try {
			if (!talker.isReadingData())
				talker.requestStatus();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public boolean isReadingData() { // check if getting status
		return talker.isReadingData();
	}

//<Battery>
	public float getBatteryLevel() { // get the latest battery level,
										// 0(empty) to 1(full)
		return batteryLevel;
	}

	// <LED Lights>
	public boolean isLED() { // check the specified LED illumination.
		return isIlluminated = talker.isLEDIlluminated() ;
	}

	public void setLED(boolean illumination) { // set the specified LED
														// illumination.
		try {
			isIlluminated = illumination;
			talker.setLEDIlluminated(isIlluminated);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//<utils>
	public Loc guessCoG() {
	  float ttl = (sqr(topRight)+sqr(bottomRight)
	              + sqr(topLeft)+sqr(bottomLeft));
	  float xrate = (sqr(topRight)+sqr(bottomRight)) / ttl;
	  float yrate = (sqr(bottomLeft)+sqr(bottomRight)) / ttl;
	  return new Loc(xrate,yrate,0);
	}
	
	public boolean isLeftZone() {
		return (topRight + bottomRight) < (topLeft + bottomLeft);
	}
	
	public boolean isTopZone() {
		return (bottomLeft + bottomRight) < (topLeft + topRight);
	}
	
	public boolean isCentralZone(float tol) {
		float[][] b = sensed;
		float mx = Math.max(Math.max(b[0][0],b[0][1]),Math.max(b[1][0],b[1][1]));
		float mn = Math.min(Math.min(b[0][0],b[0][1]),Math.min(b[1][0],b[1][1]));
		return (mx-mn < weight/4*tol);
	}
	public boolean isCentralZone() {
		return isCentralZone(0.4f);
	}
	private float sqr(float x) {return x*x;}

}
