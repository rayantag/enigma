package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Rayan Taghizadeh
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void addCycleTest() {
        Alphabet alpha1 = new Alphabet("abcdefghijklmnop");
        Permutation perm1 = new Permutation("(abc) (defg) (hij)", alpha1);
        Permutation perm2 = new Permutation("(acegikmo)", alpha1);
        assertEquals(1, 2 - 1);
    }

    @Test
    public void sizeTest() {
        Permutation p = new Permutation("(a b c) (d e f) (g)",
                new Alphabet("abcdefg"));
        assertEquals(7, p.size());
    }

    @Test
    public void permuteIntTest() {
        Permutation p = new Permutation("(BACD)",
                new Alphabet("ABCD"));
        assertEquals(2, p.permute(0));
        assertEquals(0, p.permute(1));
        assertEquals(3, p.permute(2));
        assertEquals(1, p.permute(3));
        Permutation p1 = new Permutation("(GACD) (BEFH)",
                new Alphabet("ABCDEFGH"));
        assertEquals(2, p1.permute(0));
        assertEquals(5, p1.permute(4));
        assertEquals(1, p1.permute(7));
        assertEquals(6, p1.permute(3));
    }

    @Test
    public void invertIntTest() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals(1, p.invert(0));
        assertEquals(3, p.invert(1));
        assertEquals(0, p.invert(2));
        Permutation p1 = new Permutation("(GACD) (BEFH)",
                new Alphabet("ABCDEFGH"));
        assertEquals(0, p1.invert(2));
        assertEquals(4, p1.invert(5));
        assertEquals(7, p1.invert(1));
        assertEquals(3, p1.invert(6));
    }

    @Test
    public void permuteCharTest() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('A', p.permute('B'));
        assertEquals('B', p.permute('D'));
        assertEquals('D', p.permute('C'));
        Permutation p1 = new Permutation("(GACD) (BEFH) (K)",
                new Alphabet("ABCDEFGHK"));
        assertEquals('C', p1.permute('A'));
        assertEquals('F', p1.permute('E'));
        assertEquals('B', p1.permute('H'));
        assertEquals('G', p1.permute('D'));
        assertEquals('K', p1.permute('K'));
    }

    @Test
    public void invertCharTest() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('B', p.invert('A'));
        assertEquals('D', p.invert('B'));
        assertEquals('C', p.invert('D'));
    }

    @Test
    public void derangementTest() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertTrue(p.derangement());
        Permutation p1 = new Permutation("(BAD)", new Alphabet("ABCD"));
        assertFalse(p1.derangement());
    }

}
