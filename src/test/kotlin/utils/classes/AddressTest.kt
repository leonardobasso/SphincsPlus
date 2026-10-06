package utils.classes

import org.example.utils.classes.Address
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse

@OptIn(ExperimentalUnsignedTypes::class)
class AddressTest {
    @Test
    fun `check copyOf`() {
        val addr = Address()
        addr.setChainAddress(14u)
        val copy = addr.copyOf()
        assertContentEquals(
            addr.address,
            copy.address,
            "${addr.address.contentToString()} compared to ${copy.address.contentToString()}"
        )
        addr.setHashAddress(1u)
        assertFalse(
            addr.address.contentEquals(copy.address),
            "${addr.address.contentToString()} compared to ${copy.address.contentToString()}"
        )
    }
}