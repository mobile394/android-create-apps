package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ENStatsResultData(
    @SerializedName("enStats")
    val enStats: ENStatsData?
)