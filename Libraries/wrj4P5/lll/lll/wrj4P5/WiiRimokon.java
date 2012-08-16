package lll.wrj4P5;
import wiiremotej.*;
import wiiremotej.event.*;
import javax.sound.sampled.*;

import lll.Loc.*;

/*
[members]
  Loc sensed;	: latest sensed 3D(Loc) acceleration measured by Gravity Constant.
  Loc acc;	: latest sensed 3D(Loc) acceleration�im/sec/sec)
  Loc[] irLights; : 2D position and size of each IRlights, 3rd entry is a size of light
  WiiNunchaku nunchaku;	: the Nunchuk connected, having inittial value if no connected.
  WiiKurakon kurakon;	: the ClassicController connected, having inittial value if no connected.
  WiiGuitar guitar;	: the Gutar connected, having inittial value if no connected.

    [TODO]
//  Loc vel = new Loc();	: latest sensed 3D(Loc) velocity(m/sec)
//  Loc pos = new Loc();	: latest sensed 3D(Loc) location(m)
//  Loc cursor = new Loc();	: latest sensed 2D(Loc) location(%) of the cross cursor.
//  Loc angAcc = new Loc();	: 3D(Loc) angular acceleration(rad/sec/sec) - will need a fixed nunchuk
//  Loc angVel = new Loc();	: 3D(Loc) angular velocity(rad/sec) - will need a fixed nunchuk
//  Loc direction = new Loc();	: 3D(Loc) normalized direction - will need a fixed nunchuk

[connection/communication]
:<Attitude of 'me'>
  float stablePitch()	: current pich(radian), exact only in stable handling
  float stableRoll()	: current roll(radian), exact only in stable handling
 [too many ToDo]
:<status>
  void requestStatus()	 : command to start getting the newest status report
  boolean isReadingData()	: check if getting status
  boolean isExtensionConnected()	: has a extension connected
  boolean isConnected() 	: has 'me' connected ?
:<Battery>
  float getBatteryLevel()	: get the latest battery level, 0(empty) to 1(full)
:<LED Lights>
  boolean isLED(int lid)	: check the specified LED illumination.
  void setLED(int lid, boolean illumination)	: set the specified LED illumination.
  boolean[] getLEDs()	 : check the all LEDs illumination.
  void setLEDs(boolean[] illumination)	: set the each LEDs illumination.
:<Speaker> !! [ToDo]
  void bufferSound(AudioInputStream audioIn)	: prepare the prebufferd sound.(only one)
  float getVolume()	: check the speaker's volume level, min(0) to max(1)
  void setVolume(float volume)	: set the speaker's volume level, min(0) to max(1)
  boolean isPlaying()	: check if playing sound.
  void playBuffer()	: play the prebufferd sound.
  void play(AudioInputStream audioIn)	: play the specified AudioInputStream sound.
  void stopSound()	: stop sound.
:<Vibrator>
  boolean isVibrating()	: check if vibrating
  float getVibrationMagnitude(int magnitude)	: get vibration magnitude.(0 to 1)
  void setVibrationMagnitude(int magnitude)	: set vibration magnitude.(0 to 1)
  void startVibrating()	: start vibration with current mag.
  void vibrateFor(long time)	: vibration for given duration.(ms)
  void stopVibrating()	: stop vibration.
:<IRsenser>
  boolean isIRSensorEnabled()	: check IRsensor enabled.
*/

public class WiiRimokon extends WiiRemoteAdapter
{    
//[propaties]
    public long tNow = 0;			// current sensed cycle.
    
    public Loc sensed = new Loc();	// latest sensed 3D(Loc) acceleration measured by Gravity Constant.
    public Loc senced = sensed;	    // Just non-sence. typo
	public Loc acc = new Loc();		// latest sensed 3D(Loc) acceleration�im/sec/sec)
//	 TODO
//	public Loc vel = new Loc();		// latest sensed 3D(Loc) velocity(m/sec)
//	public Loc pos = new Loc();		// latest sensed 3D(Loc) location(m)
//	public Loc cursor = new Loc();	// latest sensed 2D(Loc) location(%) of the cross cursor.

//	public Loc angAcc = new Loc();	// 3D(Loc) angular acceleration(rad/sec/sec) - will need a fixed nunchuk
//	public Loc angVel = new Loc();	// 3D(Loc) angular velocity(rad/sec) - will need a fixed nunchuk
//	public Loc direction = new Loc();	// 3D(Loc) normalized direction - will need a fixed nunchuk
	
