package com.example.a207418_zhouxiushi_lab01

import android.app.Application
import com.example.a207418_zhouxiushi_lab01.data.AppDatabase
import com.example.a207418_zhouxiushi_lab01.data.VideoRepository

class VideoApplication : Application() {

    private val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { VideoRepository(database.videoDao()) }
}
