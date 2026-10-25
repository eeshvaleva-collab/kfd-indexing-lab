package lab2

import lab2.ProductRecommendationService.Request

object ProductRecommendationService {
    private data class Request(
        val recommendedIds: List<Long>,
        val excludedIds: List<Long>,
        val limit: Int? = null,
        val onInspect: (() -> Unit)? = null
    ) {
        init {
            if (limit != null) {
                require(limit >= 0) { "Limit cannot be negative. Provided value: $limit" }
            }
        }
    }
    @Volatile
    private var productCatalog: Map<Long, Product> = emptyMap()


    fun LoadCatalog(newCatalog: List<Product>) {
        this.productCatalog = newCatalog.associateBy { it.id }
    }

    fun selectProducts(
        recommendedIds: List<Long>,
        excludedIds: List<Long>
    ): List<Product>{
        val context = Request(recommendedIds, excludedIds)
        return selectGeneral(context)
    }

    fun selectProducts(
        recommendedIds: List<Long>,
        excludedIds: List<Long>,
        limit: Int
    ): List<Product> {
        if (limit == 0) return emptyList()
        val context = Request(recommendedIds, excludedIds, limit)

        return selectGeneral(context)
    }

    private fun selectGeneral(
        context: Request
    ): List<Product> {
        val currentMap = this.productCatalog
        val excludedSet = context.excludedIds.toSet()

        val seq = context.recommendedIds.asSequence()
            .filter { it !in excludedSet }
            .distinct().mapNotNull{ id -> currentMap[id] }.filter{ it.available}


        return if (context.limit != null) seq.take(context.limit).toList() else seq.toList()
    }
}

