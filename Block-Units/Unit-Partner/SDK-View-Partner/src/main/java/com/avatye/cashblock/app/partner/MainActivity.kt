package com.avatye.cashblock.app.partner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.avatye.cashblock.CashBlockSDK
import com.avatye.cashblock.app.partner.databinding.ActivityMainBinding
import com.avatye.cashblock.base.component.domain.entity.user.GenderType
import com.avatye.cashblock.base.component.domain.entity.user.Profile
import com.avatye.cashblock.feature.roulette.CashBlockRoulette
import com.avatye.cashblock.feature.roulette.component.model.listener.ITicketCount

class MainActivity : AppCompatActivity() {

    private val vb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vb.root)
        //MobileAds.initialize(this)
        //Configuration.setAppUserId("io2tree-2@avatye.com")

        /** set profile */
        vb.buttonAuth.setOnClickListener {
            if (vb.userId.text.isNullOrEmpty()) {
                Toast.makeText(this, "아이디를 입력하세요", Toast.LENGTH_SHORT).show()
            } else {
                setAppUserID()
            }
        }

        vb.buttonCondition.setOnClickListener {
            ticketCondition()
        }
    }

    override fun onResume() {
        viewAppUserID()
        super.onResume()
    }

    private fun viewAppUserID() {
        val appUserID = CashBlockSDK.getUserProfile().userId
        if (appUserID.isNotEmpty()) {
            vb.wrapAuth.visibility = View.GONE
            vb.wrapProfile.visibility = View.VISIBLE
            vb.profileId.text = "ID : $appUserID"
        } else {
            vb.wrapAuth.visibility = View.VISIBLE
            vb.wrapProfile.visibility = View.GONE
        }
    }

    private fun ticketCondition() {
        CashBlockRoulette.checkTicketCondition(listener = object : ITicketCount {
            override fun callback(balance: Int, condition: Int) {
                Log.d("PARTNER", "TicketCondition { balance: $balance, condition: $condition }")
                vb.ticketBalance.text = "Ticket Balance: $balance"
                vb.ticketCondition.text = "Ticket Condition: $condition"
            }
        })
    }

    private fun setAppUserID() {
        val appUserID = vb.userId.text.toString()
        CashBlockSDK.setUserProfile(profile = Profile(userId = appUserID, birthYear = 2000, gender = GenderType.MALE))
        viewAppUserID()
    }
}