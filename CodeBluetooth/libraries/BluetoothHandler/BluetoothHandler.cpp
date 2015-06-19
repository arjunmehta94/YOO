#include "BluetoothHandler.h" //include the declaration for this class
 

static const char play = '*';
static const char pause = '$';
static const char stop = '!';
HardwareSerial* printer;
 
//<<constructor>> begin serial comm
BluetoothHandler::BluetoothHandler(HardwareSerial &print)
{
	printer = &print;
	printer->begin(9600);
}
 
//<<destructor>>
BluetoothHandler::~BluetoothHandler()
{
	/*nothing to destruct*/
}

//Send databyte 

void BluetoothHandler::send(int outgoingByte)
{
        printer->print(outgoingByte);
}

//penDown

void BluetoothHandler::penDown()
{
	printer->print(play);
}
 
// temporary pen lift
void BluetoothHandler::penUp()
{
        printer->print(pause);
}
 
void BluetoothHandler::endWrite()
{
	printer->print(stop);
}