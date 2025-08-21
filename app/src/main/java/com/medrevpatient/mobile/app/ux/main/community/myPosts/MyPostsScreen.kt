package com.medrevpatient.mobile.app.ux.main.community.myPosts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.font.FontWeight.Companion.W300
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.androidisland.ezpermission.EzPermission
import com.google.gson.Gson
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.domain.response.LocalPostImages
import com.medrevpatient.mobile.app.navigation.RouteMaker
import com.medrevpatient.mobile.app.navigation.getFromBackStack
import com.medrevpatient.mobile.app.ui.DialogLoader
import com.medrevpatient.mobile.app.ui.HStack
import com.medrevpatient.mobile.app.ui.PagingResultHandler
import com.medrevpatient.mobile.app.ui.TopBarCenterAlignTextBack
import com.medrevpatient.mobile.app.ui.VStack
import com.medrevpatient.mobile.app.ui.common.BasicBottomSheet
import com.medrevpatient.mobile.app.ui.common.PermissionDialog
import com.medrevpatient.mobile.app.ui.theme.ColorSilverSand
import com.medrevpatient.mobile.app.ui.theme.MineShaft
import com.medrevpatient.mobile.app.ui.theme.MineShaft3
import com.medrevpatient.mobile.app.ui.theme.black25
import com.medrevpatient.mobile.app.ui.theme.outFit
import com.medrevpatient.mobile.app.ui.theme.white
import com.medrevpatient.mobile.app.utils.AppUtils
import com.medrevpatient.mobile.app.utils.AppUtils.noRippleClickable
import com.medrevpatient.mobile.app.utils.Constants
import com.medrevpatient.mobile.app.ux.main.community.PhotoItem
import com.medrevpatient.mobile.app.ux.main.community.postDetails.DeletedPostSuccessSheet
import com.medrevpatient.mobile.app.ux.main.community.postDetails.PostDeleteSheet
import com.medrevpatient.mobile.app.ux.main.community.postDetails.PostDetailsUiEvent
import com.medrevpatient.mobile.app.ux.main.community.postDetails.PostDetailsUiState
import com.medrevpatient.mobile.app.ux.main.community.postDetails.PostDetailsViewModel
import com.medrevpatient.mobile.app.ux.main.component.IconTextHStack
import com.medrevpatient.mobile.app.ux.main.component.OutlineTextFieldWithTrailing
import com.medrevpatient.mobile.app.ux.main.component.RoundedCarouselImageWithDescription
import com.medrevpatient.mobile.app.ux.main.component.SkaiButton
import com.medrevpatient.mobile.app.ux.main.component.SkaiButtonDefault
import com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow.showImagePickerOptions
import com.medrevpatient.mobile.app.ux.startup.onboarding.onBoardDataFlow.uriFromBitmap
import es.dmoral.toasty.Toasty
import kotlin.random.Random

@Composable
fun MyPostsScreen(
    viewModel: MyPostsViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        containerColor = Color.White,
        topBar = {
            TopBarCenterAlignTextBack(
                title = "",
                onBackPress = {
                    viewModel.popBackStack()
                }
            )
        }
    ) { innerPadding ->
        MyPostsContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            viewModel = viewModel,
            event = viewModel::event,
            uiState = uiState
        )
        if (uiState.isLoading) DialogLoader()
        getFromBackStack<Boolean>(navController = navController, key = RouteMaker.Keys.REFRESH)?.let {
            viewModel.event(MyPostsUiEvent.RefreshPostList)
        }
    }
}

