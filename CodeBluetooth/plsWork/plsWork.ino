#include <BluetoothHandler.h>
int outgoingbyte;    // outgoing data
int  analogX;    // input pin 1
int  analogY; // input pin 2
int x, y;
bool writing;
BluetoothHandler blu(Serial);
int digitalPressure;
void setup()
{
  Serial.begin(9600);
  analogX = 0; // initialization
  analogY = 1;
  x=0;
  y=0;
  writing = false;
  digitalPressure = 0;
}

void loop()
{
  x = analogRead(analogX)/64;
  y = analogRead(analogY)/64;
  if(writing != digitalRead(digitalPressure))
  {
    writing = digitalRead(digitalPressure);
    if(writing)
      blu.penDown();
    else
      blu.penUp();
  }
  outgoingbyte = x*16 + y;
  blu.send(outgoingbyte);
}