	public WiiNunchaku nunchaku;		// the Nunchuk connected, having inittial value if no connected.
	public WiiKurakon kurakon;		// the ClassicController connected, having inittial value if no connected.
	public WiiGuitar guitar;		    // the Gutar connected, having inittial value if no connected.
	public Loc[] irLights={new Loc(-1,-1,-1), new Loc(-1,-1,-1),
						  new Loc(-1,-1,-1), new Loc(-1,-1,-1)};
									// 2D position and size of each IRlights, 3rd entry is a size of light
	   
	private Wrj4P5 parent;
	private WiiRemote talker;
	private int myId;
	private Loc psensed = new Loc();	// previous sensed value.
	private float batteryLevel;
	private boolean[] isIlluminated = {false, false, false, false}; //status of 4 LEDs
	private PrebufferedSound prebuf;
	private float viblationMagnitude=1;
	private boolean isModurate=false;
	private long viblationInterval=40;
	private boolean isVibrating=false;
	private long lastCom = 0;
	private long birthTime;
/*	private byte[][] irSens1 = {
			{0x02, 0x00, 0x00, 0x71, 0x01, 0x00, 0x64, 0x00, (byte) (0xfe)},
			{0x02, 0x00, 0x00, 0x71, 0x01, 0x00, (byte) (0x96), 0x00, (byte) (0xb4)},
			{0x02, 0x00, 0x00, 0x71, 0x01, 0x00, (byte) (0xaa), 0x00, 0x64},
			{0x02, 0x00, 0x00, 0x71, 0x01, 0x00, (byte) (0xc8), 0x00, 0x36},
			{0x07, 0x00, 0x00, 0x71, 0x01, 0x00, 0x72, 0x00, 0x20},
	};
	private byte[][] irSens2 = {
			{(byte) (0xfd), 0x05},
			{(byte) (0xb3), 0x04},
			{0x63, 0x03},
			{0x35, 0x03},
			{0x1f, 0x03},
	};
*/
    public WiiRimokon(Wrj4P5 wii)
    {
    	   super();
    	   nunchaku = new WiiNunchaku();
    	   kurakon = new WiiKurakon();
    	   guitar = new WiiGuitar();
    	   this.parent = wii;
    }

    public WiiRemote addTalker(WiiRemote talker, int id, boolean ir, int irSens)
    {
 	   myId = id;
	   this.talker = talker;
	   birthTime = System.currentTimeMillis();
    	  try {
    		if (talker != null && talker.isConnected()) {
    			talker.setIRSensorEnabled(true, (ir?WRIREvent.EXTENDED:WRIREvent.BASIC),
   						(irSens<=0?IRSensitivitySettings.WII_LEVEL_1:
   						 irSens==1?IRSensitivitySettings.WII_LEVEL_2:
   	   					 irSens==2?IRSensitivitySettings.WII_LEVEL_3:
   	    					 irSens==3?IRSensitivitySettings.WII_LEVEL_4:
   	    						 	  IRSensitivitySettings.WII_LEVEL_5)
   	    				);
       			talker.setAccelerometerEnabled(true);
       			talker.setSpeakerEnabled(true);
       			talker.setLEDLights(isIlluminated);
    		} else {
    			System.out.println("Fail to connect. please restart the sketch.");
    		}
    	   } catch(Exception e) {e.printStackTrace();}
    	   return talker;
    }
    
    public void disconnected()
    {
        System.out.println("Remote disconnected... Please Wii again.");
        parent.disconnected(myId);
    }
    
    public void statusReported(WRStatusEvent evt)
    {
        batteryLevel = (float) evt.getBatteryLevel()/200;
        isIlluminated = evt.getLEDStatus();
    }
    
