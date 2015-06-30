//#include <QueueList.h>
#include <BluetoothHandler.h>

BluetoothHandler blu(Serial);
char x, y, rand;
void setup()
{
  Serial.begin(9600);
  
}

void loop()
{
  //simulate receiving signal from android
  rand = random(0, 255);
  for (int i = 0; i<rand; i++){
    x = random(-128, 127);
    y = random(-128, 127);
    blu.send(x, y, '*', i);
  }
  
//  x = random(0, 127);
//  y = random(0, 127);
  
}