@Composable
fun MyPostsContent(
    modifier: Modifier = Modifier, viewModel: MyPostsViewModel, event: (MyPostsUiEvent) -> Unit,
    uiState: MyPostsUiState
) {
    val myPosts = viewModel.myPosts.collectAsLazyPagingItems()
    if (myPosts.itemCount == 0 && myPosts.loadState.refresh !is androidx.paging.LoadState.Loading) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(id = R.string.no_posts_found),
                fontSize = 16.sp,
                fontFamily = outFit,
                fontWeight = SemiBold,
                color = MineShaft,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        PagingResultHandler(myPosts) { pagingState ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = modifier,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(pagingState.itemCount) {
                    val post = myPosts[it] ?: return@items
                    RoundedCarouselImageWithDescription(
                        images = post.images,
                        isImageVisible = post.images.isNotEmpty(),
                        isOnlyCaptionPost = post.images.isEmpty(),
                        captionContent = if (post.images.isEmpty()) post.content else "",
                        isBackgroundGradient = true,
                        clipPercent = 12,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(216.dp)
                            .noRippleClickable {
                                val postData = Gson().toJson(post)
                                event(
                                    MyPostsUiEvent.NavigateTo(
                                        RouteMaker.PostDetails.createRoute(
                                            postData
                                        )
                                    )
                                )
                            },
                        content = {
                            HStack(
                                spaceBy = 0.dp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 13.dp)
                            ) {
                                IconTextHStack(
                                    icon = R.drawable.calendar,
                                    text = AppUtils.formatTimestampForMyPosts(post.createdAt ?: 0),
                                    spaceBy = 3.dp,
                                    tint = MineShaft,
                                    style = TextStyle(
                                        fontWeight = Normal,
                                        fontSize = 10.sp,
                                        color = MineShaft
                                    ),
                                    iconSize = 15.dp,
                                    modifier = Modifier.weight(1f)
                                )
                                Image(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.edit_with_bg),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .padding(end = 6.dp)
                                        .size(25.dp)
                                        .noRippleClickable {
                                            event(MyPostsUiEvent.ClearImageList)
                                            if (post.images.isNotEmpty()) event(MyPostsUiEvent.PostImages(post.images.map { it1 -> LocalPostImages(id = it1.id, url = Uri.parse(it1.url)) }))
                                            if (post.content?.isNotEmpty() == true) event(MyPostsUiEvent.OnPostContentValueChange(post.content))
                                            event(MyPostsUiEvent.StoreAPIContentValue(post.content ?: ""))
                                            event(MyPostsUiEvent.ShowEditPostDialog(true, post.id ?: ""))
                                        }
                                )
                                Image(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.delete_with_bg),
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(25.dp)
                                        .noRippleClickable {
                                            event(
                                                MyPostsUiEvent.ShowDeletePostDialog(
                                                    true,
                                                    post.id ?: ""
                                                )
                                            )
                                        }
                                )
                            }
                        }
                    )
                }
            }
        }
    }


    if (uiState.showDeletePostDialog) {
        DeletePostDialog(event = event, uiState = uiState)
    }

    if (uiState.showDeletePostSuccessDialog) {
        DeletePostSuccessDialog(event = event, uiState = uiState)
    }

    if (uiState.showEditPostDialog) {
        EditPostDialog(uiState = uiState, event = event, viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletePostDialog(
    event: (MyPostsUiEvent) -> Unit,
    uiState: MyPostsUiState
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = {
                event(
                    MyPostsUiEvent.ShowDeletePostDialog(
                        false,
                        uiState.postId
                    )
                )
            },
            isSheetVisible = { uiState.showDeletePostDialog }
        ) {
            PostDeleteSheet(
                onYesClick = {
                    event(MyPostsUiEvent.DeletePost(uiState.postId))
                },
                onNoClick = {
                    event(MyPostsUiEvent.ShowDeletePostDialog(false, uiState.postId))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeletePostSuccessDialog(
    event: (MyPostsUiEvent) -> Unit,
    uiState: MyPostsUiState
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = {
                event(MyPostsUiEvent.ShowDeletePostSuccessDialog(false))
            },
            isSheetVisible = { uiState.showDeletePostSuccessDialog }
        ) {
            DeletedPostSuccessSheet(
                onClickOfGetStarted = {
                    event(MyPostsUiEvent.PerformSeeMyPostsClick)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPostDialog(
    uiState: MyPostsUiState,
    event: (MyPostsUiEvent) -> Unit,
    viewModel: MyPostsViewModel
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    Column {
        BasicBottomSheet(
            sheetState = { sheetState },
            onDismissRequest = { event(MyPostsUiEvent.ShowEditPostDialog(false, uiState.postId)) },
            isSheetVisible = { uiState.showEditPostDialog },
            title = stringResource(R.string.edit_post)
        ) {
            EditPost(viewModel = viewModel, isFrom = Constants.Keywords.MY_POST)
        }
    }
}

@SuppressLint("InlinedApi")
@Composable
fun EditPost(viewModel: ViewModel, isFrom: String) {
    val context = LocalContext.current
    val uiState by if (isFrom == Constants.Keywords.MY_POST) (viewModel as MyPostsViewModel).uiState.collectAsStateWithLifecycle() else (viewModel as PostDetailsViewModel).uiState.collectAsStateWithLifecycle()

    val android13PermissionList: ArrayList<String> = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_VIDEO
    )
    val permissionList: ArrayList<String> = arrayListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val showPermissionDialog by if (isFrom == Constants.Keywords.MY_POST) (uiState as MyPostsUiState).showPermissionDialog.collectAsStateWithLifecycle() else (uiState as PostDetailsUiState).showPermissionDialog.collectAsStateWithLifecycle()

    var imageUri by remember { mutableStateOf(if (isFrom == Constants.Keywords.MY_POST) (uiState as MyPostsUiState).postImages else (uiState as PostDetailsUiState).postImages) }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            if (uris.size > 3) {
                AppUtils.Toast(context, context.getString(R.string.max_3_images)).show()
                return@rememberLauncherForActivityResult
            }
            val selectedUris = uris.take(3)
            if (selectedUris.isNotEmpty()) {
                if (isFrom == Constants.Keywords.MY_POST) {
                    val viewModels = viewModel as MyPostsViewModel
                    viewModels.event(MyPostsUiEvent.EnableUpdateButton(true))
                } else {
                    val viewModels = viewModel as PostDetailsViewModel
                    viewModels.event(PostDetailsUiEvent.EnableUpdateButton(true))
                }
            }
            imageUri = imageUri + selectedUris.map { LocalPostImages(id = Random.nextInt().toString(), url = it) }
            if (isFrom == Constants.Keywords.MY_POST) {
                if (imageUri.size > 3) {
                    AppUtils.Toast(context, context.getString(R.string.max_3_images)).show()
                    return@rememberLauncherForActivityResult
                }
                imageUri = imageUri.take(3)
                val viewModels = viewModel as MyPostsViewModel
                viewModels.event(MyPostsUiEvent.PostImages(imageUri))
            } else {
                if (imageUri.size > 3) {
                    AppUtils.Toast(context, context.getString(R.string.max_3_images)).show()
                    return@rememberLauncherForActivityResult
                }
                imageUri = imageUri.take(3)
                val viewModels = viewModel as PostDetailsViewModel
                viewModels.event(PostDetailsUiEvent.PostImages(imageUri))
            }
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let { it ->
                val uri = uriFromBitmap(context, it)
                if (uri != Uri.EMPTY) {
                    if (isFrom == Constants.Keywords.MY_POST) {
                        val viewModels = viewModel as MyPostsViewModel
                        viewModels.event(MyPostsUiEvent.EnableUpdateButton(true))
                    } else {
                        val viewModels = viewModel as PostDetailsViewModel
                        viewModels.event(PostDetailsUiEvent.EnableUpdateButton(true))
                    }
                }
                imageUri = imageUri + LocalPostImages(id = Random.nextInt().toString(), url = uri)
                if (isFrom == Constants.Keywords.MY_POST) {
                    if (imageUri.size > 3) {
                        AppUtils.Toast(context, context.getString(R.string.max_3_images)).show()
                        return@rememberLauncherForActivityResult
                    }
                    imageUri = imageUri.take(3)
                    val viewModels = viewModel as MyPostsViewModel
                    viewModels.event(MyPostsUiEvent.PostImages(imageUri))
                } else {
                    if (imageUri.size > 3) {
                        AppUtils.Toast(context, context.getString(R.string.max_3_images)).show()
                        return@rememberLauncherForActivityResult
                    }
                    imageUri = imageUri.take(3)
                    val viewModels = viewModel as PostDetailsViewModel
                    viewModels.event(PostDetailsUiEvent.PostImages(imageUri))
                }
            }
        }

    val startForCameraPermissionResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { _: ActivityResult -> }

    if (showPermissionDialog) {
        PermissionDialog(
            onDismissRequest = { if (isFrom == Constants.Keywords.MY_POST) (uiState as MyPostsUiState).onShowPermissionDialog(false) else (uiState as PostDetailsUiState).onShowPermissionDialog(false) },
            title = stringResource(R.string.app_name),
            description = stringResource(R.string.camera_permission_txt),
            negativeText = stringResource(R.string.cancel),
            positiveText = stringResource(R.string.open_setting),
            onPositiveClick = {
                if (isFrom == Constants.Keywords.MY_POST) (uiState as MyPostsUiState).onShowPermissionDialog(false) else (uiState as PostDetailsUiState).onShowPermissionDialog(false)
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uriIntent = Uri.fromParts("package", context.packageName, null)
                intent.data = uriIntent
                startForCameraPermissionResult.launch(intent)
            },
        )
    }
    VStack(
        spaceBy = 0.dp, modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .background(white)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.upload_photos),
            fontSize = 16.sp,
            fontFamily = outFit,
            fontWeight = SemiBold,
            color = MineShaft
        )
        Text(
            text = stringResource(R.string.you_can_add_max_3_photos),
            fontSize = 12.sp,
            fontFamily = outFit,
            fontWeight = Medium,
            color = ColorSilverSand,
            modifier = Modifier.padding(top = 5.dp)
        )
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .background(
                    color = MineShaft3,
                    shape = RoundedCornerShape(25)
                )
                .padding(horizontal = 12.dp, vertical = 15.dp)
                .noRippleClickable {
                    permissionCheck(
                        context = context,
                        android13PermissionList = android13PermissionList,
                        permissionList = permissionList,
                        galleryLauncher = launcher,
                        cameraLauncher = cameraLauncher,
                        viewModel = viewModel,
                    )
                }

        ) {
            Text(
                text = stringResource(R.string.upload_photo),
                fontSize = 14.sp,
                fontFamily = outFit,
                fontWeight = Light,
                color = MineShaft,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.camera),
                contentDescription = ""
            )
        }
        if (imageUri.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 15.dp),
            ) {
                imageUri.take(3).forEach {
                    PhotoItem(imageData = it.url ?: Uri.EMPTY) {
                        imageUri = imageUri.filterNot { it1 -> it1 == it } // Remove selected image
                        if (it.url.toString().contains("https")) {
                            if (isFrom == Constants.Keywords.MY_POST) {
                                val viewModels = viewModel as MyPostsViewModel
                                viewModels.event(MyPostsUiEvent.ManageDeleteImages(it.id ?: ""))
                                viewModels.event(MyPostsUiEvent.PostImages(imageUri))
                                viewModels.event(MyPostsUiEvent.EnableUpdateButton(true))
                            } else {
                                val viewModels = viewModel as PostDetailsViewModel
                                viewModels.event(PostDetailsUiEvent.ManageDeleteImages(it.id ?: ""))
                                viewModels.event(PostDetailsUiEvent.PostImages(imageUri))
                                viewModels.event(PostDetailsUiEvent.EnableUpdateButton(true))
                            }
                        } else {
                            // Remove locally
                            if (isFrom == Constants.Keywords.MY_POST) {
                                val viewModels = viewModel as MyPostsViewModel
                                viewModels.event(MyPostsUiEvent.PostImages(imageUri))
                                viewModels.event(MyPostsUiEvent.EnableUpdateButton(true))
                            } else {
                                val viewModels = viewModel as PostDetailsViewModel
                                viewModels.event(PostDetailsUiEvent.PostImages(imageUri))
                                viewModels.event(PostDetailsUiEvent.EnableUpdateButton(true))
                            }
                        }
                    }
                }
            }
        }
        Text(
            text = stringResource(R.string.write_about_post),
            fontSize = 16.sp,
            fontFamily = outFit,
            fontWeight = Bold,
            color = MineShaft,
            modifier = Modifier.padding(top = 15.dp)
        )
        Text(
            text = stringResource(R.string.share_details_of_your_post),
            fontSize = 12.sp,
            fontFamily = outFit,
            fontWeight = Medium,
            color = ColorSilverSand,
            modifier = Modifier.padding(top = 5.dp)
        )
        OutlineTextFieldWithTrailing(
            value = if (isFrom == Constants.Keywords.MY_POST) (uiState as MyPostsUiState).postContentValue else (uiState as PostDetailsUiState).postContentValue,
            onValueChange = {
                if (isFrom == Constants.Keywords.MY_POST) {
                    val viewModels = viewModel as MyPostsViewModel
                    viewModels.event(MyPostsUiEvent.OnPostContentValueChange(it))
                } else {
                    val viewModels = viewModel as PostDetailsViewModel
                    viewModels.event(PostDetailsUiEvent.OnPostContentValueChange(it))
                }
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.write_here),
                    fontWeight = W300,
                    color = black25,
                    fontSize = 14.sp
                )
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            maxLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(top = 5.dp),
            trailingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_description),
                    contentDescription = "",
                    modifier = Modifier
                        .offset(y = (-25).dp)
                        .size(20.dp)
                )
            },
            shape = RoundedCornerShape(20)
        )
        Spacer(Modifier.padding(top = 50.dp))
        HStack(8.dp) {
            SkaiButton(
                text = stringResource(R.string.cancel),
                makeUpperCase = true,
                color = white,
                textStyle = SkaiButtonDefault.textStyle.copy(black25, fontSize = 14.sp),
                borderStroke = BorderStroke(1.dp, black25),
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 15.dp),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                elevation = 0.dp,
                onClick = {
                    if (isFrom == Constants.Keywords.MY_POST) {
                        val viewModels = viewModel as MyPostsViewModel
                        viewModels.event(MyPostsUiEvent.ShowEditPostDialog(false, (uiState as MyPostsUiState).postId))
                    } else {
                        val viewModels = viewModel as PostDetailsViewModel
                        viewModels.event(PostDetailsUiEvent.ShowEditPostDialog(false, (uiState as PostDetailsUiState).postId))
                    }
                }
            )
            Spacer(modifier = Modifier.weight(0.03f))
            SkaiButton(
                text = stringResource(R.string.update),
                innerPadding = PaddingValues(horizontal = 28.dp, vertical = 18.dp),
                makeUpperCase = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 15.dp),
                elevation = 3.dp,
                color = if (checkUpdatedValues(isFrom, viewModel)) MineShaft else MineShaft.copy(alpha = 0.3f),
                textStyle = SkaiButtonDefault.textStyle.copy(fontSize = 14.sp),
                enable = checkUpdatedValues(isFrom, viewModel)
            ) {
                if (isFrom == Constants.Keywords.MY_POST) {
                    val viewModels = viewModel as MyPostsViewModel
                    if ((uiState as MyPostsUiState).postContentValue.isNotEmpty() || (uiState as MyPostsUiState).postImages.isNotEmpty()) {
                        viewModels.event(MyPostsUiEvent.PerformUpdatePostClick)
                    } else {
                        Toasty.info(context, context.getString(R.string.post_edit_error)).show()
                    }
                } else {
                    val viewModels = viewModel as PostDetailsViewModel
                    if ((uiState as PostDetailsUiState).postContentValue.isNotEmpty() || (uiState as PostDetailsUiState).postImages.isNotEmpty()) {
                        viewModels.event(PostDetailsUiEvent.PerformUpdatePostClick)
                    } else {
                        Toasty.info(context, context.getString(R.string.post_edit_error)).show()
                    }
                }
            }
        }
    }
}

