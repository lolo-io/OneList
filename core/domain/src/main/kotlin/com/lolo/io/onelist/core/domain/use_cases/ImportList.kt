package com.lolo.io.onelist.core.domain.use_cases

import android.net.Uri
import com.lolo.io.onelist.core.model.ItemList
import com.lolo.io.onelist.core.data.repository.OneListRepository

class ImportList(private val repository: OneListRepository) {
    suspend operator fun invoke(uri: Uri): ItemList {
        return repository.importList(uri)
    }
}