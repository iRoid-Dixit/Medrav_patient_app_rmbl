package com.medrevpatient.mobile.app.ux.main.programandworkout

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.TempDataSource
import com.medrevpatient.mobile.app.data.source.remote.EndPoints.ResultType.FOR_ME
import com.medrevpatient.mobile.app.data.source.remote.dto.ForMe
import com.medrevpatient.mobile.app.data.source.remote.dto.Note
import com.medrevpatient.mobile.app.data.source.remote.dto.OnDemandClasses
import com.medrevpatient.mobile.app.data.source.remote.dto.Program
import com.medrevpatient.mobile.app.data.source.remote.dto.Recipe
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.RouteMaker.AllPrograms
import com.medrevpatient.mobile.app.navigation.RouteMaker.CrudNote
import com.medrevpatient.mobile.app.navigation.RouteMaker.Keys.REFRESH_FOR_ME
import com.medrevpatient.mobile.app.navigation.RouteMaker.Recipes
import com.medrevpatient.mobile.app.navigation.getFromBackStack
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.NetworkResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextAndBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.utils.alias.drawable
import com.medrevpatient.mobile.app.utils.alias.string
import com.medrevpatient.mobile.app.utils.ext.toJsonString
import com.medrevpatient.mobile.app.ux.main.component.HeaderContentWrapper
import com.medrevpatient.mobile.app.ux.main.component.NoItemPlaceHolder
import com.medrevpatient.mobile.app.ux.main.component.OnDemandClassesItem
import com.medrevpatient.mobile.app.ux.main.component.RoundedImageWithRowDescription
import com.medrevpatient.mobile.app.ux.main.notes.NotesItem
import com.medrevpatient.mobile.app.ux.main.player.PlayerActivity
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.ProgramItemComponent
import com.medrevpatient.mobile.app.ux.main.programandworkout.component.ProgramsItem
import timber.log.Timber


@Composable
fun ForMeScreen(
    navController: NavHostController,
    viewModel: ForMeViewModel,
    modifier: Modifier = Modifier
) {

    val forMe by viewModel.forMe.collectAsStateWithLifecycle()

    NetworkResultHandler(
        networkResult = forMe.getForMe,
        barTitle = "For Me",
        onBackPress = { viewModel.popBackStack() },
        onRetry = {
            viewModel.fetchForMe()
        }
    ) { onSuccess ->

        Scaffold(
            topBar = {
                TopBarCenterAlignTextAndBack(
                    title = "For Me",
                    onBackPress = { viewModel.popBackStack() }
                )
            },
            containerColor = Color.White,
            modifier = modifier
                .statusBarsPadding()
                .fillMaxSize()
        ) { innerPadding ->
            ForMeScreenContent(
                forMe = onSuccess.data ?: ForMe(),
                event = viewModel::event,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
            )
        }
    }

    getFromBackStack<Boolean>(key = REFRESH_FOR_ME, navController = navController)?.let {
        Timber.d("Update Note : $it")
        viewModel.fetchForMe()
    }
}

@Composable
private fun ForMeScreenContent(
    forMe: ForMe,
    event: ForMeEvent,
    modifier: Modifier = Modifier,
) {

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        /* item { TODO : Remove this changes as per 11-Mar-25.
             Header(modifier = Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp))
         }*/

        item {
            Programs(
                programs = forMe.programs,
                event = event
            )
        }

        item { //TODO: remove comments
            /*OnDemandClasses(
                demandClasses = forMe.moveNow,
                event = event
            )*/

            Notes(forMe.notes, event)
        }

        item {
            Recipes(
                recipes = forMe.recipes,
                event = event
            )
        }
    }
}


@Composable
private fun Programs(
    programs: List<Program>,
    event: ForMeEvent,
    modifier: Modifier = Modifier
) {
    if (programs.isEmpty()) return

    HeaderContentWrapper(
        title = "Completed Program".uppercase(),
        trailingText = if (programs.size > 5) "All" else null,
        onAllClick = {
            event(
                ForMeUiEvent.NavigateTo(
                    AllPrograms.createRoute(
                        FOR_ME
                    )
                )
            )
        },
        wrapperPadding = 18.dp,
    ) {

        LazyRow(
            modifier = modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            items(programs.take(5)) { program ->
                ProgramItemComponent(
                    programItem = ProgramsItem(
                        url = program.image,
                        title = program.name,
                        time = stringResource(string.append_days, program.days),
                        calories = stringResource(string.append_kcal, program.kcal)
                    ),
                    modifier = Modifier.width(168.dp)
                ) {
                    event(ForMeUiEvent.NavigateTo(RouteMaker.ViewProgram.createRoute(program.id)))
                }
            }
        }
    }
}


