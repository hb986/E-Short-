package com.eshort.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val White = Color(0xFFFFF8FF)
private val Glass = Color(0x35FFFFFF)
private val Purple = Color(0xFF7137B7)
private val Blue = Color(0xFF426DEB)
private val Pink = Color(0xFFE45DBD)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(43, 23, 81)
        window.navigationBarColor = android.graphics.Color.rgb(28, 16, 56)
        setContent { EShortApp() }
    }
}

@Composable
private fun EShortApp() {
    var page by remember { mutableStateOf("Home") }
    var mode by remember { mutableStateOf("AUTO VIRAL") }
    var url by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("30 sec") }
    var ratio by remember { mutableStateOf("9:16") }
    var clips by remember { mutableStateOf("5") }
    var message by remember { mutableStateOf("") }

    MaterialTheme(colorScheme = darkColorScheme(primary = Color(0xFFE2C6FF), background = Color(0xFF1B1038), surface = Color(0xFF35205C), onBackground = White, onSurface = White)) {
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF211044), Color(0xFF542887), Color(0xFF354FA8), Color(0xFF251044))))) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(42.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Pink, Blue))), contentAlignment = Alignment.Center) { Icon(Icons.Default.Movie, null, tint = White) }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("E SHORT", color = White, fontSize = 21.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
                        Text(page.uppercase(), color = White.copy(.65f), fontSize = 9.sp, letterSpacing = 2.sp)
                    }
                    IconButton(onClick = { page = "Home" }) { Icon(Icons.Default.Home, "Home", tint = White) }
                }
                when (page) {
                    "Studio" -> StudioPage(mode, { mode = it; page = "Setup" }, { page = it })
                    "Setup" -> SetupPage(url, { url = it }, duration, { duration = it }, ratio, { ratio = it }, clips, { clips = it }, mode) { message = "Settings saved. AI processing is not connected yet." }
                    "Editor" -> EditorPage()
                    "Profile" -> InfoPage("Creator settings", listOf("Purple glass appearance", "Arabic / English interface", "Export quality options", "Local project workspace"))
                    "Face Tracking" -> InfoPage("AUTO FACE TRACKING", listOf("Face detection workflow", "Speaker focus and subject selection", "Vertical smart reframing", "Stable crop guidance"), true)
                    "Smart Captions" -> InfoPage("SMART CAPTIONS", listOf("Arabic and English subtitles", "Word highlighting", "Caption style controls", "Manual text correction"), true)
                    "Viral AI" -> InfoPage("VIRAL INTELLIGENCE", listOf("Hook strength analysis", "Pacing suggestions", "Title and hashtag ideas", "Clip ranking guidance"), true)
                    else -> HomePage(url, { url = it }, { page = "Setup" }, { page = "Studio" }, message)
                }
                Spacer(Modifier.weight(1f))
                Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp).clip(RoundedCornerShape(25.dp)).background(Glass).border(1.dp, Color.White.copy(.2f), RoundedCornerShape(25.dp)).padding(7.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("Home" to Icons.Default.Home, "Studio" to Icons.Default.AutoAwesome, "Editor" to Icons.Default.Movie, "Profile" to Icons.Default.Person).forEach { (name, icon) ->
                        Column(Modifier.clip(RoundedCornerShape(16.dp)).background(if (page == name) Brush.horizontalGradient(listOf(Purple, Blue)) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))).clickable { page = name }.padding(horizontal = 14.dp, vertical = 7.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icon, null, tint = White, modifier = Modifier.size(19.dp))
                            Text(name, color = White, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePage(url: String, onUrl: (String) -> Unit, setup: () -> Unit, studio: () -> Unit, message: String) {
    LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
        item {
            Text("Create videos\nworth watching.", color = White, fontSize = 29.sp, fontWeight = FontWeight.Bold, lineHeight = 34.sp)
            Text("Turn long videos into short-form stories.", color = White.copy(.72f), fontSize = 13.sp)
        }
        item {
            GlassCard {
                Text("VIDEO SOURCE", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                OutlinedTextField(url, onUrl, Modifier.fillMaxWidth(), placeholder = { Text("Paste a video link…", color = White.copy(.5f)) }, singleLine = true, shape = RoundedCornerShape(17.dp), colors = fieldColors())
                GlowButton("CHOOSE CLIP SETTINGS", Icons.Default.Tune, setup)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ToolTile("Face Tracking", Icons.Default.CenterFocusStrong, Modifier.weight(1f), studio)
                ToolTile("Smart Captions", Icons.Default.Subtitles, Modifier.weight(1f), studio)
            }
        }
        item {
            GlassCard {
                Text("YOUR AI SHORTS STUDIO", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Pick a workflow, review the suggested clips and keep control of every edit.", color = White.copy(.7f), fontSize = 12.sp)
                GlowButton("OPEN AI STUDIO", Icons.Default.AutoAwesome, studio)
            }
        }
        if (message.isNotBlank()) item { Text(message, color = Color(0xFFFFD9A8), fontSize = 11.sp) }
    }
}

@Composable
private fun StudioPage(mode: String, select: (String) -> Unit, open: (String) -> Unit) {
    LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
        item { Text("Choose your workflow", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold); Text("AI tools with you in control.", color = White.copy(.7f), fontSize = 12.sp) }
        item { ModeTile("AUTO VIRAL", "Find highlight moments automatically", Icons.Default.Bolt, mode == "AUTO VIRAL") { select("AUTO VIRAL") } }
        item { ModeTile("CUSTOM", "Choose duration, format and clip count", Icons.Default.Tune, mode == "CUSTOM") { select("CUSTOM") } }
        item { ModeTile("PRO EDIT", "Review and refine your clips", Icons.Default.ContentCut, mode == "PRO EDIT") { select("PRO EDIT") } }
        item { Text("AI TOOLS", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
        item { ToolTile("Auto Face Tracking", Icons.Default.CenterFocusStrong, Modifier.fillMaxWidth()) { open("Face Tracking") } }
        item { ToolTile("Smart Captions", Icons.Default.Subtitles, Modifier.fillMaxWidth()) { open("Smart Captions") } }
        item { ToolTile("Viral Intelligence & Hook Lab", Icons.Default.Insights, Modifier.fillMaxWidth()) { open("Viral AI") } }
        item { GlowButton("CUSTOMIZE CLIPS", Icons.Default.ArrowForward) { open("Setup") } }
    }
}

@Composable
private fun SetupPage(url: String, onUrl: (String) -> Unit, duration: String, onDuration: (String) -> Unit, ratio: String, onRatio: (String) -> Unit, clips: String, onClips: (String) -> Unit, mode: String, save: () -> Unit) {
    LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(13.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
        item { Text("Clip settings", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold); Text("Workflow: $mode", color = Color(0xFFE0C5FF), fontSize = 12.sp) }
        item { GlassCard { Text("VIDEO LINK", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp); OutlinedTextField(url, onUrl, Modifier.fillMaxWidth(), placeholder = { Text("Paste a supported video URL") }, singleLine = true, shape = RoundedCornerShape(16.dp), colors = fieldColors()) } }
        item { OptionsCard("CLIP DURATION", listOf("15 sec", "30 sec", "45 sec", "60 sec", "90 sec"), duration, onDuration) }
        item { OptionsCard("VIDEO FORMAT", listOf("9:16", "16:9", "1:1", "4:5"), ratio, onRatio) }
        item { OptionsCard("NUMBER OF CLIPS", listOf("3", "5", "10", "20", "Auto"), clips, onClips) }
        item { GlassCard { Text("AI OPTIONS", color = White, fontWeight = FontWeight.Bold); Text("Face tracking, speech recognition, highlight analysis and rendering require their processing engines to be connected.", color = White.copy(.7f), fontSize = 11.sp); GlowButton("SAVE SETTINGS", Icons.Default.Check, save) } }
    }
}

@Composable
private fun EditorPage() {
    LazyColumn(Modifier.fillMaxWidth().padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        item { Text("Video Editor", color = White, fontSize = 25.sp, fontWeight = FontWeight.Bold); Text("Preview and editing workspace", color = White.copy(.7f), fontSize = 12.sp) }
        item { GlassCard { Box(Modifier.fillMaxWidth().height(205.dp).clip(RoundedCornerShape(22.dp)).background(Brush.linearGradient(listOf(Purple, Blue, Pink))), contentAlignment = Alignment.Center) { Icon(Icons.Default.PlayCircle, null, tint = White, modifier = Modifier.size(65.dp)) }; Text("Import a video to preview it here", color = White.copy(.7f), fontSize = 11.sp) } }
        item { GlassCard { Text("TIMELINE", color = White, fontWeight = FontWeight.Bold); Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) { repeat(6) { Box(Modifier.weight(1f).height(42.dp).clip(RoundedCornerShape(8.dp)).background(Brush.linearGradient(listOf(Blue, Purple, Pink)))) } }; Spacer(Modifier.height(10.dp)); Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) { listOf("Trim", "Effects", "Color", "Text").forEach { Text(it, Modifier.clip(CircleShape).background(Glass).padding(horizontal = 12.dp, vertical = 8.dp), color = White, fontSize = 10.sp) } } } }
    }
}

@Composable
private fun InfoPage(title: String, rows: List<String>, explain: Boolean = false) {
    Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
        Text(title, color = White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        GlassCard {
            rows.forEach { Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 7.dp)) { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFFD8B8FF)); Spacer(Modifier.width(10.dp)); Text(it, color = White, fontSize = 13.sp) } }
        }
        if (explain) Text("UI preview only: the actual AI processing engine is not connected in this starter version.", color = Color(0xFFFFD7A8), fontSize = 11.sp)
    }
}

