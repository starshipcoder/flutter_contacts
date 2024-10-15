class AccountInfo {
  AccountInfo({
    required this.name,
    required this.type,
    required this.rawId,
    required this.mimetypes,
    required this.accountId,
    required this.contactCount,
    required this.addressCount,
  });

  final String name;
  final String type;
  final String rawId;
  final List<String> mimetypes;
  final String accountId;
  final int contactCount;
  final int addressCount;

  factory AccountInfo.fromJson(Map<String, dynamic> json) =>
      AccountInfo(
        name: (json['name'] as String?) ?? '',
        type: (json['type'] as String?) ?? '',
        rawId: (json['rawId'] as String?) ?? '',
        mimetypes: (json['mimetypes'] as List?)?.map((e) => e as String).toList() ?? [],
        accountId: (json['accountId'] as String?) ?? '',
        contactCount: (json['contactCount'] as int?) ?? 0,
        addressCount: (json['addressCount'] as int?) ?? 0,
      );

  Map<String, dynamic> toJson() =>
      <String, dynamic>{
        'name': name,
        'type': type,
        'rawId': rawId,
        'mimetypes': mimetypes,
        'accountId': accountId,
        'contactCount': contactCount,
        'addressCount': addressCount,
      };

  @override
  String toString() =>
      'AccountInfo(name=$name, type= $type, accountId=$accountId, contactCount=$contactCount, addressCount=$addressCount)';
}
