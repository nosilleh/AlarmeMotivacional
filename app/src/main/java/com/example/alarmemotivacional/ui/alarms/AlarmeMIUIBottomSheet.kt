package com.example.alarmemotivacional.ui.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.chargemap.compose.numberpicker.NumberPicker
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun AlarmComposeTheme(content: @Composable () -> Unit) {
    val colorScheme = darkColorScheme(
        primary = Color(0xFF2D7CFF),
        secondary = Color(0xFF3A3A3A),
        background = Color(0xFF000000),
        surface = Color(0xFF1F1F1F),
        onSurface = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun AlarmTimeSelector(
    hour: Int,
    minute: Int,
    onTimeChanged: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    onAdditionalSettings: (() -> Unit)? = null
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var currentHour by rememberSaveable { mutableIntStateOf(hour) }
    var currentMinute by rememberSaveable { mutableIntStateOf(minute) }

    LaunchedEffect(hour, minute) {
        currentHour = hour
        currentMinute = minute
    }

    Column(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 156.dp)
                .clickable { showSheet = true },
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF2B2B2B),
            tonalElevation = 2.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.edit_alarm_title),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format("%02d:%02d", currentHour, currentMinute),
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                val (hoursDiff, minutesDiff) = calcularTempoParaDisparo(currentHour, currentMinute)
                Text(
                    text = stringResource(id = R.string.alarm_will_ring_in, hoursDiff, minutesDiff),
                    color = Color(0xFFCFCFCF),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { showSheet = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3A3A3A),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(id = R.string.edit_time_button))
                }
            }
        }

        if (showSheet) {
            AlarmeMIUIBottomSheet(
                hour = currentHour,
                minute = currentMinute,
                onDismissRequest = { showSheet = false },
                onTimeSelected = { h, m ->
                    currentHour = h
                    currentMinute = m
                    showSheet = false
                    onTimeChanged(h, m)
                },
                onAdditionalSettings = {
                    showSheet = false
                    onAdditionalSettings?.invoke()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmeMIUIBottomSheet(
    hour: Int,
    minute: Int,
    onDismissRequest: () -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onAdditionalSettings: (() -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentHour by remember { mutableIntStateOf(hour) }
    var currentMinute by remember { mutableIntStateOf(minute) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFF2B2B2B),
        tonalElevation = 8.dp,
        scrimColor = Color(0x99000000)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.edit_alarm_title),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center
            )

            val (hoursDiff, minutesDiff) = calcularTempoParaDisparo(currentHour, currentMinute)
            Text(
                text = stringResource(id = R.string.alarm_will_ring_in, hoursDiff, minutesDiff),
                color = Color(0xFFCFCFCF),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(
                    modifier = Modifier.weight(1f),
                    value = currentHour,
                    range = 0..23,
                    onValueChange = { currentHour = it },
                    dividersColor = Color.Transparent,
                    textStyle = MaterialTheme.typography.displaySmall.copy(
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                )
                Box(modifier = Modifier.padding(horizontal = 8.dp)) {
                    Text(
                        text = ":",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium)
                    )
                }
                NumberPicker(
                    modifier = Modifier.weight(1f),
                    value = currentMinute,
                    range = 0..59,
                    onValueChange = { currentMinute = it },
                    dividersColor = Color.Transparent,
                    textStyle = MaterialTheme.typography.displaySmall.copy(
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        onAdditionalSettings?.invoke()
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3A3A3A),
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.additional_settings_button),
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = { onTimeSelected(currentHour, currentMinute) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D7CFF),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = stringResource(id = R.string.save_alarm_button))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

fun calcularTempoParaDisparo(hour: Int, minute: Int): Pair<Int, Int> {
    val now = LocalDateTime.now()
    var target = now.withHour(hour).withMinute(minute).withSecond(0).withNano(0)
    if (!target.isAfter(now)) {
        target = target.plusDays(1)
    }
    val duration = Duration.between(now, target)
    val totalMinutes = duration.toMinutes()
    val hoursPart = (totalMinutes / 60).toInt()
    val minutesPart = (totalMinutes % 60).toInt()
    return hoursPart to minutesPart
}
