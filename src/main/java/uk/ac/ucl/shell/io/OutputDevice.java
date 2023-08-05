package uk.ac.ucl.shell.io;

public interface OutputDevice {
    enum Color {
        RESET,
        BLUE,
        RED
    }

    void write(String content);

    // Set color on supported devices
    void setColor(Color color);
}