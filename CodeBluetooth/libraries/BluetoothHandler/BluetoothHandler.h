#ifndef BLUETOOTHHANDLER_H
#define BLUETOOTHHANDLER_H
#include <Arduino.h> 
#include <QueueList.h>
class BluetoothHandler
{
public:
	BluetoothHandler(HardwareSerial&);
	~BluetoothHandler();
	void penUp();
	void penDown();
	void endWrite();
	void send(signed int, signed int);
	String processNumber(signed int);
	String popBuffer();
	String peekBuffer();
	bool isEmptyBuffer();
	int countBuffer();
	void pushBuffer(String item);
private:
	QueueList<String> queue; //buffer for dx dy points, delimiter
};

#endif