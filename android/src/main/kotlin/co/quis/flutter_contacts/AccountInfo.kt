package co.quis.flutter_contacts

data class AccountInfo(
    var name: String,
    var type: String,
    var mimetypes: List<String> = listOf<String>(),
    var accountId: String,
    var contactCount: Int,
    var addressCount: Int,

) {
    companion object {
        fun fromMap(m: Map<String, Any?>): AccountInfo {
            return AccountInfo(
                m["name"] as String,
                m["type"] as String,
                m["mimetypes"] as List<String>,
                m["accountId"] as String,
                m["contactCount"] as Int,
                m["addressCount"] as Int,
            )
        }
    }

    fun toMap(): Map<String, Any?> = mapOf(
        "name" to name,
        "type" to type,
        "mimetypes" to mimetypes,
        "accountId" to accountId,
        "contactCount" to contactCount,
        "addressCount" to addressCount,

    )
}
