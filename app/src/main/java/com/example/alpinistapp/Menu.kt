package com.example.alpinistapp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun ExpandableFab(navController: NavController) {

    var expanded by remember { mutableStateOf(false) }

    val items = listOf(
        Icons.Default.Settings,
        Icons.Default.Person,
        Icons.Default.Search,
        Icons.Default.Terrain,
        Icons.Default.Home
        )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        val radius = 240f

        items.forEachIndexed { index, icon ->

            val angle = Math.toRadians((index * 22).toDouble())

            val y = (radius * Math.sin(angle)).dp
            val x = (radius * Math.cos(angle)).dp

            val alpha by animateFloatAsState(
                targetValue = if (expanded) 1f else 0f,
                label = ""
            )

            val offsetX by animateDpAsState(
                targetValue = if (expanded) -x else 0.dp,
                label = ""
            )
            val offsetY by animateDpAsState(
                targetValue = if (expanded) -y else 0.dp,
                label = ""
            )


            if(expanded){
                GradientFab(
                    icon = icon,
                    onClick = {},
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .alpha(alpha),
                    isItBlue = false
                )
            }
        }
        GradientFab(
            icon = if (expanded) Icons.Default.Close else Icons.Default.Menu,
            onClick = { expanded = !expanded },
            isItBlue = if (expanded) true else false
        )

    }
}


@Composable
fun GradientFab(
    icon: ImageVector,
    onClick: () -> Unit,
    isItBlue: Boolean,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    if(isItBlue){
                        listOf(
                            Color(0xFF173963),
                            Color(0xFF175294),
                            Color(0xFF17635D)
                        )
                    } else{
                        listOf(
                            Color(0xffff6e3d),
                            Color(0xffff9b3d),
                            Color(0xfffec93b)
                        )
                    }
                )
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = if(isItBlue){ Color(0XFFFFFFFF)}
            else Color(0xFF175294)
        )
    }
}