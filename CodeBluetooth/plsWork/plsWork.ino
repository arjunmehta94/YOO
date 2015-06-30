#include <BluetoothHandler.h>

BluetoothHandler blu(Serial);
char x, y;
int rand1;
void setup()
{
  Serial.begin(9600);
  
}

void loop()
{
  //simulate receiving signal from android
  rand1 = random(255);
  for (char i = 0; i<rand1; i++){
    x = random(-128, 127);
    y = random(-128, 127);
    blu.send(x, y, '*', i);
  }
  
//  x = random(0, 127);
//  y = random(0, 127);
  
}
