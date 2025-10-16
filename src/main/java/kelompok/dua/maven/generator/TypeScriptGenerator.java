package kelompok.dua.maven.generator;

import kelompok.dua.maven.model.*;
import kelompok.dua.maven.util.TypeScriptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generator utama untuk mengkonversi xTUML model ke TypeScript
 */
public class TypeScriptGenerator {
    private static final Logger logger = LoggerFactory.getLogger(TypeScriptGenerator.class);

    private final Path outputDirectory;
    private final XtumlModel model;
    private final Map<String, ClassDefinition> classMap;

    public TypeScriptGenerator(XtumlModel model, Path outputDirectory) {
        this.model = model;
        this.outputDirectory = outputDirectory;
        this.classMap = buildClassMap();
    }

    /**
     * Generate semua file TypeScript dari model
     */
    public void generateAll() throws IOException {
        logger.info("Mulai generate TypeScript files ke: {}", outputDirectory);

        // Buat direktori output jika belum ada
        Files.createDirectories(outputDirectory);

        // Generate file untuk setiap domain
        for (Domain domain : model.getDomains()) {
            generateDomain(domain);
        }

        // Generate index file
        generateIndexFile();

        logger.info("Selesai generate TypeScript files");
    }

    /**
     * Generate files untuk satu domain
     */
    private void generateDomain(Domain domain) throws IOException {
        logger.info("Generate domain: {}", domain.getName());

        Path domainDir = outputDirectory.resolve(TypeScriptUtils.toCamelCase(domain.getName()));
        Files.createDirectories(domainDir);

        // Generate interfaces untuk semua classes
        for (ClassDefinition classDef : domain.getClasses()) {
            generateInterface(classDef, domainDir);
            generateClass(classDef, domainDir);
        }

        // Generate relationship interfaces
        if (domain.getRelationships() != null) {
            for (Relationship relationship : domain.getRelationships()) {
                generateRelationship(relationship, domainDir);
            }
        }

        // Generate domain index file
        generateDomainIndex(domain, domainDir);
    }

