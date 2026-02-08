package model;

public enum Color {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[33m"),
    BLUE("\u001B[34m"),
    MAGENTA("\u001B[35m"),
    CYAN("\u001B[36m"),

    BRIGHT_RED("\u001B[91m"),
    BRIGHT_GREEN("\u001B[92m"),
    BRIGHT_YELLOW("\u001B[93m"),
    BRIGHT_BLUE("\u001B[94m");

    private static final String RESET = "\u001B[0m";
    private final String ansi;

    Color(String ansi) {
        this.ansi = ansi;
    }

    public String paint(String symbol) {
        return ansi + symbol + RESET;
    }

    public String paintHead(String symbol) {
        return ansi + "\u001B[1m" + symbol + RESET;
    }

}
