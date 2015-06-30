#include "BluetoothHandler.h" //include the declaration for this class
 

static const char play = '*';
static const char pause = '$';
static const char stop = '!';
HardwareSerial* printer;
 
//<<constructor>> begin serial comm
BluetoothHandler::BluetoothHandler(HardwareSerial &print) {
	printer = &print;
	printer->begin(9600);
}

//<<destructor>>
BluetoothHandler::~BluetoothHandler() {
	/*nothing to destruct*/
}

//Send databyte 

void BluetoothHandler::checkLibrary() {
	printer->print("this is the library!!!!");
}

void BluetoothHandler::send(int8_t numberX, int8_t numberY, char mode, uint8_t index) {
	int8_t tmpX = abs(numberX);
	int8_t tmpY = abs(numberY);
	String ret;

	if(tmpX < 10) {
		if(numberX > 0) {// if numX between 0 and 9
			ret = "+";
			ret += "00";
			ret += String(tmpX);
		}
		else {// if num between 0 and -9
			ret = "-";
			ret += "00";
			ret += String(tmpX);
		}
	}
	else if(tmpX < 100) {
		if (numberX > 0) {// if numX between 10 and 99
			ret = "+"; 
			ret += "0";
			ret += String(tmpX);
		}
		else {// if numX between -10 and -99
			ret += "-";
			ret += "0"; 
			ret += String(tmpX);
		}
	}
	else if(tmpX > 100) {
		if (numberX > 0) {// if numX between 100 and 127
			ret = "+";
			ret += String(tmpX);
		}
		else {// if numX between -100 and -128
			ret = "-";
			ret += String(tmpX);
		}
	}

	if(tmpY < 10) {
		if(numberY > 0) {// if numY between 0 and 9
			ret += "+"; 
			ret += "00"; 
			ret += String(tmpY);
		}
		else {// if numY between 0 and -9
			ret += "-";
			ret += "00";
			ret += String(tmpY);
		}
	}
	else if(tmpY < 100) {
		if (numberY > 0) {// if num between 10 and 99
			ret += "+";
			ret += "0";
			ret += String(tmpY);
		}
		else {// if numY between -10 and -99
			ret += "-";
			ret += "0";
			ret += String(tmpY);
		}
	}
	else if(tmpY > 100) {
		if (numberY > 0) {// if numY between 100 and 127
			ret += "+";
			ret += String(tmpY);
		}
		else {// if numY between -100 and -128
			ret += "-";
			ret += String(tmpY);
		}
	}
	ret += mode;
	if (index < 10){
		ret += "00";
		ret += String(index);
	}
	else if(index < 100){
		ret += "0";
		ret += String(index);
	}
	else if(index > 100){
		ret += String(index);
	}
	ret += ")"; 
	printer->print(ret);
}

void BluetoothHandler::penDown() {
	printer->print(play);
}
 
// temporary pen lift
void BluetoothHandler::penUp() {
        printer->print(pause);
}
 
void BluetoothHandler::endWrite() {
	printer->print(stop);
}