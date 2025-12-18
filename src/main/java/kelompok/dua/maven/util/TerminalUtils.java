package kelompok.dua.maven.util;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class untuk formatting terminal output dengan colors dan styling
 */
public class TerminalUtils {

    static {
        // Initialize ANSI console untuk cross-platform support
        AnsiConsole.systemInstall();
    }

    // Color constants
    public static final String RESET = Ansi.ansi().reset().toString();
    public static final String BOLD = Ansi.ansi().bold().toString();

    // Colors
    public static final String RED = Ansi.ansi().fgRed().toString();
    public static final String GREEN = Ansi.ansi().fgGreen().toString();
    public static final String YELLOW = Ansi.ansi().fgYellow().toString();
    public static final String BLUE = Ansi.ansi().fgBlue().toString();
    public static final String MAGENTA = Ansi.ansi().fgMagenta().toString();
    public static final String CYAN = Ansi.ansi().fgCyan().toString();
    public static final String WHITE = Ansi.ansi().fgDefault().toString();

    // Bright colors
    public static final String BRIGHT_GREEN = Ansi.ansi().fgBrightGreen().toString();
    public static final String BRIGHT_BLUE = Ansi.ansi().fgBrightBlue().toString();
    public static final String BRIGHT_YELLOW = Ansi.ansi().fgBrightYellow().toString();
    public static final String BRIGHT_CYAN = Ansi.ansi().fgBrightCyan().toString();
    public static final String BRIGHT_MAGENTA = Ansi.ansi().fgBrightMagenta().toString();
    public static final String BRIGHT_WHITE = Ansi.ansi().fgBrightDefault().toString();

    /**
     * Print success message dengan green color
     */
    public static void printSuccess(String message) {
        System.out.println(GREEN + "[OK] " + message + RESET);
    }

    /**
     * Print error message dengan red color
     */
    public static void printError(String message) {
        System.out.println(RED + "[ERROR] " + message + RESET);
    }

    /**
     * Print warning message dengan yellow color
     */
    public static void printWarning(String message) {
        System.out.println(YELLOW + "[WARNING] " + message + RESET);
    }

    /**
     * Print info message dengan blue color
     */
    public static void printInfo(String message) {
        System.out.println(BLUE + "[INFO] " + message + RESET);
    }

    /**
     * Print header dengan styling
     */
    public static void printHeader(String title) {
        String border = "═".repeat(60);
        System.out.println(BRIGHT_CYAN + border + RESET);
        System.out.println(BRIGHT_CYAN + "  " + BOLD + title + RESET + BRIGHT_CYAN + RESET);
        System.out.println(BRIGHT_CYAN + border + RESET);
    }

    /**
     * Print subheader dengan styling
     */
    public static void printSubHeader(String title) {
        System.out.println(BRIGHT_BLUE + "\n📂 " + BOLD + title + RESET);
        System.out.println(BRIGHT_BLUE + "─".repeat(title.length() + 3) + RESET);
    }

    /**
     * Print progress dengan percentage
     */
    public static void printProgress(String message, int current, int total) {
        int percentage = (current * 100) / total;
        String progressBar = createProgressBar(percentage);
        System.out.print("\r" + BRIGHT_YELLOW + "🔄 " + message + " " + progressBar + " " + percentage + "%" + RESET);
        if (current == total) {
            System.out.println(); // New line when complete
        }
    }

    /**
     * Print file generation message
     */
    public static void printFileGenerated(String fileName, String type) {
        String icon = getFileIcon(type);
        System.out.println(GREEN + "  " + icon + " " + fileName + RESET + CYAN + " (" + type + ")" + RESET);
    }

    /**
     * Print summary dengan styling
     */
    public static void printSummary(String title, String content) {
        System.out.println(BRIGHT_MAGENTA + "📊 " + BOLD + title + ": " + RESET + BRIGHT_WHITE + content + RESET);
    }

    /**
     * Print banner dengan ASCII art
     */
    public static void printBanner() {
        System.out.println(BRIGHT_CYAN + """
                ╔═══════════════════════════════════════════════════════════╗
                ║                                                           ║
                ║    xTUML TypeScript Compiler v1.1.0                      ║
                ║                                                           ║
                ║    Advanced Model-to-Code Generation                     ║
                ║    Action Language Support                               ║
                ║    Type-Safe TypeScript Output                           ║
                ║                                                           ║
                ╚═══════════════════════════════════════════════════════════╝
                """ + RESET);
    }

    /**
     * Create progress bar visual
     */
    private static String createProgressBar(int percentage) {
        int barLength = 20;
        int filled = (percentage * barLength) / 100;
        StringBuilder bar = new StringBuilder("[");

        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append("]");
        return bar.toString();
    }

    /**
     * Get icon based on file type
     */
    private static String getFileIcon(String type) {
        return switch (type.toLowerCase()) {
            case "interface" -> "[I]";
            case "class" -> "[C]";
            case "abstract class" -> "[A]";
            case "association class" -> "[AC]";
            case "index" -> "[IDX]";
            default -> "[F]";
        };
    }

    /**
     * Get current timestamp untuk header comments
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Enable terminal colors (Windows compatibility)
     */
    public static void enableColors() {
        AnsiConsole.systemInstall();
    }

    /**
     * Print separator line
     */
    public static void printSeparator() {
        System.out.println(CYAN + "─".repeat(60) + RESET);
    }

    /**
     * Show progress indicator
     */
    public static void showProgress(String task, int percentage) {
        String bar = createProgressBar(percentage);
        // Clear line first, then print progress
        System.out.print("\r\033[K" + BLUE + task + ": " + bar + " " + percentage + "%" + RESET);
        System.out.flush();
        if (percentage >= 100) {
            System.out.println(); // New line when complete
        }
    }

    /**
     * Print beautiful banner
     */
    public static void printBanner(String title, String version) {
        String border = "═".repeat(60);
        System.out.println();
        System.out.println(BRIGHT_CYAN + border + RESET);
        System.out.println(BRIGHT_CYAN + "║" + RESET +
                centerText(title + " " + version, 58) +
                BRIGHT_CYAN + "║" + RESET);
        System.out.println(BRIGHT_CYAN + "║" + RESET +
                centerText("xTUML Model Compiler", 58) +
                BRIGHT_CYAN + "║" + RESET);
        System.out.println(BRIGHT_CYAN + border + RESET);
        System.out.println();
    }

    /**
     * Center text within given width
     */
    private static String centerText(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        int padding = (width - text.length()) / 2;
        String leftPadding = " ".repeat(padding);
        String rightPadding = " ".repeat(width - text.length() - padding);
        return leftPadding + text + rightPadding;
    }

    /**
     * Cleanup ANSI console pada shutdown
     */
    public static void cleanup() {
        AnsiConsole.systemUninstall();
    }
}