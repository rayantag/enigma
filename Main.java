package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Rayan Taghizadeh
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enigma = readConfig();
        String setting = _input.nextLine();
        setUp(enigma, setting);
        while (_input.hasNextLine()) {
            String nextLine = _input.nextLine();
            if (!nextLine.contains(String.valueOf('*'))) {
                String nospace1 = nextLine.replaceAll("\\s", "");
                String converted = enigma.convert(nospace1);
                printMessageLine(converted);
            } else {
                setUp(enigma, nextLine);
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            HashMap<String, Rotor> allRotors = new HashMap<>();
            _allRotor = new ArrayList<>();
            String alphaString = _config.next();
            _alphabet = new Alphabet(alphaString);
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            while (_config.hasNext()) {
                Rotor rotor = readRotor();
                allRotors.put(rotor.name(), rotor);
                _allRotor.add(rotor);
            }
            return new Machine(_alphabet, numRotors, pawls, _allRotor);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Rotor resultRotor;
            String rotorName = _config.next();
            String rotorTypenotch = _config.next();
            char rotorType = rotorTypenotch.charAt(0);
            String notches = rotorTypenotch.substring(1);
            String cycle = "";
            while (_config.hasNext(".*[\\\\(|\\\\)]+.*")) {
                cycle += _config.next();
            }
            if (rotorType == 'M') {
                resultRotor = new MovingRotor(rotorName,
                        new Permutation(cycle, _alphabet), notches);
            } else if (rotorType == 'N') {
                resultRotor = new FixedRotor(rotorName,
                        new Permutation(cycle, _alphabet));
            } else if (rotorType == 'R') {
                resultRotor = new Reflector(rotorName,
                        new Permutation(cycle, _alphabet));
            } else {
                throw error("1:48");
            }
            return resultRotor;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner s = new Scanner(settings);
        Scanner m = new Scanner(settings);
        if (!(s.next().charAt(0) == '*')) {
            throw error("erroneous setting line start");
        }
        String plug = "";
        String[] classRotors = new String[M.numRotors()];
        for (int i = 0; i < M.numRotors(); i++) {
            classRotors[i] = s.next();
        }
        M.insertRotors(classRotors);
        if (!(M.getRotor(0).reflecting())) {
            throw error("first element must be a reflector");
        }
        for (int i = 0; i < 2; i++) {
            for (int j = 2; j < M.numRotors(); j++) {
                if (M.getRotor(i).name() == M.getRotor(j).name()) {
                    throw error("duplicate name");
                }
            }
        }
        String nextSettings = s.next();
        if (nextSettings.length() != M.numRotors() - 1) {
            throw error("not valid settings length");
        }
        M.setRotors(nextSettings);
        while (s.hasNext()) {
            plug += s.next() + "";
        }
        Permutation pb = new Permutation(plug, _alphabet);
        M.setPlugboard(pb);
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String nospace = msg.replaceAll("\\s", "");
        if (nospace.length() == 0) {
            _output.println();
        } else {
            while (nospace.length() > 5) {
                _output.print(nospace.substring(0, 5) + " ");
                nospace = nospace.substring(5);
            }
            _output.println(nospace);
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** track of all rotors. */
    private Collection<Rotor> _allRotor;
}
