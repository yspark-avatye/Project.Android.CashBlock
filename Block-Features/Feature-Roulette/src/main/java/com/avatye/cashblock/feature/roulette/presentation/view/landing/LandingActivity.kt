package com.avatye.cashblock.feature.roulette.presentation.view.landing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.avatye.cashblock.base.component.domain.entity.base.LandingType
import com.avatye.cashblock.feature.roulette.databinding.AcbsrActivityLandingBinding
import com.avatye.cashblock.feature.roulette.presentation.view.box.TicketBoxActivity

internal class LandingActivity : AppCompatActivity() {


    private val binding: AcbsrActivityLandingBinding by lazy {
        AcbsrActivityLandingBinding.inflate(LayoutInflater.from(this))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        when (LandingType.from(intent?.action ?: "")) {
            LandingType.ROULETTE_MAIN -> toMain()
            LandingType.ROULETTE_TICKET_BOX -> toTicketBox()
            LandingType.ROULETTE_VIDEO_TICKET -> toVideoTicket()
            LandingType.ROULETTE_TOUCH_TICKET -> toTouchTicket()
            else -> toMain()
        }
    }


    private fun toMain() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.action = Intent.ACTION_MAIN
        intent?.addCategory(Intent.CATEGORY_LAUNCHER)
        startActivity(intent)
        finish()
    }


    private fun toTouchTicket() {
        finish()

    }


    private fun toVideoTicket() {
        finish()
    }


    private fun toTicketBox() {
        startActivity(Intent(this, TicketBoxActivity::class.java).apply {
            this.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP)
        })
        finish()
    }
}