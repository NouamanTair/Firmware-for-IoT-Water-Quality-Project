package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity2 : AppCompatActivity() {

    // Structure de données pour les normes de qualité de l'eau potable
    data class WaterQualityStandard(
        val pH: ClosedFloatingPointRange<Double>,
        val turbidity: ClosedFloatingPointRange<Double>,
        val tds: ClosedFloatingPointRange<Double>,
        val temperature: ClosedFloatingPointRange<Double>
    )

    // Données de l'eau (normalement, vous obtiendriez cela via des capteurs ou saisie utilisateur)
    data class WaterData(
        val pH: Double,
        val turbidity: Double,
        val tds: Double,
        val temperature: Double
    )

    // Normes pour l'eau potable
    val drinkingStandards = WaterQualityStandard(
        pH = 6.5..8.5,
        turbidity = 0.0..5.0,
        tds = 0.0..600.0,
        temperature = 0.0..25.0
    )

    // Normes pour l'usage agricole
    val agricultureStandards = WaterQualityStandard(
        pH = 5.5..7.0, // Example range, adjust based on actual agriculture needs
        turbidity = 0.0..10.0, // Example range, adjust based on actual agriculture needs
        tds = 0.0..2000.0, // Example range, adjust based on actual agriculture needs
        temperature = 1.0..30.0 // Example range, adjust based on actual agriculture needs
    )

    // Norms for Aquaculture use
    val aquacultureStandards = WaterQualityStandard(
        pH = 6.5..9.0, // Example range, adjust based on actual aquaculture needs
        turbidity = 0.0..30.0, // Higher turbidity might be tolerable
        tds = 0.0..1500.0, // Depending on the species
        temperature = 5.0..30.0 // Species dependent
    )

    // Norms for Industrial use
    val industrialStandards = WaterQualityStandard(
        pH = 6.0..9.0, // Industrial processes may have a wider acceptable range
        turbidity = 0.0..50.0, // Depends on the industrial application
        tds = 0.0..10000.0, // Varies widely based on the type of industry
        temperature = 0.0..50.0 // Industrial tolerance might be higher
    )

    // Norms for Domestic use (non-drinking purposes like washing, cleaning)
    val domesticStandards = WaterQualityStandard(
        pH = 6.0..8.5, // A bit more flexible than drinking water
        turbidity = 0.0..10.0, // Slightly higher turbidity is acceptable
        tds = 0.0..500.0, // Similar to drinking water, but a bit more lenient
        temperature = 0.0..35.0 // Comfortable temperature range for domestic use
    )



    // Function to simulate water data retrieval, now with temperature as a parameter
    private fun getWaterData(pH: Double, temperature: Double, tds: Double, turbidity: Double): WaterData {
        // Replace this with actual logic to fetch data
        return WaterData(
            pH = pH, // Example value
            turbidity = turbidity,
            tds = tds,
            temperature = temperature // Real-time temperature from Firebase
        )
    }

    // Évaluer la qualité de l'eau
    fun evaluateWaterQuality(standards: WaterQualityStandard, waterData: WaterData): Boolean {
        return waterData.pH in standards.pH &&
                waterData.turbidity in standards.turbidity &&
                waterData.tds in standards.tds &&
                waterData.temperature in standards.temperature
    }

    // Afficher le résultat à l'utilisateur
    fun displayResult(isGood: Boolean) {
        val message = if (isGood) "Good to Use" else "Bad to Use"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    // Initialize Firebase Database
    private val database = FirebaseDatabase.getInstance().reference
    // Variable to hold the latest temperature value
    private var latestTemperature: Double = 0.0
    // Variable to hold the latest pH value
    private var latestPH: Double = 0.0 // Default or example value
    // Variable to hold the latest TDS value
    private var latestTDS: Double = 0.0 // Default or example value
    // Variable to hold the latest turbidity value
    private var latestTurbidity: Double = 0.0 // Default or example value



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity2", "onCreate called")
        setContentView(R.layout.activity_main2)

        //Button Area select
        val drinkingButton = findViewById<MaterialButton>(R.id.drinkingButton)
        val agricultureButton = findViewById<MaterialButton>(R.id.agricultureButton)


        database.child("temperature").child("last").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming the temperature is stored as a Double
                    latestTemperature = dataSnapshot.getValue(Double::class.java) ?: 0.0
                    Log.d("MainActivity2", "Latest temperature: $latestTemperature")
                } else {
                    Log.d("MainActivity2", "DataSnapshot does not exist")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("MainActivity2", "Failed to read value: ${databaseError.message}")
            }
        })

        // Inside onCreate or another appropriate initialization method
        database.child("pH").child("last").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming the pH is stored as a Double
                    latestPH = dataSnapshot.getValue(Double::class.java) ?: 7.0 // Default pH value
                    Log.d("MainActivity2", "Latest pH: $latestPH")
                } else {
                    Log.d("MainActivity2", "DataSnapshot for pH does not exist")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("MainActivity2", "Failed to read pH value: ${databaseError.message}")
            }
        })

        //TDS
        database.child("TDS").child("lastValue").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming the TDS is stored as a Double
                    latestTDS = dataSnapshot.getValue(Double::class.java) ?: 0.0 // Default TDS value
                    Log.d("MainActivity2", "Latest TDS: $latestTDS")
                } else {
                    Log.d("MainActivity2", "DataSnapshot for TDS does not exist")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("MainActivity2", "Failed to read TDS value: ${databaseError.message}")
            }
        })

        //Turdivity
        database.child("turbidity").child("last").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Assuming turbidity is stored as a Double
                    latestTurbidity = dataSnapshot.getValue(Double::class.java) ?: 0.0 // Default turbidity value
                    Log.d("MainActivity2", "Latest turbidity: $latestTurbidity")
                } else {
                    Log.d("MainActivity2", "DataSnapshot for turbidity does not exist")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("MainActivity2", "Failed to read turbidity value: ${databaseError.message}")
            }
        })



        agricultureButton.setOnClickListener {
            val waterData = getWaterData(latestPH, latestTemperature, latestTDS, latestTurbidity)
            val isSuitableForAgriculture = evaluateWaterQuality(agricultureStandards, waterData)
            displayResults(waterData, isSuitableForAgriculture)
        }




        drinkingButton.setOnClickListener {

            val waterData = getWaterData(latestPH, latestTemperature, latestTDS, latestTurbidity)
            val isGood = evaluateWaterQuality(drinkingStandards, waterData)
            displayResults(waterData, isGood)
        }

        val aquacultureButton = findViewById<MaterialButton>(R.id.aquacultureButton)
        aquacultureButton.setOnClickListener {
            val waterData = getWaterData(latestPH, latestTemperature, latestTDS, latestTurbidity)
            val isGood = evaluateWaterQuality(aquacultureStandards, waterData)
            displayResults(waterData, isGood)
        }

        val industrialButton = findViewById<MaterialButton>(R.id.industrialButton)
        industrialButton.setOnClickListener {
            val waterData = getWaterData(latestPH, latestTemperature, latestTDS, latestTurbidity)
            val isGood = evaluateWaterQuality(industrialStandards, waterData)
            displayResults(waterData, isGood)
        }

        val domesticButton = findViewById<MaterialButton>(R.id.domesticButton)
        domesticButton.setOnClickListener {
            val waterData = getWaterData(latestPH, latestTemperature, latestTDS, latestTurbidity)
            val isGood = evaluateWaterQuality(domesticStandards, waterData)
            displayResults(waterData, isGood)
        }
    }



    // Function to display the results, moved outside of onCreate
    private fun displayResults(waterData: WaterData, isGood: Boolean) {
        val intent = Intent(this, MainActivity3::class.java).apply {
            putExtra("pH", waterData.pH)
            putExtra("turbidity", waterData.turbidity)
            putExtra("tds", waterData.tds)
            putExtra("temperature", waterData.temperature)
            putExtra("isGood", isGood)
        }
        startActivity(intent)
    }





}