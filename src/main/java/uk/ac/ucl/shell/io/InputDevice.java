package uk.ac.ucl.shell.io;

public interface InputDevice {
    /**
     *
     * @return A line from the input. The newline character is not included.
     */
    String readline();

    boolean inputEnded();
}