    public void IRInputReceived(WRIREvent evt)
    {
    		IRLight[] spots = evt.getIRLights();
    		for (int i=0;i<4;i++){
    			irLights[i].move((i<spots.length-1 && spots[i]!=null ? (float) spots[i].getX() : -1),
    							(i<spots.length-1 && spots[i]!=null ? (float) spots[i].getY() : -1),
    							(i<spots.length-1 && spots[i]!=null ? (float) spots[i].getSize() : -1) );
    		}
    }
    
    public void accelerationInputReceived(WRAccelerationEvent evt)
    {
            psensed.move(sensed);
            sensed.move(	(float)evt.getXAcceleration(),
            				(float)evt.getYAcceleration(),
            				(float)evt.getZAcceleration()
            			  );
            acc.move(sensed.mul(Wrj4P5.ONE_G)); 
            tNow++;            
    }
    
    public void extensionInputReceived(WRExtensionEvent evt)
    {
        if (evt instanceof WRNunchukExtensionEvent) {
        		nunchaku.inputEvent((WRNunchukExtensionEvent)evt);
             if (parent.nunchakuPressed != null 
            		 && isJustNunchakuPressed((WRNunchukExtensionEvent)evt) ) {
             	NunchakuPressedThread t = new NunchakuPressedThread(
             			(WRNunchukExtensionEvent) evt,
             			parent,
             			myId
             		);
             	t.run();
             }
            	if (parent.nunchakuReleased != null 
            			&& isJustNunchakuReleased((WRNunchukExtensionEvent)evt) ) {
             	NunchakuReleasedThread t = new NunchakuReleasedThread(
             			(WRNunchukExtensionEvent) evt,
             			parent,
             			myId
             		);
             	t.run();
             }
        } else if (evt instanceof WRClassicControllerExtensionEvent) {
        		kurakon.inputEvent((WRClassicControllerExtensionEvent)evt);
    			if (parent.kurakonPressed != null
    					&& isJustKurakonPressed((WRClassicControllerExtensionEvent)evt)) {
         		KurakonPressedThread t = new KurakonPressedThread(
         				(WRClassicControllerExtensionEvent) evt,
         				parent,
         				myId
         			);
         		t.run();
     		}
    			if (parent.kurakonReleased != null
    					&& isJustKurakonReleased((WRClassicControllerExtensionEvent)evt)) {
         		KurakonReleasedThread t = new KurakonReleasedThread(
         				(WRClassicControllerExtensionEvent) evt,
         				parent,
         				myId
         			);
         		t.run();
         	}
        } else if (evt instanceof WRGuitarExtensionEvent) {
    			guitar.inputEvent((WRGuitarExtensionEvent)evt);
			if (parent.guitarPressed != null
					&& isJustGuitarPressed((WRGuitarExtensionEvent)evt)) {
     		    GuitarPressedThread t = new GuitarPressedThread(
     				(WRGuitarExtensionEvent) evt,
     				parent,
     				myId
     			);
     		t.run();
 		}
			if (parent.guitarReleased != null
					&& isJustGuitarReleqsed((WRGuitarExtensionEvent)evt)) {
     		    GuitarReleasedThread t = new GuitarReleasedThread(
     		    			(WRGuitarExtensionEvent) evt,
     		    			parent,
     		    			myId
     				);
     		    t.run();
     	    	}
        } else {
        		System.err.println("Unknown extension connected!");
        }
    	}
    
    public void extensionConnected(WiiRemoteExtension extension)
    {
        System.out.println("Extension connected!");
        try
        {
            talker.setExtensionEnabled(true);
        }catch(Exception e){e.printStackTrace();}
    }
    
    public void extensionPartiallyInserted()
    {
//    	 TODO
        System.out.println("Extension(#"+myId+") partially inserted. Push it in more next time, jerk!");
    }
    
    public void extensionUnknown()
    {
// 		TODO
    		System.out.println("Extension(#"+myId+") unknown. Did you try to plug in a toaster or something?");
    }
    
    public void extensionDisconnected(WiiRemoteExtension extension)
    {
        System.out.println("Extension(#"+myId+") disconnected. Why'd you unplug it, retard?");
        parent.exDisconnected(myId);
    }
       