    /**
     * Generate interface untuk class
     */
    private void generateInterface(ClassDefinition classDef, Path domainDir) throws IOException {
        String interfaceName = TypeScriptUtils.toInterfaceName(classDef.getName());
        String fileName = TypeScriptUtils.toFileName(interfaceName);
        Path filePath = domainDir.resolve(fileName);

        StringBuilder content = new StringBuilder();

        // Import parent interface if has inheritance
        if (classDef.getInheritsFrom() != null) {
            String parentInterface = TypeScriptUtils.toInterfaceName(classDef.getInheritsFrom());
            String parentFileName = TypeScriptUtils.toFileName(parentInterface).replace(".ts", "");
            content.append("import { ").append(parentInterface).append(" } from './").append(parentFileName)
                    .append("';\n\n");
        }

        // Generate comment
        if (classDef.getDescription() != null) {
            content.append(TypeScriptUtils.generateComment(classDef.getDescription()));
        }

        // Generate interface declaration
        content.append("export interface ").append(interfaceName);

        // Handle inheritance
        if (classDef.getInheritsFrom() != null) {
            String parentInterface = TypeScriptUtils.toInterfaceName(classDef.getInheritsFrom());
            content.append(" extends ").append(parentInterface);
        }

        content.append(" {\n");

        // Generate properties
        if (classDef.getAttributes() != null) {
            for (Attribute attr : classDef.getAttributes()) {
                generateInterfaceProperty(attr, content);
            }
        }

        // Add state property if has state machine
        if (classDef.getStateMachine() != null) {
            generateStateProperty(classDef, content);
        }

        content.append("}\n");

        // Write to file
        Files.writeString(filePath, content.toString(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        logger.debug("Generated interface: {}", filePath);
    }

    /**
     * Generate class implementation
     */
    private void generateClass(ClassDefinition classDef, Path domainDir) throws IOException {
        String className = TypeScriptUtils.toPascalCase(classDef.getName());
        String interfaceName = TypeScriptUtils.toInterfaceName(classDef.getName());
        String fileName = TypeScriptUtils.toFileName(className);
        Path filePath = domainDir.resolve(fileName);

        StringBuilder content = new StringBuilder();

        // Import interface
        String interfaceFileName = TypeScriptUtils.toFileName(interfaceName);
        content.append("import { ").append(interfaceName).append(" } from './")
                .append(interfaceFileName.replace(".ts", "")).append("';\n");

        // Import parent class if has inheritance
        if (classDef.getInheritsFrom() != null) {
            String parentClass = TypeScriptUtils.toPascalCase(classDef.getInheritsFrom());
            String parentFileName = TypeScriptUtils.toFileName(parentClass).replace(".ts", "");
            content.append("import { ").append(parentClass).append(" } from './").append(parentFileName).append("';\n");
        }

        content.append("\n");

        // Generate comment
        if (classDef.getDescription() != null) {
            content.append(TypeScriptUtils.generateComment(classDef.getDescription()));
        }

        // Generate class declaration
        if (Boolean.TRUE.equals(classDef.getIsAbstract())) {
            content.append("export abstract class ");
        } else {
            content.append("export class ");
        }

        content.append(className);

        // Handle inheritance
        if (classDef.getInheritsFrom() != null) {
            String parentClass = TypeScriptUtils.toPascalCase(classDef.getInheritsFrom());
            content.append(" extends ").append(parentClass);
        }

        content.append(" implements ").append(interfaceName).append(" {\n");

        // Generate properties (only if not inherited)
        if (classDef.getAttributes() != null) {
            for (Attribute attr : classDef.getAttributes()) {
                generateClassProperty(attr, content);
            }
        }

        // Add state property if has state machine
        if (classDef.getStateMachine() != null) {
            generateStateClassProperty(classDef, content);
        }

        // Generate constructor
        generateConstructor(classDef, content);

        // Generate state machine methods if applicable
        if (classDef.getStateMachine() != null) {
            generateStateMachineMethods(classDef, content);
        }

        content.append("}\n");

        // Write to file
        Files.writeString(filePath, content.toString(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        logger.debug("Generated class: {}", filePath);
    }

    /**
     * Generate property untuk interface
     */
    private void generateInterfaceProperty(Attribute attr, StringBuilder content) {
        String propName = TypeScriptUtils.toCamelCase(attr.getName());
        String propType = TypeScriptUtils.convertDataType(attr.getDataType());

        content.append("  ").append(propName);

        // Optional property jika bukan naming attribute
        if (!"naming".equals(attr.getAttributeType())) {
            content.append("?");
        }

        content.append(": ").append(propType).append(";\n");
    }

    /**
     * Generate state property untuk interface
     */
    private void generateStateProperty(ClassDefinition classDef, StringBuilder content) {
        StateMachine sm = classDef.getStateMachine();
        if (sm.getStates() != null && !sm.getStates().isEmpty()) {
            String stateUnion = sm.getStates().stream()
                    .map(State::getName)
                    .map(name -> "'" + name + "'")
                    .collect(Collectors.joining(" | "));

            content.append("  state: ").append(stateUnion).append(";\n");
        }
    }

    /**
     * Generate property untuk class
     */
    private void generateClassProperty(Attribute attr, StringBuilder content) {
        String propName = TypeScriptUtils.toCamelCase(attr.getName());
        String propType = TypeScriptUtils.convertDataType(attr.getDataType());

        content.append("  ");

        // Access modifier
        if ("naming".equals(attr.getAttributeType())) {
            content.append("public ");
        } else {
            content.append("public ");
        }

        content.append(propName);

        // Optional property
        if (!"naming".equals(attr.getAttributeType())) {
            content.append("?");
        }

        content.append(": ").append(propType);

        // Default value
        if (attr.getDefaultValue() != null) {
            content.append(" = ");
            if ("string".equals(propType)) {
                content.append("'").append(TypeScriptUtils.escapeString(attr.getDefaultValue())).append("'");
            } else {
                content.append(attr.getDefaultValue());
            }
        }

        content.append(";\n");
    }

    /**
     * Generate state property untuk class
     */
    private void generateStateClassProperty(ClassDefinition classDef, StringBuilder content) {
        StateMachine sm = classDef.getStateMachine();
        if (sm.getStates() != null && !sm.getStates().isEmpty()) {
            String stateUnion = sm.getStates().stream()
                    .map(State::getName)
                    .map(name -> "'" + name + "'")
                    .collect(Collectors.joining(" | "));

            content.append("  public state: ").append(stateUnion);

            if (sm.getInitialState() != null) {
                content.append(" = '").append(sm.getInitialState()).append("'");
            }

            content.append(";\n");
        }
    }

    /**
     * Generate constructor
     */
    private void generateConstructor(ClassDefinition classDef, StringBuilder content) {
        content.append("\n  constructor(");

        // Constructor parameters (only naming attributes)
        List<Attribute> namingAttrs = classDef.getAttributes() != null ? classDef.getAttributes().stream()
                .filter(attr -> "naming".equals(attr.getAttributeType()))
                .collect(Collectors.toList()) : new ArrayList<>();

        // Get parent naming attributes if has inheritance
        List<Attribute> parentNamingAttrs = new ArrayList<>();
        if (classDef.getInheritsFrom() != null) {
            ClassDefinition parentClass = classMap.get(classDef.getInheritsFrom());
            if (parentClass != null && parentClass.getAttributes() != null) {
                parentNamingAttrs = parentClass.getAttributes().stream()
                        .filter(attr -> "naming".equals(attr.getAttributeType()))
                        .collect(Collectors.toList());
            }
        }

        // All naming attributes (parent + current)
        List<Attribute> allNamingAttrs = new ArrayList<>(parentNamingAttrs);
        allNamingAttrs.addAll(namingAttrs);

        for (int i = 0; i < allNamingAttrs.size(); i++) {
            Attribute attr = allNamingAttrs.get(i);
            String propName = TypeScriptUtils.toCamelCase(attr.getName());
            String propType = TypeScriptUtils.convertDataType(attr.getDataType());

            if (i > 0)
                content.append(", ");
            content.append(propName).append(": ").append(propType);
        }

        content.append(") {\n");

        // Call super if has parent
        if (classDef.getInheritsFrom() != null && !parentNamingAttrs.isEmpty()) {
            content.append("    super(");
            for (int i = 0; i < parentNamingAttrs.size(); i++) {
                Attribute attr = parentNamingAttrs.get(i);
                String propName = TypeScriptUtils.toCamelCase(attr.getName());
                if (i > 0)
                    content.append(", ");
                content.append(propName);
            }
            content.append(");\n");
        }

        // Assign current class naming attributes
        for (Attribute attr : namingAttrs) {
            String propName = TypeScriptUtils.toCamelCase(attr.getName());
            content.append("    this.").append(propName).append(" = ").append(propName).append(";\n");
        }

        content.append("  }\n");
    }

    /**
     * Generate state machine methods
     */
    private void generateStateMachineMethods(ClassDefinition classDef, StringBuilder content) {
        StateMachine sm = classDef.getStateMachine();
        if (sm.getTransitions() != null) {
            content.append("\n  // State Machine Methods\n");

            for (Transition transition : sm.getTransitions()) {
                String methodName = TypeScriptUtils.toCamelCase(transition.getEvent());
                content.append("  public ").append(methodName).append("(): boolean {\n");
                content.append("    if (this.state === '").append(transition.getFromState()).append("') {\n");
                content.append("      this.state = '").append(transition.getToState()).append("';\n");

                // Add actions if any
                if (transition.getActions() != null) {
                    for (Action action : transition.getActions()) {
                        if ("log".equals(action.getType())) {
                            // Replace template variables in message
                            String message = action.getMessage();
                            if (message != null) {
                                // Replace ${self.NIM} with this.nim, etc.
                                if (message.contains("${self.NIM}")) {
                                    message = message.replace("${self.NIM}", "`+ this.nim +`");
                                }
                                // Wrap with template literal if contains template replacement
                                if (message.contains("`+ this.")) {
                                    message = "`" + message.replace("'", "\\'") + "`";
                                } else {
                                    message = "'" + TypeScriptUtils.escapeString(message) + "'";
                                }
                            } else {
                                message = "''";
                            }
                            content.append("      console.log(").append(message).append(");\n");
                        }
                    }
                }

                content.append("      return true;\n");
                content.append("    }\n");
                content.append("    return false;\n");
                content.append("  }\n\n");
            }
        }
    }

    /**
     * Generate relationship interface
     */
    private void generateRelationship(Relationship relationship, Path domainDir) throws IOException {
        if (relationship.getAssociationClass() != null) {
            AssociationClass assocClass = relationship.getAssociationClass();

            // Treat association class as regular class
            ClassDefinition classDef = new ClassDefinition();
            classDef.setEntityType("association_class");
            classDef.setName(assocClass.getName());
            classDef.setKeyLetter(assocClass.getKeyLetter());
            classDef.setAttributes(assocClass.getAttributes());
            classDef.setDescription("Association class for relationship " + relationship.getRelationshipId());

            generateInterface(classDef, domainDir);
            generateClass(classDef, domainDir);
        }
    }

    /**
     * Generate index file untuk domain
     */
    private void generateDomainIndex(Domain domain, Path domainDir) throws IOException {
        Path indexPath = domainDir.resolve("index.ts");
        StringBuilder content = new StringBuilder();

        content.append("// Auto-generated index file for domain: ").append(domain.getName()).append("\n\n");

        // Export all interfaces and classes
        for (ClassDefinition classDef : domain.getClasses()) {
            String interfaceName = TypeScriptUtils.toInterfaceName(classDef.getName());
            String className = TypeScriptUtils.toPascalCase(classDef.getName());
            String interfaceFile = TypeScriptUtils.toFileName(interfaceName).replace(".ts", "");
            String classFile = TypeScriptUtils.toFileName(className).replace(".ts", "");

            content.append("export { ").append(interfaceName).append(" } from './").append(interfaceFile)
                    .append("';\n");
            content.append("export { ").append(className).append(" } from './").append(classFile).append("';\n");
        }

        // Export association classes
        if (domain.getRelationships() != null) {
            for (Relationship relationship : domain.getRelationships()) {
                if (relationship.getAssociationClass() != null) {
                    String className = TypeScriptUtils.toPascalCase(relationship.getAssociationClass().getName());
                    String interfaceName = TypeScriptUtils
                            .toInterfaceName(relationship.getAssociationClass().getName());
                    String interfaceFile = TypeScriptUtils.toFileName(interfaceName).replace(".ts", "");
                    String classFile = TypeScriptUtils.toFileName(className).replace(".ts", "");

                    content.append("export { ").append(interfaceName).append(" } from './").append(interfaceFile)
                            .append("';\n");
                    content.append("export { ").append(className).append(" } from './").append(classFile)
                            .append("';\n");
                }
            }
        }

        Files.writeString(indexPath, content.toString(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        logger.debug("Generated domain index: {}", indexPath);
    }

    /**
     * Generate main index file
     */
    private void generateIndexFile() throws IOException {
        Path indexPath = outputDirectory.resolve("index.ts");
        StringBuilder content = new StringBuilder();

        content.append("// Auto-generated index file for system: ").append(model.getSystemName()).append("\n");
        content.append("// Version: ").append(model.getVersion()).append("\n\n");

        // Export all domains
        for (Domain domain : model.getDomains()) {
            String domainName = TypeScriptUtils.toCamelCase(domain.getName());
            content.append("export * from './").append(domainName).append("';\n");
        }

        Files.writeString(indexPath, content.toString(), StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);
        logger.debug("Generated main index: {}", indexPath);
    }

    /**
     * Build map of all classes untuk reference
     */
    private Map<String, ClassDefinition> buildClassMap() {
        Map<String, ClassDefinition> map = new HashMap<>();

        for (Domain domain : model.getDomains()) {
            if (domain.getClasses() != null) {
                for (ClassDefinition classDef : domain.getClasses()) {
                    map.put(classDef.getName(), classDef);
                }
            }
        }

        return map;
    }
}