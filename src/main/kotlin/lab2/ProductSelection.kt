package lab2

fun selectProducts(
    products: List<Product>,
    recommendedIds: List<Long>,
    excludedIds: List<Long>
): List<Product> {
    val productMap = products.associateBy { it.id }
    val excludedSet = excludedIds.toSet()

    val ids = recommendedIds.asSequence()
        .filter { it !in excludedSet }
        .distinct()

    return ids.mapNotNull{ id -> productMap[id] }.filter{ it.available }.toList()
}