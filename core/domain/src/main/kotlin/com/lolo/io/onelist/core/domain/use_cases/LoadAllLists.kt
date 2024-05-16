package com.lolo.io.onelist.core.domain.use_cases

import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.lolo.io.onelist.core.data.datamodel.ListsWithErrors
import kotlinx.coroutines.flow.Flow
import java.io.FileNotFoundException
import com.lolo.io.onelist.core.data.repository.OneListRepository

class LoadAllLists(private val repository: OneListRepository) {

    @Throws(FileNotFoundException::class, JsonSyntaxException::class, JsonIOException::class)
    suspend operator fun invoke(): Flow<ListsWithErrors> {
        return repository.getAllLists()
    }
}