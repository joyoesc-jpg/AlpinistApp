package com.example.alpinistapp.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ExpandableFab(navController: NavController) {

    var expanded by remember { mutableStateOf(false) }


    val item = listOf(
        FabItem(Icons.Default.Settings, "settings"),
        FabItem(Icons.Default.Person, "profile"),
        FabItem(Icons.Default.Search, "search"),
        FabItem(Icons.Default.Terrain, "expedition"),
        FabItem(Icons.Default.Home, "home"),

    )

    val alphaBg by animateFloatAsState(
        targetValue = if (expanded) 0.5f else 0f,
        animationSpec = tween(300),
        label = ""
    )

    if(alphaBg > 0f){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = alphaBg))
                .clickable { expanded = false }
        )
    }

    Box (
        modifier = Modifier.fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp, end = 32.dp),
            contentAlignment = Alignment.BottomEnd,
        ) {


            val radius = 240f

            item.forEachIndexed { index, fabItem ->

                val angle = Math.toRadians((index * 22).toDouble())

                val y = (radius * sin(angle)).dp
                val x = (radius * cos(angle)).dp

                val alpha by animateFloatAsState(
                    targetValue = if (expanded) 1f else 0f,
                    label = ""
                )

                val offsetX by animateDpAsState(
                    targetValue = if (expanded) -x else 0.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = ""
                )
                val offsetY by animateDpAsState(
                    targetValue = if (expanded) -y else 0.dp,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = ""
                )


                GradientFab(
                    icon = fabItem.icon,
                    onClick = {
                        if (expanded) {
                            expanded = false
                            navController.navigate(fabItem.route)
                        }
                    },
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .alpha(alpha),
                    isItBlue = false
                )
            }
            GradientFab(
                icon = if (expanded) Icons.Default.Close else Icons.Default.Menu,
                onClick = { expanded = !expanded },
                isItBlue = if (expanded) true else false,
                modifier = Modifier.shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    clip = false
                )
            )
        }
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
                    if (isItBlue) {
                        listOf(
                            Color(0xFF173963),
                            Color(0xFF175294),
                            Color(0xFF17635D)
                        )
                    } else {
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
