/*
 Main part of the WRJ4P5 lib - https://sourceforge.jp/projects/wrj4p5
 
 the Model of the Wii device for Processing/Proce55ing/P5

 Copyright (c) 2007 Classiclll

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General
 Public License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 Boston, MA  02111-1307  USA
 */

package lll.wrj4P5;
import processing.core.*;
import wiiremotej.*;
import wiiremotej.event.*;

//import java.awt.Image;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Wrj4P5 is the base class for managing the available WiiRemotes and extentions. 
 * It provides you with methods
 *  to get information from WiiRemotes and one of extensins
 * and
 *  to communicate with some WiiRemotes, extensions and/or Wii Balance Boards
 * on your sketch.

[events for your sketch]
 void buttonPressed(RimokonEvent ev, int rid)	: some button pressed on WiiRemote.
 void nunchakuPressed(NunchukEvent ev, int rid)	: some button/stick pressed on Nunchuk.
 void kurakonPressed(KurakonEvent ev, int rid)	: some button/stick pressed on 
 void guitarPressed(GuitarEvent ev, int rid)	: some button/stick pressed on Wii Guitar.
 void boardPressed(int bid)	: the button pressed on Balance Board.
 void buttonReleased(RimokonEvent ev, int rid)	: some button released on WiiRemote.
 void nunchakuReleased(NunchukEvent ev, int rid)	: some button/stick released on Nunchuk.
 void kurakonReleased(KurakonEvent ev, int rid)	: some button/stick released on Classic Controller.
 void guitarReleased(GuitarEvent ev, int rid)	: some button/stick released on Wii Guitar.
 void boardReleased(int bid)	: the button released on Balance Board.
 void disconnected(int rid)	: some WiiRemote is disconnected.
 void exDisconnected(int rid)	: a Nunchuk/ClassicController is disconnected from some WiiRemote.

[class member/method]
  PApplet parent;
  int dCount;	: the number of WiiDevices(motes/boards) connected.(at most 7)
  int rCount;	: the number of WiiRemotes connected.(at most 7)
  int bCount;	: the number of WiiBoard connected.(at most 7)
  float ONE_G = 9.80665f;	:  Gravity_Unit(m/sec/sec)
  boolean IR=true, EX=false;	: 'EX' is with extensions (no information of IR soptsize)
		: 'IR' is with IR soptsize (no information from any extention)
  WiiBoard board;	: the first connected WiiBoard.
  WiiRimokon rimokon;	: the first connected WiiRemote.
  WiiNunchaku nunchaku;	: the Nunchuk of the first WiiRemote.
  WiiKurakon kurakon;	: the ClassicController of the first WiiRemote.
  WiiGuitar guitar;	: the WiiGuitar of the first WiiRemote.
  WiiBoard board(int bid)	: the (bid+1)th WiiBoard, or null
  WiiRimokon rimokon(int rid)	: the (rid+1)th WiiRemote, or null
  WiiNunchaku nunchaku(int rid)	: the (rid+1)th Nunchuk, or null
  WiiKurakon kurakon(int rid)	: the (rid+1)th ClassicController, or null
  WiiGuitar guitar(int rid)	: the (rid+1)th WiiGuitar, or null
  boolean isConnecting()	: if connection is not completed, draws a splash screen

  public Wrj4P5(PApplet parent)	: only one constructor

// connect, must call after the construction of this instance. (asynchronized process)
  Wrj4P5 connect()	= connect(1)
  Wrj4P5 connect(int n)	= connect(n, false, false)
  Wrj4P5 connect(boolean ir)	= connect(1, ir, false)
  Wrj4P5 connect(int n, boolean ir)	= connect(n, ir, false)
  Wrj4P5 connect(int n, boolean ir, boolean log)	= connect(n, ir, log, 2)
  Wrj4P5 connect(int n, boolean ir, boolean log, int sense)
	int n  : number of WiiRemotes to be connected.(1 is default)
	bool ir : EX(use extensions - default) / IR(use IR spotsize) 
	bool log: do you want a detailed log?(false is default)
	int sense: sensitivity of IR-Camera, 0(lowest)/1/2(default)/3/4(highest) 
*/