@Composable
private fun Recipes(
    recipes: List<Recipe>,
    event: ForMeEvent,
    modifier: Modifier = Modifier
) {
    if (recipes.isEmpty()) return

    HeaderContentWrapper(
        title = "Meal Plan",
        trailingText = if (recipes.size > 5) "All" else null,
        onAllClick = {
            event(
                ForMeUiEvent.NavigateTo(
                    Recipes.createRoute(FOR_ME)
                )
            )
        },
        wrapperPadding = 18.dp
    ) {

        LazyRow(
            modifier = modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            items(recipes.take(5)) { recipe ->

                RoundedImageWithRowDescription(
                    image = recipe.image,
                    modifier = Modifier
                        .height(162.dp)
                        .width(166.dp)
                        .clip(RoundedCornerShape(18))
                        .background(black25)
                        .clickable {
                            event(
                                ForMeUiEvent.NavigateTo(
                                    RouteMaker.ViewRecipe.createRoute(
                                        recipe.id
                                    )
                                )
                            )
                        }
                ) {
                    Text(
                        text = recipe.name,
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun OnDemandClasses(
    demandClasses: List<OnDemandClasses>,
    event: ForMeEvent,
    modifier: Modifier = Modifier
) {

    if (demandClasses.isEmpty()) return
    val ctx = LocalContext.current

    HeaderContentWrapper(
        title = "ON DEMAND CLASSES",
        trailingText = if (demandClasses.size > 5) "All" else null,
        onAllClick = {
            event(
                ForMeUiEvent.NavigateTo(
                    RouteMaker.AllOnDemandClasses.createRoute(
                        FOR_ME
                    )
                )
            )
        },
        wrapperPadding = 18.dp
    ) {

        LazyRow(
            modifier = modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            items(demandClasses.take(5)) { onDemandClass ->
                OnDemandClassesItem(
                    url = onDemandClass.thumbnail,
                    title = onDemandClass.videoTitle,
                    time = onDemandClass.duration,
                    level = onDemandClass.levelDescription,
                ) {
                    Intent(ctx, PlayerActivity::class.java).apply {
                        val gSon = onDemandClass.toJsonString()
                        this.putExtra(Constants.Intents.Player_ONE_KEY, gSon)
                        ctx.startActivity(this)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun NotesPreview() {
    Notes(
        notes = TempDataSource.sampleNotes,
        event = {}
    )
}

@Composable
fun Notes(
    notes: List<Note>,
    event: ForMeEvent,
    modifier: Modifier = Modifier
) {

    HeaderContentWrapper(
        title = "Notes".uppercase(),
        trailingText = "All",
        onAllClick = {
            event(
                ForMeUiEvent.NavigateTo(
                    RouteMaker.AllNotes.createRoute()
                )
            )
        },
        wrapperPadding = 18.dp,
    ) {
        if (notes.isNotEmpty()) {
            LazyRow(
                modifier = modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(18.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                items(notes.take(5)) { note ->
                    NotesItem(
                        title = note.title,
                        body = note.body,
                        modifier = Modifier.size(168.dp, 218.dp),
                        onClick = {
                            event(ForMeUiEvent.Notes.NavigateTo(CrudNote.createRoute(note)))
                        }
                    )
                }
            }
        } else {
            NoItemPlaceHolder(
                modifier = Modifier.fillMaxWidth(),
                title = "Let’s start saving your notes related to the fitness.",
                subTitle = "No fitness notes have been created yet.",
                icon = drawable.ic_notes,
                btnText = "Create Note"
            ) {
                event(ForMeUiEvent.Notes.NavigateTo(CrudNote.createRoute(Note())))
            }
        }
    }
}

//@Preview
@Composable
private fun Header(
    modifier: Modifier = Modifier
) {
    VStack(
        8.dp,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .clip(RoundedCornerShape(25))
            .background(black25)
            .padding(18.dp)
    ) {
        HStack(
            2.dp,
            modifier = Modifier.height(30.dp)
        ) {

            AsyncImage(
                model = "",
                placeholder = painterResource(R.drawable.img_portrait_placeholder),
                error = painterResource(R.drawable.img_portrait_placeholder),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                clipToBounds = true,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(30.dp)
            )
            Spacer(Modifier.padding(2.dp))

            Text(
                text = "@CoachSkye",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = SemiBold,
                color = Color.White
            )

            Image(
                painter = painterResource(R.drawable.golden_verify_badge),
                contentDescription = "verified",
            )
        }
        Text(
            text = "Here are a few recommendations from your coach to try based on your goals.",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = W300,
            color = Color.White
        )
    }
}


//@Preview(heightDp = 800)
@Composable
private fun ForMeScreenPreview() {
    Scaffold(
        topBar = {
            TopBarCenterAlignTextAndBack(
                title = "For Me",
                onBackPress = { }
            )
        },
        containerColor = Color.White,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
    ) { innerPadding ->
        ForMeScreenContent(
            forMe = ForMe(),
            event = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        )
    }
}