#ifndef BLUETOOTHHANDLER_H
#define BLUETOOTHHANDLER_H
#include <Arduino.h> 

class BluetoothHandler
{
	public:
	BluetoothHandler(HardwareSerial&);
	~BluetoothHandler();
	void penUp();
	void penDown();
	void endWrite();
	void send(int8_t,int8_t);
	private:
	char sendDataX;
	char sendDataY;
};

#endif