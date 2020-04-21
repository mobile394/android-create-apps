package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

class StartBLEMonitoringServiceUseCase(
    private val openTraceRepository: OpenTraceRepository
) {

    fun execute(delay: Long = 0) {
        openTraceRepository.startBLEMonitoringService(delay)
    }
}