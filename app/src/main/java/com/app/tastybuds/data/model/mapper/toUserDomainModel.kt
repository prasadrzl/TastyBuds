package com.app.tastybuds.data.model.mapper

import com.app.tastybuds.domain.model.User
import com.app.tastybuds.domain.model.Location
import com.app.tastybuds.domain.model.LocationResponse
import com.app.tastybuds.domain.model.UpdateProfileRequest
import com.app.tastybuds.domain.model.UpdateUserRequest
import com.app.tastybuds.domain.model.UserResponse

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

fun UpdateUserRequest.toUpdateUserDataModel(): UpdateProfileRequest {
    return UpdateProfileRequest(
        name = name,
        email = email,
        profileUrl = profileUrl
    )
}