@Composable
private fun OptionsCard(title: String, options: List<String>, selected: String, choose: (String) -> Unit) {
    GlassCard {
        Text(title, color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            options.forEach { option ->
                val active = option == selected
                Box(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(if (active) Brush.horizontalGradient(listOf(Purple, Blue)) else Brush.horizontalGradient(listOf(Glass, Glass))).border(1.dp, Color.White.copy(if (active) .5f else .13f), RoundedCornerShape(12.dp)).clickable { choose(option) }.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text(option, color = White, fontSize = 9.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
    }
}

@Composable
private fun ModeTile(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, click: () -> Unit) {
    GlassCard(Modifier.clickable { click() }.border(1.dp, Color.White.copy(if (selected) .5f else .16f), RoundedCornerShape(25.dp))) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Pink, Blue))), contentAlignment = Alignment.Center) { Icon(icon, null, tint = White) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(title, color = White, fontWeight = FontWeight.Bold); Text(description, color = White.copy(.68f), fontSize = 11.sp) }
            Icon(Icons.Default.ArrowForward, null, tint = White)
        }
    }
}

@Composable
private fun ToolTile(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, click: () -> Unit) {
    GlassCard(modifier.clickable { click() }) {
        Icon(icon, null, tint = Color(0xFFE7C6FF), modifier = Modifier.size(25.dp))
        Text(title, color = White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(Brush.linearGradient(listOf(Color(0x48FFFFFF), Color(0x20D5B7FF), Color(0x284B8FFF)))).border(1.dp, Color.White.copy(.22f), RoundedCornerShape(26.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp), content = content)
}

@Composable
private fun GlowButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, click: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Brush.horizontalGradient(listOf(Color(0xFFEBDFFF), Color(0xFFC9B4FF), Color(0xFFB9D4FF)))).clickable { click() }.padding(15.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFF29134E)); Spacer(Modifier.width(8.dp)); Text(label, color = Color(0xFF29134E), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = White, unfocusedTextColor = White,
    focusedBorderColor = Color(0xFFD7B8FF), unfocusedBorderColor = Color.White.copy(.25f),
    cursorColor = White
)
