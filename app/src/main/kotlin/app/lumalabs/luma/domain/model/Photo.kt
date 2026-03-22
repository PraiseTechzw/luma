package app.lumalabs.luma.domain.model

data class Photo(
    val uri: String,
    val name: String,
    val size: Long,
    val dateTaken: Long,
    val path: String? = null
)
