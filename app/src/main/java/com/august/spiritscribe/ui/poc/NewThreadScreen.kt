package com.august.spiritscribe.ui.poc

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.august.spiritscribe.R
import com.august.spiritscribe.ui.theme.AppTheme

@Composable
fun NewThreadItem() {
    var input: String by remember { mutableStateOf("Start a Thread ...") }
    Column(modifier = Modifier.wrapContentHeight()) {
        Row {
            Column {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier = Modifier
                        .background(shape = CircleShape, color = Color.Unspecified)
                        .size(40.dp)
                )
                Spacer(Modifier.height(16.dp))
                VerticalDivider(Modifier.width(4.dp), color = Color.LightGray)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = "kmodi21")
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.background(color = Color.Transparent),
                )
                Spacer(Modifier.weight(1f))
                Row(modifier = Modifier.padding(top = 16.dp)){
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp).size(36.dp),
                        tint = Color.LightGray
                    )
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 4.dp).size(36.dp),
                        tint = Color.LightGray
                    )
                    Icon(
                        imageVector = Icons.Outlined.MailOutline,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 4.dp).size(36.dp),
                        tint = Color.LightGray
                    )
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 4.dp).size(36.dp),
                        tint = Color.LightGray
                    )
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 4.dp).size(36.dp),
                        tint = Color.LightGray
                    )
                }
            }
        }
    }
}


@Preview(heightDp = 300)
@Composable
fun NewThreadItemPreview() {
    AppTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            NewThreadItem()
        }
    }
}