#include <Arduino.h>
#include "ph_sensor.h"
#include "utils.h"

#define SensorPin 34
#define ArrayLength 40

static int pHArray[ArrayLength];
static int pHArrayIndex = 0;
static float calibrationSlope = 1.3574;
static float calibrationIntercept = 3.66065;

void initPHSensor() {
    pHArrayIndex = 0;
}

float readPH() {
    pHArray[pHArrayIndex] = analogRead(SensorPin);
    pHArrayIndex = (pHArrayIndex + 1) % ArrayLength;

    float avgVoltage = averageArray(pHArray, ArrayLength) * (5.0 / 4095.0);
    return calibrationSlope * avgVoltage + calibrationIntercept;
}
