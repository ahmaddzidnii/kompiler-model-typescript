package kelompok.dua.maven;

import kelompok.dua.maven.generator.TypeScriptGenerator;
import kelompok.dua.maven.model.XtumlModel;
import kelompok.dua.maven.parser.XtumlModelParser;
import kelompok.dua.maven.parser.XtumlParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * CLI Application untuk mengkompilasi model xTUML dari JSON ke TypeScript
 */
@Command(name = "xtuml-ts-compiler", description = "Kompiler untuk mengkonversi model xTUML dari JSON ke TypeScript", version = "1.0.0", mixinStandardHelpOptions = true)
public class XtumlCompilerCli implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(XtumlCompilerCli.class);

    @Parameters(index = "0", description = "Path ke file JSON input yang berisi model xTUML")
    private String inputFile;

    @Option(names = { "-o",
            "--output" }, description = "Direktori output untuk file TypeScript yang dihasilkan (default: ./output)", defaultValue = "./output")
    private String outputDir;

    @Option(names = { "-v", "--verbose" }, description = "Enable verbose logging")
    private boolean verbose;

    @Option(names = { "-f", "--force" }, description = "Force overwrite jika direktori output sudah ada")
    private boolean force;

    @Option(names = { "--clean" }, description = "Bersihkan direktori output sebelum generate")
    private boolean clean;

    public static void main(String[] args) {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");

        int exitCode = new CommandLine(new XtumlCompilerCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        try {
            // Setup logging level
            if (verbose) {
                System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "DEBUG");
            }

            logger.info("xTUML TypeScript Compiler v1.0.0");
            logger.info("Input: {}", inputFile);
            logger.info("Output: {}", outputDir);

            // Validasi input file
            Path inputPath = Paths.get(inputFile);
            if (!Files.exists(inputPath)) {
                logger.error("File input tidak ditemukan: {}", inputFile);
                return 1;
            }

            if (!Files.isRegularFile(inputPath)) {
                logger.error("Path input bukan file: {}", inputFile);
                return 1;
            }

            // Setup output directory
            Path outputPath = Paths.get(outputDir);
            if (Files.exists(outputPath)) {
                if (!Files.isDirectory(outputPath)) {
                    logger.error("Path output bukan direktori: {}", outputDir);
                    return 1;
                }

                if (!force && hasFiles(outputPath)) {
                    logger.error("Direktori output tidak kosong. Gunakan --force untuk overwrite: {}", outputDir);
                    return 1;
                }

                if (clean) {
                    logger.info("Membersihkan direktori output...");
                    deleteDirectoryContents(outputPath);
                }
            }

            // Parse model
            logger.info("Parsing model xTUML...");
            XtumlModelParser parser = new XtumlModelParser();
            XtumlModel model = parser.parseFromFile(inputPath);

            logger.info("Model berhasil diparsing: {} v{}", model.getSystemName(), model.getVersion());
            logger.info("Jumlah domain: {}", model.getDomains().size());

            // Generate TypeScript
            logger.info("Generating TypeScript files...");
            TypeScriptGenerator generator = new TypeScriptGenerator(model, outputPath);
            generator.generateAll();

            logger.info("Kompilasi selesai! File TypeScript tersimpan di: {}", outputPath.toAbsolutePath());

            // Print summary
            printSummary(model, outputPath);

            return 0;

        } catch (XtumlParseException e) {
            logger.error("Error parsing model: {}", e.getMessage());
            if (verbose && e.getCause() != null) {
                logger.error("Cause: ", e.getCause());
            }
            return 2;

        } catch (IOException e) {
            logger.error("Error I/O: {}", e.getMessage());
            if (verbose) {
                logger.error("Details: ", e);
            }
            return 3;

        } catch (Exception e) {
            logger.error("Error tidak terduga: {}", e.getMessage());
            if (verbose) {
                logger.error("Details: ", e);
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
     * Print summary hasil kompilasi
     */
    private void printSummary(XtumlModel model, Path outputPath) throws IOException {
        logger.info("=== SUMMARY ===");
        logger.info("System: {} v{}", model.getSystemName(), model.getVersion());

        int totalClasses = 0;
        int totalRelationships = 0;

        for (var domain : model.getDomains()) {
            int classCount = domain.getClasses() != null ? domain.getClasses().size() : 0;
            int relCount = domain.getRelationships() != null ? domain.getRelationships().size() : 0;

            totalClasses += classCount;
            totalRelationships += relCount;

            logger.info("Domain '{}': {} classes, {} relationships",
                    domain.getName(), classCount, relCount);
        }

        logger.info("Total: {} classes, {} relationships", totalClasses, totalRelationships);

        // Count generated files
        long fileCount = Files.walk(outputPath)
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".ts"))
                .count();

        logger.info("Generated {} TypeScript files in: {}", fileCount, outputPath.toAbsolutePath());
        logger.info("===============");
    }
}