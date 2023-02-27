package com.mus.meetings

import android.content.Intent
import android.graphics.Color.WHITE
import android.graphics.Color.red
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Colors
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.mus.meetings.ui.theme.MeetingsTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
            val openDialog = remember { mutableStateOf(false) }
            val checked = remember { mutableStateOf(false) }
            var member1 by remember { mutableStateOf(Member(
                "Вы",
                false,
                R.drawable.ava1,
                R.drawable.ava11)) }
            val member2 by remember { mutableStateOf(Member(
                "My name is John Silver, but also they call me Gammon and One-legged",
                false,
                R.drawable.ava2,
                R.drawable.ava22)) }
            var reversed by remember { mutableStateOf(false) }
            val members by remember(member1, member2, reversed) {
                derivedStateOf {
                    if(reversed)
                        listOf(member1,member2)
                    else
                        listOf(member2, member1)
                }
            }

            intent = Intent(Intent.ACTION_MAIN)
            MembersBottomSheet(members = members, sheetState = bottomSheetState, coroutineScope = coroutineScope) {

                MeetingsTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorResource(R.color.darkdarkgray))


                    ) {


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                modifier = Modifier,
                                onClick = {
                                    intent.addCategory(Intent.CATEGORY_APP_MESSAGING)
                                    startActivity(intent)
                                    intent.removeCategory(Intent.CATEGORY_APP_MESSAGING)
                                }
                            ) {
                                Icon(
                                    modifier = Modifier,
                                    tint = Color.White,
                                    imageVector = Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = null
                                )
                            }
                            IconButton(
                                modifier = Modifier,
                                onClick = {
                                    coroutineScope.launch {
                                        bottomSheetState.animateTo(ModalBottomSheetValue.HalfExpanded)
                                    }
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                ) {
                                    Icon(
                                        tint = Color.White,
                                        imageVector = Icons.Outlined.Group,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = members.size.toString(),
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .drawBehind {
                                                drawCircle(
                                                    color = Color.White,
                                                    radius = 20f
                                                )
                                            }
                                            .align(
                                                Alignment.TopEnd
                                            )
                                    )
                                }
                            }

                            IconButton(
                                modifier = Modifier,
                                onClick = { reversed = !reversed }
                            ) {
                                Icon(
                                    tint = Color.White,
                                    imageVector = Icons.Outlined.ViewAgenda,
                                    contentDescription = null
                                )
                            }
                        }

                        LazyColumn {
                            items(members, key = { it.name }) {
                                MemberCard(
                                    it,
                                    Modifier.animateItemPlacement()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(50.dp, 5.dp)
                                .align(CenterHorizontally)
                                .background(Color.White, CircleShape)
                        )
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .width(275.dp)
                                .align(CenterHorizontally),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconToggleButton(
                                checked = checked.value,
                                onCheckedChange = { checked.value = it },
                                modifier = if (checked.value) Modifier.background(
                                    Color.Gray,
                                    CircleShape
                                ) else Modifier.background(Color.White, CircleShape)
                            ) {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = if (checked.value) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                    contentDescription = null,
                                )
                            }
                            IconToggleButton(
                                checked = member1.MicOnOff,
                                onCheckedChange = { member1 = member1.copy(MicOnOff = it) },
                                modifier = if (member1.MicOnOff) Modifier.background(
                                    Color.Gray,
                                    CircleShape
                                ) else Modifier.background(Color.White, CircleShape)
                            ) {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = if (member1.MicOnOff) Icons.Default.Mic else Icons.Default.MicOff,
                                    contentDescription = null,
                                )
                            }
                            IconButton(
                                modifier = Modifier.background(Color.Gray, CircleShape),
                                onClick = { openDialog.value = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WavingHand,
                                    contentDescription = null
                                )
                            }
                            if (openDialog.value) {
                                AlertDialog(
                                    onDismissRequest = {
                                        openDialog.value = false
                                    },
                                    title = { Text(text = "Привет!", fontSize = 30.sp) },
                                    confirmButton = {
                                        Button(
                                            onClick = { openDialog.value = false }
                                        ) {
                                            Text("И Вам привет!", fontSize = 22.sp)
                                        }
                                    }
                                )
                            }
                            IconButton(
                                modifier = Modifier.background(Color.Red, CircleShape),
                                onClick = { finishAffinity() }
                            ) {
                                Icon(
                                    modifier = Modifier,
                                    imageVector = Icons.Default.CallEnd,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
fun MemberCard(member: Member, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Image(
            painter = painterResource(id = member.BackgroundID),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .height(250.dp)



        )
        Image(
            painter = painterResource(id = member.AvatarID),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .align(Center)
        )
        Row(
            modifier = Modifier.align(BottomCenter)
        ) {
            Text(
                color = Color.White,
                modifier = Modifier.weight(1f, fill = false),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = member.name
            )
            Icon(
                tint = Color.White,
                modifier = Modifier,
                imageVector = if (member.MicOnOff) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = null
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MembersBottomSheet(
    members: List<Member>,
    sheetState: ModalBottomSheetState,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    content: @Composable () -> Unit
) {
    BackHandler(sheetState.isVisible) {
        coroutineScope.launch { sheetState.hide() }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        modifier = modifier,
        sheetShape = RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp
        ),
        sheetBackgroundColor = colorResource(R.color.darkdarkgray),
        sheetContentColor = Color.White,
        sheetContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Участники",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(6.dp))


                Text(
                    text = members.size.toString(),
                    color = Color.Gray,
                    fontSize = 13.sp,
                )

            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(members, key = {it.name} ) { member ->
                    BottomSheetMemberCard(
                        member = member
                    )
                }
            }
        },
        content = content
    )
}

@Composable
private fun BottomSheetMemberCard(
    member: Member,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = member.AvatarID,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(45.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = member.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f, fill = false)
            )
        }
}

