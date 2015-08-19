#include <BLE.h>


HardwareSerial* printer;
int i = 0;

BLE::BLE(HardwareSerial* p) {
	printer = p;
}

void BLE::checkLibrary() {
	printer->println("working library");
}

void BLE::send(int8_t xL, int8_t xH, int8_t yL, int8_t yH, int8_t pen) {
	// if(x==-128) {
	// 	x = -127;
	// } else if(y==-128) {
	// 	y = -127;
	// } else if(pen==-128) {
	// 	pen = -127;
	// }
	char buf[] = {-128,xL,xH,yL,yH,pen};

	printer->write(buf, 6);
	delay(10);
}