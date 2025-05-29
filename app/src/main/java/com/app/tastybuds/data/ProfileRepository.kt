package com.app.tastybuds.data

interface ProfileRepository {
}

class ProfileRepositoryImpl @Inject constructor(private val profileRemoteDataSource: ProfileRemoteDataSource) :
    ProfileRepository {

}