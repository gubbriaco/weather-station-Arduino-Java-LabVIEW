/**
 * Arduino Firmware about SAM project.
 * @author Giorgio Ubbriaco & Francesco Nicoletti
 * @version 1.0
 * @date 26/04/2023
 */

#include "DHT.h"
#include <LiquidCrystal_I2C.h>


/** 
 * In this initial section the pins are defined for each sensor and component used and, 
 * in addition, the objects that make use of external libraries.
 */

/**
 * In Arduino, the const construct is used to define constants, i.e. variables that cannot 
 * be changed during program execution. Declaring a variable with the const construct tells 
 * the compiler that the variable's value cannot be changed during program execution, and 
 * therefore any attempt to change the variable during program execution will cause a compile 
 * error.
 * In other words, using the const construct for variables in the Arduino indicates that those 
 * variables are read-only and their value cannot be changed in the program. This is especially 
 * useful for defining constants such as memory addresses, clock frequencies, configuration 
 * values, and other constants that must remain constant during program execution.
 */

/** DHT11 pin */
const int dht_pin = 7;
/** DHT type (DHT11 or DHT22) */
const int dht_type = DHT11;
/** DHT object creation */
DHT dht(dht_pin, dht_type);
// my DHT11 Thresholds
const float critical_heat_perceived_temperature = 40;
const float moderate_perceived_temperature = 25;
const float critical_cold_perceived_temperature = 10;


/** buzzer pin */
const int buzzer_pin = 2;

/** Number of characters (columns) defined for LCD I2C */
const int n_characters = 16;
/** Number of lines (rows) defined for LCD I2C */
const int n_lines = 2;
/** LCD I2C object creation */
LiquidCrystal_I2C lcd(0x27, n_characters, n_lines);

/** Blue Led pin */
const int blue_led_pin = 3;
/** Green Led pin */
const int green_led_pin = 4;
/** Yellow Led pin */
const int yellow_led_pin = 5;
/** Red Led pin */
const int red_led_pin = 6;

/** Char array used for the Serial print */
/** 
 * The size of the formatted string will depend on the maximum value that each variable can assume.
 * In general, I know that a 32-bit float can take values between -3.4028235E+38 and 3.4028235E+38. Also, to represent 
 * these values as strings, I need one byte for each digit and another byte for the sign (in the case of 
 * negative values). So for each variable, I need at least 8 bytes to represent its values.
 * To hold the formatted string representing three 32-bit integers, I need at least 12 bytes of space. 
 * However, it's always best to allocate some extra space to avoid any overflow or memory issues. I might 
 * then consider increasing the size of my "message" variable to 20 bytes or more to make sure I have enough 
 * space to hold the formatted string.
 */
char message[20];



/* 
 * This section defines some utility methods. 
 */

void dht_initialization();
void dht_read(const float& humidity, const float& temperature, const float& perceived_temperature);
void leds_initialization();
void buzzer_initialization();
void manage_parameters(const float humidity, const float temperature, const float perceived_temperature);


/** setup code here, to run once */
void setup() {
  
  // starts serial communication at 9600 baud
  Serial.begin(9600);

  leds_initialization();
  
  // sensors initialization
  dht_initialization();

  // support components initialization
  buzzer_initialization();
  lcdi2c_initialization();

}


/** main code here, to run repeatedly */
void loop() {

  float lecture_delay = Serial.parseFloat();
  delay(lecture_delay*1000);

  // parameters reading
  float humidity, temperature, perceived_temperature;
  dht_read(humidity, temperature, perceived_temperature);

  // components management
  manage_parameters(humidity, temperature, perceived_temperature);

  // LCD and SERIAL printing
  lcd_print(humidity, temperature, perceived_temperature);
  serial_print(humidity, temperature, perceived_temperature);

  
  delay(lecture_delay);

}



/** Implementation of the utility methods */

/**
 * This method initialize dht11.
 * @warning The sensor must be a 3 pin DHT11. Otherwise, if the sensor you have is 4-pin, then you will have 
 * to adapt it, via jumper, to the 3-pin configuration used in this case.
 * @note Make sure the jumpers are connected correctly to the DHT11, otherwise you risk damaging the sensor.
 */
void dht_initialization(){
  dht.begin();
}

/** 
 * This method allow dht11 parameters reading.
 * @param &humidity DHT11 Output Humidity
 * @param &temperature DHT11 Output Temperature
 * @param &perceived_temperature DHT11 Output Perceived Temperature
 * @warning The sensor must be a 3 pin DHT11. Otherwise, if the sensor you have is 4-pin, then you will have to 
 * adapt it, via jumper, to the 3-pin configuration used in this case.
 * @note Make sure the jumpers are connected correctly to the DHT11, otherwise you risk damaging the sensor. 
 * Furthermore, the parameters read, in the event of incorrect connections, will most likely not be congruent 
 * with the real ones.
 */
void dht_read(float& humidity, float& temperature, float& perceived_temperature) {
  humidity = dht.readHumidity();
  temperature = dht.readTemperature();
  // a check is made regarding the parameters read by the dht11 sensor
  if( isnan(humidity) || isnan(temperature) ) {
    Serial.println(F("Unable to read from DHT sensor!"));
    return;
  }
  perceived_temperature = dht.computeHeatIndex(temperature, humidity, false);
}

/** 
 * This method initialize leds.
 * @warning The leds must be 2 pin leds.
 * @note Make sure the jumpers are connected correctly to the leds, otherwise you risk damaging the leds.
 */
