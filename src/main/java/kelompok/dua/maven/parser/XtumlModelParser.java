package kelompok.dua.maven.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;
import kelompok.dua.maven.model.XtumlModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Parser untuk membaca dan memvalidasi model xTUML dari file JSON
 */
public class XtumlModelParser {
    private static final Logger logger = LoggerFactory.getLogger(XtumlModelParser.class);
    private final ObjectMapper objectMapper;

    public XtumlModelParser() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Parse model xTUML dari file JSON
     * 
     * @param inputPath Path ke file JSON input
     * @return XtumlModel object yang telah diparsing
     * @throws XtumlParseException jika terjadi error dalam parsing
     */
    public XtumlModel parseFromFile(Path inputPath) throws XtumlParseException {
        if (!Files.exists(inputPath)) {
            throw new XtumlParseException("File input tidak ditemukan: " + inputPath);
        }

        if (!Files.isRegularFile(inputPath)) {
            throw new XtumlParseException("Path yang diberikan bukan file: " + inputPath);
        }

        if (!inputPath.toString().toLowerCase().endsWith(".json")) {
            logger.warn("File yang diberikan tidak berekstensi .json: {}", inputPath);
        }

        try {
            logger.info("Memulai parsing file: {}", inputPath);
            XtumlModel model = objectMapper.readValue(inputPath.toFile(), XtumlModel.class);

            validateModel(model);

            logger.info("Berhasil parsing model: {}", model.getSystemName());
            return model;

        } catch (JsonMappingException e) {
            throw new XtumlParseException("Error dalam mapping JSON ke model object: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new XtumlParseException("Error dalam membaca file: " + e.getMessage(), e);
        }
    }

    /**
     * Parse model xTUML dari string JSON
     * 
     * @param jsonContent String JSON content
     * @return XtumlModel object yang telah diparsing
     * @throws XtumlParseException jika terjadi error dalam parsing
     */
    public XtumlModel parseFromString(String jsonContent) throws XtumlParseException {
        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            throw new XtumlParseException("JSON content tidak boleh null atau kosong");
        }

        try {
            logger.info("Memulai parsing dari string JSON");
            XtumlModel model = objectMapper.readValue(jsonContent, XtumlModel.class);

            validateModel(model);

            logger.info("Berhasil parsing model: {}", model.getSystemName());
            return model;

        } catch (JsonMappingException e) {
            throw new XtumlParseException("Error dalam mapping JSON ke model object: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new XtumlParseException("Error dalam parsing JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Validasi model yang telah diparsing
     * 
     * @param model Model yang akan divalidasi
     * @throws XtumlParseException jika model tidak valid
     */
    private void validateModel(XtumlModel model) throws XtumlParseException {
        if (model == null) {
            throw new XtumlParseException("Model tidak boleh null");
        }

        if (model.getSystemName() == null || model.getSystemName().trim().isEmpty()) {
            throw new XtumlParseException("System name tidak boleh null atau kosong");
        }

        if (model.getVersion() == null || model.getVersion().trim().isEmpty()) {
            throw new XtumlParseException("Version tidak boleh null atau kosong");
        }

        if (model.getDomains() == null || model.getDomains().isEmpty()) {
            throw new XtumlParseException("Model harus memiliki minimal satu domain");
        }

        // Validasi setiap domain
        for (int i = 0; i < model.getDomains().size(); i++) {
            var domain = model.getDomains().get(i);
            if (domain.getName() == null || domain.getName().trim().isEmpty()) {
                throw new XtumlParseException("Domain ke-" + (i + 1) + " tidak memiliki nama");
            }

            if (domain.getKeyLetter() == null || domain.getKeyLetter().trim().isEmpty()) {
                throw new XtumlParseException("Domain '" + domain.getName() + "' tidak memiliki key letter");
            }

            if (domain.getClasses() == null || domain.getClasses().isEmpty()) {
                throw new XtumlParseException("Domain '" + domain.getName() + "' harus memiliki minimal satu class");
            }
        }

        logger.info("Model berhasil divalidasi");
    }
}