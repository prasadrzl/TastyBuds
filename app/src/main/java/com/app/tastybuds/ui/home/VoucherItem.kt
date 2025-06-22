package com.app.tastybuds.ui.home

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.tastybuds.R
import com.app.tastybuds.ui.theme.PrimaryColor
import com.app.tastybuds.util.ui.AppTopBar

data class VoucherItem(
    val id: String,
    val title: String,
    val description: String,
    val discountValue: String,
    val minOrder: String? = null,
    val expiryDate: String,
    val isUsed: Boolean = false,
    val voucherType: VoucherType,
    val restaurantName: String? = null,
    val backgroundColor: Color = Color(0xFFFFF3E0)
)

enum class VoucherType {
    PERCENTAGE,
    FIXED_AMOUNT,
    FREE_DELIVERY,
    BUY_ONE_GET_ONE
}

@Composable
fun AllVouchersScreen(
    onBackClick: () -> Unit = {},
    onVoucherClick: (String) -> Unit = {}
) {
    val vouchers = remember { getDummyVouchers() }
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

        when (selectedTab) {
            0 -> {
                VouchersContent(
                    vouchers = activeVouchers,
                    onVoucherClick = onVoucherClick,
                    emptyMessage = stringResource(R.string.no_active_vouchers_available),
                    emptySubMessage = stringResource(R.string.check_back_later_for_new_vouchers_and_deals)
                )
            }

            1 -> {
                VouchersContent(
                    vouchers = usedVouchers,
                    onVoucherClick = onVoucherClick,
                    emptyMessage = stringResource(R.string.no_used_vouchers),
                    emptySubMessage = stringResource(R.string.your_used_vouchers_will_appear_here)
                )
            }
        }
    }
}

@Composable
fun VouchersContent(
    vouchers: List<VoucherItem>,
    onVoucherClick: (String) -> Unit,
    emptyMessage: String,
    emptySubMessage: String
) {
    if (vouchers.isEmpty()) {
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
                    text = emptyMessage,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = emptySubMessage,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
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

@Composable
fun VoucherCard(
    voucher: VoucherItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !voucher.isUsed) { onClick() },
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
                        else getVoucherIconBackground(voucher.voucherType)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = getVoucherIcon(voucher.voucherType),
                        contentDescription = stringResource(R.string.voucher),
                        modifier = Modifier.size(24.dp),
                        tint = if (voucher.isUsed) Color.Gray else Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = voucher.discountValue,
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

                voucher.minOrder?.let { minOrder ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.min_order, minOrder),
                        fontSize = 12.sp,
                        color = if (voucher.isUsed) Color.Gray else Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }

                voucher.restaurantName?.let { restaurant ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.valid_at, restaurant),
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
                            stringResource(R.string.expires, voucher.expiryDate)
                        },
                        fontSize = 12.sp,
                        color = if (voucher.isUsed) Color.Gray else Color.Gray
                    )
                }
            }

            if (voucher.isUsed) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = stringResource(id = R.string.used),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            } else {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryColor),
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Text(
                        text = stringResource(R.string.use_now),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

fun getVoucherIcon(type: VoucherType) = when (type) {
    VoucherType.PERCENTAGE -> Icons.Sharp.Build
    VoucherType.FIXED_AMOUNT -> Icons.Sharp.Star
    VoucherType.FREE_DELIVERY -> Icons.Sharp.Menu
    VoucherType.BUY_ONE_GET_ONE -> Icons.Sharp.Add
}

fun getVoucherIconBackground(type: VoucherType) = when (type) {
    VoucherType.PERCENTAGE -> Color(0xFF4CAF50)
    VoucherType.FIXED_AMOUNT -> Color(0xFF2196F3)
    VoucherType.FREE_DELIVERY -> PrimaryColor
    VoucherType.BUY_ONE_GET_ONE -> Color(0xFF9C27B0)
}

fun getDummyVouchers(): List<VoucherItem> {
    return listOf(
        VoucherItem(
            id = "v1",
            title = "Free Delivery",
            description = "Get free delivery on your next order",
            discountValue = "FREE",
            minOrder = "$25",
            expiryDate = "Dec 31",
            voucherType = VoucherType.FREE_DELIVERY,
            restaurantName = "All Restaurants"
        ),
        VoucherItem(
            id = "v2",
            title = "10% Off",
            description = "Get 10% discount on orders above $30",
            discountValue = "10% OFF",
            minOrder = "$30",
            expiryDate = "Dec 28",
            voucherType = VoucherType.PERCENTAGE,
            restaurantName = "Hana Chicken"
        ),
        VoucherItem(
            id = "v3",
            title = "$5 Off",
            description = "Save $5 on your order today",
            discountValue = "$5 OFF",
            minOrder = "$20",
            expiryDate = "Dec 25",
            voucherType = VoucherType.FIXED_AMOUNT
        ),
        VoucherItem(
            id = "v4",
            title = "Buy 1 Get 1",
            description = "Buy one burger, get one free",
            discountValue = "BOGO",
            expiryDate = "Dec 30",
            voucherType = VoucherType.BUY_ONE_GET_ONE,
            restaurantName = "Burger Palace"
        ),
        VoucherItem(
            id = "v5",
            title = "Weekend Special",
            description = "15% off on weekend orders",
            discountValue = "15% OFF",
            minOrder = "$35",
            expiryDate = "Dec 22",
            voucherType = VoucherType.PERCENTAGE,
            isUsed = true
        )
    )
}

@Preview(showBackground = true)
@Composable
fun AllVouchersScreenPreview() {
    AllVouchersScreen()
}