void leds_initialization() {
  pinMode(green_led_pin, OUTPUT);
  pinMode(yellow_led_pin, OUTPUT);
  pinMode(red_led_pin, OUTPUT);
}

/** 
 * This method initialize buzzer. 
 * @warning The buzzer must be a 2 pin Buzzer.
 * @note Make sure the jumpers are connected correctly to the Buzzer, otherwise you risk damaging component.
 */
void buzzer_initialization() {
  pinMode(buzzer_pin, OUTPUT);
}

/** 
 * This method allows the management about parameters read.
 * @param humidity DHT11 Humidity
 * @param temperature DHT11 Temperature
 * @param perceived_temperature DHT11 Perceived Temperature
 * @warning The sensors must be a 3 pinDHT11 and a 3 pin Flame Sensor. Otherwise, if the sensor you have is 
 * 4-pin, then you will have to adapt it, via jumper, to the 3-pin configuration used in this case.
 * @note Make sure the jumpers are connected correctly to the DHT11 and to the Flame Sensor, otherwise you 
 * risk damaging sensors. Furthermore, the parameters read, in the event of incorrect connections, will most 
 * likely not be congruent with the real ones. 
 */
void manage_parameters(const float humidity, const float temperature, const float perceived_temperature) {
  if( perceived_temperature >= critical_heat_perceived_temperature ) {
    digitalWrite(red_led_pin, HIGH);
    digitalWrite(yellow_led_pin, LOW);
    digitalWrite(green_led_pin, LOW);
    digitalWrite(blue_led_pin, LOW); 

    digitalWrite(buzzer_pin, HIGH);
    delay(50);
    digitalWrite(buzzer_pin, LOW);
    
  } 
  else if( perceived_temperature < critical_heat_perceived_temperature && perceived_temperature >= moderate_perceived_temperature ) {
    digitalWrite(red_led_pin, LOW);
    digitalWrite(yellow_led_pin, HIGH);
    digitalWrite(green_led_pin, LOW);
    digitalWrite(blue_led_pin, LOW); 

    digitalWrite(buzzer_pin, LOW);
    //noTone(buzzer_pin);
  }
  else if ( perceived_temperature < moderate_perceived_temperature && perceived_temperature >= critical_cold_perceived_temperature ) {
    digitalWrite(red_led_pin, LOW);
    digitalWrite(yellow_led_pin, LOW);
    digitalWrite(green_led_pin, HIGH);
    digitalWrite(blue_led_pin, LOW);  

    digitalWrite(buzzer_pin, LOW);
  } 
  else if( perceived_temperature < critical_cold_perceived_temperature ) {
    digitalWrite(red_led_pin, LOW);
    digitalWrite(yellow_led_pin, LOW);
    digitalWrite(green_led_pin, LOW);    
    digitalWrite(blue_led_pin, HIGH);

    digitalWrite(buzzer_pin, HIGH);
    delay(50);
    digitalWrite(buzzer_pin, LOW);   
  }
}

/** 
 * This method initialize LCD.
 * @warning The buzzer must be a I2C 2-rows 16-columns LCD.
 * @note Make sure the jumpers are connected correctly to the LCD, otherwise you risk damaging component.
 */
void lcdi2c_initialization() {
  lcd.begin();
  lcd.backlight();
}

/** 
 * This method allows LCD printing.
 * @param humidity DHT11 Humidity
 * @param temperature DHT11 Temperature
 * @param perceived_temperature DHT11 Perceived Temperature 
 * @warning The buzzer must be a I2C 2-rows 16-columns LCD.
 * @note Make sure the jumpers are connected correctly to the LCD, otherwise you risk damaging component. 
 * Furthermore, it could happen that if not connected well, the expected content would not be displayed 
 * correctly. 
 */
void lcd_print(const float humidity, const float temperature, const float perceived_temperature) {

  String humidity_s = "H:";
  humidity_s.concat(humidity);

  String temperature_s = "T:";
  temperature_s.concat(temperature);

  String perceived_temperature_s = "PT:";
  perceived_temperature_s.concat(perceived_temperature);

  lcd.setCursor(0, 0);
  lcd.print(humidity_s);
  lcd.setCursor(9, 0);
  lcd.print(temperature_s);
  lcd.setCursor(4, 1);
  lcd.print(perceived_temperature_s);    
}

/**
 * This method allows serial printing. 
 * @param humidity DHT11 Humidity
 * @param temperature DHT11 Temperature
 * @param perceived_temperature DHT11 Perceived Temperature
 * @note The serial print will be performed on the serial port foreseen by the user of this code.
 */
void serial_print(const float humidity, const float temperature, const float perceived_temperature) {
  /**
  * The function converts these values into strings using the dtostrf function (from the Arduino standard 
  * library) and then concatenates them into the message buffer.\
  * The dtostrf function converts a float value into a string with a certain format. In the code provided, 
  * the format specified is 6, 2, which means that 6 total characters will be used to represent the value, 
  * of which 2 will be reserved for decimals. In practice, a maximum of 6 characters will be used, including 
  * the sign, decimals and integer part.
  * Using the + operator and the strlen function (from the Arduino standard library), the code moves the 
  * pointer inside the buffer message in order to concatenate the successive strings without overwriting the 
  * ones already present. In this way, a single string is created containing the values of humidity, 
  * temperature and perceived temperature.
  * Finally, the Serial.println function is used to send the concatenated string to the serial port for 
  * display or transmission to other connected devices.
  */
  dtostrf(humidity, 6, 2, message);
  dtostrf(temperature, 6, 2, message + strlen(message));
  dtostrf(perceived_temperature, 6, 2, message + strlen(message));
  Serial.println(message);
}


