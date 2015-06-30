#ifndef BLUETOOTHHANDLER_H
#define BLUETOOTHHANDLER_H
#include <Arduino.h> 

class BluetoothHandler {
	public:

	BluetoothHandler(HardwareSerial&);
	~BluetoothHandler();
	void checkLibrary();
	void send(int8_t,int8_t,char, uint8_t);
	void penUp();
	void penDown();
	void endWrite();
};

#endif