public class Wrj4P5 implements WiiDeviceDiscoveryListener {
  public PApplet parent;
  public static int dCount;// the number of devices connected.(at most 7)
  public static int rCount;// the number of WiiRemotes connected.(at most 7)
  public static int bCount;// the number of BalanceBoard connected.(at most 7)
  public static final float ONE_G = 9.80665f; // Gravity_Unit(m/sec/sec)
  public static final boolean IR=true, EX=false;
  private boolean isIR=EX;
  private int irSens=2;

  public static WiiRimokon rimokon;   //WiiRemoteAdapter=Listener is the WiiRemote, first connected.
  private static WiiRimokon[] rims = new WiiRimokon[7];// connected WiiRemotes(at most 7)

  public static WiiBoard board;   //WiiRemoteAdapter=Listener is the WiiRemote, first connected.
  private static WiiBoard[] brds = new WiiBoard[7];// connected WiiRemotes(at most 7)

  public static WiiNunchaku nunchaku; //WiiNunchakuExtention is the Nunchuk of the first WiiRemote.
//  private static WiiNunchaku[] nuns = new WiiNunchaku[7];// connected Nunchuks(at most 7)

  public static WiiKurakon kurakon;   //WiiClassicControllerExtention  is the ClassicController of the first WiiRemote.
//  private static WiiKurakon[] kurs = new WiiKurakon[7];// connected ClassicControllers(at most 7)
  
  public static WiiGuitar guitar;   //WiiGuitarExtention  is the Guitar of the first WiiRemote.
//  private static WiiGuitar[] guits = new WiiGuitar[7];// connected Guitar(at most 7)

  private static PImage waitingImg;

