#include <Arduino.h>
#include "wifi_manager.h"
#include "firebase_manager.h"
#include "ds18b20_sensor.h"
#include "ph_sensor.h"
#include "tds_sensor.h"
#include "turbidity_sensor.h"

void setup() {
    Serial.begin(115200);

    // Initialisation des modules
    connectToWiFi();
    initFirebase();
    initDS18B20();
    initPHSensor();
    initTDSSensor();
}

void loop() {
    // Lecture des capteurs
    float temperature = readTemperature();
    float pH = readPH();
    float tds = readTDS(temperature);
    float turbidity = readTurbidity();

    // Affichage des valeurs
    Serial.printf("Temp: %.2f °C, pH: %.2f, TDS: %.2f ppm, Turbidity: %.2f NTU\n",
                  temperature, pH, tds, turbidity);

    // Envoi des données à Firebase
    sendToFirebase(temperature, pH, tds, turbidity);

    delay(1000); // Pause entre deux cycles
}
