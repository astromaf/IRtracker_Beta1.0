///////////////////////

void botones(){ //Two buttons are created
  //REC
  rec(); //The rec one
  
  //Clear
  clr(); //And the clear

}

//////////////////////////////////
void registrar(int i){  //In this subroutine we  distinguish the 4 markers by color and store data in a txt file
  
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
         a=#1A48B9;//azul
         azul.println(px + "\t" +py);
        break;
       case 1:
         a=#F9FA05;//amarillo
         amarillo.println(x + "\t" +y);
        break;
       case 2:
         a=#26BF25;//verde
         verde.println(x + "\t" +y);
        break;
       case 3:
         a=#F7347F;//rosa
         rosa.println(x + "\t" +y);
        break; 
   }//switch
  }//IF
  
  else{
  
  switch(i){ //If rec not clicked, only paint the tracking point
       case 0:
         a=#1A48B9;//azul
         
        break;
       case 1:
         a=#F9FA05;//amarillo
        
        break;
       case 2:
         a=#26BF25;//verde
        
        break;
       case 3:
         a=#F7347F;//rosa
         
        break; 
   }//switch
   
  }//else
 }
 
 ///////////////////////////////////////
 void cronometro(){ //We use this function to measure the recording time
   
    fill(#3E3C3D);
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
 void track(){  //The HEART of the program. Based on WiiMote library. 
 
 for (int i=0;i<4;i++) { //We look for 4 IR lights and track tehm.
    Loc p=wii.rimokon.irLights[i];
      
   if (p.x>-1) {
      smooth();
      
      x=p.x*width/2;
      y= (1.-p.y)*height/2;
      
      registrar(i); //differents colors. Save the data file
       
       fill(a);
       ellipse(x+14,y+64, 10, 10); //The track marker circle.
       
    }//if
    
  }//for
  
 }
  
  ////////////////////////////////////
  
  void clr(){ //We create a Button to clear the screen and file
   //Clear
  
  fill(colorClear);
  stroke(255);
  rect(120,10,100,40);
  
  fill(255);
  smooth();
  text("Clear", 130,42);
   
  
  }
  
  ///////////////////////
  
  void rec(){ //When REC is clicked the data from the tracking is saved to a txt file
  
   //REC
  fill(coloRec);
  stroke(255);
  rect(10,10,100,40);
  
  fill(255);
  smooth();
  text("REC.", 30,42);
  
  
  
  }
  
  ///////////////////////
  
  void patio(){ //This function create the area where the track markers will be paint
 
  stroke(128);
  fill(#2D2C31);
  //stroke(255);
  rect(10,60,512,384);
  
  noStroke(); 
  
  }
  
  ///////////////////////
