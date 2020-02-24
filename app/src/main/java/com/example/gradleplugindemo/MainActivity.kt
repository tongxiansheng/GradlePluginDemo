package com.example.gradleplugindemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.mylibrary.SecondActivity

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }
    }
}
