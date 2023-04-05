package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetTest {
    Alphabet test = new Alphabet("ABCD");
    String testString = "abcdefzyxw1092";
    Alphabet test1 = new Alphabet(testString);

    @Test
    public void sizeTest() {
        assertEquals(4, test.size());
    }

    @Test
    public void containsTest() {
        assertTrue(test.contains('A'));
        assertFalse(test.contains('Z'));
    }

    @Test
    public void toTests() {
        assertEquals(0, test.toInt('A'));
        assertEquals(3, test.toInt('D'));
        assertEquals('B', test.toChar(1));
        assertEquals('D', test.toChar(3));
    }

    @Test
    public void testComplicated() {
        assertEquals(14, test1.size());
        for (int i = 0; i < testString.length(); i++) {
            char curr = testString.charAt(i);
            assertEquals(curr, test1.toChar(i));
            assertEquals(i, test1.toInt(curr));
            assertTrue(test1.contains(curr));
        }
        assertFalse(test1.contains('A'));
    }
}