fun permissionCheck(
    context: Context,
    android13PermissionList: ArrayList<String>,
    permissionList: ArrayList<String>,
    galleryLauncher: ManagedActivityResultLauncher<String, List<Uri>>,
    cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    viewModel: ViewModel
) {
    var isGranted = false

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        EzPermission.with(context)
            .permissions(android13PermissionList)
            .request { granted, denied, _ ->
                android13PermissionList.forEach {
                    if (granted.contains(it)) {
                        isGranted = true
                    } else if (denied.contains(it)) {
                        isGranted = false
                    }
                }
                if (isGranted) {
                    showImagePickerOptions(context, galleryLauncher, cameraLauncher)
                } else {
                    if (viewModel is MyPostsViewModel) {
                        viewModel.uiState.value.onShowPermissionDialog(true)
                    } else {
                        (viewModel as PostDetailsViewModel).uiState.value.onShowPermissionDialog(true)
                    }
                }
            }
    } else {
        EzPermission.with(context)
            .permissions(permissionList)
            .request { granted, denied, _ ->
                permissionList.forEach {
                    if (granted.contains(it)) {
                        isGranted = true
                    } else if (denied.contains(it)) {
                        isGranted = false
                    }
                }
                if (isGranted) {
                    showImagePickerOptions(context, galleryLauncher, cameraLauncher)
                } else {
                    if (viewModel is MyPostsViewModel) {
                        viewModel.uiState.value.onShowPermissionDialog(true)
                    } else {
                        (viewModel as PostDetailsViewModel).uiState.value.onShowPermissionDialog(true)
                    }
                }
            }
    }
}


@Composable
private fun checkUpdatedValues(isFrom: String, viewModel: ViewModel): Boolean {
    val value: Boolean
    val uiState by if (isFrom == Constants.Keywords.MY_POST) (viewModel as MyPostsViewModel).uiState.collectAsStateWithLifecycle() else (viewModel as PostDetailsViewModel).uiState.collectAsStateWithLifecycle()
    value = if (isFrom == Constants.Keywords.MY_POST) {
        (uiState as MyPostsUiState).postContentValue != (uiState as MyPostsUiState).postContentFromAPIValue || (uiState as MyPostsUiState).enableUpdateButton
    } else {
        (uiState as PostDetailsUiState).postContentValue != (uiState as PostDetailsUiState).postContentFromAPIValue || (uiState as PostDetailsUiState).enableUpdateButton
    }
    return value
}




