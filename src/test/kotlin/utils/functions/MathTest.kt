package utils.functions

import org.example.utils.functions.log2W
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith


class MathTest {
    @Test
    fun `using an illegal base parameter for log2W`(){
        assertFailsWith(IllegalArgumentException::class){
            log2W(5)
        }
        assertFailsWith(IllegalArgumentException::class){
            log2W(5u)
        }
    }
}