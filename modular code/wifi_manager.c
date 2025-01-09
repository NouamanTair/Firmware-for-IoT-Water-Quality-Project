#include <WiFi.h>
#include "wifi_manager.h"

#define WIFI_SSID "AndroidAP5FA4"
#define WIFI_PASSWORD "noua2022"

void connectToWiFi() {
    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    Serial.print("Connecting to WiFi");
    while (WiFi.status() != WL_CONNECTED) {
        Serial.print(".");
        delay(300);
    }
    Serial.println(" connected.");
}
