package com.example.composetutorial

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import android.media.Image
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text
import java.util.EventListener

class MainActivity : AppCompatActivity() {
    private var drinkCount: Int = 2
    private lateinit var sensor: Sensor
    private lateinit var sensorManger: SensorManager
    private var sensorEventL: SensorEventListener? = null
    private lateinit var viewtext: TextView
    private lateinit var coffeeIcon: ImageView
    private var nowImageName = "coffee2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView()
        initSensor()
    }

    private fun setView() {
        Log.i(Log.INFO.toString(), "Start!!!!!")
        setContentView(R.layout.activity_main)
        coffeeIcon = findViewById(R.id.coffee)
        val againButton = findViewById<Button>(R.id.again_button)
        val textHello = findViewById<TextView>(R.id.textView)
        viewtext = findViewById<TextView>(R.id.textView2)
        coffeeIcon.setOnClickListener(View.OnClickListener() {
            if (drinkCount < 6) {
                nowImageName = "coffee%s".format(drinkCount)
                val resId = resources.getIdentifier(nowImageName, "drawable", packageName)
                coffeeIcon.setImageResource(resId)
                drinkCount += 1
                if (drinkCount == 6) {
                    againButton.visibility = View.VISIBLE
                    textHello.text = "Would you like to have more...?"
                }
            }
        })
        againButton.setOnClickListener(View.OnClickListener {
            nowImageName = "coffee2"
            val resId = resources.getIdentifier(nowImageName, "drawable", packageName)
            coffeeIcon.setImageResource(resId)
            drinkCount = 3
            againButton.visibility = View.INVISIBLE
            textHello.text = "Here you are!"
        })
    }

    private fun initSensor() {
        sensorManger = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManger.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onResume() {
        super.onResume()
        sensorEventL = object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                var changeImageName = nowImageName
                if (p0!!.values[0] <= 2.0 && p0!!.values[0] >= -2.0 && p0!!.values[1] >= 0.0) {
                    // not implement
                } else if (p0!!.values[0] > 2.0 && p0!!.values[0] <= 6.0 && p0!!.values[1] >= 0.0) {
                    changeImageName = "coffee_tilt_left"
                } else if (p0!!.values[0] < -2.0 && p0!!.values[0] >= -6.0 && p0!!.values[1] >= 0.0) {
                    changeImageName = "coffee_tilt_right"
                } else if (p0!!.values[0] > 6.0 && p0!!.values[1] >= -4.0) {
                    changeImageName = "coffee_out_left"
                } else if (p0!!.values[0] < -6.0 && p0!!.values[1] >= -4.0) {
                    changeImageName = "coffee_out_right"
                } else {
                    changeImageName = "coffee_out_down"
                }
                viewtext.text = "%.2f/%.2f/%.2f".format(p0!!.values[0], p0!!.values[1], p0!!.values[2])
                val resId = resources.getIdentifier(changeImageName, "drawable", packageName)
                coffeeIcon.setImageResource(resId)
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                pushToast(">>> よくわかんないチェンジ：%s".format(p1))
            }
        }
        sensor?.also {sensor ->
            sensorManger.registerListener(sensorEventL, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManger.unregisterListener(sensorEventL)
    }

    private fun pushToast(msg: String) {
        Toast.makeText(this, "%s".format(msg), Toast.LENGTH_LONG).show()
    }
}
