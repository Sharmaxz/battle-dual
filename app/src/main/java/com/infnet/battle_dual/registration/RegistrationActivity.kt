package com.infnet.battle_dual.registration

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.infnet.battle_dual.shared.DisplayManager
import com.infnet.battle_dual.R

@SuppressLint("Registration")
class RegistrationActivity : AppCompatActivity() {

    private var metrics : DisplayMetrics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        metrics = resources.displayMetrics

        setContentView(R.layout.activity_registration)
    }

    fun arrow() {
        val view = findViewById<ConstraintLayout>(R.id.layout)
        val down_threshold = metrics?.heightPixels?.minus(96)?.times(95)?.div(100)!!.toFloat() //metrics?.heightPixels?.times(95)?.div(100)!!.toFloat()
        val up_threshold = metrics?.heightPixels?.times(40)?.div(100)!!.toFloat()
        val arrow = findViewById<ImageView>(R.id.arrow_main)
        val arrow0 = findViewById<ImageView>(R.id.arrow_0)
        //arrow0.y = arrow.y - 10
        val arrow1 = findViewById<ImageView>(R.id.arrow_1)
        //arrow1.y = arrow0.y - 10
        val arrow2 = findViewById<ImageView>(R.id.arrow_2)
        //arrow2.y = arrow1.y - 10
        RegistrationArrowAnim(this, view, arrow, arrow0, arrow1, arrow2, up_threshold, down_threshold)
    }

}