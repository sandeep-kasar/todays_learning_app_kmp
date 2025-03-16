package com.todays.learning.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.todays.learning.domain.models.TimeTable
import com.todays.learning.models.LineParameters
import com.todays.learning.models.StrokeParameters
import com.todays.learning.models.TimelineNodePosition
import com.todays.learning.ui.components.TimelineNode
import todayslearning.shared.generated.resources.Res

@Composable
fun LazyTimeline(
    navigator: NavHostController,
    timeTableList: List<TimeTable>,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(), content = {
            itemsIndexed(timeTableList) { index, timeTable ->
                TimelineNode(
                    position = mapToTimelineNodePosition(index, timeTableList.size),
                    circleParameters = CircleParametersDefaults.circleParameters(
                        backgroundColor = getIconColor(timeTable),
                        stroke = getIconStrokeColor(timeTable)
                    ),
                    lineParameters = getLineBrush(
                        circleRadius = 12.dp, index = index, items = timeTableList
                    ),
                    contentStartOffset = 10.dp,
                    spacer = 24.dp,

                    ) { modifier ->
                    Row(
                        modifier = modifier.height(150.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = timeTable.time.toString(), Modifier.wrapContentSize()
                        )
                        Row(
                            modifier = modifier.height(150.dp)
                                .clickable { navigator.navigate("/details/${timeTable.subject?.subject}") },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                Modifier.wrapContentSize().weight(1f, true),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp),
                                    text = timeTable.subject?.subject ?: "",
                                )
                                Text(
                                    fontSize = 14.sp,
                                    modifier = Modifier.wrapContentHeight(align = Alignment.CenterVertically),
                                    text = timeTable.subject?.learning ?: "",
                                    textAlign = TextAlign.Center
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ChevronRight,
                                contentDescription = "Forward Arrow"
                            )
                        }
                    }


                }
            }
        }, contentPadding = PaddingValues(16.dp)
    )
}

@Composable
private fun getLineBrush(circleRadius: Dp, index: Int, items: List<TimeTable>): LineParameters? {
    return if (index != items.lastIndex) {
        val currentStage: TimeTable = items[index]
        val nextStage: TimeTable = items[index + 1]
        val circleRadiusInPx = with(LocalDensity.current) { circleRadius.toPx() }
        LineParametersDefaults.linearGradient(
            strokeWidth = 2.dp,
            startColor = (getIconStrokeColor(currentStage)?.color ?: getIconColor(currentStage)),
            endColor = (getIconStrokeColor(nextStage)?.color ?: getIconColor(items[index + 1])),
            startY = circleRadiusInPx * 2
        )
    } else {
        null
    }
}

private fun getIconColor(stage: TimeTable): Color {
    return Color.White
}

private fun getIconStrokeColor(stage: TimeTable): StrokeParameters? {
    return StrokeParameters(color = Color.LightGray, width = 2.dp)
}

@Composable
private fun getIcon(stage: TimeTable): Int? {
    return Res.drawable.hashCode()
}

private fun mapToTimelineNodePosition(index: Int, collectionSize: Int) = when (index) {
    0 -> TimelineNodePosition.FIRST
    collectionSize - 1 -> TimelineNodePosition.LAST
    else -> TimelineNodePosition.MIDDLE
}