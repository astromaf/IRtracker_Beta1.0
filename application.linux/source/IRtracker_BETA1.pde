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



import lll.wrj4P5.*;
import lll.Loc.*;
Wrj4P5 wii;

PrintWriter azul,amarillo,verde,rosa;

PFont fontCalibri;

color a;
color coloRec=#6A6666;
color colorClear=#6A6666;
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

void setup() {
  size(535,500);
  wii=new Wrj4P5(this).connect(Wrj4P5.IR);
  
  fontCalibri = loadFont("CaviarDreams-Bold-32.vlw");
  textFont(fontCalibri, 32);
 
 
  //Output Files
  azul = createWriter("Azul.txt");
  amarillo = createWriter("Amarillo.txt");
  verde = createWriter("Verde.txt");
  rosa = createWriter("Rosa.txt");
  
  background(#3E3C3D);
  patio();
 
} //end setup


/////////DRAW///////////

void draw() {
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




