package app.lumalabs.luma.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import app.lumalabs.luma.domain.model.Photo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun queryAllPhotos(): Flow<List<Photo>> = flow {
        val photos = mutableListOf<Photo>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATA
        )

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                photos.add(
                    Photo(
                        uri = contentUri.toString(),
                        name = cursor.getString(nameColumn),
                        size = cursor.getLong(sizeColumn),
                        dateTaken = cursor.getLong(dateColumn),
                        path = cursor.getString(pathColumn)
                    )
                )
            }
        }
        emit(photos)
    }.flowOn(Dispatchers.IO)

    fun createTrashRequest(uris: List<Uri>) = MediaStore.createTrashRequest(
        context.contentResolver,
        uris,
        true
    )

    fun createDeleteRequest(uris: List<Uri>) = MediaStore.createDeleteRequest(
        context.contentResolver,
        uris
    )
}
