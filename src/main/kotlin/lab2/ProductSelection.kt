package lab2
fun selectProducts(
    products: List<Product>,
    recommendedIds: List<Long>,
    excludedIds: List<Long>
): List<Product> {
    val mockService = ProductRecommendationService()
    mockService.LoadCatalog(products)

    return mockService.selectProducts(recommendedIds, excludedIds)
}
