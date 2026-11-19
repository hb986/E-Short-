package com.eshort.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF160D2B)
private val Panel = Color(0xFF2A1B49)
private val Lilac = Color(0xFFD9C2FF)
private val Accent = Color(0xFF9B72F2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(22, 13, 43)
        window.navigationBarColor = android.graphics.Color.rgb(22, 13, 43)
        setContent { EShortApp() }
    }
}

@Composable
private fun EShortApp() {
    val context = LocalContext.current
    var source by remember { mutableStateOf<Uri?>(null) }
    var duration by remember { mutableIntStateOf(30) }
    var maxClips by remember { mutableIntStateOf(5) }
    var busy by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var outputs by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) { }
            source = uri
            result = ""
            outputs = emptyList()
        }
    }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Lilac, secondary = Accent, background = Ink, surface = Panel,
        onPrimary = Ink, onBackground = Color.White, onSurface = Color.White
    )) {
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFF1A1032), Color(0xFF35205C), Color(0xFF17294C)))
            )
        ) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("E SHORT", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text("VIDEO CLIP MAKER", color = Lilac, fontSize = 11.sp, letterSpacing = 2.sp)
                    Text("Split a video into ready-to-share short clips.", color = Color.White.copy(.75f), fontSize = 14.sp)
                }

                Surface(color = Color.White.copy(.08f), shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("1. CHOOSE YOUR VIDEO", color = Lilac, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            source?.lastPathSegment?.substringAfterLast('/') ?: "No video selected",
                            color = Color.White.copy(.8f), fontSize = 13.sp
                        )
                        Button(
                            onClick = { picker.launch(arrayOf("video/*")) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !busy
                        ) { Text("SELECT VIDEO FROM PHONE") }
                        OutlinedButton(
                            onClick = {
                                source?.let {
                                    try {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW).setDataAndType(it, "video/*")
                                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        )
                                    } catch (_: Exception) { result = "No video player is available on this phone." }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = source != null && !busy
                        ) { Text("PREVIEW SELECTED VIDEO") }
                    }
                }

                Surface(color = Color.White.copy(.08f), shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("2. CLIP SETTINGS", color = Lilac, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Length of each clip", color = Color.White, fontWeight = FontWeight.SemiBold)
                        ChoiceRow(listOf(15, 30, 45, 60, 90), duration) { duration = it }
                        Text("Maximum number of clips", color = Color.White, fontWeight = FontWeight.SemiBold)
                        ChoiceRow(listOf(1, 3, 5, 10, 20), maxClips) { maxClips = it }
                        Text(
                            "Clips are cut one after another from the start of the video. The final clip may be shorter.",
                            color = Color.White.copy(.65f), fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        val input = source
                        if (input == null) {
                            result = "Choose a video first."
                        } else {
                            busy = true
                            result = ""
                            outputs = emptyList()
                            progress = "Preparing video…"
                            VideoClipExporter.export(
                                context = context,
                                source = input,
                                clipDurationSeconds = duration,
                                maxClips = maxClips,
                                onProgress = { progress = it },
                                onComplete = {
                                    busy = false
                                    progress = ""
                                    outputs = it
                                    result = "Done. ${it.size} clip(s) saved in Movies/E SHORT."
                                },
                                onError = {
                                    busy = false
                                    progress = ""
                                    result = it
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = !busy
                ) {
                    Text(if (busy) "CREATING CLIPS…" else "CREATE SHORT CLIPS", fontWeight = FontWeight.Bold)
                }

                if (busy) {
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                    Text(progress, color = Color.White, fontSize = 12.sp)
                }
                if (result.isNotBlank()) {
                    Surface(color = Color.White.copy(.10f), shape = RoundedCornerShape(16.dp)) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(result, color = Color.White, fontSize = 13.sp)
                            outputs.lastOrNull()?.let { last ->
                                OutlinedButton(onClick = {
                                    try {
                                        context.startActivity(
                                            Intent(Intent.ACTION_VIEW).setDataAndType(last, "video/*")
                                                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        )
                                    } catch (_: Exception) { result = "Clip saved, but no video player is available." }
                                }) { Text("PLAY LAST CREATED CLIP") }
                            }
                        }
                    }
                }

                Text(
                    "Offline processing • No account or API key required",
                    color = Color.White.copy(.55f), fontSize = 11.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun ChoiceRow(values: List<Int>, selected: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        values.forEach { value ->
            val active = value == selected
            FilterChip(
                selected = active,
                onClick = { onSelect(value) },
                label = { Text(value.toString(), fontSize = 11.sp) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
