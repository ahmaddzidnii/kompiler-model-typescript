# xTUML TypeScript Compiler v1.1.0

Kompiler CLI untuk mengkonversi model xTUML dari format JSON ke kode TypeScript dengan type safety yang lengkap, dukungan action language yang canggih, dan UI CLI yang profesional seperti Power Designer.

## ✨ Fitur Terbaru v1.1.0

- 🎨 **Beautiful CLI UI**: Interface command line dengan colors dan progress indicators
- 📋 **Compact Professional Headers**: Header komentar profesional yang compact untuk semua file yang dihasilkan
- 🌈 **Cross-platform Colors**: Dukungan ANSI colors untuk Windows, Linux, dan macOS
- 📊 **Enhanced Summary**: Summary hasil kompilasi yang lebih detail dan informatif
- ⚡ **Progress Indicators**: Real-time progress untuk parsing dan generation
- 🎯 **Banner Display**: Beautiful startup banner dengan branding yang jelas

## 🚀 Fitur Utama

- ✅ **Parser JSON**: Membaca dan memvalidasi model xTUML dari file JSON
- ✅ **Generator TypeScript**: Menghasilkan interface dan class TypeScript yang type-safe
- ✅ **State Machine**: Mengkonversi state machine menjadi method TypeScript dengan type-safe transitions
- ✅ **Action Language**: Mendukung action language dengan operations, parameters, dan action steps
- ✅ **Template Variables**: Menangani template variables dalam log messages (`${self.NIM}`, `${parameter}`)
- ✅ **Attribute Validation**: Validasi otomatis untuk attribute updates dalam action steps
- ✅ **Inheritance**: Mendukung inheritance hierarchy dengan proper constructor chaining
- ✅ **Association Classes**: Mengkonversi relationship association classes
- ✅ **Professional Documentation**: Header komentar Power Designer-style untuk semua file
- ✅ **CLI Interface**: Command line interface yang cantik dengan colored output
- ✅ **Validation**: Validasi lengkap terhadap struktur model input

## Quick Start

### 1. Build Project

```bash
mvn clean package
```

### 2. Jalankan Kompiler

```bash
# Basic usage dengan UI cantik
java -jar target/kompiler-model-typescript-1.0-SNAPSHOT.jar xtuml-model.json

# Dengan Maven (development)
mvn exec:java -Dexec.mainClass="kelompok.dua.maven.XtumlCompilerCli" -Dexec.args="xtuml-model.json --clean --verbose"

# Dengan opsi lengkap
java -jar target/kompiler-model-typescript-1.0-SNAPSHOT.jar xtuml-model.json \
  --output ./generated-ts \
  --verbose \
  --clean \
  --force

# Disable colors (untuk CI/CD)
java -jar target/kompiler-model-typescript-1.0-SNAPSHOT.jar xtuml-model.json --no-color
```

### 3. CLI Options

| Option       | Short | Description                                     | Default    |
| ------------ | ----- | ----------------------------------------------- | ---------- |
| `--output`   | `-o`  | Direktori output untuk file TypeScript          | `./output` |
| `--verbose`  | `-v`  | Enable verbose logging                          | `false`    |
| `--force`    | `-f`  | Force overwrite jika direktori output sudah ada | `false`    |
| `--clean`    |       | Bersihkan direktori output sebelum generate     | `false`    |
| `--no-color` |       | Disable colored output (berguna untuk CI/CD)    | `false`    |
| `--help`     | `-h`  | Tampilkan help message                          |            |
| `--version`  | `-V`  | Tampilkan versi aplikasi                        |            |

