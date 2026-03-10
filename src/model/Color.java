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

    /**
     * Convertit la couleur en java.awt.Color pour l'interface graphique
     */
    public java.awt.Color toAWT() {
        return switch (this) {
            case RED -> java.awt.Color.RED;
            case BLUE -> java.awt.Color.BLUE;
            case GREEN -> java.awt.Color.GREEN;
            case YELLOW -> java.awt.Color.YELLOW;
            case MAGENTA -> java.awt.Color.MAGENTA;
            case CYAN -> java.awt.Color.CYAN;
            case BRIGHT_RED -> new java.awt.Color(255, 100, 100);
            case BRIGHT_GREEN -> new java.awt.Color(100, 255, 100);
            case BRIGHT_YELLOW -> new java.awt.Color(255, 255, 100);
            case BRIGHT_BLUE -> new java.awt.Color(100, 150, 255);
        };
    }

    /**
     * Retourne un emoji représentant la couleur
     */
    public String getEmoji() {
        return switch (this) {
            case RED -> "🔴";
            case BLUE -> "🔵";
            case GREEN -> "🟢";
            case YELLOW -> "🟡";
            case MAGENTA -> "🟣";
            case CYAN -> "🔵";
            case BRIGHT_RED -> "🔴";
            case BRIGHT_GREEN -> "🟢";
            case BRIGHT_YELLOW -> "🟡";
            case BRIGHT_BLUE -> "🔵";
        };
    }
}