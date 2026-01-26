package com.example.learning

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.learning.utils.LocaleHelper

abstract class BaseActivity : AppCompatActivity() {
    
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { LocaleHelper.onAttach(it) })
    }
} 