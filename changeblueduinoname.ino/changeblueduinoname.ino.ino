//http://wiki.aprbrother.com/wiki/Firmware/ZeroBeacon
#include <SoftwareSerial.h>

//SoftwareSerial mySerial(11, 12); //RX,TX

String tmp; 

void setup() {
  Serial.begin(9600);
  Serial1.begin(9600); 
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo only
  }
  Serial.println("hello!");
};

void loop() {

  while (Serial1.available() > 0)  {
    //Serial.print("response");
    tmp += char(Serial1.read());
    delay(2);
  }

  if(tmp.length() > 0) {
    Serial.println(tmp);
    tmp = "";
  }

  if (Serial.available()) {
    //Serial.println("found");
    Serial1.write(Serial.read());
  }
}