    public void buttonInputReceived(WRButtonEvent evt)
    {
    		if (parent.buttonPressed != null && isJustPressed(evt)) {
    			ButtonPressedThread t = new ButtonPressedThread(
     				(WRButtonEvent) evt,
     				parent,
     				myId
     			);
     		t.run();
    		}
    		if (parent.buttonReleased != null && isJustReleased(evt)) {
    			ButtonReleasedThread t = new ButtonReleasedThread(
         				(WRButtonEvent) evt,
         				parent,
         				myId
         			);
         		t.run();
        	}
     }
    
/*    public void combinedInputReceived(WRCombinedEvent evt)
    {
    	 	WRAccelerationEvent	aevt = evt.getAccelerationEvent();
    	 	if (aevt!=null) accelerationInputReceived(aevt);

         WRButtonEvent	bevt = evt.getButtonEvent(); 
 	 	if (bevt!=null) buttonInputReceived(bevt);

         WRExtensionEvent	exevt = evt.getExtensionEvent();
 	 	if (exevt!=null) extensionInputReceived(exevt);

         WRIREvent	ievt = evt.getIREvent();
 	 	if (ievt!=null) IRInputReceived(ievt);
     }
*/
 // <connection/communication>
    public void disconnect() { //disconnect the WiiRemote device
    		if (talker != null) {talker.disconnect();nunchaku=null;kurakon=null;talker=null;}
    		parent.disconnected(myId);
    }
//
    public boolean isConnected() {
    		return (canCommunicateMe() ? talker.isConnected() : false);
    }
    public boolean isExtensionConnected() {
    		return (canCommunicateMe() ? talker.isExtensionConnected() : false);
    }
    public void requestStatus() { // start getting the newest status report
    		if (!canCommunicateMe()) return;
    		try {
    			if (!talker.isReadingData()) talker.requestStatus();
    		} catch (Exception e){System.out.println(e);}
    }
    public boolean isReadingData() { // check if getting status
    		return (canCommunicateMe() ? talker.isReadingData() : false);
    }

//  <Battery>
    public float getBatteryLevel() { // get the latest battery level, 0(empty) to 1(full)
    		return batteryLevel;
    }

//  <LED Lights>
    public boolean isLED(int lid) { // check the specified LED illumination.
	    if (canCommunicateMe()) isIlluminated = talker.getLEDLights();
		return isIlluminated[(lid<0?0:(lid>3?3:lid))];
    }
    public void setLED(int lid, boolean illumination) { //set the specified LED illumination.
		try {
			isIlluminated[lid]=illumination;
			if (canCommunicateMe()) talker.setLEDLights(isIlluminated);
 	   } catch(Exception e) {e.printStackTrace();}
    }
    public boolean[] getLEDs() { //check the all LEDs illumination.
    	    if (canCommunicateMe()) isIlluminated = talker.getLEDLights();
		return isIlluminated;
    }
    public void setLEDs(boolean[] illumination) { //set the each LEDs illumination.
		try {
			boolean isSame = true;
			for (int i=0;i<4;i++) isSame = isSame && (isIlluminated[i]==illumination[i]);
			if (!isSame && canCommunicateMe()) {
				talker.setLEDLights(illumination);
				for (int i=0;i<4;i++) isIlluminated[i]=illumination[i];
			}
 	   } catch(Exception e) {e.printStackTrace();}
    }

//  <Speaker>
    public void bufferSound(AudioInputStream audioIn) { // prepare the prebufferd sound.(only one)
		try {
			prebuf = WiiRemote.bufferSound(audioIn);
		} catch (Exception e){System.out.println(e);}
    }
    public float getVolume() {// check the speaker's volume level, min(0) to max(1)
    		return (canCommunicateMe() ? (float) talker.getSpeakerVolume() : 0);
    }
    public void setVolume(float volume) {// set the speaker's volume level, min(0) to max(1)
    		if (!canCommunicateMe()) return;
    		try {
    			talker.setSpeakerVolume(volume);
    		} catch (Exception e){System.out.println(e);}
   }
    public boolean isPlaying() {// check if playing sound.
    		return (canCommunicateMe() ? talker.isPlayingSound() : false);
    }
    public void playBuffer() { // play the prebufferd sound.
    		if (!canCommunicateMe()) return;
    		try {
    			talker.playPrebufferedSound(prebuf, WiiRemote.SF_ADPCM4U);
    		} catch (Exception e){System.out.println(e);}
    }
    public void play(AudioInputStream audioIn) {// play the specified AudioInputStream sound.
		if (!canCommunicateMe()) return;
    		try {
    			talker.playSound(audioIn, WiiRemote.SF_ADPCM4U);
    		} catch (Exception e){System.out.println(e);}
    }
    public void stopSound() {// stop sound.
		if (canCommunicateMe()) talker.stopSound();
    }   
//    <Vibrator>
    public boolean isVibrating() {// check if vibrating
		return (canCommunicateMe() ? isVibrating=talker.isVibrating() : isVibrating);
    }
    public float getVibrationMagnitude(int magnitude) {// get vibration magnitude.(0 to 1)
    		return viblationMagnitude;
    }
    public void setVibrationMagnitude(int magnitude) {// set vibration magnitude.(0 to 1)
    		viblationMagnitude = (magnitude<0?0:(magnitude>1?1:magnitude));
    		isModurate = (magnitude>.9);
    		viblationInterval = (long) (20+200*(1-viblationMagnitude));
    }
    public void startVibrating() {// start vibration with current mag.
		if (!canCommunicateMe()) return;
		try {
			if (!isModurate) talker.startVibrating();
			else talker.startModulatedVibrating(viblationInterval);
		} catch (Exception e){System.out.println(e);}
    }
    public void vibrateFor(long time) {// vibration for given duration.(ms)
		if (!canCommunicateMe()) return;
		try {
			if (!isModurate) talker.vibrateFor((time>10?time:10));
			else talker.modulatedVibrateFor((time>10?time:10), viblationInterval);
		} catch (Exception e){System.out.println(e);e.printStackTrace();}
	}
    public void stopVibrating() {// stop vibration.
		if (!canCommunicateMe()) return;
		try {
			if (!isModurate) talker.stopModulatedVibrating();
			else talker.stopVibrating();
		} catch (Exception e){System.out.println(e);}
    }
    
//  <IRsencer>
    public boolean isIRSensorEnabled() {// check IRsensor enabled.
    		return (canCommunicateMe() ? talker.isIRSensorEnabled() : false);
    }

//  <Stable pitch and roll>
    public float stablePitch() {// exact only in stable handling
    		return (float) Math.asin(sensed.y/Wrj4P5.ONE_G);
}

