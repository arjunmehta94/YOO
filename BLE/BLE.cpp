#include <BLE.h>


HardwareSerial* printer;
int i = 0;

BLE::BLE(HardwareSerial* p) {
	printer = p;
}

void BLE::checkLibrary() {
	printer->println("working library");
}

<<<<<<< HEAD
void BLE::send(int8_t xL, int8_t xH, int8_t yL, int8_t yH, int8_t pen) {
	// if(x==-128) {
	// 	x = -127;
	// } else if(y==-128) {
	// 	y = -127;
	// } else if(pen==-128) {
	// 	pen = -127;
	// }
	char buf[] = {-128,xL,xH,yL,yH,pen};
=======
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
>>>>>>> 17bb673dd6d9c3b9ccd2f74a0ce408301fd68570

	printer->write(buf, 6);
	delay(10);
}
