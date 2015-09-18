#include <BLE.h>
#include <ADNS9800.h>

//ADNS9800 adns(6,7,8,9,Serial);

ADNS9800 adns(9,8,6,7,5,&Serial);
BLE ble(&Serial1);
int index = 0;

void setup() {
  // put your setup code here, to run once:
    Serial.begin(9600);
    Serial1.begin(9600);
    while(!Serial);
    while(!Serial1);
    pinMode(9,INPUT);
    //ble.checkLibrary();
    adns.powerUp();
    adns.checkCommunication();
    //adns.powerUp();
    //adns.checkCommunication();
}

int8_t data[5];
int8_t buf[3];
int a = 0;
int b = 0;
short xH;
short yH;
short mergedX = 0;
short mergedY = 0;
unsigned long StartTime;
unsigned long CurrentTime;
unsigned long ElapsedTime; 
int prevXH = 0;
int prevYH = 0;
void loop() {

  if(!digitalRead(9)) {
        //StartTime = millis();
        adns.readXY(data);
        mergedX = (data[2]<<8) | (data[1] & 255);
        mergedY = (data[4]<<8) | (data[3] & 255);
        if ((data[0] >> 6) == -2  && (mergedX!=0 || mergedY!=0)) {
          ble.send(data[1], data[2], data[3], data[4], 1);
       }
    }

}







  
//    int8_t dX = adns.readDeltaX();
//    int8_t dY = adns.readDeltaY();
    
//    Serial.print("X:");
//    Serial.print(dX);
//    Serial.print(",Y: ");
//    Serial.println(dY);
//    if(digitalRead(9)) {
//        Serial.print("MOTION");
//    }
    
//    if(!(dX==0&&dY==0)) {
//        Serial.print(dX);
//        Serial.print('\t');
//        Serial.println(dY);
//        ble.send(dX,dY,127);
//    }

    //Serial.println(adns.getLiftDetection());


        
        //CurrentTime = millis();
        //ElapsedTime = CurrentTime-StartTime;
        //StartTime = CurrentTime;
        //Serial.print(ElapsedTime);
        //Serial.print('\t');
        //Serial.print(data[0]);
        //Serial.print('\t');
        //Serial.print(data[1]);
        //Serial.print('\t');
        //Serial.print(data[2]);
        //Serial.print('\t');
        //Serial.print(mergedX);
//        Serial.print('\t');
//        Serial.print(data[3]);
//        Serial.print('\t');
//        Serial.print(data[4]);
//        Serial.print('\t');
//        Serial.print(mergedY);
        //Serial.println();
//        Serial.print(xH);
//        Serial.print('\t');
//        Serial.print(yH);
//        Serial.print('\t');
//        Serial.print(mergedX);
//        Serial.print('\t');
//        Serial.println(mergedY);
        
        //Serial.println(millis());
//        prevXH = data[2];
//        prevYH = data[4];
//        if((prevXH>0&&mergedX<0)) {
//          
//        }
//        
//        if((prevXH>0 && mergedX>0)&&(prevYH>0 && mergedY>0)) {
//            ble.send(data[1], data[2], data[3], data[4], 1);
//        } else if((prevXH<-1 && mergedX<0)&&(prevYH<-1 && mergedY<0)) {
//            ble.send(data[1], data[2], data[3], data[4], 1);
//        } else if((prevXH==0&&prevYH==0)) {
//            ble.send(data[1], data[2], data[3], data[4], 1);
//        }
          
          
//        a += data[1];
//        b += data[3];
//        Serial.print(a);
//        Serial.print("\t");
//        Serial.println(b);
