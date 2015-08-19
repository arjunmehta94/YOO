#include "ADNS3530.h"
HardwareSerial* printer1;

ADNS3530::ADNS3530(int mosi, int miso, int sclk, int ncs, HardwareSerial &p)
{
	_MOSI = mosi;
	pinMode(_MOSI,OUTPUT);
	_MISO = miso;
	pinMode(_MISO,INPUT);
	_SCLK = sclk;
	pinMode(_SCLK,OUTPUT);
	_NCS = ncs;
	pinMode(_NCS,OUTPUT);

    _index=0;

    printer1 = &p;
    printer1->begin(9600);
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
}

void ADNS3530::resetData() {
	digitalWrite(_NCS, HIGH);
	digitalWrite(_NCS, LOW);
	write(POWER_UP_RESET, 0x5A);

	int8_t mot = read(MOTION);
	int8_t tempX = read(DELTA_X);
	int8_t tempY = read(DELTA_Y);
}

int8_t ADNS3530::checkCommunication() {
    return read(PRODUCT_ID);
}

bool ADNS3530::checkOverFlow() {
    return ((read(MOTION)<<1)>>7);
}

bool ADNS3530::available() {
    return (read(MOTION)>>7);
}

int8_t ADNS3530::readDeltaX() {
	return read(DELTA_X);
}

int8_t ADNS3530::readDeltaY() {
	return read(DELTA_Y);
}

int8_t ADNS3530::readSQUAL() {
	return read(SQUAL);
}

bool ADNS3530::readPixelGrab() {
    //int pixel_array[484];
	write(PIXEL_GRAB, 0);
	//int8_t pixel_array[462];
    /*while(!(read(MOTION) & 0b01000000)) {//ready to read
        printer1->println("waiting");
    }*/

    //printer1->println("ready to print");
    for(int i=0; i<22; i++) {
        for(int j=0; j<22; j++) {
            printer1->print(read(PIXEL_GRAB));
            printer1->print("\t");
        }
        printer1->println("");
        //printer1->print(read(PIXEL_GRAB));
        //printer1->print("reading");
        /*while(!read(MOTION) & 0b01000000) {
            printer1->print("lollllll");
        }*/
        //printer1->println("done");
    }
    delayMicroseconds(20);
    if(read(MOTION) & 0b00100000) {//MOTION register's PIXFIRST variable set to HIGH
        printer1->println("completed!");
        return true;
    }
    printer1->println("done");
    return false;
}

int8_t ADNS3530::read(int8_t address) {
	int8_t value=0;
    digitalWrite(_NCS, LOW);
    digitalWrite(_SCLK, HIGH);
    delayMicroseconds(10);
    address &= 0x7F;
    for(int address_bit=7; address_bit >=0; address_bit--){
        digitalWrite(_SCLK, LOW);
        if(address & (1<<address_bit)){
            digitalWrite(_MOSI, HIGH);
        }
        else{
            digitalWrite(_MOSI, LOW);
        }
        digitalWrite(_SCLK, HIGH);
    }
    delayMicroseconds(10);   
    for(int value_bit=7; value_bit >= 0; value_bit--){
        digitalWrite(_SCLK, LOW);
        digitalWrite(_SCLK, HIGH);
        //delayMicroseconds(10);
        if(digitalRead(_MISO))
			value |= (1<<value_bit);
    }
    delayMicroseconds(10);
    digitalWrite(_NCS, HIGH);
    delayMicroseconds(10);
    return value;
}

void ADNS3530::write(int8_t address, int8_t data) {
    digitalWrite(_SCLK, HIGH);
    digitalWrite(_NCS,LOW);

    delayMicroseconds(10);
    address |= 0x80;
    for(int address_bit=7; address_bit >=0; address_bit--){
        digitalWrite(_SCLK, LOW);
        if(address & (1<<address_bit))
        	digitalWrite(_MOSI, HIGH);
        else
        	digitalWrite(_SCLK, LOW);
        digitalWrite(_SCLK, HIGH);
    }
    for(int data_bit=7; data_bit >= 0; data_bit--){
        digitalWrite(_SCLK, LOW);
        if(data & (1<<data_bit))
        	digitalWrite(_MOSI, HIGH);
        else 
        	digitalWrite(_MOSI, LOW);
        digitalWrite(_SCLK, HIGH);
	}

	delayMicroseconds(20);
	digitalWrite(_NCS, HIGH);
	delayMicroseconds(30);
}