  /**
   * void buttonPressed(RimokonEvent ev, int rid) Call when some button pressed on WiiRemote.
   * Method to check if the parent Applet calls buttonPressed
   */
  Method buttonPressed;
  /**
   * void nunchakuPressed(NunchukEvent ev, int rid) Call when some button/stick pressed on Nunchuk.
   * Method to check if the parent Applet calls nunchakuPressed
   */
  Method nunchakuPressed;
  /**
   * void kurakonPressed(KurakonEvent ev, int rid) Call when some button/stick pressed on Classic Controller.
   * Method to check if the parent Applet calls kurakonPressed
   */
  Method kurakonPressed;
  /**
   * void guitarReleased(GuitarEvent ev, int rid) Call when some button pressed on Wii Guitar.
   * Method to check if the parent Applet calls guitarPressed
   */
  Method guitarPressed;
  /**
   * void guitarReleased(int bid) Call when some button pressed on Balance Board.
   * Method to check if the parent Applet calls boardPressed
   */
  Method boardPressed;
  /**
   * void buttonReleased(RimokonEvent ev, int rid) Call when some button released on WiiRemote.
   * Method to check if the parent Applet calls buttonPressed
   */
  Method buttonReleased;
  /**
   * void nunchakuReleased(NunchukEvent ev, int rid) Call when some button/stick released on Nunchuk.
   * Method to check if the parent Applet calls nunchakuPressed
   */
  Method nunchakuReleased;
  /**
   * void kurakonReleased(KurakonEvent ev, int rid) Call when some button/stick released on Classic Controller.
   * Method to check if the parent Applet calls kurakonReleased
   */
  Method kurakonReleased;
  /**
   * void guitarReleased(GuitarEvent ev, int rid) Call when some button/stick released on Wii Guitar.
   * Method to check if the parent Applet calls guitarReleased
   */
  Method guitarReleased;
  /**
   * void boardReleased(int bid) Call when some button/stick released on Balance Board.
   * Method to check if the parent Applet calls boardReleased
   */
  Method boardReleased;
  /**
   * void disconnected(int rid) Call when some WiiRemote is disconnected.
   * Method to check if the parent Applet calls disconnected
   */
  Method disconnected;
  /**
   * void exDisconnected(int rid) Call when a Nunchuk/ClassicController is disconnected from some WiiRemote.
   * Method to check if the parent Applet calls exDisconnected
   */
  Method exDisconnected;
 /*
  *constructor
  */
  public Wrj4P5(PApplet parent) {
	this.parent = parent;
	this.isIR = EX;
	prepareReflection();
  }
  private void prepareReflection() {
//  void buttonPressed(RimokonEvent ev, int rid) Call when some button pressed on WiiRemote.
	try{
		buttonPressed = parent.getClass().getMethod("buttonPressed", new Class[] {RimokonEvent.class,Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
// void nunchakuPressed(NunchukEvent ev, int rid) Call when some button/stick pressed on Nunchuk.
	try{
		nunchakuPressed = parent.getClass().getMethod("nunchakuPressed", new Class[] {NunchakuEvent.class,Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
//	 void kurakonPressed(KurakonEvent ev, int rid) Call when some button/stick pressed on Classic Controller.
	try{
		kurakonPressed = parent.getClass().getMethod("kurakonPressed", new Class[] {KurakonEvent.class,Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
//	 void guitarPressed(GuitarEvent ev, int rid) Call when some button/stick pressed on WiiGuitar.
	try{
		guitarPressed = parent.getClass().getMethod("guitarPressed", new Class[] {GuitarEvent.class,Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
//	 void boardPressed(GuitarEvent ev, int bid) Call when some button/stick pressed on Balance Board.
	try{
		boardPressed = parent.getClass().getMethod("boardPressed", new Class[] {Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
//	 void buttonReleased(RimokonEvent ev, int rid) Call when some button released on WiiRemote.
	try{
		buttonReleased = parent.getClass().getMethod("buttonReleased", new Class[] {RimokonEvent.class,Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
// void nunchakuReleased(NunchukEvent ev, int rid) Call when some button/stick released on Nunchuk.
	try{
		nunchakuReleased = parent.getClass().getMethod("nunchakuReleased", new Class[] {NunchakuEvent.class,Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
//	 void kurakonReleased(KurakonEvent ev, int rid) Call when some button/stick released on Classic Controller.
	try{
		kurakonReleased = parent.getClass().getMethod("kurakonReleased", new Class[] {KurakonEvent.class,Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
//	 void guitarReleased(GuitarEvent ev, int rid) Call when some button/stick released on Wii Guitar.
	try{
		guitarReleased = parent.getClass().getMethod("guitarReleased", new Class[] {GuitarEvent.class,Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
//	 void boardReleased(BoardEvent ev, int bid) Call when some button/stick released on Balance Board.
	try{
		boardReleased = parent.getClass().getMethod("boardReleased", new Class[] {Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
// void disconnected(int rid) Call when some WiiRemote is disconnected.
	try{
		disconnected = parent.getClass().getMethod("disconnected", new Class[] {Integer.TYPE});
	}catch (Exception e){/* just ignore */ }
// void exDisconnected(int rid) Call when a Nunchuk/ClassicController is disconnected from some WiiRemote.
	try{
		exDisconnected = parent.getClass().getMethod("exDisconnected", new Class[] {Integer.TYPE});
	}catch (Exception e){/* just ignore */ }

//	this.parent.registerPre(this);// The requirement of PApplet of Proce55ing
    this.parent.registerDispose(this);// The requirement of PApplet of Proce55ing

    for (int i=0;i<7;i++) {
    		rims[i]=new WiiRimokon(this);
    		brds[i]=new WiiBoard(this);
    }
	rimokon=rims[0];
	nunchaku=rims[0].nunchaku;
	kurakon=rims[0].kurakon;
	guitar=rims[0].guitar;
	board = brds[0];
  }

/*
  connect one or more remotes�B
*/
  public Wrj4P5 connect() {
	  return this.connect(1);
  }
  public Wrj4P5 connect(boolean ir) {
	  return this.connect(1, ir, false);
  }
  public Wrj4P5 connect(int n) {
	  return this.connect(n, false, false);
  }
  public Wrj4P5 connect(int n, boolean ir) {
	  return this.connect(n, ir, false);
  }
  public Wrj4P5 connect(int n, boolean ir, boolean log){
	  return this.connect(n, ir, log, 2);
  }
  public Wrj4P5 connect(int n, boolean ir, boolean log, int sense)
  {
		isIR = ir;
		irSens = Math.max(4,Math.min(0,sense));
		URL resource = null;
		Image img = null;
		try {
			if (log) {WiiRemoteJ.setConsoleLoggingAll();}
			else 	{WiiRemoteJ.setConsoleLoggingOff();} 
			WiiRemoteJ.findDevices(this, WiiRemoteJ.ALL, (int) (0<n&&n<8 ? n : 1));
			System.out.println("trying to find a wii");
		} catch (IllegalStateException e) {
			System.out.println("!! There may be no USB dongle/device. !!");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
// get the path of varid CodeBase, both under online and offline. thanky you, fjen. 
		if (waitingImg==null) {
			File f = new File((parent.online?"": parent.sketchPath("")) + "data/WiiStart.jpg"); 
			if (f.exists()) { 
				try {
					resource = f.toURL();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			} else {
				resource = this.getClass().getResource("WiiStart.jpg");
			}
//
		    if (parent.online && resource!=null) {
		      img = parent.getImage(resource);
		    } 
		    else if (resource!=null) {
		      img = Toolkit.getDefaultToolkit().getImage(resource);
		    }
//		    if (img!=null) waitingImg = parent.loadSImageync(img);
		    if (img!=null) waitingImg = parent.loadImage(resource.toString());
		}
		return this;
  }

  public void wiiDeviceDiscovered(WiiDeviceDiscoveredEvent evt) {
	  int id = evt.getNumber();
	  WiiDevice found = evt.getWiiDevice();
      if (found instanceof WiiRemote) {
    	    id = rCount++;
	    ((WiiRemote) found).addWiiRemoteListener(rims[id]);
		rims[id].addTalker((WiiRemote) found,id,isIR,irSens);
		System.out.println("Discover Remote called # " + id);
      } else if (found instanceof BalanceBoard) {
    	    id = bCount++;
  	    ((BalanceBoard) found).addBalanceBoardListener(brds[id]);
		brds[id].addTalker((BalanceBoard) found,id);
		System.out.println("Discover Board called # " + id);    	  
      }
      dCount++;
	}

  public void findFinished(int numberFound) {
        dCount = numberFound + 1;		
  }
//return the (bid+1).th WiiBoard, or null if no connected.
  public WiiBoard board(int bid) {
	  return (0<=bid&&bid<bCount ? brds[bid] : board);
  }
//return the (rid+1).th WiiRemote, or null if no connected.
  public WiiRimokon rimokon(int rid) {
	  return (0<=rid&&rid<rCount ? rims[rid] : rimokon);
  }
// return the (rid+1).th Nunchuk, or null if no connected.
  public WiiNunchaku nunchaku(int rid) {
	  return (0<=rid&&rid<rCount ? rims[rid].nunchaku : nunchaku);
  }
//return the (rid+1).th ClassicController, or null if not connected.
  public WiiKurakon kurakon(int rid) {
	  return (0<=rid&&rid<rCount ? rims[rid].kurakon : kurakon);
  }
//return the (rid+1).th Guitar, or null if not connected.
  public WiiGuitar guitar(int rid) {
	  return (0<=rid&&rid<rCount ? rims[rid].guitar : guitar);
  }
//
  public void disconnected(int rid) {
   	try{
		if (disconnected != null) 
			disconnected.invoke(
				parent, 
				new Object[] {
					new Integer(rid),
				}
			);
	}catch (Exception e){
		System.err.println("Disabling controller() for " + parent.getName() + " because of an error.");
		e.printStackTrace();
		disconnected = null;
	}
  }
//
  public void exDisconnected(int rid) {
   	try{
		if (exDisconnected != null) 
			exDisconnected.invoke(
				parent, 
				new Object[] {
					new Integer(rid),
				}
			);
	}catch (Exception e){
		System.err.println("Disabling controller() for " + parent.getName() + " because of an error.");
		e.printStackTrace();
		exDisconnected = null;
	}
  }
//
  public void pre() {
//	TODO
  }
 
  public void dispose() {
	  for(int i=0;i<rCount;i++) rims[i].disconnect();
	  for(int i=0;i<bCount;i++) brds[i].disconnect();
  }

// Only part needs PApplet
  public boolean isConnecting() {
	if (dCount>0) return false;
	parent.background(0);
	if (waitingImg!=null) parent.image(waitingImg
							,parent.width/2-waitingImg.width/2
							,parent.height/2-waitingImg.height/2
							);
	return true;
  }
}