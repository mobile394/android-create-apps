package pl.gov.mc.protegosafe.domain.usecase.restrictions

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.CovidInfoItem
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class UpdateDistrictsRestrictionsUseCase(
    private val covidInfoRepository: CovidInfoRepository,
    private val notifyDistrictsUpdatedUseCase: NotifyDistrictsUpdatedUseCase,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Completable {
        return covidInfoRepository.getCovidInfo()
            .flatMapCompletable { covidInfo ->
                notifyUserAboutDistrictsUpdate(covidInfo)
                    .andThen(syncWithDbAndSaveTimestamp(covidInfo))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun notifyUserAboutDistrictsUpdate(covidInfo: CovidInfoItem): Completable {
        return covidInfoRepository.getCovidInfoUpdateTimestamp()
            .flatMapCompletable { updateTimestamp ->
                if (updateTimestamp == 0L) {
                    Completable.complete()
                } else {
                    notifyDistrictsUpdatedUseCase.execute(
                        covidInfo.voivodeships
                            .map { it.districts }
                            .flatten()
                    )
                }
            }
    }

    private fun syncWithDbAndSaveTimestamp(covidInfo: CovidInfoItem): Completable {
        return syncWithDb(covidInfo.voivodeships)
            .andThen(saveTimeUpdateTimestamp(covidInfo.lastUpdate))
    }

    private fun syncWithDb(voivodeships: List<VoivodeshipItem>): Completable {
        return covidInfoRepository.syncDistrictsRestrictionsWithDb(voivodeships)
    }

    private fun saveTimeUpdateTimestamp(timestamp: Long): Completable {
        return covidInfoRepository.saveCovidInfoUpdateTimestamp(timestamp)
    }
}
