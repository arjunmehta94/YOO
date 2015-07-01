#include "ADNS3530.h"
HardwareSerial* printer1;

ADNS3530::ADNS3530(HardwareSerial &print, int mosi, int miso, int sclk, int ncs) {
	printer1 = &print;
	printer1->begin(9600);
	_MOSI = mosi;
	pinMode(_MOSI,OUTPUT);
	_MISO = miso;
	pinMode(_MISO,INPUT);
	_SCLK = sclk;
	pinMode(_SCLK,OUTPUT);
	_NCS = ncs;
	pinMode(_NCS,OUTPUT);
}

void ADNS3530::checkLibrary() {
	printer1->print("ADNS3530Library :)");
}

void ADNS3530::powerUp() {
	digitalWrite(_NCS, HIGH);
	delayMicroseconds(1);
	digitalWrite(_NCS, LOW);
	delayMicroseconds(1);
	digitalWrite(_NCS, HIGH);
	delayMicroseconds(1);
	write(POWER_UP_RESET, 0x5A);

	int8_t mot = read(MOTION);
	int8_t tempX = read(DELTA_X);
	int8_t tempY = read(DELTA_Y);

	printer1->print("MOT: ");
	printer1->println(mot);
	printer1->print("DX: ");
	printer1->println(tempX);
	printer1->print("DY: ");
	printer1->print(tempY);
}

void ADNS3530::sync() {
	digitalWrite(_NCS, HIGH);
	digitalWrite(_NCS, LOW);
	write(POWER_UP_RESET, 0x5A);

	int8_t mot = read(MOTION);
	int8_t tempX = read(DELTA_X);
	int8_t tempY = read(DELTA_Y);

	/*printer1->print("MOT: ");
	printer1->println(mot);
	printer1->print("DX: ");
	printer1->println(tempX);
	printer1->print("DY: ");
	printer1->print(tempY);*/
}

int8_t ADNS3530::readProductID() {
	return read(PRODUCT_ID);
}

void ADNS3530::readMotion() {

}

int8_t ADNS3530::readDX() {
	return read(DELTA_X);
}

int8_t ADNS3530::readDY() {
	return read(DELTA_Y);
}

int8_t ADNS3530::read(int8_t address) {
	int8_t value=0;
	//pinMode(_MOSI, OUTPUT); //Make sure the SDIO pin is set as an output.
    digitalWrite(_NCS, LOW);
    digitalWrite(_SCLK, HIGH); //Make sure the clock is high.
    delayMicroseconds(10);
    address &= 0x7F;    //Make sure the highest bit of the address byte is '0' to indicate a read.
    //Send the Address to the PAW3205
    for(int address_bit=7; address_bit >=0; address_bit--){
        digitalWrite(_SCLK, LOW);  //Lower the clock
        if(address & (1<<address_bit)){
            digitalWrite(_MOSI, HIGH);
        }
        else{
            digitalWrite(_MOSI, LOW);
        }
        delayMicroseconds(10);
        digitalWrite(_SCLK, HIGH);
        delayMicroseconds(10);
    }
    //Serial.println("Yo");
    delayMicroseconds(10);   //Allow extra time for PAW3205 to transition the SDIO pin (per datasheet)
    //Make SDIO an input on the microcontroller
    //pinMode(_sdio, INPUT);	//Make sure the SDIO pin is set as an input.
	//digitalWrite(_sdio, HIGH); //Enable the internal pull-up
    //delayMicroseconds(10);
    for(int value_bit=7; value_bit >= 0; value_bit--){
        digitalWrite(_SCLK, LOW);  //Lower the clock
        //delayMicroseconds(1); //Allow the PAW3205 to configure the SDIO pin
        
        digitalWrite(_SCLK, HIGH);  //Raise the clock
        delayMicroseconds(10);
        //delayMicroseconds(1);
        if(digitalRead(_MISO))
			value |= (1<<value_bit);
		delayMicroseconds(10);
        //If the SDIO pin is high, set the current bit in the 'value' variable. If low, leave the value bit default (0).    
		//if((ADNS_PIN & (1<<ADNS_sdio)) == (1<<ADNS_sdio))value|=(1<<value_bit);
    }
    delayMicroseconds(10);
    digitalWrite(_NCS, HIGH);
    delayMicroseconds(10);
    return value;
}

void ADNS3530::write(int8_t address, int8_t data) {
    digitalWrite(_SCLK, HIGH);          //Make sure the clock is high.
    digitalWrite(_NCS,LOW);

    delayMicroseconds(10);
    address |= 0x80;    //Make sure the highest bit of the address byte is '1' to indicate a write.

    //Send the Address to the PAW3205
    for(int address_bit=7; address_bit >=0; address_bit--){
        digitalWrite(_SCLK, LOW); //Lower the clock
        
        //delayMicroseconds(10); //Give a small delay (only needed for the first iteration to ensure that the PAW3205 relinquishes
               		//control of SDIO if we are performing this write after a 'read' command.
        
        //If the current bit is a 1, set the SDIO pin. If not, clear the SDIO pin
        if(address & (1<<address_bit))
        	digitalWrite(_MOSI, HIGH);
        else
        	digitalWrite(_SCLK, LOW);
        delayMicroseconds(10);
        digitalWrite(_SCLK, HIGH);
        delayMicroseconds(10);
    }
    
    //Send the Value byte to the PAW3205
    for(int data_bit=7; data_bit >= 0; data_bit--){
        digitalWrite(_SCLK, LOW);  //Lower the clock
        //If the current bit is a 1, set the SDIO pin. If not, clear the SDIO pin
        if(data & (1<<data_bit))
        	digitalWrite(_MOSI, HIGH);
        else 
        	digitalWrite(_MOSI, LOW);
        delayMicroseconds(10);
        digitalWrite(_SCLK, HIGH);
        delayMicroseconds(10);
	}

	delayMicroseconds(20);
	digitalWrite(_NCS, HIGH);
	delayMicroseconds(30);
}