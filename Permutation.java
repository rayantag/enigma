package enigma;

import java.util.HashMap;
import java.util.Map;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Rayan Taghizadeh
 */
class Permutation {
    /** cycles. */
    private String _cycles;

    /** cycles cleaned up. */
    private String[] _cleanCycle;

    /** cycle hashmap. */
    private Map<Integer, Integer> _altCycle = new HashMap<Integer, Integer>();

    /** reverse cycle hashmap. */
    private Map<Integer, Integer> _altCycleReverse
            = new HashMap<Integer, Integer>();

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        String p = cycles.replaceAll("[()]", " ");
        _cleanCycle = p.split(" ");
        for (int i = 0; i < _alphabet.size(); i++) {
            _altCycle.put(i, -1);
            _altCycleReverse.put(i, -1);
        }
        for (int i = 0; i < _cleanCycle.length; i++) {
            addCycle(_cleanCycle[i]);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            if (cycle.length() == 1) {
                _altCycle.replace(_alphabet.toInt(cycle.charAt(i)),
                        _alphabet.toInt(cycle.charAt(i)));
            } else if (i == cycle.length() - 1) {
                _altCycle.replace(_alphabet.toInt(cycle.charAt(i)),
                        _alphabet.toInt(cycle.charAt(0)));
            } else {
                _altCycle.replace(_alphabet.toInt(cycle.charAt(i)),
                        _alphabet.toInt(cycle.charAt(i + 1)));
            }
        }
        for (int i = 0; i < cycle.length(); i++) {
            if (cycle.length() == 1) {
                _altCycleReverse.replace(_alphabet.toInt(cycle.charAt(i)),
                        _alphabet.toInt(cycle.charAt(i)));
            } else if (i == 0) {
                _altCycleReverse.replace(_alphabet.toInt(cycle.charAt(i)),
                        _alphabet.toInt(cycle.charAt(cycle.length() - 1)));
            } else {
                _altCycleReverse.replace(_alphabet.toInt(cycle.charAt(i)),
                        _alphabet.toInt(cycle.charAt(i - 1)));
            }
        }
        for (int i = 0; i < _alphabet.size(); i++) {
            if (_altCycle.get(i) == -1) {
                _altCycle.replace(i, i);
            }
            if (_altCycleReverse.get(i) == -1) {
                _altCycleReverse.replace(i, i);
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _altCycle.get(p);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _altCycleReverse.get(c);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return (_alphabet.toChar(permute(_alphabet.toInt(p))));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return (_alphabet.toChar(invert(_alphabet.toInt(c))));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {

        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (Map.Entry<Integer, Integer> entry : _altCycle.entrySet()) {
            if (entry.getValue().equals(entry.getKey())) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

}
