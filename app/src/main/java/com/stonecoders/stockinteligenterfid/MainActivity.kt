package com.stonecoders.stockinteligenterfid

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.stonecoders.stockinteligenterfid.room.viewmodels.BusinessViewModel


class MainActivity : AppCompatActivity(), BaseActivity {
    private val businessViewModel: BusinessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun businessViewModel(): BusinessViewModel = businessViewModel


}


interface BaseActivity {

    fun businessViewModel(): BusinessViewModel
}