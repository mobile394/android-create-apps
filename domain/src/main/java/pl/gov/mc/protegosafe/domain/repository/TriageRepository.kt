package pl.gov.mc.protegosafe.domain.repository

import pl.gov.mc.protegosafe.domain.model.TriageItem

interface TriageRepository {
    fun getLastTriageCompletedTimestamp(): Long
    fun saveTriageCompletedTimestamp(timestamp: Long)
    fun parseBridgePayload(payload: String): TriageItem
}
