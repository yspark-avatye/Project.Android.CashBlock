package com.avatye.cashblock.app.partner

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.avatye.cashblock.app.partner.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {

    private val vb: ActivityIntroBinding by lazy {
        ActivityIntroBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1000L)
    }

}