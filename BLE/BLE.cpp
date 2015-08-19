#include <BLE.h>

HardwareSerial* printer;
int i = 0;

BLE::BLE(HardwareSerial* p) {
	printer = p;
}

void BLE::checkLibrary() {
	printer->println("working library");
}

void BLE::send(int8_t xl, int8_t xh, int8_t yl, int8_t yh, int8_t pen) {
	if(xl==-128) {
		xl = -127;
	} else if(yl==-128) {
		yl = -127;
	} 
	char buf[] = {-128,xl,xh,yl, yh, pen};
	//printer->write("qwertyuiopasdfghjklz");
	printer->write(buf, 6);
	delay(7);
}

