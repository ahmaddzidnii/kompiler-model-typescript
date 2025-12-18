# xTUML TypeScript Compiler v1.1.0

Kompiler berbasis Command Line Interface (CLI) yang berfungsi untuk mengonversi model xTUML dari representasi JSON menjadi kode TypeScript secara otomatis, dengan jaminan keamanan tipe (type safety) serta keluaran kode yang siap digunakan dalam pengembangan perangkat lunak.

---

## Table of Contents

- [Prerequisites](#prerequisites)
  - [Tutorial Instalasi](#-tutorial-instalasi)
  - [Verifikasi Instalasi](#verifikasi-instalasi)
- [Quick Start](#quick-start)
  - [1. Build Project](#1-build-project)
  - [2. Menjalankan Kompiler](#2-menjalankan-kompiler)
- [CLI Options](#cli-options)
  - [CLI Usage](#cli-usage)
- [Contoh Model Input (JSON)](#contoh-model-input-json)
- [Struktur Model JSON](#struktur-model-json)
  - [Domain](#domain)
  - [Class Definition](#class-definition)
  - [Attribute](#attribute)
- [State Machine & Action Language](#state-machine--action-language)
  - [Definisi State Machine](#definisi-state-machine)
  - [Operation Definition](#operation-definition)
  - [Action Steps](#action-steps)
- [Relationship](#relationship)
- [Example Output Structure](#example-output-structure)
- [Beautiful CLI Output](#beautiful-cli-output)
- [Development](#development)
  - [Build & Test](#build--test)
  - [Project Structure](#project-structure)

---

## Prerequisites

Sebelum memulai, pastikan sistem Anda telah terinstal:

- **Java 17 atau lebih tinggi**
- **Maven 3.6 atau lebih tinggi**

### 🎥 Tutorial Instalasi

Untuk panduan lengkap instalasi Java dan Maven, silakan tonton video tutorial berikut:

**[Tutorial Instalasi Java](https://youtu.be/jiUxHm9l1KY?si=WUwVFai5gBDDHUOQ)**
<br/>
**[Tutorial Instalasi Maven](https://youtu.be/VYA7NzIZFdg?si=rkO0ZjgbgFUFGhyo)**

### Verifikasi Instalasi

Setelah instalasi selesai, verifikasi dengan menjalankan perintah berikut di terminal:

```bash
# Cek versi Java
java -version
# Output yang diharapkan: java version "17.x.x" atau lebih tinggi

# Cek versi Maven
mvn -version
# Output yang diharapkan: Apache Maven 3.6.x atau lebih tinggi
```

---

## Quick Start

### 1. Build Project

```bash
mvn clean package
```

### 2. Menjalankan Kompiler

```bash
# Basic usage (dengan UI CLI cantik)
java -jar target/kompiler-typescript-kelompok-2.jar xtuml-model.json

# Dengan Maven (mode development)
mvn exec:java \
  -Dexec.mainClass="kelompok.dua.maven.XtumlCompilerCli" \
  -Dexec.args="xtuml-model.json --clean --debug"

# Dengan opsi lengkap
java -jar target/kompiler-typescript-kelompok-2.jar xtuml-model.json \
  --output ./generated-ts \
  --debug \
  --clean \
  --force

# Nonaktifkan warna (untuk CI/CD)
java -jar target/kompiler-typescript-kelompok-2.jar xtuml-model.json --no-color
```

---

## CLI Options

| Option       | Short | Deskripsi                                   | Default    |
| ------------ | ----- | ------------------------------------------- | ---------- |
| `--output`   | `-o`  | Direktori output file TypeScript            | `./output` |
| `--debug`    |       | Aktifkan debug logging                      | `false`    |
| `--force`    | `-f`  | Paksa overwrite jika output sudah ada       | `false`    |
| `--clean`    |       | Bersihkan direktori output sebelum generate | `false`    |
| `--no-color` |       | Nonaktifkan colored output (untuk CI/CD)    | `false`    |
| `--help`     | `-h`  | Tampilkan help message                      | —          |
| `--version`  | `-V`  | Tampilkan versi aplikasi                    | —          |

### CLI Usage

```text
Usage: xtuml-ts-compiler [-fhvV] [--clean] [-o=<outputDir>] <inputFile>

Parameters:
  <inputFile>            Path ke file JSON berisi model xTUML

Options:
  -o, --output=<outputDir>   Direktori output (default: ./output)
  -v, --debug              Enable debug logging
  -f, --force                Force overwrite output directory
      --clean                Bersihkan output sebelum generate
  -h, --help                 Show help message
  -V, --version              Show version information
```

---

## Contoh Model Input (JSON)

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

---

## Struktur Model JSON

### Domain

```json
{
  "name": "Domain Name",
  "key_letter": "DN",
  "classes": [],
  "relationships": []
}
```

### Class Definition

```json
{
  "entity_type": "class | superclass | passive_class",
  "name": "ClassName",
  "key_letter": "CL",
  "inherits_from": "ParentClass",
  "is_abstract": true,
  "description": "Description",
  "attributes": [],
  "state_machine": {}
}
```

### Attribute

```json
{
  "name": "AttributeName",
  "data_type": "boolean | string | integer | real | date | timestamp | id",
  "attribute_type": "naming | descriptive | referential",
  "default_value": "DefaultValue"
}
```

#### Tipe Data Inti

- `boolean` → `boolean`
- `string` → `string`
- `integer` → `number`
- `real` → `number`
- `date` → `Date`
- `timestamp` → `Date`
- `id` → `string`

> **Catatan:** Alias lama seperti `uuid`, `int`, `float`, `double`, `datetime`, dan `state` tetap didukung (backward compatibility).

---

## State Machine & Action Language

### Definisi State Machine

```json
{
  "initial_state": "InitialState",
  "states": [{ "name": "State1" }, { "name": "State2" }],
  "transitions": []
}
```

### Operation Definition

```json
{
  "name": "operationName",
  "parameters": [{ "name": "paramName", "type": "string | integer | date | boolean" }],
  "actions": []
}
```

### Action Steps

**Update Action**

```json
{
  "type": "update",
  "target": "this",
  "attribute": "AttributeName",
  "value": "NewValue"
}
```

**Log Action**

```json
{
  "type": "log",
  "message": "Message with ${self.AttributeName} and ${parameterName}."
}
```

**Template Variables**

- `${self.AttributeName}` → atribut objek saat ini
- `${parameterName}` → parameter operation

---

## Relationship

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
    "attributes": []
  }
}
```

---

## Example Output Structure

```text
output/
├── index.ts
└── manajemenCivitasAkademika/
    ├── index.ts
    ├── iperson.ts
    ├── person.ts
    ├── imahasiswa.ts
    ├── mahasiswa.ts
    ├── idosen.ts
    ├── dosen.ts
    ├── imatakuliah.ts
    ├── matakuliah.ts
    ├── ikrs.ts
    ├── krs.ts
    ├── ijadwalajar.ts
    └── jadwalajar.ts
```

---

## Beautiful CLI Output

```text
════════════════════════════════════════════════════════════
║             xTUML TypeScript Compiler v1.1.0             ║
║                    xTUML Model Compiler                  ║
════════════════════════════════════════════════════════════

ℹ️  Input  : xtuml-model.json
ℹ️  Output : ./output
────────────────────────────────────────────────────────────
ℹ️  Validating input file...
✅ Input file validated
ℹ️  Parsing xTUML model...
✅ Model successfully parsed
ℹ️  Generating TypeScript files...
✅ TypeScript generation completed
────────────────────────────────────────────────────────────
✅ GENERATION COMPLETE
ℹ️  Execution Time: 431ms
ℹ️  Output Location: ./output
```

---

## Development

### Build & Test

```bash
# Compile project
mvn compile

# Run tests
mvn test

# Build JAR package
mvn package
```

### Project Structure

Proyek ini menggunakan:

- **Java 17+** sebagai runtime
- **Maven** untuk build automation
- **Picocli** untuk CLI interface
- **Jackson** untuk JSON parsing

---
