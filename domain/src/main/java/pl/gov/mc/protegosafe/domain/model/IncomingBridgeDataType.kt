package pl.gov.mc.protegosafe.domain.model

enum class IncomingBridgeDataType(val code: Int) {
    TRIAGE(1),
    REQUEST_PERMISSION(32),
    REQUEST_BLUETOOTH(33),
    REQUEST_CHANGE_BATTERY_OPTIMIZATION(34)
    ;
    companion object {
        fun valueOf(value: Int): IncomingBridgeDataType = IncomingBridgeDataType.values().find { it.code == value } ?: throw IllegalAccessException()
    }
}