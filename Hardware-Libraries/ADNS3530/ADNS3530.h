#ifndef ADNS3530_H
#define ADNS3530_H
#include <Arduino.h>
class ADNS3530 {
	public:
	ADNS3530(int,int,int,int);
	void checkLibrary();
	void powerUp();
	void resetData();
	int8_t checkCommunication();
	void readMotion();

	int8_t readDeltaX();
	int8_t readDeltaY();
	int8_t readSQUAL();
	void readPixelGrab();
	bool checkOverFlow();
	bool available();

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
#define SQUAL					0x05
#define PIXEL_GRAB				0x0B
#define POWER_UP_RESET			0x3A


#endif