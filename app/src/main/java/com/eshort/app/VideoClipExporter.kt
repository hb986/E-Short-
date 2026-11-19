package com.eshort.app

import android.content.ContentValues
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Locale

/**
 * Creates consecutive short clips from a user-selected local video.
 * Files are exported to the public Movies/E SHORT folder.
 */
object VideoClipExporter {
    fun export(
        context: Context,
        source: Uri,
        clipDurationSeconds: Int,
        maxClips: Int,
        onProgress: (String) -> Unit,
        onComplete: (List<Uri>) -> Unit,
        onError: (String) -> Unit
    ) {
        if (Build.VERSION.SDK_INT < 29) {
            onError("Video export currently requires Android 10 or newer.")
            return
        }
        val durationSeconds = try {
            val retriever = MediaMetadataRetriever()
            try {
                retriever.setDataSource(context, source)
                val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull() ?: 0L
                duration / 1000L
            } finally {
                retriever.release()
            }
        } catch (e: Exception) {
            onError("Could not read this video: ${e.localizedMessage ?: "unsupported format"}")
            return
        }
        if (durationSeconds < 1L) {
            onError("The selected video has no readable duration.")
            return
        }
        val clipMs = clipDurationSeconds.coerceIn(5, 180) * 1000L
        val total = minOf(maxClips.coerceIn(1, 20), ((durationSeconds * 1000L + clipMs - 1L) / clipMs).toInt())
        val outputDir = File(context.cacheDir, "eshort_exports").apply { mkdirs() }
        val saved = mutableListOf<Uri>()

        fun exportNext(index: Int) {
            if (index >= total) {
                onProgress("Finished")
                onComplete(saved.toList())
                return
            }
            val startMs = index * clipMs
            val endMs = minOf(startMs + clipMs, durationSeconds * 1000L)
            val tempFile = File(outputDir, "clip_${System.currentTimeMillis()}_${index + 1}.mp4")
            onProgress("Creating clip ${index + 1} of $total…")
            val mediaItem = MediaItem.Builder()
                .setUri(source)
                .setClippingConfiguration(
                    MediaItem.ClippingConfiguration.Builder()
                        .setStartPositionMs(startMs)
                        .setEndPositionMs(endMs)
                        .build()
                )
                .build()
            val editedItem = EditedMediaItem.Builder(mediaItem).build()
            val transformer = Transformer.Builder(context)
                .addListener(object : Transformer.Listener {
                    override fun onCompleted(composition: androidx.media3.transformer.Composition, exportResult: ExportResult) {
                        var outputUri: Uri? = null
                        try {
                            if (!tempFile.exists() || tempFile.length() == 0L) {
                                throw IOException("The video encoder produced an empty file.")
                            }
                            val values = ContentValues().apply {
                                put(MediaStore.Video.Media.DISPLAY_NAME, String.format(Locale.US, "E_SHORT_%02d_%d.mp4", index + 1, System.currentTimeMillis()))
                                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/E SHORT")
                                put(MediaStore.Video.Media.IS_PENDING, 1)
                            }
                            outputUri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
                                ?: throw IOException("Could not create a video in the gallery.")
                            context.contentResolver.openOutputStream(outputUri!!)?.use { output ->
                                FileInputStream(tempFile).use { input -> input.copyTo(output) }
                            } ?: throw IOException("Could not write the exported video.")
                            values.clear()
                            values.put(MediaStore.Video.Media.IS_PENDING, 0)
                            context.contentResolver.update(outputUri!!, values, null, null)
                            saved.add(outputUri!!)
                            tempFile.delete()
                            exportNext(index + 1)
                        } catch (e: Exception) {
                            outputUri?.let { try { context.contentResolver.delete(it, null, null) } catch (_: Exception) { } }
                            tempFile.delete()
                            onError("Could not save clip ${index + 1}: ${e.localizedMessage ?: "storage error"}")
                        }
                    }

                    override fun onError(
                        composition: androidx.media3.transformer.Composition,
                        exportResult: ExportResult,
                        exportException: ExportException
                    ) {
                        tempFile.delete()
                        onError("Clip ${index + 1} failed: ${exportException.localizedMessage ?: "video export error"}")
                    }
                })
                .build()
            try {
                transformer.start(editedItem, tempFile.absolutePath)
            } catch (e: Exception) {
                tempFile.delete()
                onError("Could not start video export: ${e.localizedMessage ?: "unknown error"}")
            }
        }
        exportNext(0)
    }
}
