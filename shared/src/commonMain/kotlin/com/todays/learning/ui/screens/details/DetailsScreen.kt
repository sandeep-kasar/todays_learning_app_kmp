@file:OptIn(KoinExperimentalAPI::class)

package com.todays.learning.ui.screens.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.todays.learning.domain.models.Subject
import com.kmpalette.loader.rememberNetworkLoader
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navigator: NavHostController,
    viewModel: DetailsViewModel = koinViewModel<DetailsViewModel>(),
    subject: String
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.getSubjectDetails(subject = subject)
    }

    val networkLoader = rememberNetworkLoader(httpClient = koinInject())
    val subjectDetailsState by viewModel.subjectDetailsState.collectAsState()
    val selectedTabState by viewModel.selectedTabState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(MaterialTheme.colorScheme.primaryContainer),
                title = {
                    Text(
                        text = subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigator.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(top = 10.dp)) {
            if (subjectDetailsState.isLoading && subjectDetailsState.subjectDetails == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (!subjectDetailsState.error.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.align(Alignment.Center).padding(20.dp),
                    text = "Error:\n${subjectDetailsState.error}",
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Top
                ) {
                    SubjectTabs(viewModel)
                    Text(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 30.dp, start = 50.dp, end = 50.dp),
                        text = setLayoutData(selectedTabState, subjectDetailsState.subjectDetails),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Normal,
                        fontSize = 20.sp,
                        color = DarkGray
                    )
                }

            }
        }
    }
}

private fun setLayoutData(
    selectedTab: Int,
    subject: Subject?
): String {
    when (selectedTab) {
        0 -> {
            return subject?.learning.toString()
        }

        1 -> {
            return subject?.homework.toString()
        }

        2 -> {
            return subject?.keyPoints.toString()
        }
    }
    return subject?.learning.toString()
}

@Composable
fun SubjectTabs(
    viewModel: DetailsViewModel
) {

    val subjectTabs = arrayListOf<String>()
    subjectTabs.add("Learning")
    subjectTabs.add("Homework")
    subjectTabs.add("Key Points")
    subjectTabs.add("Ask Me")

    var selectedItem by remember { mutableStateOf<String?>("Learning") }

    LazyRow(modifier = Modifier.fillMaxWidth().padding(10.dp, 0.dp, 10.dp, 0.dp)) {
        itemsIndexed(subjectTabs) { index, tab ->
            SubjectCard(
                subjectTab = tab,
                setSelected = selectedItem?.let {
                    tab == it
                } ?: false) {
                selectedItem = it
                viewModel.updateClick(index)
            }

        }
    }

}

@Composable
fun SubjectCard(
    subjectTab: String,
    setSelected: Boolean,
    isSelected: (String?) -> Unit
) {

    val cardColor = if (setSelected) MaterialTheme.colorScheme.secondaryContainer else White
    val borderColor =
        if (setSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (setSelected) DarkGray else Gray

    Card(
        modifier = Modifier.padding(4.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        onClick = {
            isSelected(if (!setSelected) subjectTab else null)
        }
    ) {
        Text(
            modifier = Modifier.padding(10.dp, 4.dp, 10.dp, 4.dp),
            text = subjectTab,
            textAlign = TextAlign.Center,
            color = textColor

        )
    }
}


