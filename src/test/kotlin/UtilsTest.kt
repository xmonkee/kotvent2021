import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.reflect.typeOf

internal class UtilsTest {

    @Test
    fun getListInput() {
        val inp = Utils.getListInput(1);
        assert(inp.isNotEmpty());
    }

    @Test
    fun getNumInput() {
        val inp = Utils.getNumInput(1);
        assert(inp.isNotEmpty());
    }
}