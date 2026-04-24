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
import androidx.compose.material.icons.filled.Search
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

// ==================== Lab4 Data Class ====================
data class VideoInfo(//储存视频信息类型
    val videoTitle: String,
    val videoSize: String
)

// ==================== Lab4 ViewModel ====================
class VideoViewModel : ViewModel() {//记住你点了哪个视频
    private val _currentVideo = mutableStateOf(VideoInfo("", ""))
    val currentVideo: State<VideoInfo> = _currentVideo

    fun setCurrentVideo(title: String, size: String) {
        _currentVideo.value = VideoInfo(title, size)
    }
}

// 你原来的VideoItem不动
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
                // ==================== Lab4 Navigation 入口 ====================
                VideoAppNavigation()
            }
        }
    }
}

// ==================== Lab4 导航总控制 ====================
@Composable//管理三个页面跳转
fun VideoAppNavigation() {
    val navController = rememberNavController()
    val videoViewModel: VideoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home" // 首页作为启动页
    ) {
        // 屏幕1：首页（你原来的整个界面）
        composable("home") {
            MXVideoHomePage(
                matricNumber = "a207418",
                navController = navController,
                viewModel = videoViewModel
            )
        }

        // 屏幕2：视频详情页
        composable("video_detail") {
            VideoDetailScreen(
                navController = navController,
                viewModel = videoViewModel
            )
        }

        // 屏幕3：设置页
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}

// ==================== 屏幕2：视频详情页 ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    navController: NavHostController,//返回首页
    viewModel: VideoViewModel//拿视频数据
) {
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
    ) { padding ->
        Column(//整个页面居中、放文字
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
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

// ==================== 屏幕3：设置页 ====================
@OptIn(ExperimentalMaterial3Api::class)//点底部 Settings跳到这个页面
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
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

// ==================== 你原来的主界面：只加导航参数与点击跳转 ====================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MXVideoHomePage(
    matricNumber: String,
    navController: NavHostController,
    viewModel: VideoViewModel
) {

    var searchText by remember { mutableStateOf("") }//保存搜索结果
    var selectedTab by remember { mutableStateOf(0) }//记住顶部选中的东西
    var selectedBottomNav by remember { mutableStateOf(0) }//记住底部选中的按钮
    val tabs = listOf("Video", "Album", "Folder", "Music")

    // ==================== 保留你改好的4个视频 ====================
    val videoList = listOf(
        VideoItem(
            R.drawable.video_thumb_1,
            "Jay Chou 周杰伦 Loving you is no big deal MV",
            "03:36",
            "46.67 MB"
        ),
        VideoItem(
            R.drawable.video_thumb_2,
            "Jay Chou 周杰伦 I am not worthy MV",
            "03:36",
            "74.52 MB"
        ),
        VideoItem(
            R.drawable.video_thumb_3,
            "周杰伦 JAY CHOU Bullfighting",
            "04:17",
            "101.65 MB"
        ),
        VideoItem(
            R.drawable.video_thumb_4,
            "周杰伦 Jay Chou The Secret That Cannot Be Told MV",
            "05:25",
            "12.00 MB"
        )
    )//这是首页的主功能

    // ==================== Lab2 动态过滤 ====================
    val filteredVideos = videoList.filter {
        it.title.contains(searchText, ignoreCase = true)
    }


    val dynamicMessage = if (searchText.isNotBlank()) {
        "Searching for: $searchText"
    } else {
        "Showing all videos"
    }//filter 会只显示包含关键词的视频，基于搜索结果

    Scaffold(
        topBar = {
            Column(//Vertical arrangement在顶部
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                // ==================== Lab2 TextField + Button ====================
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },//前面会把新内容更新到后面
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    placeholder = { Text("Search videos...") },//没输入内容时会显示后面
                    trailingIcon = {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }//搜索框右边放了一个清除按钮
                    },
                    shape = RoundedCornerShape(8.dp),//圆角
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )//背景灰色，下划线后面
                )


                Row(//Four labels arranged horizontally
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Text(
                            text = tab,
                            fontSize = 20.sp,
                            fontWeight = if (index == selectedTab) FontWeight.Bold else FontWeight.Normal,
                            color = if (index == selectedTab) Color(0xFFFF9800) else Color.Gray,//将选中的橙色加粗
                            modifier = Modifier
                                .clickable { selectedTab = index }//点哪个哪个就是后面
                                .padding(vertical = 4.dp)
                        )//创建四个标签
                    }
                }
            }
        },
        // ==================== 底部导航：点击跳转到对应页面 ====================
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFF5F5F5),
                tonalElevation = 0.dp
            ) {
                val bottomItems = listOf(
                    "Home" to Icons.Default.Home,
                    "Playlist" to Icons.Default.List,
                    "Youtube" to Icons.Default.Search,
                    "Settings" to Icons.Default.Settings
                )

                bottomItems.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (index == selectedBottomNav) Color(0xFFFF9800) else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                label,
                                color = if (index == selectedBottomNav) Color(0xFFFF9800) else Color.Gray
                            )
                        },
                        selected = index == selectedBottomNav,
                        onClick = {
                            selectedBottomNav = index
                            if (index == 3) {
                                navController.navigate("settings") // 点击Settings跳设置页
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->//Avoid content being obscured by top and bottom bars
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // 动态提示文字
            Text(
                text = dynamicMessage,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                color = Color(0xFFFF9800),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            // ==================== 两列网格视频列表 ====================
            LazyVerticalGrid(//两下面 of grid
                columns = GridCells.Fixed(2),//两列
                contentPadding = PaddingValues(8.dp),//留Margin
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)//Fill the space
            ) {
                items(filteredVideos.size) { index ->//创建对应个数的卡片。
                    val video = filteredVideos[index]//视频的信息
                    var expanded by remember { mutableStateOf(false) }//展开 / 收起

                    // ==================== Lab3 Card + 动画 ====================
                    Card(
                        onClick = {
                            expanded = !expanded
                            // ==================== Lab4 点击卡片跳转详情页 + 传数据 ====================
                            viewModel.setCurrentVideo(video.title, video.size)
                            navController.navigate("video_detail")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),//变大smoothly
                        shape = RoundedCornerShape(8.dp),//圆角
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 视频封面+时长+播放按钮
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            ) {
                                Image(//视频cover图片
                                    painter = painterResource(id = video.thumbnailRes),
                                    contentDescription = video.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                )

                                // 时长标签
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = video.duration,
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }

                                // 播放按钮
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(end = 36.dp, bottom = 8.dp)
                                        .size(24.dp)
                                )
                            }

                            // 视频标题+大小+更多按钮
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = video.title,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = video.size,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )

                                    // Lab3 点击展开动画
                                    if (expanded) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Resolution: 1080p • Format: MP4",
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // 更多菜单按钮
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {}
                                )
                            }
                        }
                    }
                }
            }

            // ==================== Lab2 学号显示 ====================
            Text(
                text = "Matric Number: $matricNumber",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}