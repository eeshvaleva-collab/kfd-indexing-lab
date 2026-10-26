package lab2

import lab2.Product
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach


class ListWithCounter(elements: List<Long>) : ArrayList<Long>(elements) {
    var inspectCount: Int = 0
        private set

    override fun iterator(): MutableIterator<Long> {
        val originalIterator = super.iterator()

        return object : MutableIterator<Long> by originalIterator {
            override fun next(): Long {
                inspectCount++
                return originalIterator.next()
            }
        }
    }
}

class ProductServiceTest {
    private fun createKeyboard(available: Boolean = true) = Product(1, "Keyboard", available)
    private fun createMouse(available: Boolean = true) = Product(2, "Mouse", available)
    private fun createPrinter(available: Boolean = true) = Product(3, "Printer", available)
    private fun createScanner(available: Boolean = true) = Product(4, "Scanner", available)
    private fun createWebcam(available: Boolean = true) = Product(5, "Webcam", available)
    private fun createMonitor(available: Boolean = true) = Product(6, "Monitor", available)
    private val keyboard = createKeyboard()
    private val mouse = createMouse()
    private val printer = createPrinter()
    private val scanner = createScanner()
    private val webcam = createWebcam()
    private val monitor = createMonitor()
    private val catalog = listOf(keyboard, mouse, printer, scanner, webcam, monitor)

    @BeforeEach
    fun setUp() {
        ProductRecommendationService.loadCatalog(catalog)
    }

    @Test
    fun `should terminate early based on limit and track inspected recommendations`() {
        val rec1 = ListWithCounter(listOf(1L, 1L, 1L, 3L, 2L, 3L, 99L, 1L, 4L))

        val result1 = ProductRecommendationService.selectProducts(
            rec1,
            emptyList(),
            1
        )
        assertEquals(listOf(keyboard), result1)
        assertEquals(1, rec1.inspectCount)

        val rec = ListWithCounter(listOf(1L, 1L, 1L, 3L, 2L, 3L, 99L, 1L, 4L))

        val result = ProductRecommendationService.selectProducts(
            rec,
            listOf(1L),
            1
        )

        assertEquals(listOf(printer), result)
        assertEquals(4, rec.inspectCount)
    }

    @Test
    fun `should return an empty list in case of zero limit`() {
        val rec = ListWithCounter(listOf(1L, 1L, 1L, 3L, 2L, 3L, 99L, 1L, 4L))

        val result = ProductRecommendationService.selectProducts(
            rec,
            listOf(3L),
            0
        )

        assertEquals(emptyList<Product>(), result)
        assertEquals(0, rec.inspectCount)
    }

    @Test
    fun `should return all matching products if total matches are less than limit`() {
        val rec = listOf(1L, 2L, 3L, 4L)

        val result = ProductRecommendationService.selectProducts(
            rec,
            listOf(3L),
            7
        )

        assertEquals(listOf(keyboard, mouse, scanner), result)
    }

    @Test
    fun `should handle multiple independent requests to the same catalog`() {
        val result1 = ProductRecommendationService.selectProducts(
            recommendedIds = listOf(1L, 2L, 3L),
            excludedIds = emptyList(),
            limit = 2
        )

        assertEquals(listOf(keyboard, mouse), result1)

        val result2 = ProductRecommendationService.selectProducts(
            recommendedIds = listOf(1L, 3L, 5L),
            excludedIds = listOf(1L),
            limit = 2
        )
        assertEquals(listOf(printer, webcam), result2)

        val result3 = ProductRecommendationService.selectProducts(
            recommendedIds = listOf(1L, 3L, 5L),
            excludedIds = emptyList(),
            limit = 0
        )
        assertEquals(emptyList<Product>(), result3)
    }

    @Test
    fun `should correctly return all matches with updated catalog`() {
        val rec = listOf(1L, 2L, 3L, 4L)

        val result = ProductRecommendationService.selectProducts(
            rec,
            listOf(3L),
            3
        )

        assertEquals(listOf(keyboard, mouse, scanner), result)

        val catalogNew = listOf(
            Product(1, "Keyboard", true),
            Product(2, "Mouse", false),
            Product(3, "Monitor", true),
            Product(4, "Webcam", true)
        )

        ProductRecommendationService.loadCatalog(catalogNew)

        val resultNew = ProductRecommendationService.selectProducts(
            rec,
            listOf(3L),
            3
        )

        assertEquals(listOf(Product(1, "Keyboard", true),
            Product(4, "Webcam", true)
        ), resultNew)

    }

    @Test
    fun `should guarantee immutability of input data and independence of repeated calls`() {
        val recommendedIds = listOf(1L, 2L, 3L, 1L)
        val excludedIds = listOf(2L)

        val firstResult = ProductRecommendationService.selectProducts(recommendedIds, excludedIds, limit = 2)

        assertEquals(listOf(keyboard, printer), firstResult)
        assertEquals(listOf(keyboard, mouse, printer, scanner, webcam, monitor), catalog)

        val secondResult = ProductRecommendationService.selectProducts(recommendedIds, excludedIds, limit = 2)

        assertEquals(firstResult, secondResult)
    }

}
