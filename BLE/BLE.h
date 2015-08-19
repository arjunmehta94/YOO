#define BLE_H
#include <Arduino.h>

class BLE {
	public:
	BLE(HardwareSerial*);
	void checkLibrary();
	void send(int8_t,int8_t, int8_t, int8_t,int8_t);
};