package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.data.model.LocationResponse
import com.app.tastybuds.data.model.UserResponse
import com.app.tastybuds.domain.model.User
import com.app.tastybuds.domain.model.Location

fun UserResponse.toUserDomainModel(): User {
    return User(
        id = id,
        name = name,
        email = email,
        profileUrl = profileUrl,
        currentLocation = currentLocation?.toLocationDomainModel(),
        createdAt = createdAt
    )
}

fun LocationResponse.toLocationDomainModel(): Location {
    return Location(
        latitude = latitude,
        longitude = longitude,
        address = address
    )
}
