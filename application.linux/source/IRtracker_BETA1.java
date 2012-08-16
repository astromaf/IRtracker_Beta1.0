import processing.core.*; 
import processing.xml.*; 

import lll.wrj4P5.*; 
import lll.Loc.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class IRtracker_BETA1 extends PApplet {

/*
' =========================================================================
'
'   File...... IRtracker Beta-v1 .PDE (Processing Language)
'   Purpose... Demonstrate features of Wiimote device for Robotics Tracking purpose
'   Authors... Juan Gonzalez Gomez (Obijuan) .&&. Miguel Angel de Frutos Carro
'   E-mail.... ma.defrutoscarro@gmail.com
'   Updated... 18/April/12
'
' =========================================================================

' -----[ Program Description ]---------------------------------------------
    We want demonstrate features of Wiimote device to be used as an easy and fast "tracking purpose" in robotics.
    This is an app made using Processing to track up to 4 Ir lights using a Wiimote Device linked by Bluetooth.
    You must install all the libraries listed in .zip  in the Library Proceessing Folder.
    Turn on the Bluetooth of computer.
    Execute the program. Press  wimote buttons 1 and 2 to link the remote control.
    When blue lights stop blinking, the linking has been stablished sucessfully.
    Now you can track up to 4 IR emitters located in your robotics platform.
    When you are ready to record the movement press REC button.
    Then this button become red and the IR marker path start to display on area terminal.
    While REC button is active the chronometer show you the time of the recorder process.
    In the same folder where is located the program it will be created 4 diferents TXT file where the X-Y coordinates of each IR Tracker.
    You can distinguish it by the color.(Blue, is First - Yellow - Green - Pink.)
    If something was wrong, you can presh Clear button to restart the the process.
    Before exit, PRESS ENTER to save the data in the files.
*/





Wrj4P5 wii;

PrintWriter azul,amarillo,verde,rosa;

PFont fontCalibri;

int a;
int coloRec=0xff6A6666;
int colorClear=0xff6A6666;
float x,y,px,py,Fx,Fy;
boolean rec=false;
boolean srec=false;
boolean clr=false;
boolean marc=false;
boolean Ftime=true;

int h,h1;
int m,m1;
float s,s1;

float time,time2;


//////////SETUP///////////

public void setup() {
  size(535,500);
  wii=new Wrj4P5(this).connect(Wrj4P5.IR);
  
  fontCalibri = loadFont("CaviarDreams-Bold-32.vlw");
  textFont(fontCalibri, 32);
 
 
  //Output Files
  azul = createWriter("Azul.txt");
  amarillo = createWriter("Amarillo.txt");
  verde = createWriter("Verde.txt");
  rosa = createWriter("Rosa.txt");
  
  background(0xff3E3C3D);
  patio();
 
} //end setup


/////////DRAW///////////

public void draw() {
   //if (wii.isConnecting()) return;rop
   
  if(rec==true){
   marc=true;
  }
  else if( (rec==false)&&(marc==false) ){///estoy aqui
  patio();
  
  }  
  time=millis();
  
  botones();
  cronometro();
  track();
  
}//draw




///////////////////////

public void botones(){ //Two buttons are created
  //REC
  rec(); //The rec one
  
  //Clear
  clr(); //And the clear

}

//////////////////////////////////
public void registrar(int i){  //In this subroutine we  distinguish the 4 markers by color and store data in a txt file
  
py=(500-y);
px=x;

   
 //println("x="+px+" y="+y);
  
  if(rec==true){
  
    if(Ftime==true){ //Save the tracker point first value to scale.
     Fx=px;
     Fy=py;   
    Ftime=false;
    }
      
      py=py-Fy;
      px=px-Fx;
    
   switch(i){   //If rec is clicked store the data
       case 0:
         a=0xff1A48B9;//azul
         azul.println(px + "\t" +py);
        break;
       case 1:
         a=0xffF9FA05;//amarillo
         amarillo.println(x + "\t" +y);
        break;
       case 2:
         a=0xff26BF25;//verde
         verde.println(x + "\t" +y);
        break;
       case 3:
         a=0xffF7347F;//rosa
         rosa.println(x + "\t" +y);
        break; 
   }//switch
  }//IF
  
  else{
  
  switch(i){ //If rec not clicked, only paint the tracking point
       case 0:
         a=0xff1A48B9;//azul
         
        break;
       case 1:
         a=0xffF9FA05;//amarillo
        
        break;
       case 2:
         a=0xff26BF25;//verde
        
        break;
       case 3:
         a=0xffF7347F;//rosa
         
        break; 
   }//switch
   
  }//else
 }
 
 ///////////////////////////////////////
 public void cronometro(){ //We use this function to measure the recording time
   
    fill(0xff3E3C3D);
 noStroke();
 rect(380,10,200,50);
   
  if(rec==true){
   
 
 // h1=hour();
 // m1=minute();
  s1=millis();
  
  s1=s1-s;
  s1=s1/100;
  if(s1>60){
  m1=m1+1;
  s1=0;
  s=millis();
  }
  
  
  fill(255);
 text(h1+ ":"+ m1+ ":"+ s1, 390, 42);
  }
  
  else{  //If res is not clicked, the time must be 0:0:0.0
  //fill(255);
 //text("0:0:0", 410, 42);
  fill(255);
  text(h1+ ":"+ m1+ ":"+ s1, 390, 42);
  }

 
 }
 
 
 ////////////////////////////////////
 public void track(){  //The HEART of the program. Based on WiiMote library. 
 
 for (int i=0;i<4;i++) { //We look for 4 IR lights and track tehm.
    Loc p=wii.rimokon.irLights[i];
      
   if (p.x>-1) {
      smooth();
      
      x=p.x*width/2;
      y= (1.f-p.y)*height/2;
      
      registrar(i); //differents colors. Save the data file
       
       fill(a);
       ellipse(x+14,y+64, 10, 10); //The track marker circle.
       
    }//if
    
  }//for
  
 }
  
  ////////////////////////////////////
  
  public void clr(){ //We create a Button to clear the screen and file
   //Clear
  
  fill(colorClear);
  stroke(255);
  rect(120,10,100,40);
  
  fill(255);
  smooth();
  text("Clear", 130,42);
   
  
  }
  
  ///////////////////////
  
  public void rec(){ //When REC is clicked the data from the tracking is saved to a txt file
  
   //REC
  fill(coloRec);
  stroke(255);
  rect(10,10,100,40);
  
  fill(255);
  smooth();
  text("REC.", 30,42);
  
  
  
  }
  
  ///////////////////////
  
  public void patio(){ //This function create the area where the track markers will be paint
 
  stroke(128);
  fill(0xff2D2C31);
  //stroke(255);
  rect(10,60,512,384);
  
  noStroke(); 
  
  }
  
  ///////////////////////
/////////////////////////////////////////
public void mousePressed() {
  
  if(mouseX > 9 && mouseX <111  && mouseY > 9 && mouseY < 51){ //Red button area
    if(srec==false){
    coloRec=0xffC60606;//rojo fuerte- Red
    rec=true;
    srec=true;
    s=millis();
    
    }
    else if (srec==true){
      coloRec=0xff6A6666; //gris fuerte_ Grey
      rec=false;
      srec=false;
    }
 
  }//if
  if(mouseX > 119 && mouseX <220  && mouseY > 9 && mouseY < 51){ //Clear button area
    colorClear=0xffBCB409;//
    Ftime=true;
    clr(); 
    h1=0;
    m1=0;
    s1=0;
    marc=false;
    patio();
  
  
  }
  
}//end mouse pressed


/////////////////////////////////////////
public void mouseMoved() { 
  checkButtons(); //It is useful to detect when the mouse is over a button
}

/////////////////////////////////////////
/*void mouseReleased() { //For testing, not really necessary here

checkButtons();
}//mouse realeased*/

/////////////////////////////////////////
public void checkButtons() { //We check is the mouse is over one of the button. If true, we change the color of the button.
 
 if(mouseX > 9 && mouseX <111  && mouseY > 9 && mouseY < 51){ //Rec button area
   
    if(srec==false){
    coloRec=0xff837F7F;//gris suave- Light Grey 
    
    }
    else if (srec==true){
      coloRec=0xffB24141;//rojo suave -Light Red
      
    }
 
  }//if
  
  else if( mouseX > 119 && mouseX <220  && mouseY > 9 && mouseY < 51){ // Clear button area
  
   colorClear=0xff837F7F;//gris suave_ Light grey
   
   if(srec==false){
    coloRec=0xff6A6666; //gris fuerte_ Grey
    
   
    }
    else if (srec==true){
      coloRec=0xffC60606;//rojo fuerte_ Red
      
    }
  
  }
  
   else{
     colorClear=0xff6A6666; //gris fuerte_ Grey
   
    if(srec==false){
    coloRec=0xff6A6666; //gris fuerte_ grey
    
   
    }
    else if (srec==true){
      coloRec=0xffC60606;//rojo fuerte_Red
      
    }
 
  }//if
  
}
/////////////////////////////////////////

public void keyPressed() { // Press ENTER key to save the data

if(key==ENTER){
  azul.flush(); // Write the remaining data
  azul.close(); // Finish the file
  
  amarillo.flush();
  amarillo.close();
  
  verde.flush();
  verde.close();
  
  rosa.flush();
  rosa.close();
  
  exit(); // Stop the program. Just you do it.
}
}
//////////////////////////////

  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "IRtracker_BETA1" });
  }
}
