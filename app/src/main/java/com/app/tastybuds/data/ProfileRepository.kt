package com.app.tastybuds.data

import javax.inject.Inject

interface ProfileRepository {
}

class ProfileRepositoryImpl @Inject constructor(private val profileRemoteDataSource: ProfileRemoteDataSource) :
    ProfileRepository {

}