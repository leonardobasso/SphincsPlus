package utils.functions

import org.example.utils.functions.toByteArray
import org.example.utils.functions.toInt
import org.junit.jupiter.api.Test
import kotlin.collections.contentToString
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)

class ConversionTest {

    @Test
    fun `check if the UInt to ByteString conversion is right`(){
        val ui = 12345u
        val bs = ui.toByteArray(3)
        val exp = ubyteArrayOf(0u, 48u, 57u)
        assertContentEquals(exp, bs, "${bs.contentToString()} was compared to ${exp.contentToString()}")
    }

    @Test
    fun `check if the ByteArray to UInt conversion is right`() {
        val bs = ubyteArrayOf(0u, 0u,  212u, 49u)
        val ui = bs.toInt()
        val exp = 54321u
        assertEquals(exp, ui, "$ui was compared to $exp")

    }

    @Test
    fun `check preservation property of the toByteArray and toInt functions`(){
        val bs = ubyteArrayOf(212u, 49u)
        val check = bs.toInt().toByteArray(2)
        assertContentEquals(bs, check, "${bs.contentToString()} was compared to ${check.contentToString()}")

        val ui = 54321u
        val check2 = ui.toByteArray(5).toInt()

        assertEquals(ui, check2, "$ui was compared to $check2")
    }
    @Test

    fun `check losing values`(){
        val ui = 12345u
        val bs = ui.toByteArray(1)
        val exp = ubyteArrayOf(57u)
        assertContentEquals(exp, bs, "${bs.contentToString()} was compared to ${exp.contentToString()}")
    }

}