### 4. Contoh Model Input dengan Action Language

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
          "entity_type": "class",
          "name": "Mahasiswa",
          "inherits_from": "Person",
          "key_letter": "MHS",
          "description": "Merepresentasikan seorang mahasiswa yang terdaftar.",
          "attributes": [
            { "name": "NIM", "data_type": "string", "attribute_type": "descriptive" },
            { "name": "Status", "data_type": "state", "attribute_type": "descriptive", "default_value": "Aktif" }
          ],
          "state_machine": {
            "initial_state": "Aktif",
            "states": [{ "name": "Aktif" }, { "name": "Cuti" }, { "name": "Lulus" }],
            "transitions": [
              {
                "from_state": "Aktif",
                "to_state": "Cuti",
                "event": "ambilCuti",
                "actions": [
                  {
                    "actionLanguage": {
                      "operations": [
                        {
                          "name": "ambilCuti",
                          "parameters": [{ "name": "alasan", "type": "string" }],
                          "actions": [
                            {
                              "type": "update",
                              "target": "this",
                              "attribute": "Status",
                              "value": "Cuti"
                            },
                            {
                              "type": "log",
                              "message": "Mahasiswa ${self.NIM} mengajukan cuti dengan alasan: ${alasan}."
                            }
                          ]
                        }
                      ]
                    }
                  }
                ]
              }
            ]
          }
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

export interface IMahasiswa extends IPerson {
  nim?: string;
  status?: string;
  state: "Aktif" | "Cuti" | "Lulus";
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

export class Mahasiswa extends Person implements IMahasiswa {
  public nim?: string;
  public status?: string = "Aktif";
  public state: "Aktif" | "Cuti" | "Lulus" = "Aktif";

  constructor(id: string) {
    super(id);
  }
}
```

### State Machine dengan Action Language

```typescript
export class Mahasiswa extends Person implements IMahasiswa {
  public state: "Aktif" | "Cuti" | "Lulus" = "Aktif";

  // Generated from actionLanguage operations
  public ambilcuti(alasan: string): boolean {
    if (this.state === "Aktif") {
      this.status = "Cuti"; // update action step
      console.log(`Mahasiswa ${this.nim} mengajukan cuti dengan alasan: ${alasan}.`); // log action step
      this.state = "Cuti";
      return true;
    }
    return false;
  }

