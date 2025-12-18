package kelompok.dua.maven;

import kelompok.dua.maven.generator.TypeScriptGenerator;
import kelompok.dua.maven.model.XtumlModel;
import kelompok.dua.maven.parser.XtumlModelParser;
import kelompok.dua.maven.parser.XtumlParseException;
import kelompok.dua.maven.util.TerminalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Callable;

/**
 * CLI Application untuk mengkompilasi model xTUML dari JSON ke TypeScript
 */
@Command(name = "xtuml-ts-compiler", description = "Kompiler untuk mengkonversi model xTUML dari JSON ke TypeScript", version = "1.1.0", mixinStandardHelpOptions = true)
public class XtumlCompilerCli implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(XtumlCompilerCli.class);

    @Parameters(index = "0", description = "Path ke file JSON input yang berisi model xTUML")
    private String inputFile;

    @Option(names = { "-o",
            "--output" }, description = "Direktori output untuk file TypeScript yang dihasilkan (default: ./output)", defaultValue = "./output")
    private String outputDir;

    @Option(names = { "-d", "--debug" }, description = "Enable debug logging")
    private boolean debug;

    @Option(names = { "-f", "--force" }, description = "Force overwrite jika direktori output sudah ada")
    private boolean force;

    @Option(names = { "--clean" }, description = "Bersihkan direktori output sebelum generate")
    private boolean clean;

    @Option(names = { "--no-color" }, description = "Disable colored output")
    private boolean noColor;

    public static void main(String[] args) {
        // Set root logger to OFF by default
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.OFF);

        int exitCode = new CommandLine(new XtumlCompilerCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        Instant startTime = Instant.now();

        try {
            // Initialize terminal colors
            if (!noColor) {
                TerminalUtils.enableColors();
            }

            // Print beautiful banner
            TerminalUtils.printBanner("xTUML TypeScript Compiler", "v1.1.0");

            // Setup logging level
            if (debug) {
                ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory
                        .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
                rootLogger.setLevel(Level.DEBUG);
                TerminalUtils.printInfo("Debug mode enabled");
            }

            TerminalUtils.printInfo("Input: " + inputFile);
            TerminalUtils.printInfo("Output: " + outputDir);
            TerminalUtils.printSeparator();

            // Validasi input file
            TerminalUtils.printInfo("Validating input file...");
            Path inputPath = Paths.get(inputFile);
            if (!Files.exists(inputPath)) {
                TerminalUtils.printError("File input tidak ditemukan: " + inputFile);
                return 1;
            }

            if (!Files.isRegularFile(inputPath)) {
                TerminalUtils.printError("Path input bukan file: " + inputFile);
                return 1;
            }
            TerminalUtils.printSuccess("Input file validated");

            // Setup output directory
            TerminalUtils.printInfo("Setting up output directory...");
            Path outputPath = Paths.get(outputDir);
            if (Files.exists(outputPath)) {
                if (!Files.isDirectory(outputPath)) {
                    TerminalUtils.printError("Path output bukan direktori: " + outputDir);
                    return 1;
                }

                if (hasFiles(outputPath)) {
                    TerminalUtils.printWarning("Cleaning output directory...");
                    deleteDirectoryContents(outputPath);
                    TerminalUtils.printSuccess("Output directory cleaned");
                }
            }
            TerminalUtils.printSuccess("Output directory ready");

            // Parse model
            TerminalUtils.printInfo("Parsing xTUML model...");
            TerminalUtils.showProgress("Parsing", 0);

            XtumlModelParser parser = new XtumlModelParser();
            XtumlModel model = parser.parseFromFile(inputPath);

            TerminalUtils.showProgress("Parsing", 100);
            TerminalUtils.printSuccess("Model successfully parsed");
            TerminalUtils.printInfo("  System: " + model.getSystemName() + " v" + model.getVersion());
            TerminalUtils.printInfo("  Domains: " + model.getDomains().size());

            // Generate TypeScript
            TerminalUtils.printSeparator();
            TerminalUtils.printInfo("Generating TypeScript files...");

            TypeScriptGenerator generator = new TypeScriptGenerator(model, outputPath);
            generator.generateAll();

            TerminalUtils.printSuccess("TypeScript generation completed!");

            // Calculate execution time
            Duration duration = Duration.between(startTime, Instant.now());

            // Print beautiful summary
            printBeautifulSummary(model, outputPath, duration);

            return 0;

        } catch (XtumlParseException e) {
            TerminalUtils.printError("❌ Parse Error: " + e.getMessage());
            if (debug && e.getCause() != null) {
                TerminalUtils.printError("Cause: " + e.getCause().getMessage());
            }
            return 2;

        } catch (IOException e) {
            TerminalUtils.printError("❌ I/O Error: " + e.getMessage());
            if (debug) {
                TerminalUtils.printError("Details: " + e.toString());
            }
            return 3;

        } catch (Exception e) {
            TerminalUtils.printError("❌ Unexpected Error: " + e.getMessage());
            if (debug) {
                TerminalUtils.printError("Details: " + e.toString());
            }
            return 4;
        }
    }

    /**
     * Check apakah direktori memiliki file
     */
    private boolean hasFiles(Path directory) throws IOException {
        return Files.list(directory).findAny().isPresent();
    }

    /**
     * Hapus semua konten direktori
     */
    private void deleteDirectoryContents(Path directory) throws IOException {
        Files.walk(directory)
                .sorted((a, b) -> b.compareTo(a)) // Sort descending untuk hapus file dulu baru direktori
                .filter(path -> !path.equals(directory)) // Jangan hapus direktori root
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        logger.warn("Gagal menghapus: {}", path);
                    }
                });
    }

    /**
     * Print beautiful summary hasil kompilasi
     */
    private void printBeautifulSummary(XtumlModel model, Path outputPath, Duration duration) throws IOException {
        TerminalUtils.printSeparator();
        TerminalUtils.printHeader("COMPILATION SUMMARY");

        // System info
        TerminalUtils.printInfo("System: " + model.getSystemName() + " v" + model.getVersion());

        int totalClasses = 0;
        int totalRelationships = 0;
        int totalStateMachines = 0;

        for (var domain : model.getDomains()) {
            int classCount = domain.getClasses() != null ? domain.getClasses().size() : 0;
            int relCount = domain.getRelationships() != null ? domain.getRelationships().size() : 0;
            int smCount = domain.getClasses() != null
                    ? (int) domain.getClasses().stream().filter(c -> c.getStateMachine() != null).count()
                    : 0;

            totalClasses += classCount;
            totalRelationships += relCount;
            totalStateMachines += smCount;

            TerminalUtils.printInfo("Domain '" + domain.getName() + "': " +
                    classCount + " classes, " +
                    relCount + " relationships, " +
                    smCount + " state machines");
        }

        // Count generated files
        long fileCount = Files.walk(outputPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".ts"))
                .count();

        TerminalUtils.printSeparator();
        TerminalUtils.printSuccess("GENERATION COMPLETE");
        TerminalUtils.printInfo("Total Statistics:");
        TerminalUtils.printInfo("   - Domains: " + model.getDomains().size());
        TerminalUtils.printInfo("   - Classes: " + totalClasses);
        TerminalUtils.printInfo("   - Relationships: " + totalRelationships);
        TerminalUtils.printInfo("   - State Machines: " + totalStateMachines);
        TerminalUtils.printInfo("   - Generated Files: " + fileCount);
        TerminalUtils.printInfo("Execution Time: " + formatDuration(duration));
        TerminalUtils.printInfo("Output Location: " + outputPath.toAbsolutePath());

        TerminalUtils.printSeparator();
        TerminalUtils.printSuccess("Ready to use! Import your TypeScript modules from the output directory.");
    }

    /**
     * Format duration menjadi string yang readable
     */
    private String formatDuration(Duration duration) {
        long millis = duration.toMillis();
        if (millis < 1000) {
            return millis + "ms";
        } else {
            return String.format("%.2fs", millis / 1000.0);
        }
    }
}