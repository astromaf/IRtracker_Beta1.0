/////////////////////////////////////////
void mousePressed() {
  
  if(mouseX > 9 && mouseX <111  && mouseY > 9 && mouseY < 51){ //Red button area
    if(srec==false){
    coloRec=#C60606;//rojo fuerte- Red
    rec=true;
    srec=true;
    s=millis();
    
    }
    else if (srec==true){
      coloRec=#6A6666; //gris fuerte_ Grey
      rec=false;
      srec=false;
    }
 
  }//if
  if(mouseX > 119 && mouseX <220  && mouseY > 9 && mouseY < 51){ //Clear button area
    colorClear=#BCB409;//
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
void mouseMoved() { 
  checkButtons(); //It is useful to detect when the mouse is over a button
}

/////////////////////////////////////////
/*void mouseReleased() { //For testing, not really necessary here

checkButtons();
}//mouse realeased*/

/////////////////////////////////////////
void checkButtons() { //We check is the mouse is over one of the button. If true, we change the color of the button.
 
 if(mouseX > 9 && mouseX <111  && mouseY > 9 && mouseY < 51){ //Rec button area
   
    if(srec==false){
    coloRec=#837F7F;//gris suave- Light Grey 
    
    }
    else if (srec==true){
      coloRec=#B24141;//rojo suave -Light Red
      
    }
 
  }//if
  
  else if( mouseX > 119 && mouseX <220  && mouseY > 9 && mouseY < 51){ // Clear button area
  
   colorClear=#837F7F;//gris suave_ Light grey
   
   if(srec==false){
    coloRec=#6A6666; //gris fuerte_ Grey
    
   
    }
    else if (srec==true){
      coloRec=#C60606;//rojo fuerte_ Red
      
    }
  
  }
  
   else{
     colorClear=#6A6666; //gris fuerte_ Grey
   
    if(srec==false){
    coloRec=#6A6666; //gris fuerte_ grey
    
   
    }
    else if (srec==true){
      coloRec=#C60606;//rojo fuerte_Red
      
    }
 
  }//if
  
}
/////////////////////////////////////////

void keyPressed() { // Press ENTER key to save the data

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

