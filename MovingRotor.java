package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Rayan Taghizadeh
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notchess = notches;
    }

    @Override
    boolean atNotch() {
        char car = permutation().alphabet().toChar(setting());
        return _notchess.contains(String.valueOf(car));
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    @Override
    String notches() {
        return _notchess;
    }

    @Override
    boolean rotates() {
        return true;
    }
/** track of notches. */
    private String _notchess;

}
