package com.lolo.io.onelist.core.domain.use_cases

import com.lolo.io.onelist.core.data.persistence.PersistenceHelper

class Version(private val persistenceHelper: PersistenceHelper) {

    // todo add a repository

    operator fun invoke(): String {
        return persistenceHelper.version
    }

    operator fun invoke(version: String) {
        persistenceHelper.version = version
    }
}