package lab2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

private fun assertAllEquals(expected: List<Product>, vararg items: List<Product>, message: String? = null) {
    if (items.isEmpty()) return
    for (item in items) {
        assertEquals(expected, item, message)
    }
}

class ProductSelectionTest {
    private fun createKeyboard(available: Boolean = true) = Product(1, "Keyboard", available)
    private fun createMouse(available: Boolean = true) = Product(2, "Mouse", available)
    private fun createPrinter(available: Boolean = true) = Product(3, "Printer", available)
    private fun createScanner(available: Boolean = true) = Product(4, "Scanner", available)
    private fun createWebcam(available: Boolean = true) = Product(5, "Webcam", available)
    private fun createMonitor(available: Boolean = true) = Product(6, "Monitor", available)

    @Test
    fun selectsAvailableProductsInRecommendationOrder() {
        val keyboard = Product(1, "Keyboard", true)
        val mouse = Product(2, "Mouse", false)
        val monitor = Product(3, "Monitor", true)
        val webcam = Product(4, "Webcam", true)
        val products = listOf(keyboard, mouse, monitor, webcam)

        val result = selectProducts(
            products,
            listOf(3L, 2L, 3L, 99L, 1L, 4L),
            listOf(1L)
        )

        assertEquals(listOf(monitor, webcam), result)
    }

    @Test
    fun returnsEmptyListWhenThereAreNoRecommendations() {
        val products = listOf(Product(1, "Keyboard", true))

        val result = selectProducts(products, emptyList(), emptyList())

        assertEquals(emptyList<Product>(), result)
    }

    @Test
    fun `should select all matching products if excluded list is empty or contains non-existent IDs`() {
        val keyboard = createKeyboard()
        val mouse = createMouse()
        val printer = createPrinter()
        val scanner = createScanner()
        val catalog = listOf(keyboard, mouse, printer, scanner)

        val result1 = selectProducts(
            catalog,
            listOf(1L, 2L, 3L, 4L),
            emptyList()
        )

        val result2 = selectProducts(
            catalog,
            listOf(1L, 2L, 3L, 4L),
            listOf(7L)
        )

        assertAllEquals(listOf(keyboard, mouse, printer, scanner), result1, result2)
    }

    @Test
    fun `should maintain recommendation order regardless of initial catalog sorting`(){
        val keyboard = createKeyboard()
        val mouse = createMouse()
        val monitor = createMonitor()
        val webcam = createWebcam()
        val printer = createPrinter()
        val scanner = createScanner()

        val catalog1 = listOf(keyboard, mouse, monitor, webcam, printer, scanner)
        val catalog2 = catalog1.reversed()
        val catalog3 =  listOf(printer, scanner, keyboard, webcam, mouse, monitor)

        val recs = listOf(5L, 2L, 3L, 4L, 1L)
        val exclusions= listOf(5L)

        assertAllEquals(
            listOf(mouse, printer, scanner, keyboard),
            selectProducts(catalog1, recs, exclusions),
            selectProducts(catalog2, recs, exclusions),
            selectProducts(catalog3, recs, exclusions)
        )
    }

    @Test
    fun `should handle duplicate IDs in recommendations without duplicating results`(){
        val keyboard = createKeyboard()
        val mouse = createMouse()
        val printer = createPrinter()
        val scanner = createScanner()
        val webcam = createWebcam()
        val monitor = createMonitor()

        val catalog = listOf(keyboard, mouse, monitor, webcam, printer, scanner)

        val recsWithDuplicates = listOf(2L, 3L, 4L, 1L, 2L, 3L, 4L, 2L, 3L, 4L, 4L, 4L, 5L, 6L, 6L)
        val exclusions= listOf(4L)
        val exclusionsWithDuplicates = listOf(4L, 4L)


        assertAllEquals(
            listOf(mouse, printer, keyboard, webcam, monitor),
            selectProducts(catalog, recsWithDuplicates, exclusions),
            selectProducts(catalog, recsWithDuplicates, exclusionsWithDuplicates)
        )
    }

    @Test
    fun `should return empty list when all recommended products are explicitly excluded`(){
        val catalog = listOf(
            createKeyboard(),
            createMouse(),
            createPrinter(),
            createScanner(),
            createWebcam(),
            createMonitor()
        )

        val recs = listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)
        val exclusions= listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)

        assertEquals(emptyList<Product>(), selectProducts(catalog, recs, exclusions))
    }

    @Test
    fun `should return empty list if either the base catalog or recommendations list is empty`(){
        val catalog = listOf(createKeyboard(), createMouse())

        val resEmptyCatalog = selectProducts(emptyList(), listOf(1L, 2L), emptyList())
        val resEmptyRecs = selectProducts(catalog, emptyList(), listOf(1L))

        assertEquals(emptyList<Product>(), resEmptyCatalog)
        assertEquals(emptyList<Product>(), resEmptyRecs)
    }

    @Test
    fun `should ignore unavailable, excluded, and unknown product IDs`() {
        val catalog = listOf(
            createKeyboard(),
            createMouse(available = false),
            createPrinter(),
            createScanner()
        )

        val recs = listOf(3L, 2L, 99L, 1L, 4L)
        val exclusions= listOf(4L)

        val result = selectProducts(catalog, recs, exclusions)

        assertEquals(listOf(createPrinter(), createKeyboard()), result)
    }

    @Test
    fun `should return empty list when no recommended products match active criteria`() {
        val catalog = listOf(
            createKeyboard(available = false),
            createMouse(available = false)
        )
        val recs = listOf(1L, 2L, 99L)
        val exclusions = listOf(1L)

        val result = selectProducts(catalog, recs, exclusions)

        assertEquals(emptyList<Product>(), result)
    }

    @Test
    fun `should guarantee immutability of input data and independence of repeated calls`() {
        val catalog = mutableListOf(createKeyboard(), createMouse(), createPrinter())
        val recsInput = mutableListOf(3L, 1L)
        val exclusions = mutableListOf(1L)

        val firstResult = selectProducts(catalog, recsInput, exclusions)
        assertEquals(listOf(createPrinter()), firstResult)

        assertEquals(listOf(createKeyboard(), createMouse(), createPrinter()), catalog, "Catalog mutation detected!")
        assertEquals(listOf(3L, 1L), recsInput, "Recommendations mutation detected!")
        assertEquals(listOf(1L), exclusions, "Exclusions mutation detected!")

        val secondResult = selectProducts(catalog, recsInput, exclusions)
        assertEquals(listOf(createPrinter()), secondResult, "Subsequent call returned a different result!")
    }

}
