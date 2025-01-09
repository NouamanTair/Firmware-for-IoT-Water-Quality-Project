#ifndef FIREBASE_MANAGER_H
#define FIREBASE_MANAGER_H

void initFirebase();
void sendToFirebase(float temperature, float pH, float tds, float turbidity);

#endif
