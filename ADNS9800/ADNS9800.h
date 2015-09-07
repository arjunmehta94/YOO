#define ADNS9800_H
#include <Arduino.h>
#include "adns9800_srom_A4.h"

class ADNS9800 {
	public:
	ADNS9800(int,int,int,int,int,Serial_*);
	void checkLibrary();
	void powerUp();
	void uploadFirmware();
	void checkCommunication();//readProductID
	int8_t readDeltaX();
	int8_t readDeltaY();
	void readXY(int8_t[]);
	int8_t readMotion();
	int8_t getLiftDetection();

	private:
	int _MOSI;
	int _MISO;
	int _SCLK;
	int _NCS;
	int _MOTION;

	void sromLoadBurstWrite();
	void write(int8_t,int8_t);
	int8_t read(int8_t);
};

#define REG_Product_ID                           0x00
#define REG_Revision_ID                          0x01
#define REG_Motion                               0x02
#define REG_Delta_X_L                            0x03
#define REG_Delta_X_H                            0x04
#define REG_Delta_Y_L                            0x05
#define REG_Delta_Y_H                            0x06
#define REG_SQUAL                                0x07
#define REG_Pixel_Sum                            0x08
#define REG_Maximum_Pixel                        0x09
#define REG_Minimum_Pixel                        0x0a
#define REG_Shutter_Lower                        0x0b
#define REG_Shutter_Upper                        0x0c
#define REG_Frame_Period_Lower                   0x0d
#define REG_Frame_Period_Upper                   0x0e
#define REG_Configuration_I                      0x0f
#define REG_Configuration_II                     0x0
#define REG_Frame_Capture                        0x2
#define REG_SROM_Enable                          0x3
#define REG_Run_Downshift                        0x4
#define REG_Rest1_Rate                           0x5
#define REG_Rest1_Downshift                      0x6
#define REG_Rest2_Rate                           0x7
#define REG_Rest2_Downshift                      0x8
#define REG_Rest3_Rate                           0x9
#define REG_Frame_Period_Max_Bound_Lower         0xa
#define REG_Frame_Period_Max_Bound_Upper         0xb
#define REG_Frame_Period_Min_Bound_Lower         0xc
#define REG_Frame_Period_Min_Bound_Upper         0xd
#define REG_Shutter_Max_Bound_Lower              0xe
#define REG_Shutter_Max_Bound_Upper              0xf
#define REG_LASER_CTRL0                          0x20
#define REG_Observation                          0x24
#define REG_Data_Out_Lower                       0x25
#define REG_Data_Out_Upper                       0x26
#define REG_SROM_ID                              0x2a
#define REG_Lift_Detection_Thr                   0x2e
#define REG_Configuration_V                      0x2f
#define REG_Configuration_IV                     0x39
#define REG_Power_Up_Reset                       0x3a
#define REG_Shutdown                             0x3b
#define REG_Inverse_Product_ID                   0x3f
#define REG_Motion_Burst                         0x50
#define REG_SROM_Load_Burst                      0x62
#define REG_Pixel_Burst                          0x64