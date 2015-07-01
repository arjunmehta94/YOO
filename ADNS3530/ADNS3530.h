#ifndef ADNS3530_H
#define ADNS3530_H
#include <Arduino.h>
class ADNS3530 {
	public:
	ADNS3530(HardwareSerial&,int,int,int,int);
	void checkLibrary();
	void powerUp();
	void sync();
	int8_t readProductID();
	void readMotion();

	int8_t readDX();
	int8_t readDY();

	private:
	int _MOSI;
	int _MISO;
	int _SCLK;
	int _NCS;
	void write(int8_t, int8_t);
	int8_t read(int8_t);
};

#define PRODUCT_ID 				0x00
#define MOTION 					0x02
#define DELTA_X					0x03
#define DELTA_Y					0x04
#define POWER_UP_RESET			0x3A


#endif