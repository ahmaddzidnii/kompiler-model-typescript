# xTUML TypeScript Compiler

Kompiler CLI untuk mengkonversi model xTUML dari format JSON ke kode TypeScript dengan type safety yang lengkap.

## Fitur

- ✅ **Parser JSON**: Membaca dan memvalidasi model xTUML dari file JSON
- ✅ **Generator TypeScript**: Menghasilkan interface dan class TypeScript yang type-safe
- ✅ **State Machine**: Mengkonversi state machine menjadi method TypeScript dengan type-safe transitions
- ✅ **Inheritance**: Mendukung inheritance hierarchy dengan proper constructor chaining
- ✅ **Association Classes**: Mengkonversi relationship association classes
- ✅ **CLI Interface**: Command line interface yang user-friendly dengan berbagai opsi
- ✅ **Validation**: Validasi lengkap terhadap struktur model input

## Quick Start

### 1. Build Project

```bash
mvn clean package
```

### 2. Jalankan Kompiler

```bash
# Basic usage
java -jar target/kompiler-model-typescript-1.0-SNAPSHOT.jar input.json

# Dengan opsi lengkap
java -jar target/kompiler-model-typescript-1.0-SNAPSHOT.jar input.json \
  --output ./generated-ts \
  --verbose \
  --clean
```

### 3. Contoh Model Input

```json
{
  "system_name": "SistemAkademik",
  "version": "1.0.0",
  "domains": [
    {
      "name": "Manajemen Civitas Akademika",
      "key_letter": "MCA",
      "classes": [
        {
          "entity_type": "superclass",
          "name": "Person",
          "key_letter": "PRS",
          "is_abstract": true,
          "description": "Superclass untuk semua individu dalam sistem.",
          "attributes": [
            { "name": "ID", "data_type": "uuid", "attribute_type": "naming" },
            { "name": "Nama", "data_type": "string", "attribute_type": "descriptive" },
            { "name": "Email", "data_type": "string", "attribute_type": "descriptive" }
          ]
        }
      ]
    }
  ]
}
```

## Output TypeScript

### Interface

```typescript
export interface IPerson {
  id: string;
  nama?: string;
  email?: string;
}
```

### Class

```typescript
export abstract class Person implements IPerson {
  public id: string;
  public nama?: string;
  public email?: string;

  constructor(id: string) {
    this.id = id;
  }
}
```

### State Machine

```typescript
export class Mahasiswa extends Person implements IMahasiswa {
  public state: "Aktif" | "Cuti" | "Lulus" = "Aktif";

  public ambilcuti(): boolean {
    if (this.state === "Aktif") {
      this.state = "Cuti";
      console.log(`Mahasiswa ${this.nim} mengajukan cuti.`);
      return true;
    }
    return false;
  }
}
```

## CLI Options

```
Usage: xtuml-ts-compiler [-fhvV] [--clean] [-o=<outputDir>] <inputFile>

Parameters:
  <inputFile>            Path ke file JSON input yang berisi model xTUML

Options:
  -o, --output=<outputDir>   Direktori output untuk file TypeScript
                             (default: ./output)
  -v, --verbose              Enable verbose logging
  -f, --force                Force overwrite jika direktori output sudah ada
      --clean                Bersihkan direktori output sebelum generate
  -h, --help                 Show this help message and exit
  -V, --version              Print version information and exit
```

## Struktur Model JSON

### Domain

```json
{
  "name": "Domain Name",
  "key_letter": "DN",
  "classes": [...],
  "relationships": [...]
}
```

### Class Definition

```json
{
  "entity_type": "class|superclass|passive_class",
  "name": "ClassName",
  "key_letter": "CL",
  "inherits_from": "ParentClass",
  "is_abstract": true,
  "description": "Description",
  "attributes": [...],
  "state_machine": {...}
}
```

### Attribute

```json
{
  "name": "AttributeName",
  "data_type": "string|integer|uuid|boolean|state",
  "attribute_type": "naming|descriptive|referential",
  "default_value": "DefaultValue"
}
```

### State Machine

```json
{
  "initial_state": "InitialState",
  "states": [{ "name": "State1" }, { "name": "State2" }],
  "transitions": [
    {
      "from_state": "State1",
      "to_state": "State2",
      "event": "eventName",
      "actions": [{ "type": "log", "message": "Log message" }]
    }
  ]
}
```

### Relationship

```json
{
  "relationship_id": "R1",
  "description": "Relationship description",
  "participants": [
    {
      "class_name": "Class1",
      "role": "role1",
      "multiplicity": "1..*"
    }
  ],
  "association_class": {
    "name": "AssociationClass",
    "key_letter": "AC",
    "attributes": [...]
  }
}
```

## Type Mapping

| xTUML Type | TypeScript Type |
| ---------- | --------------- |
| `string`   | `string`        |
| `integer`  | `number`        |
| `uuid`     | `string`        |
| `boolean`  | `boolean`       |
| `state`    | Union type      |

## Development

### Prerequisites

- Java 17+
- Maven 3.6+

### Build & Test

```bash
# Compile
mvn compile

# Run tests
mvn test

# Package
mvn package

# Run with test model
java -jar target/kompiler-model-typescript-1.0-SNAPSHOT.jar src/model.json
```

### Architecture

```
src/main/java/kelompok/dua/maven/
├── model/              # Model classes untuk JSON mapping
├── parser/             # JSON parser dan validasi
├── generator/          # TypeScript code generator
├── util/               # Utility classes
└── XtumlCompilerCli.java # Main CLI application
```

## Contributing

1. Fork repository
2. Create feature branch
3. Add tests untuk fitur baru
4. Submit pull request

## License

MIT License - lihat file LICENSE untuk detail lengkap.
