#include <OneWire.h>
#include <DallasTemperature.h>
#include "ds18b20_sensor.h"

#define DS18B20_PIN 14
OneWire oneWire(DS18B20_PIN);
DallasTemperature sensors(&oneWire);

void initDS18B20() {
    sensors.begin();
}

float readTemperature() {
    sensors.requestTemperatures();
    return sensors.getTempCByIndex(0);
}
