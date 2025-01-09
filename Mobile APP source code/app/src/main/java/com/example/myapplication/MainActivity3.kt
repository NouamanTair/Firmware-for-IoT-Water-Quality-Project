package com.example.myapplication

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        // Retrieve water quality data from the intent
        val pH = intent.getDoubleExtra("pH", 0.0)
        val turbidity = intent.getDoubleExtra("turbidity", 0.0)
        val tds = intent.getDoubleExtra("tds", 0.0)
        val temperature = intent.getDoubleExtra("temperature", 0.0)
        val isGood = intent.getBooleanExtra("isGood", true)
        // Update the title to show the water quality result
        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = if (isGood) "Good to Use" else "Bad to Use"

        // Update UI with the received data
        findViewById<TextView>(R.id.phValue).text = String.format("%.2f", pH)
        findViewById<TextView>(R.id.turbidityValue).text = String.format("%.2f NTU", turbidity)
        findViewById<TextView>(R.id.tdsValue).text = String.format("%.2f mg/L", tds)
        findViewById<TextView>(R.id.temperatureValue).text = String.format("%.2f°C", temperature)

        // Optionally, display a message about the overall water quality
        if (isGood) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Water Quality Information")
                .setMessage("Good to Use. The water quality meets the required standards.")
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_info) // Use a standard info icon

            val dialog = builder.create()

            dialog.show()

            // Set the message background color to green
            val messageView = dialog.findViewById<TextView>(android.R.id.message)
            messageView?.setBackgroundColor(Color.GREEN)
            // This requires you to know the layout structure of the AlertDialog; it might be different for various Android versions
            val parentPanel = dialog.findViewById<View>(android.R.id.content)?.parent as? View
            parentPanel?.setBackgroundColor(Color.GREEN)

        } else {
            // Build the AlertDialog as usual
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Water Quality Warning")
                .setMessage("Bad to Use. The water quality does not meet the required standards.")
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)

            // Create the AlertDialog object and return it
            val dialog = builder.create()

            // Show the dialog
            dialog.show()

            // Now that the dialog is shown, you can search for its view elements and modify them
            // Find the TextView for the message
            val messageView = dialog.findViewById<TextView>(android.R.id.message)
            // Set the background color of the TextView to red
            messageView?.setBackgroundColor(Color.RED)

            // If you want to change the background of the entire dialog, you can find the parent view and set its background
            // This requires you to know the layout structure of the AlertDialog; it might be different for various Android versions
            val parentPanel = dialog.findViewById<View>(android.R.id.content)?.parent as? View
            parentPanel?.setBackgroundColor(Color.RED)
        }


    }
}