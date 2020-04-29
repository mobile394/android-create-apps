package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ClearMapper
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.TraceStatusMapper
import pl.gov.mc.protegosafe.domain.repository.TriageRepository

class OnSetBridgeDataUseCase(
    private val postExecutionThread: PostExecutionThread,
    private val triageRepository: TriageRepository,
    private val enableBTServiceUseCase: EnableBTServiceUseCase,
    private val servicesStatusUseCase: GetServicesStatusUseCase,
    private val clearBtDataUseCase: ClearBtDataUseCase,
    private val traceStatusMapper: TraceStatusMapper,
    private val clearMapper: ClearMapper
) {

    fun execute(input: IncomingBridgeDataItem, onBridgeData: (Int, String) -> Unit): Completable =
        when (input.type) {
            IncomingBridgeDataType.TRIAGE -> {
                Completable.fromAction {
                    triageRepository.saveTriageCompletedTimestamp(
                        triageRepository.parseBridgePayload(
                            input.payload
                        ).timestamp
                    )
                }
            }
            IncomingBridgeDataType.REQUEST_ENABLE_BT_SERVICE -> {
                enableBTServiceUseCase.execute(
                    traceStatusMapper.toDomainItem(input.payload).enableBtService
                ).andThen(Completable.fromAction {
                    onBridgeData(
                        OutgoingBridgeDataType.SERVICE_STATUS_CHANGE.code,
                        servicesStatusUseCase.execute()
                    )
                })
            }
            IncomingBridgeDataType.REQUEST_CLEAR_BT_DATA -> {
                clearBtDataUseCase.execute(
                    clearMapper.toEntity(input.payload)
                )
            }
            else -> throw IllegalStateException("Illegal input type")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
}