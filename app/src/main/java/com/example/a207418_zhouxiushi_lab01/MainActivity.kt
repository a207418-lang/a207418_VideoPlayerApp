package com.example.a207418_zhouxiushi_lab01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a207418_zhouxiushi_lab01.ui.theme.A207418_ZHOUXIUSHI_Lab01Theme

// ==================== Data Class ====================
data class VideoInfo(
    val videoTitle: String,
    val videoSize: String
)

// ==================== ViewModel ====================
class VideoViewModel : ViewModel() {
    private val _currentVideo = mutableStateOf(VideoInfo("", ""))
    val currentVideo: State<VideoInfo> = _currentVideo

    fun setCurrentVideo(title: String, size: String) {
        _currentVideo.value = VideoInfo(title, size)
    }
}

data class VideoItem(
    val thumbnailRes: Int,
    val title: String,
    val duration: String,
    val size: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            A207418_ZHOUXIUSHI_Lab01Theme {
                VideoAppNavigation()
            }
        }
    }
}

// ==================== 5-Page Navigation ====================
@Composable
fun VideoAppNavigation() {
    val navController = rememberNavController()
    val videoViewModel: VideoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            MXVideoHomePage(navController, videoViewModel)
        }
        composable("video_detail") {
            VideoDetailScreen(navController, videoViewModel)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("video_list") {
            VideoListScreen(navController)
        }
        composable("add_video") {
            AddVideoScreen(navController, videoViewModel)
        }
    }
}

// ==================== Page 1: Home ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MXVideoHomePage(
    navController: NavHostController,
    viewModel: VideoViewModel
) {
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var selectedBottomNav by remember { mutableStateOf(0) }
    val tabs = listOf("Video", "Album", "Folder", "Music")

    val videoList = listOf(
        VideoItem(R.drawable.video_thumb_1, "Jay Chou 周杰伦 Loving you is no big deal MV", "03:36", "46.67 MB"),
        VideoItem(R.drawable.video_thumb_2, "Jay Chou 周杰伦 I am not worthy MV", "03:36", "74.52 MB"),
        VideoItem(R.drawable.video_thumb_3, "周杰伦 JAY CHOU Bullfighting", "04:17", "101.65 MB"),
        VideoItem(R.drawable.video_thumb_4, "周杰伦 Jay Chou The Secret That Cannot Be Told MV", "05:25", "12.00 MB")
    )

    val filteredVideos = videoList.filter {
        it.title.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    placeholder = { Text("Search videos...") },
                    trailingIcon = {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Text(
                            text = tab,
                            fontSize = 20.sp,
                            fontWeight = if (index == selectedTab) FontWeight.Bold else FontWeight.Normal,
                            color = if (index == selectedTab) Color(0xFFFF9800) else Color.Gray,
                            modifier = Modifier.clickable { selectedTab = index }.padding(4.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFFF5F5F5)) {
                val bottomItems = listOf(
                    "Home" to Icons.Default.Home,
                    "List" to Icons.Default.List,
                    "Add" to Icons.Default.Add,
                    "Settings" to Icons.Default.Settings
                )
                bottomItems.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        selected = index == selectedBottomNav,
                        onClick = {
                            selectedBottomNav = index
                            when (index) {
                                0 -> navController.navigate("home")
                                1 -> navController.navigate("video_list")
                                2 -> navController.navigate("add_video")
                                3 -> navController.navigate("settings")
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color(0xFFF5F5F5))
        ) {
            Text(
                text = "Showing all videos",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = Color(0xFFFF9800),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredVideos.size) { index ->
                    val video = filteredVideos[index]
                    var expand by remember { mutableStateOf(false) }
                    Card(
                        onClick = {
                            expand = !expand
                            viewModel.setCurrentVideo(video.title, video.size)
                            navController.navigate("video_detail")
                        },
                        modifier = Modifier.fillMaxWidth().animateContentSize(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                                Image(
                                    painter = painterResource(id = video.thumbnailRes),
                                    contentDescription = video.title,
                                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = video.duration, color = Color.White, fontSize = 12.sp)
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = video.title, fontSize = 16.sp, maxLines = 1)
                                    Text(text = video.size, fontSize = 12.sp, color = Color.Gray)
                                    if (expand) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Resolution: 1080p • Format: MP4",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More",
                                    modifier = Modifier.size(20.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = "Matric Number: a207418",
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

// ==================== Page 2: Video Detail ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(navController: NavHostController, viewModel: VideoViewModel) {
    val currentVideo = viewModel.currentVideo.value
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Home, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Current Video Info",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF9800)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Title: ${currentVideo.videoTitle}", fontSize = 18.sp)
            Text(text = "Size: ${currentVideo.videoSize}", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Back to Home")
            }
        }
    }
}

// ==================== Page 3: Settings ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Settings, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "MX Video Player Settings", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Version: 1.0.0", fontSize = 16.sp)
            Text(text = "Matric: a207418", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { navController.navigate("home") }) {
                Text("Back to Home")
            }
        }
    }
}

// ==================== Page 4: Video List ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(navController: NavHostController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Video List") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("All Videos", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            Text("• Jay Chou MV 1", fontSize = 18.sp)
            Text("• Jay Chou MV 2", fontSize = 18.sp)
            Text("• Jay Chou MV 3", fontSize = 18.sp)
            Text("• Jay Chou MV 4", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(30.dp))
            Button(onClick = { navController.navigate("add_video") }) {
                Text("Go to Add Video")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Home")
            }
        }
    }
}

// ==================== Page 5: Add Video ====================
// ==================== Page 5: Add Video ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVideoScreen(navController: NavHostController, viewModel: VideoViewModel) {
    var videoTitle by remember { mutableStateOf("") }
    var videoSize by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add New Video") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = videoTitle,
                onValueChange = {
                    videoTitle = it
                    showError = false
                },
                label = { Text("Video Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                value = videoSize,
                onValueChange = {
                    videoSize = it
                    showError = false
                },
                label = { Text("Video Size") },
                modifier = Modifier.fillMaxWidth()
            )
            if (showError) {
                Text("Please fill in all fields!", color = Color.Red, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                if (videoTitle.isNotBlank() && videoSize.isNotBlank()) {
                    viewModel.setCurrentVideo(videoTitle, videoSize)
                    navController.navigate("video_detail")
                } else {
                    showError = true
                }
            }) {
                Text("Save & View in Detail")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Home")
            }
        }
    }
}