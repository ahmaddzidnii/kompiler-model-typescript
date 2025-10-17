package kelompok.dua.maven.parser;

import kelompok.dua.maven.model.XtumlModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test class untuk XtumlModelParser
 */
class XtumlModelParserTest {

  private XtumlModelParser parser;

  @BeforeEach
  void setUp() {
    parser = new XtumlModelParser();
  }

  @Test
  @DisplayName("Parse model dari file JSON yang valid")
  void testParseValidJsonFile() throws Exception {
    // Test menggunakan file xtuml-model.json yang sudah ada
    Path modelPath = Paths.get("xtuml-model.json");

    XtumlModel model = parser.parseFromFile(modelPath);

    // Test generic - hanya memastikan parsing berhasil tanpa validasi content
    // spesifik
    assertNotNull(model);
    assertNotNull(model.getSystemName());
    assertNotNull(model.getVersion());
    assertNotNull(model.getDomains());
    assertTrue(model.getDomains().size() > 0);
  }

  @Test
  @DisplayName("Parse model dari string JSON yang valid")
  void testParseValidJsonString() throws Exception {
    String jsonContent = """
        {
          "system_name": "TestSystem",
          "version": "1.0.0",
          "domains": [
            {
              "name": "Test Domain",
              "key_letter": "TD",
              "classes": [
                {
                  "entity_type": "class",
                  "name": "TestClass",
                  "key_letter": "TC",
                  "description": "Test class",
                  "attributes": [
                    {
                      "name": "ID",
                      "data_type": "uuid",
                      "attribute_type": "naming"
                    }
                  ]
                }
              ],
              "relationships": []
            }
          ]
        }
        """;

    XtumlModel model = parser.parseFromString(jsonContent);

    // Test generic - hanya memastikan parsing berhasil
    assertNotNull(model);
    assertNotNull(model.getSystemName());
    assertNotNull(model.getVersion());
    assertNotNull(model.getDomains());
    assertTrue(model.getDomains().size() > 0);
  }

  @Test
  @DisplayName("Parse gagal untuk file yang tidak ada")
  void testParseNonExistentFile() {
    Path nonExistentPath = Paths.get("src/non-existent.json");

    XtumlParseException exception = assertThrows(
        XtumlParseException.class,
        () -> parser.parseFromFile(nonExistentPath));

    assertTrue(exception.getMessage().contains("File input tidak ditemukan"));
  }

  @Test
  @DisplayName("Parse gagal untuk JSON string kosong")
  void testParseEmptyJsonString() {
    XtumlParseException exception = assertThrows(
        XtumlParseException.class,
        () -> parser.parseFromString(""));

    assertTrue(exception.getMessage().contains("JSON content tidak boleh null atau kosong"));
  }

  @Test
  @DisplayName("Parse gagal untuk JSON string null")
  void testParseNullJsonString() {
    XtumlParseException exception = assertThrows(
        XtumlParseException.class,
        () -> parser.parseFromString(null));

    assertTrue(exception.getMessage().contains("JSON content tidak boleh null atau kosong"));
  }

}