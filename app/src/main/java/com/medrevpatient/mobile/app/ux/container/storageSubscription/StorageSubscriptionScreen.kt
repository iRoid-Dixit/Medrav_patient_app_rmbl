package com.medrevpatient.mobile.app.ux.container.storageSubscription
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.medrevpatient.mobile.app.R
import com.medrevpatient.mobile.app.data.source.Constants
import com.medrevpatient.mobile.app.navigation.HandleNavigation
import com.medrevpatient.mobile.app.navigation.scaffold.AppScaffold
import com.medrevpatient.mobile.app.ui.compose.common.TopBarComponent
import com.medrevpatient.mobile.app.ui.compose.common.loader.CustomLoader
import com.medrevpatient.mobile.app.ui.theme.AppThemeColor
import com.medrevpatient.mobile.app.ui.theme.Black
import com.medrevpatient.mobile.app.ui.theme.White
import com.medrevpatient.mobile.app.ui.theme.WorkSans
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.GetStoreProductsCallback
import com.revenuecat.purchases.models.StoreProduct
@ExperimentalMaterial3Api
@Composable
fun StorageSubscriptionScreen(
    navController: NavController,
    viewModel: StorageSubscriptionViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val storageSubscriptionUiState by uiState.storageDataFlow.collectAsStateWithLifecycle()
    uiState.event(StorageUiEvent.GetContext(context))
    AppScaffold(
        containerColor = AppThemeColor,
        modifier = Modifier,
        topAppBar = {
            TopBarComponent(
                onClick = { navController.popBackStack() },
                titleText = "BMI & Health Check"
            )
        },

        ) {
        StorageSubscriptionScreenContent(uiState)
    }
    if (storageSubscriptionUiState?.showLoader == true) {
        CustomLoader()
    }
    HandleNavigation(viewModelNav = viewModel, navController = navController)
}

@Composable
private fun StorageSubscriptionScreenContent(
    uiState: StorageUiState,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    var products by remember { mutableStateOf<List<StoreProduct>>(emptyList()) }
    // var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        // Fetch products from RevenueCat
        val productIds = Constants.Subscription.SubscriptionPlan.entries.map { it.productId }

        Purchases.sharedInstance.getProducts(productIds, object : GetStoreProductsCallback {
            override fun onReceived(productsList: List<StoreProduct>) {
                products = productsList
                //   isLoading = false
            }

            override fun onError(error: PurchasesError) {
                // isLoading = false
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Note: If you downgrade from a higher storage plan (e.g., 1 TB or 500 GB) to a lower-tier plan (e.g., 10 GB), only the most recent data within your new plan's limit will be retained. Any excess data may be automatically deleted without prior notice.",
            fontSize = 14.sp,
            fontFamily = WorkSans,
            lineHeight = 18.sp,
            fontWeight = FontWeight.W400,
            color = White,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Black)
                .clickable { keyboardController?.hide() },
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                StorageTierCard(
                    product = product,
                    onClick = {
                        uiState.event(
                            StorageUiEvent.DoSubscribe(
                                activityContext = context,
                                product = product
                            )
                        )
                    }
                )
            }
        }
    }
    // }
}

val productIdToSize = mapOf(
    "m_a_10_gb:m-a-10-gb" to "10 GB",
    "h_25_gb:h-25-gb" to "25 GB",
    "l_a_100_gb:l-a-100-gb" to "100 GB",
    "a_h_200_gb:a-h-200-gb" to "200 GB",
    "l_v_k_500_gb:l-v-k-500-gb" to "500 GB",
    "t_c_1_tb:t-c-1-tb" to "1 TB"
)

@Composable
private fun StorageTierCard(product: StoreProduct, onClick: () -> Unit = {}) {
    val cleanTitle = product.title.replace(" (Legacy Cache)", "")
    val size = productIdToSize[product.id]
    Log.d("TAG", "product.id: ${product.id},size:$size")
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        // .padding(horizontal = 15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF232323)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_app_icon),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = cleanTitle,
                        color = White,
                        fontFamily = WorkSans,
                        fontWeight = FontWeight.W500,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = size ?: "",
                            color = White,
                            fontFamily = WorkSans,
                            fontWeight = FontWeight.W500,
                            fontSize = 16.sp
                        )
                        Log.d("TAG", "StorageTierCard: $size")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = product.price.formatted,
                            color = White,
                            fontFamily = WorkSans,
                            fontWeight = FontWeight.W500,
                            fontSize = 28.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Buy Now",
                    color = Black,
                    fontFamily = WorkSans,
                    fontWeight = FontWeight.W600,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun AboutScreenContentPreview() {
    val uiState = StorageUiState()
    StorageSubscriptionScreenContent(uiState)
}






