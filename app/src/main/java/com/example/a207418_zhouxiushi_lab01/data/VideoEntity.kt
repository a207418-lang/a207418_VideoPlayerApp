package com.example.a207418_zhouxiushi_lab01.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_table")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val thumbnailRes: Int,
    val title: String,
    val duration: String,
    val size: String
)