    public float stableRoll() {// exact only in stable handling
		return (float) Math.asin(sensed.x/Wrj4P5.ONE_G);
}

//	utilities
    private boolean canCommunicateMe() {
    		if (talker==null) return false;
		long now = System.currentTimeMillis();
		if (now-birthTime<3000 || !talker.isConnected()) return false;
		long dlt = now-lastCom;
		if (dlt>100) lastCom = now;
    		return (dlt>100);
    }
    
    private boolean isJustPressed(WRButtonEvent evt){
        return (evt.wasPressed(WRButtonEvent.TWO)
        		|| evt.wasPressed(WRButtonEvent.ONE)
        		|| evt.wasPressed(WRButtonEvent.B)
        		|| evt.wasPressed(WRButtonEvent.A)
        		|| evt.wasPressed(WRButtonEvent.MINUS)
        		|| evt.wasPressed(WRButtonEvent.HOME)
        		|| evt.wasPressed(WRButtonEvent.LEFT)
        		|| evt.wasPressed(WRButtonEvent.RIGHT)
        		|| evt.wasPressed(WRButtonEvent.DOWN)
        		|| evt.wasPressed(WRButtonEvent.UP)
        		|| evt.wasPressed(WRButtonEvent.PLUS));
    }
    private boolean isJustNunchakuPressed(WRNunchukExtensionEvent evt){
        return (evt.wasPressed(WRNunchukExtensionEvent.C)
        		|| evt.wasPressed(WRNunchukExtensionEvent.Z));
    }
    private boolean isJustKurakonPressed(WRClassicControllerExtensionEvent evt){
        return (evt.wasPressed(WRClassicControllerExtensionEvent.A)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.B)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.X)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.Y)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.HOME)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.DPAD_LEFT)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.DPAD_RIGHT)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.DPAD_DOWN)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.DPAD_UP)
       		|| evt.wasPressed(WRClassicControllerExtensionEvent.LEFT_TRIGGER)
       		|| evt.wasPressed(WRClassicControllerExtensionEvent.RIGHT_TRIGGER)
       		|| evt.wasPressed(WRClassicControllerExtensionEvent.LEFT_Z)
       		|| evt.wasPressed(WRClassicControllerExtensionEvent.RIGHT_Z)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.MINUS)
        		|| evt.wasPressed(WRClassicControllerExtensionEvent.PLUS));
    }
    private boolean isJustGuitarPressed(WRGuitarExtensionEvent evt){
        return (evt.wasPressed(WRGuitarExtensionEvent.BLUE)
        		|| evt.wasPressed(WRGuitarExtensionEvent.GREEN)
        		|| evt.wasPressed(WRGuitarExtensionEvent.MINUS)
        		|| evt.wasPressed(WRGuitarExtensionEvent.ORANGE)
        		|| evt.wasPressed(WRGuitarExtensionEvent.PLUS)
        		|| evt.wasPressed(WRGuitarExtensionEvent.RED)
        		|| evt.wasPressed(WRGuitarExtensionEvent.STRUM_DOWN)
        		|| evt.wasPressed(WRGuitarExtensionEvent.STRUM_UP)
        		|| evt.wasPressed(WRGuitarExtensionEvent.YELLOW));
    }
    private boolean isJustReleased(WRButtonEvent evt){
        return (evt.wasReleased(WRButtonEvent.TWO)
        		|| evt.wasReleased(WRButtonEvent.ONE)
        		|| evt.wasReleased(WRButtonEvent.B)
        		|| evt.wasReleased(WRButtonEvent.A)
        		|| evt.wasReleased(WRButtonEvent.MINUS)
        		|| evt.wasReleased(WRButtonEvent.HOME)
        		|| evt.wasReleased(WRButtonEvent.LEFT)
        		|| evt.wasReleased(WRButtonEvent.RIGHT)
        		|| evt.wasReleased(WRButtonEvent.DOWN)
        		|| evt.wasReleased(WRButtonEvent.UP)
        		|| evt.wasReleased(WRButtonEvent.PLUS));
    }
    private boolean isJustNunchakuReleased(WRNunchukExtensionEvent evt){
        return (evt.wasReleased(WRNunchukExtensionEvent.C)
        		|| evt.wasReleased(WRNunchukExtensionEvent.Z));
    }
    private boolean isJustKurakonReleased(WRClassicControllerExtensionEvent evt){
        return (evt.wasReleased(WRClassicControllerExtensionEvent.A)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.B)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.X)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.Y)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.HOME)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.DPAD_LEFT)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.DPAD_RIGHT)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.DPAD_DOWN)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.DPAD_UP)
       		|| evt.wasReleased(WRClassicControllerExtensionEvent.LEFT_TRIGGER)
       		|| evt.wasReleased(WRClassicControllerExtensionEvent.RIGHT_TRIGGER)
       		|| evt.wasReleased(WRClassicControllerExtensionEvent.LEFT_Z)
       		|| evt.wasReleased(WRClassicControllerExtensionEvent.RIGHT_Z)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.MINUS)
        		|| evt.wasReleased(WRClassicControllerExtensionEvent.PLUS));
    }
    private boolean isJustGuitarReleqsed(WRGuitarExtensionEvent evt){
        return (evt.wasReleased(WRGuitarExtensionEvent.BLUE)
        		|| evt.wasReleased(WRGuitarExtensionEvent.GREEN)
        		|| evt.wasReleased(WRGuitarExtensionEvent.MINUS)
        		|| evt.wasReleased(WRGuitarExtensionEvent.ORANGE)
        		|| evt.wasReleased(WRGuitarExtensionEvent.PLUS)
        		|| evt.wasReleased(WRGuitarExtensionEvent.RED)
        		|| evt.wasReleased(WRGuitarExtensionEvent.STRUM_DOWN)
        		|| evt.wasReleased(WRGuitarExtensionEvent.STRUM_UP)
        		|| evt.wasReleased(WRGuitarExtensionEvent.YELLOW));
    }
}