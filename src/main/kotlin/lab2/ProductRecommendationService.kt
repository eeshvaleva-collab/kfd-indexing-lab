package lab2

object ProductRecommendationService {
    @Volatile
    private var productCatalog: Map<Long, Product> = emptyMap()

    fun loadCatalog(newCatalog: List<Product>){
        productCatalog = newCatalog.associateBy { it.id }
    }

    fun selectProducts(
        recommendedIds: List<Long>,
        excludedIds: List<Long>,
        limit: Int? = null
    ): List<Product> {
        if (limit != null) {
            require(limit >= 0) { "Limit cannot be negative. Provided value: $limit" }
            if (limit == 0) return emptyList()
        }

        val currentMap = this.productCatalog
        val excludedSet = excludedIds.toSet()
        val base = recommendedIds.asSequence()

        val seq = if (excludedSet.size > recommendedIds.size) {
            base.filter { it !in excludedSet }
                .distinct()
        }
        else {
            base.distinct()
                .filter { it !in excludedSet }
        }

        val processedSeq = seq.mapNotNull{ id -> currentMap[id] }.filter{ it.available}

        return if (limit != null) processedSeq.take(limit).toList() else processedSeq.toList()
    }
}

