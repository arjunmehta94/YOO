#include "BluetoothHandler.h" //include the declaration for this class
 

static const char play = (char) 127;
static const char pause = (char) 128;
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

void BluetoothHandler::send(int8_t outgoingByteX, int8_t outgoingByteY)
{
		if(outgoingByteX==127)
			outgoingByteX = 126;
		else if(outgoingByteX==-128)
			outgoingByteX = -127;

		if(outgoingByteY==127) {
			outgoingByteY = 126;
		} else if(outgoingByteY==-128) {
			outgoingByteY = -127;
		}
        //sendDataX = (char) (outgoingByteX<0?256+outgoingByteX:outgoingByteX);
        //sendDataY = (char) (outgoingByteY<0?256+outgoingByteY:outgoingByteY);
		sendDataX = outgoingByteX;
		sendDataY = outgoingByteY;
		/*printer->print("X: ");
        printer->print((int8_t)sendDataX);
        printer->print("Y: ");
        printer->println((int8_t)sendDataY);*/
        printer->print(sendDataX);
        printer->println(sendDataY);
}

//penDown

void BluetoothHandler::penDown()
{
	printer->print(127);
}
 
// temporary pen lift
void BluetoothHandler::penUp()
{
        printer->print(-128);
}