package com.app.tastybuds.ui.vouchers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Build
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun AllVouchersScreen(
    onBackClick: () -> Unit = {},
    onVoucherClick: (String) -> Unit = {},
    viewModel: VouchersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)
    val activeVouchers = vouchers.filter { !it.isUsed }
    val usedVouchers = vouchers.filter { it.isUsed }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.active, activeVouchers.size),
        stringResource(R.string.used, usedVouchers.size)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        AppTopBar(
            title = stringResource(id = R.string.my_vouchers),
            onBackClick = onBackClick
        )

        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            contentColor = PrimaryColor,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PrimaryColor
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) PrimaryColor else Color.Gray
                        )
                    }
                )
            }
        }

        when (uiState.selectedTab) {
            0 -> {
                VouchersContent(
                    uiState = uiState, // ✅ Pass uiState
                    vouchers = uiState.activeVouchers, // ✅ Use activeVouchers from uiState
                    onVoucherClick = { voucherId ->
                        viewModel.onVoucherClick(voucherId)
                        onVoucherClick(voucherId)
                    },
                    emptyMessage = stringResource(R.string.no_active_vouchers_available),
                    emptySubMessage = stringResource(R.string.check_back_later_for_new_vouchers_and_deals)
                )
            }

            1 -> {
                VouchersContent(
                    uiState = uiState, // ✅ Pass uiState
                    vouchers = uiState.usedVouchers, // ✅ Use usedVouchers from uiState
                    onVoucherClick = { voucherId ->
                        viewModel.onVoucherClick(voucherId)
                        onVoucherClick(voucherId)
                    },
                    emptyMessage = stringResource(R.string.no_used_vouchers),
                    emptySubMessage = stringResource(R.string.your_used_vouchers_will_appear_here)
                )
            }
        }
    }
}

@Composable
fun VouchersContent(
    uiState: VouchersUiState,
    vouchers: List<Voucher>,
    onVoucherClick: (String) -> Unit,
    emptyMessage: String,
    emptySubMessage: String
) {
    when {
        uiState.isLoading && vouchers.isEmpty() -> {
            LoadingContent()
        }
        vouchers.isEmpty() -> {
            EmptyVouchersContent(
                message = emptyMessage,
                subMessage = emptySubMessage
            )
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vouchers) { voucher -> // ✅ Now uses Voucher domain model
                    VoucherCard(
                        voucher = voucher, // ✅ Pass Voucher directly to VoucherCard
                        onClick = { onVoucherClick(voucher.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = PrimaryColor)
            Text(
                text = "Loading vouchers...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EmptyVouchersContent(
    message: String,
    subMessage: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_offer_percentage),
                contentDescription = stringResource(R.string.no_vouchers),
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subMessage,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun VoucherCard(
    voucher: Voucher, // ✅ Use the new domain model
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = voucher.buttonEnabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (voucher.isUsed) Color(0xFFF5F5F5) else voucher.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (voucher.isUsed) 1.dp else 3.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (voucher.isUsed) Color.Gray.copy(alpha = 0.3f)
                        else getVoucherIconBackground(voucher.discountType) // ✅ Use discountType
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = getVoucherIcon(voucher.discountType), // ✅ Use discountType
                        contentDescription = stringResource(R.string.voucher),
                        modifier = Modifier.size(24.dp),
                        tint = if (voucher.isUsed) Color.Gray else Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = voucher.iconText, // ✅ Use iconText from domain model
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (voucher.isUsed) Color.Gray else Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = voucher.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (voucher.isUsed) Color.Gray else Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = voucher.description,
                    fontSize = 13.sp,
                    color = if (voucher.isUsed) Color.Gray else Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                voucher.minimumOrderText?.let { minOrder -> // ✅ Use minimumOrderText
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = minOrder,
                        fontSize = 12.sp,
                        color = if (voucher.isUsed) Color.Gray else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (voucher.validityText.isNotEmpty()) { // ✅ Use validityText
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = voucher.validityText,
                        fontSize = 12.sp,
                        color = if (voucher.isUsed) Color.Gray else PrimaryColor,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.expiry),
                        modifier = Modifier.size(14.dp),
                        tint = if (voucher.isUsed) Color.Gray else Color.Gray
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (voucher.isUsed) {
                            stringResource(R.string.used)
                        } else {
                            voucher.expiryText // ✅ Use expiryText from domain model
                        },
                        fontSize = 12.sp,
                        color = if (voucher.isUsed) Color.Gray else Color.Gray
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (voucher.buttonEnabled) PrimaryColor else Color.Gray.copy(alpha = 0.2f)
                ),
                modifier = if (voucher.buttonEnabled) Modifier.clickable { onClick() } else Modifier
            ) {
                Text(
                    text = voucher.buttonText, // ✅ Use buttonText from domain model
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (voucher.buttonEnabled) Color.White else Color.Gray
                )
            }
        }
    }
}

// ✅ Update helper functions to use DiscountType enum
fun getVoucherIcon(type: DiscountType): ImageVector = when (type) {
    DiscountType.PERCENTAGE -> Icons.Sharp.Build
    DiscountType.FIXED_AMOUNT -> Icons.Sharp.Star
    DiscountType.FREE_DELIVERY -> Icons.Sharp.Menu
    DiscountType.BUY_ONE_GET_ONE -> Icons.Sharp.Add
}

fun getVoucherIconBackground(type: DiscountType): Color = when (type) {
    DiscountType.PERCENTAGE -> Color(0xFF4CAF50)
    DiscountType.FIXED_AMOUNT -> Color(0xFF2196F3)
    DiscountType.FREE_DELIVERY -> PrimaryColor
    DiscountType.BUY_ONE_GET_ONE -> Color(0xFF9C27B0)
}

// ✅ Remove the getDummyVouchers() function - no longer needed

@Preview(showBackground = true)
@Composable
fun AllVouchersScreenPreview() {
    AllVouchersScreen()
}