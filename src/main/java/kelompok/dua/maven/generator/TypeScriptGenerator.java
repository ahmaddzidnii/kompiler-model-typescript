package kelompok.dua.maven.generator;

import kelompok.dua.maven.model.*;
import kelompok.dua.maven.util.HeaderGenerator;
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
    private ClassDefinition currentClass; // Track current class being processed

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

        // Generate classes dengan embedded interfaces
        for (ClassDefinition classDef : domain.getClasses()) {
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
     * Generate class implementation with embedded interface
     */
    private void generateClass(ClassDefinition classDef, Path domainDir) throws IOException {
        this.currentClass = classDef; // Set current class context

        String className = TypeScriptUtils.toPascalCase(classDef.getName());
        String interfaceName = TypeScriptUtils.toInterfaceName(classDef.getName());
        String fileName = TypeScriptUtils.toFileName(className);
        Path filePath = domainDir.resolve(fileName);

        StringBuilder content = new StringBuilder();

        // Generate professional header
        Domain currentDomain = getCurrentDomain(classDef);
        content.append(HeaderGenerator.generateClassHeader(classDef, currentDomain, model));
        content.append("\n");

        // Import parent interface and class if has inheritance
        if (classDef.getInheritsFrom() != null) {
            String parentClass = TypeScriptUtils.toPascalCase(classDef.getInheritsFrom());
            String parentInterface = TypeScriptUtils.toInterfaceName(classDef.getInheritsFrom());
            String parentFileName = TypeScriptUtils.toFileName(parentClass).replace(".ts", "");
            content.append("import { ").append(parentInterface).append(", ").append(parentClass).append(" } from './").append(parentFileName).append("';\n");
        }

        content.append("\n");

        // Generate interface first
        content.append("export interface ").append(interfaceName);

        // Handle inheritance for interface
        if (classDef.getInheritsFrom() != null) {
            String parentInterface = TypeScriptUtils.toInterfaceName(classDef.getInheritsFrom());
            content.append(" extends ").append(parentInterface);
        }

        content.append(" {\n");

        // Generate interface properties
        if (classDef.getAttributes() != null) {
            for (Attribute attr : classDef.getAttributes()) {
                generateInterfaceProperty(attr, content);
            }
        }

        // Add state property if has state machine
        if (classDef.getStateMachine() != null) {
            generateStateProperty(classDef, content);
        }

        content.append("}\n\n");

        // Generate class declaration
        if (Boolean.TRUE.equals(classDef.getIsAbstract())) {
            content.append("export abstract class ");
        } else {
            content.append("export class ");
        }

        content.append(className);

        // Handle inheritance for class
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

            Set<String> generatedMethods = new HashSet<>();

            for (Transition transition : sm.getTransitions()) {
                // Check if transition has actionLanguage operations (new format)
                boolean hasActionLanguage = false;
                if (transition.getActionLanguage() != null && transition.getActionLanguage().getOperations() != null) {
                    for (Operation operation : transition.getActionLanguage().getOperations()) {
                        String methodName = TypeScriptUtils.toCamelCase(operation.getName());
                        if (!generatedMethods.contains(methodName)) {
                            generateOperationMethod(operation, transition, content);
                            generatedMethods.add(methodName);
                            hasActionLanguage = true;
                        }
                    }
                }

                // Check old format for backward compatibility
                if (!hasActionLanguage && transition.getActions() != null) {
                    for (Action action : transition.getActions()) {
                        if (action.getActionLanguage() != null && action.getActionLanguage().getOperations() != null) {
                            for (Operation operation : action.getActionLanguage().getOperations()) {
                                String methodName = TypeScriptUtils.toCamelCase(operation.getName());
                                if (!generatedMethods.contains(methodName)) {
                                    generateOperationMethod(operation, transition, content);
                                    generatedMethods.add(methodName);
                                    hasActionLanguage = true;
                                }
                            }
                        }
                    }
                }

                // Generate simple transition method only if no actionLanguage
                if (!hasActionLanguage) {
                    String methodName = TypeScriptUtils.toCamelCase(transition.getEvent());
                    if (!generatedMethods.contains(methodName)) {
                        generateSimpleTransitionMethod(transition, content);
                        generatedMethods.add(methodName);
                    }
                }
            }
        }
    }

    /**
     * Generate simple transition method (when no actionLanguage is present)
     */
    private void generateSimpleTransitionMethod(Transition transition, StringBuilder content) {
        String methodName = TypeScriptUtils.toCamelCase(transition.getEvent());
        content.append("  public ").append(methodName).append("(): boolean {\n");
        content.append("    if (this.state === '").append(transition.getFromState()).append("') {\n");
        content.append("      this.state = '").append(transition.getToState()).append("';\n");

        // Add legacy actions if any
        if (transition.getActions() != null) {
            for (Action action : transition.getActions()) {
                if (action.getActionLanguage() == null && "log".equals(action.getType())) {
                    String message = action.getMessage();
                    if (message != null) {
                        // Simple template variable replacement for legacy format
                        if (message.contains("${self.NIM}")) {
                            message = message.replace("${self.NIM}", "`+ this.nim +`");
                        }
                        if (message.contains("${self.NIP}")) {
                            message = message.replace("${self.NIP}", "`+ this.nip +`");
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

    /**
     * Generate method dari operation dalam action language
     */
    private void generateOperationMethod(Operation operation, Transition transition, StringBuilder content) {
        String methodName = TypeScriptUtils.toCamelCase(operation.getName());

        content.append("  public ").append(methodName).append("(");

        // Generate parameters
        if (operation.getParameters() != null) {
            for (int i = 0; i < operation.getParameters().size(); i++) {
                Parameter param = operation.getParameters().get(i);
                if (i > 0)
                    content.append(", ");
                content.append(TypeScriptUtils.toCamelCase(param.getName()))
                        .append(": ")
                        .append(TypeScriptUtils.convertDataType(param.getType()));
            }
        }

        content.append("): boolean {\n");
        content.append("    if (this.state === '").append(transition.getFromState()).append("') {\n");

        // Check if any action step updates the state machine
        boolean stateUpdated = false;
        List<ActionStep> actionSteps = null;

        // Get action steps (new format - steps)
        if (operation.getSteps() != null) {
            actionSteps = operation.getSteps();
        }
        // Backward compatibility - old format (actions)
        else if (operation.getActions() != null) {
            actionSteps = operation.getActions();
        }

        // Check if state is explicitly updated in action steps
        if (actionSteps != null) {
            for (ActionStep actionStep : actionSteps) {
                if ("update".equals(actionStep.getType()) &&
                        "this".equals(actionStep.getTarget()) &&
                        "Status".equals(actionStep.getAttribute()) &&
                        currentClass != null &&
                        currentClass.getStateMachine() != null) {
                    stateUpdated = true;
                    break;
                }
            }
        }

        // Generate action steps
        if (actionSteps != null) {
            for (ActionStep actionStep : actionSteps) {
                generateActionStep(actionStep, operation, content, transition);
            }
        }

        // Only add automatic state update if not already updated by action steps
        if (!stateUpdated) {
            content.append("      this.state = '").append(transition.getToState()).append("';\n");
        }
        content.append("      return true;\n");
        content.append("    }\n");
        content.append("    return false;\n");
        content.append("  }\n\n");
    }

    /**
     * Generate individual action step
     */
    private void generateActionStep(ActionStep actionStep, Operation operation, StringBuilder content,
            Transition transition) {
        switch (actionStep.getType()) {
            case "update":
                if ("this".equals(actionStep.getTarget()) && actionStep.getAttribute() != null
                        && actionStep.getValue() != null) {
                    String attrName = TypeScriptUtils.toCamelCase(actionStep.getAttribute());
                    String value = actionStep.getValue();

                    // Check if this is a state machine update (Status attribute in state machine
                    // context)
                    boolean isStateMachineUpdate = "Status".equals(actionStep.getAttribute()) &&
                            currentClass != null &&
                            currentClass.getStateMachine() != null;

                    // Check if value needs quotes (for string types)
                    if (!value.matches("\\d+") && !value.equals("true") && !value.equals("false")) {
                        value = "'" + TypeScriptUtils.escapeString(value) + "'";
                    }

                    // For state machine updates, update the state instead of status attribute
                    if (isStateMachineUpdate) {
                        content.append("      this.state = ").append(value).append(";\n");
                    } else {
                        // Check if the attribute exists in the current class
                        boolean attributeExists = hasAttribute(actionStep.getAttribute());

                        if (attributeExists) {
                            content.append("      this.").append(attrName).append(" = ").append(value).append(";\n");
                        } else {
                            content.append("      // Update ").append(actionStep.getAttribute()).append(" to ")
                                    .append(actionStep.getValue()).append("\n");
                            content.append("      // this.").append(attrName).append(" = ").append(value).append(";\n");
                        }
                    }
                }
                break;

            case "log":
                String message = actionStep.getMessage();
                if (message != null) {
                    // Replace template variables
                    message = replaceTemplateVariables(message, operation);
                    content.append("      console.log(").append(message).append(");\n");
                }
                break;

            default:
                // Handle other action types if needed
                content.append("      // Action type '").append(actionStep.getType()).append("' not implemented\n");
                break;
        }
    }

    /**
     * Check if attribute exists in current class definition or its parent
     */
    private boolean hasAttribute(String attributeName) {
        if (currentClass == null)
            return false;

        // Check current class attributes
        if (currentClass.getAttributes() != null) {
            for (Attribute attr : currentClass.getAttributes()) {
                if (attributeName.equals(attr.getName())) {
                    return true;
                }
            }
        }

        // Check parent class attributes
        if (currentClass.getInheritsFrom() != null) {
            ClassDefinition parentClass = classMap.get(currentClass.getInheritsFrom());
            if (parentClass != null && parentClass.getAttributes() != null) {
                for (Attribute attr : parentClass.getAttributes()) {
                    if (attributeName.equals(attr.getName())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Replace template variables dalam message
     */
    private String replaceTemplateVariables(String message, Operation operation) {
        if (message == null)
            return "''";

        String result = message;

        // Replace ${self.NIM} dengan ${this.nim}, ${self.NIP} dengan ${this.nip}, etc.
        result = result.replace("${self.NIM}", "${this.nim}");
        result = result.replace("${self.NIP}", "${this.nip}");

        // Replace parameter variables ${paramName} dengan ${paramName}
        if (operation.getParameters() != null) {
            for (Parameter param : operation.getParameters()) {
                String paramName = TypeScriptUtils.toCamelCase(param.getName());
                result = result.replace("${" + param.getName() + "}", "${" + paramName + "}");
            }
        }

        // Convert to template literal
        if (result.contains("${")) {
            result = "`" + result.replace("\\", "\\\\").replace("`", "\\`") + "`";
        } else {
            result = "'" + TypeScriptUtils.escapeString(result) + "'";
        }

        return result;
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

            generateClass(classDef, domainDir);
        }
    }

    /**
     * Generate index file untuk domain
     */
    private void generateDomainIndex(Domain domain, Path domainDir) throws IOException {
        Path indexPath = domainDir.resolve("index.ts");
        StringBuilder content = new StringBuilder();

        // Generate professional header
        content.append(HeaderGenerator.generateDomainIndexHeader(domain, model));
        content.append("\n");

        // Export all interfaces and classes from the same files
        for (ClassDefinition classDef : domain.getClasses()) {
            String interfaceName = TypeScriptUtils.toInterfaceName(classDef.getName());
            String className = TypeScriptUtils.toPascalCase(classDef.getName());
            String classFile = TypeScriptUtils.toFileName(className).replace(".ts", "");

            content.append("export { ").append(interfaceName).append(", ").append(className).append(" } from './").append(classFile).append("';\n");
        }

        // Export association classes
        if (domain.getRelationships() != null) {
            for (Relationship relationship : domain.getRelationships()) {
                if (relationship.getAssociationClass() != null) {
                    String className = TypeScriptUtils.toPascalCase(relationship.getAssociationClass().getName());
                    String interfaceName = TypeScriptUtils
                            .toInterfaceName(relationship.getAssociationClass().getName());
                    String classFile = TypeScriptUtils.toFileName(className).replace(".ts", "");

                    content.append("export { ").append(interfaceName).append(", ").append(className).append(" } from './").append(classFile)
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

        // Generate professional header
        content.append(HeaderGenerator.generateMainIndexHeader(model));
        content.append("\n");

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

    /**
     * Get domain yang berisi class tertentu
     */
    private Domain getCurrentDomain(ClassDefinition classDef) {
        for (Domain domain : model.getDomains()) {
            if (domain.getClasses() != null) {
                for (ClassDefinition cd : domain.getClasses()) {
                    if (cd.getName().equals(classDef.getName())) {
                        return domain;
                    }
                }
            }
        }
        return model.getDomains().get(0); // fallback ke domain pertama
    }
}