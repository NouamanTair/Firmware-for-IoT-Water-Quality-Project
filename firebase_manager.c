#include <Firebase_ESP_Client.h>
#include "firebase_manager.h"

#define API_KEY "AIzaSyCwEKLsHv5oVYkkq07NBo2hQKUp_v0J_N4"
#define DATABASE_URL "https://testvf-58759-default-rtdb.firebaseio.com/"

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
bool signupOK = false;

void initFirebase() {
    config.api_key = API_KEY;
    config.database_url = DATABASE_URL;

    if (Firebase.signUp(&config, &auth, "", "")) {
        Serial.println("Signed in to Firebase.");
        signupOK = true;
    } else {
        Serial.printf("Firebase sign-up failed: %s\n",
                      config.signer.signupError.message.c_str());
    }

    Firebase.begin(&config, &auth);
    Firebase.reconnectWiFi(true);
}

void sendToFirebase(float temperature, float pH, float tds, float turbidity) {
    if (Firebase.ready() && signupOK) {
        Firebase.RTDB.setFloat(&fbdo, "/temperature/last", temperature);
        Firebase.RTDB.setFloat(&fbdo, "/pH/last", pH);
        Firebase.RTDB.setFloat(&fbdo, "/TDS/lastValue", tds);
        Firebase.RTDB.setFloat(&fbdo, "/turbidity/last", turbidity);
    }
}
