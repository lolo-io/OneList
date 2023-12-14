package com.lolo.io.onelist.core.domain.use_cases

import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.lolo.io.onelist.core.data.model.AllListsWithErrors
import com.lolo.io.onelist.core.data.reporitory.OneListRepository
import kotlinx.coroutines.flow.Flow
import java.io.FileNotFoundException

class GetAllLists(private val repository: OneListRepository) {

    @Throws(FileNotFoundException::class, JsonSyntaxException::class, JsonIOException::class)
    suspend operator fun invoke(): Flow<AllListsWithErrors> {
        return repository.getAllLists()
    }
}