package com.app.tastybuds.domain

import com.app.tastybuds.data.MenuItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMenuItemsUseCase @Inject constructor(
    private val menuRepository: MenuRepository
) {
    operator fun invoke(restaurantId: String): Flow<Result<List<MenuItem>>> {
        return menuRepository.getMenuItemsByRestaurant(restaurantId)
    }
}
