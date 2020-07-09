package pl.gov.mc.protegosafe.domain.exception

sealed class UploadException(
    override val message: String = "Error occurred during attempt to upload temporary exposure keys"
) : Exception() {
    object PinVerificationError : UploadException()
    object PinVerificationFailed : UploadException()
    object UploadTemporaryExposureKeysError : UploadException()
    object GetTemporaryExposureKeysError : UploadException()
}
