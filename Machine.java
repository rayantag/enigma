package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Rayan Taghizadeh
 */
class Machine {

    /** keep track of rotors. */
    private HashMap<String, Rotor> _allRotors = new HashMap<>();

    /** keep track of rotors. */
    private ArrayList<Rotor> _allRotors1 = new ArrayList<>();

    /** track of pawls. */
    private int _pawls;

    /** chosen rotors. */
    private ArrayList<Rotor> _chosenRotors = new ArrayList<>();

    /** plugboard. */
    private Permutation _plugboard;

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _pawls = pawls;
        _numberRotors = numRotors;
        for (Rotor r : allRotors) {
            _allRotors.put(r.name(), r);
            _allRotors1.add(r);
        }
        _plugboard = new Permutation("", _alphabet);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numberRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {

        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _allRotors1.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _allRotors1.clear();
        for (String s : rotors) {
            _allRotors1.add(_allRotors.get(s));
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length()  != numRotors() - 1) {
            throw error("invalid setting size");
        }
        for (int i = 0; i < setting.length(); i++) {
            _allRotors1.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {

        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        int size1 = _allRotors1.size();
        for (int i = 1; i < size1 - 1; i++) {
            if (_allRotors1.get(i).rotates()
                    && _allRotors1.get(i + 1).atNotch()) {
                _allRotors1.get(i).advance();
            } else if (_allRotors1.get(i - 1).rotates()
                    && _allRotors1.get(i).atNotch()) {
                _allRotors1.get(i).advance();
            }
        }
        _allRotors1.get(size1 - 1).advance();
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        for (int i = _allRotors1.size() - 1; i >= 0; i--) {
            c = _allRotors1.get(i).convertForward(c);
        }
        for (int i = 1; i < _allRotors1.size(); i++) {
            c = _allRotors1.get(i).convertBackward(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String s = "";
        for (int i = 0; i < msg.length(); i++) {
            s += (_alphabet.toChar(convert(_alphabet.toInt(msg.charAt(i)))));
        }
        return s;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** number of rotors. */
    private int _numberRotors;
}
