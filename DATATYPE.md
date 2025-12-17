# Tipe Data Inti

Dokumen ini menjelaskan tipe data inti yang didukung dalam xTUML model compiler.

## Tipe Data Utama

### 1. **boolean**

- **Deskripsi**: Nilai logika (benar/salah)
- **Konversi TypeScript**: `boolean`
- **Contoh nilai**: `true`, `false`
- **Contoh penggunaan**:

```json
{
  "name": "isActive",
  "data_type": "boolean",
  "attribute_type": "descriptive",
  "default_value": "true"
}
```

### 2. **string**

- **Deskripsi**: Teks atau karakter
- **Konversi TypeScript**: `string`
- **Contoh nilai**: `"John Doe"`, `"admin@example.com"`
- **Contoh penggunaan**:

```json
{
  "name": "email",
  "data_type": "string",
  "attribute_type": "descriptive"
}
```

### 3. **integer**

- **Deskripsi**: Bilangan bulat
- **Konversi TypeScript**: `number`
- **Contoh nilai**: `0`, `42`, `-10`, `1000`
- **Contoh penggunaan**:

```json
{
  "name": "age",
  "data_type": "integer",
  "attribute_type": "descriptive",
  "default_value": "0"
}
```

### 4. **real**

- **Deskripsi**: Bilangan desimal (floating point)
- **Konversi TypeScript**: `number`
- **Contoh nilai**: `3.14`, `0.5`, `-2.718`, `99.99`
- **Contoh penggunaan**:

```json
{
  "name": "price",
  "data_type": "real",
  "attribute_type": "descriptive",
  "default_value": "0.0"
}
```

### 5. **date**

- **Deskripsi**: Tanggal (tanpa waktu)
- **Konversi TypeScript**: `Date`
- **Format**: ISO 8601 (YYYY-MM-DD)
- **Contoh nilai**: `"2025-11-02"`
- **Contoh penggunaan**:

```json
{
  "name": "birthDate",
  "data_type": "date",
  "attribute_type": "descriptive"
}
```

### 6. **timestamp**

- **Deskripsi**: Tanggal dan waktu (datetime)
- **Konversi TypeScript**: `Date`
- **Format**: ISO 8601 (YYYY-MM-DDTHH:mm:ss)
- **Contoh nilai**: `"2025-11-02T14:30:00"`
- **Contoh penggunaan**:

```json
{
  "name": "createdAt",
  "data_type": "timestamp",
  "attribute_type": "descriptive"
}
```

### 7. **id**

- **Deskripsi**: Identifier unik (UUID atau string khusus)
- **Konversi TypeScript**: `string`
- **Format**: UUID v4 atau custom ID
- **Contoh nilai**: `"550e8400-e29b-41d4-a716-446655440000"`, `"USR001"`
- **Contoh penggunaan**:

```json
{
  "name": "userId",
  "data_type": "id",
  "attribute_type": "naming"
}
```

## Tipe Data Alias (Backward Compatibility)

Untuk mendukung model lama, beberapa alias masih didukung:

| Alias      | Tipe Data Inti                 |
| ---------- | ------------------------------ |
| `int`      | `integer`                      |
| `float`    | `real`                         |
| `double`   | `real`                         |
| `bool`     | `boolean`                      |
| `uuid`     | `id`                           |
| `datetime` | `timestamp`                    |
| `state`    | `string` (untuk state machine) |

## Mapping ke TypeScript

| Tipe Data xTUML | TypeScript Type |
| --------------- | --------------- |
| `boolean`       | `boolean`       |
| `string`        | `string`        |
| `integer`       | `number`        |
| `real`          | `number`        |
| `date`          | `Date`          |
| `timestamp`     | `Date`          |
| `id`            | `string`        |

## Contoh Lengkap

```json
{
  "name": "User",
  "key_letter": "USR",
  "attributes": [
    {
      "name": "userId",
      "data_type": "id",
      "attribute_type": "naming"
    },
    {
      "name": "email",
      "data_type": "string",
      "attribute_type": "descriptive"
    },
    {
      "name": "age",
      "data_type": "integer",
      "attribute_type": "descriptive",
      "default_value": "0"
    },
    {
      "name": "balance",
      "data_type": "real",
      "attribute_type": "descriptive",
      "default_value": "0.0"
    },
    {
      "name": "isActive",
      "data_type": "boolean",
      "attribute_type": "descriptive",
      "default_value": "true"
    },
    {
      "name": "birthDate",
      "data_type": "date",
      "attribute_type": "descriptive"
    },
    {
      "name": "createdAt",
      "data_type": "timestamp",
      "attribute_type": "descriptive"
    }
  ]
}
```

Output TypeScript:

```typescript
interface IUser {
  userId: string;
  email: string;
  age: number;
  balance: number;
  isActive: boolean;
  birthDate: Date;
  createdAt: Date;
}
```

## Best Practices

1. **Gunakan `id` untuk identifier**: Lebih semantik daripada `string` atau `uuid`
2. **Gunakan `integer` untuk bilangan bulat**: Lebih jelas daripada `int`
3. **Gunakan `real` untuk desimal**: Lebih jelas daripada `float` atau `double`
4. **Gunakan `timestamp` untuk datetime**: Lebih jelas daripada `datetime`
5. **Pilih `date` vs `timestamp`**:
   - Gunakan `date` jika hanya perlu tanggal (contoh: tanggal lahir)
   - Gunakan `timestamp` jika perlu tanggal dan waktu (contoh: log aktivitas)

## Validasi

Compiler akan memvalidasi:

- Tipe data harus salah satu dari tipe inti atau alias yang valid
- Tipe data yang tidak dikenal akan di-convert ke `any` di TypeScript
- Warning akan muncul jika menggunakan tipe data yang tidak standar
