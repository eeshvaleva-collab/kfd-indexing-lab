package lab2
fun selectProducts(
    products: List<Product>,
    recommendedIds: List<Long>,
    excludedIds: List<Long>
): List<Product> {
    ProductRecommendationService.loadCatalog(products)

    return ProductRecommendationService.selectProducts(recommendedIds, excludedIds)
}
