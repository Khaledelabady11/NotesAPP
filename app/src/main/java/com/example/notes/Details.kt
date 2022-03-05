package com.example.notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_details.*

class Details : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        var title=intent.extras!!.getString("titleKey")
        var note=intent.extras!!.getString("noteKey")
        titleTxt.text=title
        noteTxt.text=note

    }
}