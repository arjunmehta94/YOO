#include "ADNS9800.h"
Serial_* printer2;

ADNS9800::ADNS9800(int motion, int mosi, int miso, int sclk, int ncs, Serial_* p) {
	_MOSI = mosi;
	pinMode(_MOSI,OUTPUT);
	_MISO = miso;
	pinMode(_MISO,INPUT);
	_SCLK = sclk;
	pinMode(_SCLK,OUTPUT);
	_NCS = ncs;
	pinMode(_NCS,OUTPUT);
	_MOTION = motion;
	pinMode(_MOTION,INPUT);

	printer2 = p;
}

void ADNS9800::checkLibrary() {
	printer2->print("checking library");
	printer2->println(SROM[0]);
}


void ADNS9800::checkCommunication() {
	printer2->println(read(REG_Product_ID));
}

void ADNS9800::readXY(int8_t data[]) {
	data[0] = read(REG_Motion);
    if ((data[0] >> 6) == -2) {
        data[1] = read(REG_Delta_X_L);
        data[2] = read(REG_Delta_X_H);
        data[3] = read(REG_Delta_Y_L);
        data[4] = read(REG_Delta_Y_H);
    }
	write(REG_Motion, data[0] & 0b01111111);
}

int8_t ADNS9800::readDeltaX() {
	return read(REG_Delta_X_L);
}

int8_t ADNS9800::readDeltaY() {
	return read(REG_Delta_Y_L);
}

int8_t ADNS9800::getLiftDetection() {
	return read(REG_Lift_Detection_Thr);
}


void ADNS9800::powerUp() {
	digitalWrite(_NCS, HIGH);
	delayMicroseconds(1);
	digitalWrite(_NCS, LOW);
	delayMicroseconds(1);
	digitalWrite(_NCS, HIGH);
	delayMicroseconds(1);

	//printer2->println(read(REG_));
	
	write(REG_Power_Up_Reset, 0x5a); // force reset
	delay(50); // wait for it to reboot
	// read registers 0x02 to 0x06 (and discard the data)
	read(REG_Motion);
	read(REG_Delta_X_L);
	read(REG_Delta_X_H);
	read(REG_Delta_Y_L);
	read(REG_Delta_Y_H);
	// upload the firmware
	uploadFirmware();
	delay(10);
	printer2->println(read(REG_Power_Up_Reset));

	//enable laser(bit 0 = 0b), in normal mode (bits 3,2,1 = 000b)
	// reading the actual value of the register is important because the real
	// default value is different from what is said in the datasheet, and if you
	// change the reserved bytes (like by writing 0x00...) it would not work.
	byte laser_ctrl0 = read(REG_LASER_CTRL0);
	write(REG_LASER_CTRL0, laser_ctrl0 & 0xf0 );

	delay(1);

	//set liftdetection higher

	write(REG_Lift_Detection_Thr, 0b00011111);

	printer2->println("ADNS9800 initialized");

	printer2->println(read(REG_Configuration_I));
	write(REG_Configuration_I,0xa4);
	printer2->println(read(REG_Configuration_I));
}

void ADNS9800::uploadFirmware(){
	// send the firmware to the chip, cf p.18 of the datasheet
	// set the configuration_IV register in 3k firmware mode
	write(REG_Configuration_IV, 0x02); // bit 1 = 1 for 3k mode, other bits are reserved 

	// write 0xd in SROM_enable reg for initializing
	write(REG_SROM_Enable, 0x1d); 

	// wait for more than one frame period
	delay(10); // assume that the frame rate is as low as 100fps... even if it should never be that low

	// write 0x8 to SROM_enable to start SROM download
	write(REG_SROM_Enable, 0x18); 

	// write the SROM file (=firmware data) 

	//digitalWrite(_NCS, LOW);
	//SPI.transfer(REG_SROM_Load_Burst | 0x80); // write burst destination adress
	// send all bytes of the firmware
	sromLoadBurstWrite();
	//digitalWrite(_NCS, HIGH);
}

void ADNS9800::sromLoadBurstWrite() {

    digitalWrite(_NCS, LOW);
	char address = REG_SROM_Load_Burst | 0x80;


	for(int address_bit=7; address_bit >=0; address_bit--){
        digitalWrite(_SCLK, LOW);
        if(address & (1<<address_bit))
        	digitalWrite(_MOSI, HIGH);
        else
        	digitalWrite(_MOSI, LOW);
        digitalWrite(_SCLK, HIGH);
    }
    delayMicroseconds(15);
    char data;
	for(int i=0; i<firmware_length; i++) {
		data=(unsigned char)pgm_read_byte(SROM + i);
		//printer2->println(data);
		for(int data_bit=7; data_bit >= 0; data_bit--){
	        digitalWrite(_SCLK, LOW);
	        if(data & (1<<data_bit))
	        	digitalWrite(_MOSI, HIGH);
	        else 
	        	digitalWrite(_MOSI, LOW);
	        digitalWrite(_SCLK, HIGH);
		}
		delayMicroseconds(15);
	}

    digitalWrite(_NCS, HIGH);
	printer2->println("Loaded firmware");
}

int8_t ADNS9800::read(int8_t address) {
	int8_t value=0;
    digitalWrite(_NCS, LOW);
    digitalWrite(_SCLK, HIGH);
    delayMicroseconds(10);
    address &= 0x7F;
    for(int address_bit=7; address_bit >=0; address_bit--){
        digitalWrite(_SCLK, LOW);
        if(address & (1<<address_bit)){
            digitalWrite(_MOSI, HIGH);
        }
        else{
            digitalWrite(_MOSI, LOW);
        }
        digitalWrite(_SCLK, HIGH);
    }
    delayMicroseconds(100);   
    for(int value_bit=7; value_bit >= 0; value_bit--){
        digitalWrite(_SCLK, LOW);
        digitalWrite(_SCLK, HIGH);
        if(digitalRead(_MISO))
			value |= (1<<value_bit);
    }
    delayMicroseconds(10);
    digitalWrite(_NCS, HIGH);
    delayMicroseconds(20);
    return value;
}

void ADNS9800::write(int8_t address, int8_t data) {
    digitalWrite(_NCS,LOW);
	digitalWrite(_SCLK, HIGH);
    delayMicroseconds(10);
    address |= 0x80;
    for(int address_bit=7; address_bit >=0; address_bit--){
        digitalWrite(_SCLK, LOW);
        if(address & (1<<address_bit))
        	digitalWrite(_MOSI, HIGH);
        else
        	digitalWrite(_MOSI, LOW);
        digitalWrite(_SCLK, HIGH);
        delayMicroseconds(5);
    }
    for(int data_bit=7; data_bit >= 0; data_bit--){
        digitalWrite(_SCLK, LOW);
        if(data & (1<<data_bit))
        	digitalWrite(_MOSI, HIGH);
        else 
        	digitalWrite(_MOSI, LOW);
        digitalWrite(_SCLK, HIGH);
        delayMicroseconds(5);
	}

	delayMicroseconds(20);
	digitalWrite(_NCS, HIGH);
	delayMicroseconds(120);
}