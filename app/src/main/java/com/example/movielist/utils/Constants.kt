package com.example.movielist.utils

object Constants {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val IMAGE_DOMAIN = "https://image.tmdb.org/t/p/w500/"
    private const val YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v="
    private const val YOUTUBE_THUMBNAIL_URL = "https://img.youtube.com/vi/"

    fun getYoutubeVideoPath(videoPath: String?): String {
        return YOUTUBE_VIDEO_URL + videoPath
    }

    @JvmStatic
    fun getYoutubeThumbnailPath(thumbnailPath: String?): String {
        return "$YOUTUBE_THUMBNAIL_URL$thumbnailPath/mqdefault.jpg"
    }
}