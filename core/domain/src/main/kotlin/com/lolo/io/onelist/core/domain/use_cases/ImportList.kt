package com.lolo.io.onelist.core.domain.use_cases

import android.net.Uri
import com.lolo.io.onelist.core.data.reporitory.OneListRepository
import com.lolo.io.onelist.core.model.ItemList

class ImportList(private val repository: OneListRepository) {
    suspend operator fun invoke(uri: Uri): ItemList {
        return repository.importList(uri)
    }
}