  public aktifkankembali(tanggalaktif: Date): boolean {
    if (this.state === "Cuti") {
      this.status = "Aktif";
      console.log(`Mahasiswa ${this.nim} aktif kembali per ${tanggalaktif}.`);
      this.state = "Aktif";
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

### State Machine dengan Action Language

```json
{
  "initial_state": "InitialState",
  "states": [{ "name": "State1" }, { "name": "State2" }],
  "transitions": [
    {
      "from_state": "State1",
      "to_state": "State2",
      "event": "eventName",
      "actions": [
        {
          "actionLanguage": {
            "operations": [
              {
                "name": "operationName",
                "parameters": [{ "name": "paramName", "type": "string" }],
                "actions": [
                  {
                    "type": "update",
                    "target": "this",
                    "attribute": "AttributeName",
                    "value": "NewValue"
                  },
                  {
                    "type": "log",
                    "message": "Log message with ${self.AttributeName} and ${paramName}."
                  }
                ]
              }
            ]
          }
        }
      ]
    }
  ]
}
```

### Action Language Operations

#### Operation Definition

```json
{
  "name": "operationName",
  "parameters": [
    { "name": "paramName", "type": "string|integer|date|boolean" }
  ],
  "actions": [...]
}
```

#### Action Steps

**Update Action:**

```json
{
  "type": "update",
  "target": "this",
  "attribute": "AttributeName",
  "value": "NewValue"
}
```

**Log Action:**

```json
{
  "type": "log",
  "message": "Message with ${self.AttributeName} and ${parameterName}."
}
```

**Template Variables:**

- `${self.AttributeName}` - Merujuk ke attribute dari object saat ini
- `${parameterName}` - Merujuk ke parameter dari operation

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

| xTUML Type | TypeScript Type | Contoh                                   |
| ---------- | --------------- | ---------------------------------------- |
| `string`   | `string`        | `"Hello World"`                          |
| `integer`  | `number`        | `42`                                     |
| `uuid`     | `string`        | `"550e8400-e29b-41d4-a716-446655440000"` |
| `boolean`  | `boolean`       | `true`, `false`                          |
| `state`    | Union type      | `'Aktif' \| 'Cuti' \| 'Lulus'`           |
| `date`     | `Date`          | `new Date()`                             |

## Features Baru v1.1.0

### 🎯 Action Language Support

- **Operations**: Mendukung definisi operation dengan parameters
- **Action Steps**: Update attributes dan log messages
- **Template Variables**: Otomatis replace `${self.attribute}` dan `${parameter}`
- **Attribute Validation**: Memverifikasi attribute ada sebelum menggunakan

### 🔍 Intelligent Code Generation

- **Smart Attribute Detection**: Otomatis detect attribute yang valid
- **Comment Invalid Actions**: Action untuk attribute yang tidak ada di-comment
- **Method Deduplication**: Mencegah duplikasi method state machine

### 📝 Enhanced Template Processing

- **Self References**: `${self.NIM}` → `${this.nim}`
- **Parameter Injection**: `${alasan}` → `${alasan}`
- **Template Literals**: Otomatis convert ke template string TypeScript

### Example Action Language Output

**Input JSON:**

```json
{
  "type": "log",
  "message": "Mahasiswa ${self.NIM} mengajukan cuti dengan alasan: ${alasan}."
}
```

**Generated TypeScript:**

```typescript
console.log(`Mahasiswa ${this.nim} mengajukan cuti dengan alasan: ${alasan}.`);
```

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

# Run with sample model
mvn exec:java -Dexec.mainClass="kelompok.dua.maven.XtumlCompilerCli" -Dexec.args="xtuml-model.json --verbose"

# Generate with specific output
java -jar target/kompiler-model-typescript-1.0-SNAPSHOT.jar xtuml-model.json -o ./output --clean
```

### Example Output Structure

```
output/
├── index.ts                           # Main export file
└── manajemenCivitasAkademika/
    ├── index.ts                       # Domain exports
    ├── iperson.ts                     # Person interface
    ├── person.ts                      # Person abstract class
    ├── imahasiswa.ts                  # Mahasiswa interface
    ├── mahasiswa.ts                   # Mahasiswa class dengan state machine
    ├── idosen.ts                      # Dosen interface
    ├── dosen.ts                       # Dosen class dengan state machine
    ├── imatakuliah.ts                 # MataKuliah interface
    ├── matakuliah.ts                  # MataKuliah class
    ├── ikrs.ts                        # KRS interface (association)
    ├── krs.ts                         # KRS class (association)
    ├── ijadwalajar.ts                 # JadwalAjar interface (association)
    └── jadwalajar.ts                  # JadwalAjar class (association)
```

### Sample Generated Code dengan Compact Professional Headers

**Interface dengan Professional Header (IDosen.ts):**

```typescript
/**
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * DOSEN Interface Definition
 *
 * Generated by: xTUML TypeScript Compiler v1.1.0
 * Generated on: 2025-10-17 05:41:03
 *
 * ──────────────────────────────────────────────────
 *
 * Model Information:
 *   System: SistemAkademik v1.0.0
 *   Domain: Manajemen Civitas Akademika (MCA)
 *   Entity: Dosen (DSN)
 *   Type: Concrete Class
 *
 * Description:
 *   Merepresentasikan seorang dosen pengajar.
 *
 * Attributes:
 *     📝 NIP (string) - descriptive
 *     📝 Status (string) - descriptive
 *
 * State Machine: Yes
 *
 * @author xTUML TypeScript Compiler
 * @version 1.1.0
 * @since 2025-10-17
 */

import { IPerson } from "./iperson";

export interface IDosen extends IPerson {
  nip?: string;
  status?: string;
  state: "Aktif Mengajar" | "Cuti Akademik" | "Pensiun";
}
```

**Class dengan Action Language dan Professional Header:**

```typescript
/**
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * DOSEN Class Implementation
 *
 * Generated by: xTUML TypeScript Compiler v1.1.0
 * Generated on: 2025-10-17 05:41:03
 *
 * Features:
 *   ✓ Type-safe property definitions
 *   ✓ Constructor with inheritance support
 *   ✓ State machine with action language support
 *   ✓ Auto-generated from xTUML model
 */

export class Dosen extends Person implements IDosen {
  public nip?: string;
  public status?: string = "Aktif Mengajar";
  public state: "Aktif Mengajar" | "Cuti Akademik" | "Pensiun" = "Aktif Mengajar";

  constructor(id: string) {
    super(id);
  }

  // State Machine Methods with Action Language
  public ajukancuti(durasicuti: number): boolean {
    if (this.state === "Aktif Mengajar") {
      this.status = "Cuti Akademik";
      console.log(`Dosen ${this.nip} mengajukan cuti selama ${durasicuti} bulan.`);
      this.state = "Cuti Akademik";
      return true;
    }
    return false;
  }
}
```

**Main Index dengan Professional Header:**

```typescript
/**
 * ════════════════════════════════════════════════════════════════════════════════
 *
 * SISTEMAKADEMIK - MAIN SYSTEM INDEX
 *
 * Features:
 *   ✓ Complete type-safe exports
 *   ✓ All domain classes and interfaces
 *   ✓ Association classes from relationships
 *   ✓ State machine support with action language
 *   ✓ Inheritance hierarchy support
 *
 * Usage:
 *   import { ClassName } from './manajemenCivitasAkademika';
 */

export * from "./manajemenCivitasAkademika";
```

    return true;

}
return false;
}

```

### Architecture

```

src/main/java/kelompok/dua/maven/
├── model/ # Model classes untuk JSON mapping
│ ├── XtumlModel.java # Root model class
│ ├── Domain.java # Domain definition
│ ├── ClassDefinition.java # Class definition dengan state machine
│ ├── StateMachine.java # State machine definition
│ ├── Transition.java # State transitions
│ ├── Action.java # Action definition (legacy + actionLanguage)
│ ├── ActionLanguage.java # Action language container
│ ├── Operation.java # Operation definition dengan parameters
│ ├── Parameter.java # Operation parameters
│ ├── ActionStep.java # Individual action steps (update, log)
│ ├── Attribute.java # Class attributes
│ └── Relationship.java # Relationships dan association classes
├── parser/ # JSON parser dan validasi
│ ├── XtumlModelParser.java # Main parser
│ └── XtumlParseException.java # Parser exceptions
├── generator/ # TypeScript code generator
│ └── TypeScriptGenerator.java # Main generator dengan action language support
├── util/ # Utility classes
│ └── TypeScriptUtils.java # TypeScript utility functions
└── XtumlCompilerCli.java # Main CLI application

````

## 🎨 Beautiful CLI Output

Kompiler v1.1.0 hadir dengan interface CLI yang cantik dan informatif:

```bash
$ mvn exec:java -Dexec.mainClass="kelompok.dua.maven.XtumlCompilerCli" -Dexec.args="xtuml-model.json --clean"

════════════════════════════════════════════════════════════
║             xTUML TypeScript Compiler v1.1.0             ║
║            Professional xTUML Model Compiler             ║
════════════════════════════════════════════════════════════

ℹ️  📥 Input: xtuml-model.json
ℹ️  📤 Output: ./output
────────────────────────────────────────────────────────────
ℹ️  🔍 Validating input file...
✅ ✓ Input file validated
ℹ️  📁 Setting up output directory...
⚠️  🧹 Cleaning output directory...
✅ ✓ Output directory cleaned
✅ ✓ Output directory ready
ℹ️  📋 Parsing xTUML model...
Parsing: [████████████████████] 100%
✅ ✓ Model successfully parsed
ℹ️    System: SistemAkademik v1.0.0
ℹ️    Domains: 1
────────────────────────────────────────────────────────────
ℹ️  ⚙️ Generating TypeScript files...
✅ ✓ TypeScript generation completed!
────────────────────────────────────────────────────────────
════════════════════════════════════════════════════════════
  COMPILATION SUMMARY
════════════════════════════════════════════════════════════
ℹ️  🎯 System: SistemAkademik v1.0.0
ℹ️  📂 Domain 'Manajemen Civitas Akademika': 4 classes, 2 relationships, 2 state machines
────────────────────────────────────────────────────────────
✅ ✅ GENERATION COMPLETE
ℹ️  📊 Total Statistics:
ℹ️     • Domains: 1
ℹ️     • Classes: 4
ℹ️     • Relationships: 2
ℹ️     • State Machines: 2
ℹ️     • Generated Files: 14
ℹ️  ⏱️ Execution Time: 431ms
ℹ️  📍 Output Location: D:\Coding\Java\maven\kompiler-model-typescript\.\output
────────────────────────────────────────────────────────────
✅ 🎉 Ready to use! Import your TypeScript modules from the output directory.
````

**Fitur CLI UI:**

- 🎨 **Colored Output**: Success (hijau), Info (biru), Warning (kuning), Error (merah)
- 📊 **Progress Bars**: Real-time progress untuk operasi parsing
- 🏷️ **Icons**: Emoji icons untuk berbagai jenis pesan
- 📋 **Professional Banner**: Startup banner dengan branding yang jelas
- 📈 **Detailed Summary**: Summary lengkap dengan statistik dan timing
- 🚫 **No-Color Option**: `--no-color` untuk environment CI/CD
- ⚡ **Performance Info**: Execution time dan file count

## Changelog

### v1.1.0 (Latest)

- 🎨 **Beautiful CLI UI**: Interface cantik dengan colors, icons, dan progress indicators
- 📋 **Compact Professional Headers**: Header komentar compact dan profesional untuk semua file generated
- 🌈 **Cross-platform Colors**: Dukungan ANSI colors menggunakan Jansi library
- 📊 **Enhanced Summary**: Summary detail dengan statistics dan timing information
- ⚡ **Progress Indicators**: Real-time progress bars untuk parsing dan generation
- 🎯 **Professional Banner**: Beautiful startup banner dengan version information
- 🚫 **No-Color Option**: Disable colors untuk CI/CD environment
- ✨ **Action Language Support**: Dukungan penuh untuk actionLanguage dalam transitions
- ✨ **Operation Parameters**: Parameter typing untuk operations
- ✨ **Template Variables**: Otomatis processing template variables
- ✨ **Attribute Validation**: Validasi attribute existence
- ✨ **Smart Code Generation**: Comment invalid actions, prevent duplicates
- 🐛 **Bug Fixes**: Perbaikan template string escaping
- 📝 **Documentation**: Update README dengan contoh action language

### v1.0.0

- 🎉 **Initial Release**: Basic xTUML to TypeScript compilation
- ✅ **State Machine**: Basic state machine support
- ✅ **Inheritance**: Class inheritance dan interfaces
- ✅ **Association Classes**: Relationship associations
- ✅ **CLI Interface**: Command line interface

## Migration Guide

### Upgrading from v1.0.0 to v1.1.0

**Model JSON Changes:**

1. Action structure sekarang mendukung `actionLanguage`:

```json
// Old format (masih didukung)
"actions": [{ "type": "log", "message": "Simple message" }]

// New format (recommended)
"actions": [
  {
    "actionLanguage": {
      "operations": [
        {
          "name": "operationName",
          "parameters": [...],
          "actions": [...]
        }
      ]
    }
  }
]
```

2. Tambahkan attribute yang hilang:

```json
// Pastikan semua class memiliki attribute yang direferensikan dalam actions
{
  "name": "Dosen",
  "attributes": [
    { "name": "NIP", "data_type": "string", "attribute_type": "descriptive" },
    { "name": "Status", "data_type": "string", "attribute_type": "descriptive", "default_value": "Aktif Mengajar" }
  ]
}
```

**Generated Code Changes:**

- Method signature sekarang include parameters dari operation
- Template variables otomatis di-convert ke TypeScript template literals
- Invalid attribute updates di-comment sebagai dokumentasi

## Contributing

1. Fork repository
2. Create feature branch
3. Add tests untuk fitur baru
4. Submit pull request

## License

MIT License - lihat file LICENSE untuk detail lengkap.
