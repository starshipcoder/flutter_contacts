import Contacts

@available(iOS 9.0, *)
struct AccountInfo {
    var rawId: String
    // local, exchange, cardDAV or unassigned
    var type: String
    var name: String
    var accountId: String

    var contactCount: Int
    var addressCount: Int

    init(fromMap m: [String: Any?]) {
        rawId = m["rawId"] as! String
        type = m["type"] as! String
        name = m["name"] as! String
        accountId = m["accountId"] as! String
        contactCount = m["contactCount"] as! Int
        addressCount = m["addressCount"] as! Int
    }

    func toMap() -> [String: Any?] { [
        "rawId": rawId,
        "type": type,
        "name": name,
        "accountId": accountId,
        "contactCount": contactCount,
        "addressCount": addressCount,
    ]
    }
}
