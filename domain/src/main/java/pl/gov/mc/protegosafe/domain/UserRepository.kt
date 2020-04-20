package pl.gov.mc.protegosafe.domain

import io.reactivex.Completable
import io.reactivex.Single

interface UserRepository {
    fun getUserHash(): Single<String>
    fun saveUserHash(hash: String): Completable
}