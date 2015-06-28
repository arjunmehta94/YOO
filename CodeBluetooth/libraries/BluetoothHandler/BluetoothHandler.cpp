#include "BluetoothHandler.h" //include the declaration for this class
 

static const char play = '*';
static const char pause = '$';
static const char stop = '!';
HardwareSerial* printer;
 

//<<constructor>> begin serial comm
BluetoothHandler::BluetoothHandler(HardwareSerial &print)
{	
	queue = new QueueList<String>();
	printer = &print;
	printer->begin(9600);
}
 
//<<destructor>>
BluetoothHandler::~BluetoothHandler()
{
	/*nothing to destruct*/
}

//Send databyte 
String BluetoothHandler::processNumber(signed int num)
{
	signed int tmp = abs(num); 
	String ret;
	
	if(tmp < 10) 
	{
		if(num > 0) // if num between 0 and 9
		{
			ret = "+" + "00" + tmp.toString();
		}
		else // if num between 0 and -9
		{
			ret = "-" + "00" + tmp.toString();
		}
		
	}
	else if(tmp < 100)
	{
		if (num > 0) // if num between 10 and 99
		{
			ret = "+" + "0" + tmp.toString();
		}
		else // if num between -10 and -99
		{
			ret = "-" + "0" + tmp.toString();
		}
		
	}
	else if(tmp > 100)
	{
		if (num > 0) // if num between 100 and 127
		{
			ret = "+" + tmp.toString();
		}
		else // if num between -100 and -128
		{
			ret = "-" + tmp.toString();
		}
		
	}
	
	return ret;	
}

// format to send: "x, y, play/pause, )" -> "!!!!????*)"
void BluetoothHandler::send(signed int x, signed int y) 
{
		String strX, strY, ret;
		strX = BluetoothHandler::processNumber(x);
		strY = BluetoothHandler::processNumber(y);
		ret = strX + strY + play + ")";
        printer->print(ret);
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

void BluetoothHandler::pushBuffer(String item)
{
	queue.push(item);
}

String BluetoothHandler::popBuffer()
{
	queue.pop();
}

String BluetoothHandler::peekBuffer()
{
	queue.peek();
}

bool BluetoothHandler::isEmptyBuffer()
{
	queue.isEmpty();
}

int BluetoothHandler::countBuffer()
{
	queue.count();
}
