package com.example.a207418_zhouxiushi_lab01.data

import kotlinx.coroutines.flow.Flow

class VideoRepository(private val videoDao: VideoDao) {

    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()

    suspend fun insert(video: VideoEntity) {
        videoDao.insertVideo(video)
    }

    suspend fun delete(video: VideoEntity) {
        videoDao.deleteVideo(video)
    }

    suspend fun deleteAll() {
        videoDao.deleteAllVideos()
    }
}
