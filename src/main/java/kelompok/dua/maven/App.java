package kelompok.dua.maven;

/**
 * Legacy App class - Gunakan XtumlCompilerCli untuk CLI application
 */
public class App {
    public static void main(String[] args) {
        System.out.println("xTUML TypeScript Compiler");
        System.out.println("Gunakan XtumlCompilerCli untuk menjalankan kompiler:");
        System.out.println("java -jar kompiler-model-typescript.jar <input.json> [options]");
        System.out.println("atau: java kelompok.dua.maven.XtumlCompilerCli <input.json> [options]");

        // Redirect ke XtumlCompilerCli
        XtumlCompilerCli.main(args);
    }
}
