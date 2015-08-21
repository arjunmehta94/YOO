#include <BLE.h>

BLE::BLE(HardwareSerial* p) {
	printer = p;
	mergedX = 0;
	mergedY = 0;
	penUpDown = 0;
}

void BLE::checkLibrary() {
	printer->println("working library");
}

void BLE::send(int8_t xL, int8_t xH, int8_t yL, int8_t yH) {
	penUpDown = digitalRead(4);
	mergedX = (xH << 8) | (xL & 255);
	mergedY = (yH << 8) | (yL & 255);
	if (mergedX == 0 && mergedY == 0){
		return;
	}
	if (mergedX == -128) {
		xL = -127;
		xH = -1;
	}
	if (mergedY == -128) {
		yL = -127;
		yH = -1;
	}
	char buf[] = {-128,xL,xH,yL,yH,penUpDown};
	printer->write(buf, 6);
	delay(10);
}