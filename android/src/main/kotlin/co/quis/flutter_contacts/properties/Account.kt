package co.quis.flutter_contacts.properties

data class Account(
    var rawId: String,
    var type: String,
    var name: String,
) {

    val accountId: String get() = "$name|$type"

    companion object {
        fun fromMap(m: Map<String, Any>): Account = Account(
            m["rawId"] as String,
            m["type"] as String,
            m["name"] as String
        )
    }

    fun toMap(): Map<String, Any> = mapOf(
        "rawId" to rawId,
        "type" to type,
        "name" to name
    )
}
