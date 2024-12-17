#include <Arduino.h>
#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <GravityTDS.h>
#include <EEPROM.h>

// Network credentials
#define WIFI_SSID "AndroidAP5FA4"
#define WIFI_PASSWORD "noua2022"

// Firebase project details
#define API_KEY "AIzaSyCwEKLsHv5oVYkkq07NBo2hQKUp_v0J_N4"
#define DATABASE_URL "https://testvf-58759-default-rtdb.firebaseio.com/"

// DS18B20 setup
#define DS18B20_PIN 14
OneWire oneWire(DS18B20_PIN);
DallasTemperature sensors(&oneWire);

// pH meter setup
#define ArrayLenth  40    // Times of collection
#define SensorPin 34
int pHArray[ArrayLenth]; // Store the average value of the sensor feedback
int pHArrayIndex = 0;
float calibrationSlope = 1.3574;  // Slope (m) of the pH calculation
float calibrationIntercept = 3.66065; // Intercept (b) of the pH calculation

// TDS meter setup
#define TdsSensorPin 35
#define EEPROM_SIZE 512
GravityTDS gravityTds;
float temperature = 25.0, tdsValue;

// Turbidity sensor setup
#define TurbiditySensorPin 36 // Assuming A0 is mapped to GPIO 32 on ESP32

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
bool signupOK = false;

void setup() {
  Serial.begin(115200);

  // Connect to WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println(" connected.");

  // Initialize the DS18B20 sensor
  sensors.begin();

  // Initialize EEPROM for TDS sensor calibration, if necessary
  EEPROM.begin(EEPROM_SIZE);

  // TDS Sensor initialization
  gravityTds.setPin(TdsSensorPin);
  gravityTds.setAref(3.3); // Default reference voltage on ADC is 3.3V for ESP32
  gravityTds.setAdcRange(4096); // 4096 for 12bit ADC of ESP32
  gravityTds.begin(); // Initialization

  // Firebase configuration
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("Signed in to Firebase anonymously.");
    signupOK = true;
  } else {
    Serial.printf("Firebase sign up failed: %s\n", config.signer.signupError.message.c_str());
  }
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  // Temperature measurement
  sensors.requestTemperatures();
  float temperatureC = sensors.getTempCByIndex(0);
  Serial.print("Temperature: ");
  Serial.println(temperatureC);

  // pH measurement with averaging
  if (pHArrayIndex == ArrayLenth) pHArrayIndex = 0; // Reset index if it reaches the limit
  pHArray[pHArrayIndex] = analogRead(SensorPin); // Read pH and store in array
  float avgVoltage = avergearray(pHArray, ArrayLenth) * (5.0 / 4095.0);
  float pH = calibrationSlope * avgVoltage + calibrationIntercept;
  Serial.print("pH: ");
  Serial.println(pH);

  // Increment the pHArrayIndex
  pHArrayIndex++;

  // TDS measurement
  gravityTds.setTemperature(temperatureC); // Update temperature for TDS calculation
  gravityTds.update();
  tdsValue = gravityTds.getTdsValue();
  Serial.print("TDS: ");
  Serial.println(tdsValue, 0);

  // Turbidity measurement
  float turbidity = measureTurbidity();
  Serial.print("Turbidity: ");
  Serial.println(turbidity, 2);

  // Send data to Firebase
  if (Firebase.ready() && signupOK) {
    Firebase.RTDB.setFloat(&fbdo, "/temperature/last", temperatureC);
    Firebase.RTDB.setFloat(&fbdo, "/pH/last", pH);
    Firebase.RTDB.setFloat(&fbdo, "/TDS/lastValue", tdsValue);
    Firebase.RTDB.setFloat(&fbdo, "/turbidity/last", turbidity);
  }

  delay(1000); // Delay between readings
}

// The avergearray function implementation
double avergearray(int* arr, int number) {
  int i;
  int max, min;
  double avg;
  long amount = 0;
  if (number <= 0) {
    Serial.println("Error number for the array to averaging!/n");
    return 0;
  }
  if (number < 5) {   // Less than 5, calculated directly statistics
    for (i = 0; i < number; i++) {
      amount += arr[i];
    }
    avg = amount / number;
    return avg;
  } else {
    if (arr[0] < arr[1]) {
      min = arr[0]; max = arr[1];
    } else {
      min = arr[1]; max = arr[0];
    }
    for (i = 2; i < number - 2; i++) { // Corrected to iterate properly within array bounds
      if (arr[i] < min) {
        amount += min;    // arr<min
        min = arr[i];
      } else if (arr[i] > max) {
        amount += max;  // arr>max
        max = arr[i];
      } else {
        amount += arr[i]; // min<=arr<=max
      }
    }
    avg = (double)amount / (number - 2);
  }
  return avg;
}

// Function to measure turbidity
float measureTurbidity() {
  int sensorValue = analogRead(TurbiditySensorPin); // Read the analog value
  float voltage = sensorValue * (5.0 / 1023.0); // Convert to voltage

  float turbidity;
  if (voltage <= 2.5) {
    turbidity = 0;
  } else if (voltage > 2.5 && voltage <= 2.8) {
    turbidity = (voltage - 2.5) * (5.0 / 0.3);
  } else {
    turbidity = 5; // Sets turbidity to 5 NTU for voltages above 2.8V
  }
  return turbidity;
}
