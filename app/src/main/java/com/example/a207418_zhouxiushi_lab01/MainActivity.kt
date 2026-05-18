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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.example.a207418_zhouxiushi_lab01.ui.theme.A207418_ZHOUXIUSHI_Lab01Theme

// ==================== Data Class ====================
data class VideoInfo(
    val videoTitle: String,
    val videoSize: String
)

data class VideoItem(
    val thumbnailRes: Int,
    val title: String,
    val duration: String,
    val size: String
)

// ==================== ViewModel ====================
class VideoViewModel : ViewModel() {
    private val _currentVideo = mutableStateOf(VideoInfo("Default Title", "Default Size"))
    val currentVideo: State<VideoInfo> = _currentVideo

    fun setCurrentVideo(title: String, size: String) {
        _currentVideo.value = VideoInfo(title, size)
    }
}

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

    val videoList = remember {
        listOf(
            VideoItem(R.drawable.video_thumb_1, "Jay Chou 周杰伦 Loving you is no big deal MV", "03:36", "46.67 MB"),
            VideoItem(R.drawable.video_thumb_2, "Jay Chou 周杰伦 I am not worthy MV", "03:36", "74.52 MB"),
            VideoItem(R.drawable.video_thumb_3, "周杰伦 JAY CHOU Bullfighting", "04:17", "101.65 MB"),
            VideoItem(R.drawable.video_thumb_4, "周杰伦 Jay Chou The Secret That Cannot Be Told MV", "05:25", "12.00 MB")
        )
    }

    val filteredVideos = videoList.filter {
        it.title.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Search videos...") },
                    trailingIcon = {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (index == selectedTab) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .clickable { selectedTab = index }
                                .padding(4.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val bottomItems = listOf(
                    "Home" to Icons.Default.Home,
                    "List" to Icons.AutoMirrored.Filled.List,
                    "Add" to Icons.Default.Add,
                    "Settings" to Icons.Default.Settings
                )
                bottomItems.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                icon,
                                contentDescription = label,
                                tint = if (selectedBottomNav == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = { Text(label) },
                        selected = index == selectedBottomNav,
                        onClick = {
                            selectedBottomNav = index
                            val target = when (index) {
                                0 -> "home"
                                1 -> "video_list"
                                2 -> "add_video"
                                3 -> "settings"
                                else -> "home"
                            }
                            navController.navigate(target) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                text = "Showing all videos",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredVideos.size) { index ->
                    val video = filteredVideos[index]
                    var expand by remember { mutableStateOf(false) }

                    Card(
                        onClick = {
                            viewModel.setCurrentVideo(video.title, video.size)
                            navController.navigate(
                                "video_detail",
                                navOptions = navOptions { launchSingleTop = true }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = video.thumbnailRes),
                                    contentDescription = video.title,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(
                                            MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.7f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = video.duration,
                                        color = MaterialTheme.colorScheme.inverseOnSurface,
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = video.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = video.size,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (expand) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Resolution: 1080p • MP4",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { expand = !expand },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "More",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = "Matric Number: a207418",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Current Video Info",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Title: ${currentVideo.videoTitle}",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Size: ${currentVideo.videoSize}",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MX Video Player Settings",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Version: 1.0.0",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Matric: a207418",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(onClick = { navController.popBackStack() }) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "All Videos",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("• Jay Chou MV 1", style = MaterialTheme.typography.titleMedium)
            Text("• Jay Chou MV 2", style = MaterialTheme.typography.titleMedium)
            Text("• Jay Chou MV 3", style = MaterialTheme.typography.titleMedium)
            Text("• Jay Chou MV 4", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
                navController.navigate(
                    "add_video",
                    navOptions = navOptions { launchSingleTop = true }
                )
            }) {
                Text("Go to Add Video")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Home")
            }
        }
    }
}

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
            Spacer(modifier = Modifier.height(16.dp))
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
                Text(
                    "Please fill in all fields!",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = {
                if (videoTitle.isNotBlank() && videoSize.isNotBlank()) {
                    viewModel.setCurrentVideo(videoTitle, videoSize)
                    navController.navigate(
                        "video_detail",
                        navOptions = navOptions { launchSingleTop = true }
                    )
                } else {
                    showError = true
                }
            }) {
                Text("Save & View in Detail")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Home")
            }
        }
    }
}