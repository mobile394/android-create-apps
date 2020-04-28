package pl.gov.mc.protegosafe.data.mapper

import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.model.ClearData
import pl.gov.mc.protegosafe.data.model.TriageData
import pl.gov.mc.protegosafe.domain.model.ClearItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationData
import pl.gov.mc.protegosafe.domain.model.PushNotificationTopic
import pl.gov.mc.protegosafe.domain.model.TriageItem

private const val FCM_NOTIFICATION_TITLE_KEY = "title"
private const val FCM_NOTIFICATION_CONTENT_KEY = "content"
fun Map<String, String>.hasNotification() =
    !get(FCM_NOTIFICATION_TITLE_KEY).isNullOrBlank()

fun Map<String, String>.toNotificationDataItem(topic: String?) = PushNotificationData(
    title = get(FCM_NOTIFICATION_TITLE_KEY)
        ?: throw IllegalArgumentException("Hash id has no value"),
    content = get(FCM_NOTIFICATION_CONTENT_KEY) ?: "",
    topic = when (topic) {
        "/topics/${BuildConfig.MAIN_TOPIC}" -> PushNotificationTopic.MAIN
        "/topics/${BuildConfig.DAILY_TOPIC}" -> PushNotificationTopic.DAILY
        else -> PushNotificationTopic.UNKNOWN
    }
)

fun TriageData.toEntity() = TriageItem(timestamp = timestamp)

fun ClearData.toEntity() = ClearItem(clearBtData = clearBtData)