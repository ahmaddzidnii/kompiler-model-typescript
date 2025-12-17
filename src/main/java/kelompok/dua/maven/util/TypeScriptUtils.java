package kelompok.dua.maven.util;

/**
 * Utility class untuk konversi tipe data dan nama
 */
public class TypeScriptUtils {

    /**
     * Konversi tipe data dari xTUML ke TypeScript
     * Tipe data inti: boolean, string, integer, real, date, timestamp, id
     */
    public static String convertDataType(String xtumlType) {
        if (xtumlType == null) {
            return "any";
        }

        switch (xtumlType.toLowerCase()) {
            // Tipe data inti
            case "boolean":
                return "boolean";
            case "string":
                return "string";
            case "integer":
                return "number";
            case "real":
                return "number";
            case "date":
                return "Date";
            case "timestamp":
                return "Date";
            case "id":
                return "string"; // ID as string in TypeScript

            // Alias untuk backward compatibility
            case "int":
                return "number"; // alias untuk integer
            case "float":
            case "double":
                return "number"; // alias untuk real
            case "bool":
                return "boolean"; // alias untuk boolean
            case "uuid":
                return "string"; // alias untuk id
            case "datetime":
                return "Date"; // alias untuk timestamp
            case "state":
                return "string"; // State as string literal type

            default:
                return "any";
        }
    }

    /**
     * Konversi nama class ke format TypeScript (PascalCase)
     */
    public static String toPascalCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Remove special characters and split by space, underscore, or hyphen
        String[] words = input.split("[\\s_-]+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase());
            }
        }

        return result.toString();
    }

    /**
     * Konversi nama property ke format TypeScript (camelCase)
     */
    public static String toCamelCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String pascalCase = toPascalCase(input);
        if (pascalCase.length() > 0) {
            return Character.toLowerCase(pascalCase.charAt(0)) + pascalCase.substring(1);
        }

        return pascalCase;
    }

    /**
     * Konversi nama interface ke format TypeScript dengan prefix 'I'
     */
    public static String toInterfaceName(String className) {
        return "I" + toPascalCase(className);
    }

    /**
     * Generate nama file TypeScript dari nama class
     */
    public static String toFileName(String className) {
        if (className == null || className.isEmpty()) {
            return "unknown";
        }

        // Convert PascalCase to kebab-case
        String result = className.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
        return result + ".ts";
    }

    /**
     * Escape string untuk digunakan dalam TypeScript string literal
     */
    public static String escapeString(String input) {
        if (input == null) {
            return "null";
        }

        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Generate TypeScript comment dari description
     */
    public static String generateComment(String description) {
        if (description == null || description.trim().isEmpty()) {
            return "";
        }

        StringBuilder comment = new StringBuilder();
        comment.append("/**\n");

        // Split description into lines for proper formatting
        String[] lines = description.split("\n");
        for (String line : lines) {
            comment.append(" * ").append(line.trim()).append("\n");
        }

        comment.append(" */\n");
        return comment.toString();
    }

    /**
     * Check apakah nama merupakan reserved keyword di TypeScript
     */
    public static boolean isReservedKeyword(String name) {
        if (name == null) {
            return false;
        }

        String[] keywords = {
                "abstract", "any", "as", "asserts", "boolean", "break", "case", "catch", "class",
                "const", "continue", "debugger", "declare", "default", "delete", "do", "else",
                "enum", "export", "extends", "false", "finally", "for", "from", "function",
                "get", "if", "implements", "import", "in", "instanceof", "interface", "is",
                "keyof", "let", "module", "namespace", "never", "new", "null", "number",
                "object", "package", "private", "protected", "public", "readonly", "require",
                "return", "set", "static", "string", "super", "switch", "symbol", "this",
                "throw", "true", "try", "type", "typeof", "undefined", "unique", "unknown",
                "var", "void", "while", "with", "yield"
        };

        for (String keyword : keywords) {
            if (keyword.equals(name.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Escape nama jika merupakan reserved keyword
     */
    public static String escapeName(String name) {
        if (isReservedKeyword(name)) {
            return name + "_";
        }
        return name;
    }
}