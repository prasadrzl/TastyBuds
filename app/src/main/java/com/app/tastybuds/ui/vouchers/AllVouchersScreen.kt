package com.app.tastybuds.ui.vouchers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Build
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.tastybuds.R
import com.app.tastybuds.domain.model.DiscountType
import com.app.tastybuds.domain.model.Voucher
import com.app.tastybuds.ui.theme.backgroundColor
import com.app.tastybuds.ui.theme.cardContentColor
import com.app.tastybuds.ui.theme.disabledBorderColor
import com.app.tastybuds.ui.theme.disabledTextColor
import com.app.tastybuds.ui.theme.infoColor
import com.app.tastybuds.ui.theme.onPrimaryColor
import com.app.tastybuds.ui.theme.primaryColor
import com.app.tastybuds.ui.theme.successColor
import com.app.tastybuds.ui.theme.surfaceColor
import com.app.tastybuds.ui.theme.textSecondaryColor
import com.app.tastybuds.util.ui.AppTopBar
import com.app.tastybuds.util.ui.EmptyVouchersContent
import com.app.tastybuds.util.ui.LoadingScreen
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun AllVouchersScreen(
    onBackClick: () -> Unit = {},
    onVoucherClick: (String) -> Unit = {},
    viewModel: VouchersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val swipeRefreshState = rememberSwipeRefreshState(uiState.isRefreshing)

    LaunchedEffect(uiState.error) {
        uiState.error?.let { _ ->
            viewModel.clearError()
        }
    }

    val tabs = listOf(
        stringResource(R.string.active, uiState.activeVouchersCount),
        stringResource(R.string.used, uiState.usedVouchersCount)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor())
    ) {
        AppTopBar(
            title = stringResource(id = R.string.my_vouchers),
            onBackClick = onBackClick
        )

        TabRow(
            selectedTabIndex = uiState.selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = surfaceColor(),
            contentColor = primaryColor(),
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTab]),
                    color = primaryColor()
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (uiState.selectedTab == index) primaryColor() else textSecondaryColor()
                        )
                    }
                )
            }
        }

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refreshVouchers() },
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState.selectedTab) {
                0 -> {
                    VouchersContent(
                        uiState = uiState,
                        vouchers = uiState.activeVouchers,
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
                        uiState = uiState,
                        vouchers = uiState.usedVouchers,
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
            LoadingScreen()
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
                items(vouchers) { voucher ->
                    VoucherCard(
                        voucher = voucher,
                        onClick = { onVoucherClick(voucher.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun VoucherCard(
    voucher: Voucher,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = voucher.buttonEnabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (voucher.isUsed) disabledBorderColor() else voucher.backgroundColor
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
                        if (voucher.isUsed) disabledTextColor().copy(alpha = 0.3f)
                        else getVoucherIconBackground(voucher.discountType)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = getVoucherIcon(voucher.discountType),
                        contentDescription = stringResource(R.string.voucher),
                        modifier = Modifier.size(24.dp),
                        tint = if (voucher.isUsed) disabledTextColor() else onPrimaryColor()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = voucher.iconText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (voucher.isUsed) disabledTextColor() else onPrimaryColor()
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
                    color = if (voucher.isUsed) disabledTextColor() else cardContentColor(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = voucher.description,
                    fontSize = 13.sp,
                    color = if (voucher.isUsed) disabledTextColor() else textSecondaryColor(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                voucher.minimumOrderText?.let { minOrder ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = minOrder,
                        fontSize = 12.sp,
                        color = if (voucher.isUsed) disabledTextColor() else textSecondaryColor(),
                        fontWeight = FontWeight.Medium
                    )
                }

                if (voucher.validityText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = voucher.validityText,
                        fontSize = 12.sp,
                        color = if (voucher.isUsed) disabledTextColor() else primaryColor(),
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
                        tint = if (voucher.isUsed) disabledTextColor() else textSecondaryColor()
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = if (voucher.isUsed) {
                            stringResource(R.string.used)
                        } else {
                            voucher.expiryText
                        },
                        fontSize = 12.sp,
                        color = if (voucher.isUsed) disabledTextColor() else textSecondaryColor()
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (voucher.buttonEnabled) primaryColor() else disabledBorderColor()
                ),
                modifier = if (voucher.buttonEnabled) Modifier.clickable { onClick() } else Modifier
            ) {
                Text(
                    text = voucher.buttonText,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (voucher.buttonEnabled) onPrimaryColor() else disabledTextColor()
                )
            }
        }
    }
}

fun getVoucherIcon(type: DiscountType): ImageVector = when (type) {
    DiscountType.PERCENTAGE -> Icons.Sharp.Build
    DiscountType.FIXED_AMOUNT -> Icons.Sharp.Star
    DiscountType.FREE_DELIVERY -> Icons.Sharp.Menu
    DiscountType.BUY_ONE_GET_ONE -> Icons.Sharp.Add
}

@Composable
fun getVoucherIconBackground(type: DiscountType): Color = when (type) {
    DiscountType.PERCENTAGE -> successColor()
    DiscountType.FIXED_AMOUNT -> infoColor()
    DiscountType.FREE_DELIVERY -> primaryColor()
    DiscountType.BUY_ONE_GET_ONE -> Color(0xFF9C27B0)
}

@Preview(showBackground = true)
@Composable
fun AllVouchersScreenPreview() {
    AllVouchersScreen()
}