package com.lolo.io.onelist.core.domain.use_cases

import com.google.gson.JsonIOException
import com.google.gson.JsonSyntaxException
import com.lolo.io.onelist.core.data.datamodel.ListsWithErrors
import kotlinx.coroutines.flow.Flow
import java.io.FileNotFoundException
import com.lolo.io.onelist.core.data.repository.OneListRepository
import kotlinx.coroutines.flow.StateFlow

class GetAllLists(private val repository: OneListRepository) {

    @Throws(FileNotFoundException::class, JsonSyntaxException::class, JsonIOException::class)
    operator fun invoke(): StateFlow<ListsWithErrors> {
        return repository.allListsWithErrors
    }
}