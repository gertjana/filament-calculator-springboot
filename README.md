# Filament Manager

A command line application for managing 3D printer filament inventory and calculating print costs.

## Features

- **Filament Type Management**: Define filament types with specifications (material, diameter, temperature settings, density)
- **Filament Spool Tracking**: Track individual spools with color, price, and weight
- **Cost Calculation**: Calculate print costs based on filament length (from slicer output)
- **Auto-increment IDs**: IDs are automatically generated when adding items
- **Interactive Mode**: Commands prompt for missing arguments interactively
- **Multiple Output Formats**: Display data as tables, JSON, or CSV
- **Result<T, E> Pattern**: Rust-inspired error handling without exceptions ([implementation](src/main/java/dev/gertjanassies/filament/util/Result.java))

## Technology Stack

- Java 21
- Spring Boot 3
- Spring Shell (interactive CLI)
- GraalVM Native Image support
- JLine for terminal input

## Usage

### Available Commands

```
AVAILABLE COMMANDS

Built-In Commands
       help: Display help about available commands

Calculate Command
       calculate: Calculates the costs for a print. Usage: calculate <id> <length in cm>

Filament Commands
       add: Adds a new filament to the collection. Usage: add [<color> <filamentTypeId> <price> <weight>]
       get: Gets a filament by its id. Usage: get <id> [-o|--output <format>]
       list: Lists all filaments in the collection [-o|--output <format>]
       delete: Deletes a filament by its id. Usage: delete <id>

Filament Type Commands
       type-add: Adds a new filament type. Usage: type-add [<name> <manufacturer> <description> <type> <diameter> <nozzleTemp> <bedTemp> <density>]
       type-delete: Deletes a filament type by its id. Usage: type-delete <id>
       type-get: Gets a filament type by its id. Usage: type-get <id> [-o|--output <format>]
       type-list: Lists all filament types [-o|--output <format>]

Version Command
       version: Displays the application version
```

**Output Formats**: Use `-o` or `--output` with `table` (default), `json`, or `csv` (case-insensitive)

**Interactive Mode**: Commands with optional arguments will prompt for input if not provided

### Examples

#### Adding a Filament Type (with arguments)

```bash
~> filament type-add "Fiberlogy Easy PLA" Fiberlogy "Easy to print PLA" PLA 1.75 "190-220" "50-60" 1.24
Filament type added successfully:
┌──────────────┬──────────────────────┐
│ID            │4                     │
├──────────────┼──────────────────────┤
│Name          │Fiberlogy Easy PLA    │
├──────────────┼──────────────────────┤
│Manufacturer  │Fiberlogy             │
├──────────────┼──────────────────────┤
│Description   │Easy to print PLA     │
├──────────────┼──────────────────────┤
│Type          │PLA                   │
├──────────────┼──────────────────────┤
│Diameter      │1.75 mm               │
├──────────────┼──────────────────────┤
│Nozzle Temp   │190-220°C             │
├──────────────┼──────────────────────┤
│Bed Temp      │50-60°C               │
├──────────────┼──────────────────────┤
│Density       │1.24 g/cm³            │
└──────────────┴──────────────────────┘
```

#### Interactive Mode (prompts for missing arguments)

```bash
~> filament type-add
Name: Fiberlogy Easy PLA
Manufacturer: Fiberlogy
Description: Easy to print PLA
Type (PLA/PETG/ABS/etc): PLA
Diameter (mm): 1.75
Nozzle Temperature (e.g., 190-220): 190-220
Bed Temperature (e.g., 50-60): 50-60
Density (g/cm³): 1.24
Filament type added successfully:
# ... (output as shown above)
```

```bash
~> filament add
Color: Blue Steel
Filament Type ID: 4
Price (€): 22.50
Weight (grams): 1000
Filament added successfully:
# ... (output as shown above)
```

#### Listing Filament Types (Table Format - Default)

```bash
~> filament type-list
┌──┬──────────────────┬────────────┬──────────────────┬──────┬────────┬───────────┬─────────┬───────────┐
│ID│Name              │Manufacturer│Description       │Type  │Diameter│Nozzle Temp│Bed Temp │Density    │
├──┼──────────────────┼────────────┼──────────────────┼──────┼────────┼───────────┼─────────┼───────────┤
│2 │ColorFabb nGEN    │ColorFabb   │nGEN copolyester  │nGEN  │1.75 mm │220-250°C  │70-85°C  │1.28 g/cm³ │
├──┼──────────────────┼────────────┼──────────────────┼──────┼────────┼───────────┼─────────┼───────────┤
│4 │Fiberlogy Easy PLA│Fiberlogy   │Easy to print PLA │PLA   │1.75 mm │190-220°C  │50-60°C  │1.24 g/cm³ │
├──┼──────────────────┼────────────┼──────────────────┼──────┼────────┼───────────┼─────────┼───────────┤
│1 │Prusa PLA         │Prusa       │Standard PLA      │PLA   │1.75 mm │190-220°C  │50-60°C  │1.24 g/cm³ │
├──┼──────────────────┼────────────┼──────────────────┼──────┼────────┼───────────┼─────────┼───────────┤
│3 │Prusa PETG        │Prusa       │Standard PETG     │PETG  │1.75 mm │220-250°C  │70-85°C  │1.27 g/cm³ │
└──┴──────────────────┴────────────┴──────────────────┴──────┴────────┴───────────┴─────────┴───────────┘
```

#### JSON Output Format

**Filament Types:**
```bash
~> filament type-list -o json
[ {
  "id" : 2,
  "name" : "ColorFabb nGEN",
  "manufacturer" : "ColorFabb",
  "description" : "nGEN copolyester",
  "type" : "nGEN",
  "diameter" : 1.75,
  "nozzleTemp" : "220-250",
  "bedTemp" : "70-85",
  "density" : 1.28
}, {
  "id" : 4,
  "name" : "Fiberlogy Easy PLA",
  "manufacturer" : "Fiberlogy",
  "description" : "Easy to print PLA",
  "type" : "PLA",
  "diameter" : 1.75,
  "nozzleTemp" : "190-220",
  "bedTemp" : "50-60",
  "density" : 1.24
} ]
```

**Filaments (with nested type information):**
```bash
~> filament list -o json
[ {
  "id" : 1,
  "color" : "Natural",
  "price" : 24.99,
  "weight" : 1000,
  "filamentType" : {
    "id" : 1,
    "name" : "Prusa PLA",
    "manufacturer" : "Prusa",
    "description" : "Standard PLA",
    "type" : "PLA",
    "diameter" : 1.75,
    "nozzleTemp" : "190-220",
    "bedTemp" : "50-60",
    "density" : 1.24
  }
}, {
  "id" : 9,
  "color" : "Mineral marble",
  "price" : 25.00,
  "weight" : 850,
  "filamentType" : {
    "id" : 4,
    "name" : "Fiberlogy Easy PLA",
    "manufacturer" : "Fiberlogy",
    "description" : "Easy to print PLA",
    "type" : "PLA",
    "diameter" : 1.75,
    "nozzleTemp" : "190-220",
    "bedTemp" : "50-60",
    "density" : 1.24
  }
} ]
```

**Single filament:**
```bash
~> filament get 9 -o json
{
  "id" : 9,
  "color" : "Mineral marble",
  "price" : 25.00,
  "weight" : 850,
  "filamentType" : {
    "id" : 4,
    "name" : "Fiberlogy Easy PLA",
    "manufacturer" : "Fiberlogy",
    "description" : "Easy to print PLA",
    "type" : "PLA",
    "diameter" : 1.75,
    "nozzleTemp" : "190-220",
    "bedTemp" : "50-60",
    "density" : 1.24
  }
}
```

#### CSV Output Format

**Filament Types:**
```bash
~> filament type-list --output csv
ID,Name,Manufacturer,Description,Type,Diameter,Nozzle Temp,Bed Temp,Density
2,ColorFabb nGEN,ColorFabb,nGEN copolyester,nGEN,1.75 mm,220-250°C,70-85°C,1.28 g/cm³
4,Fiberlogy Easy PLA,Fiberlogy,Easy to print PLA,PLA,1.75 mm,190-220°C,50-60°C,1.24 g/cm³
1,Prusa PLA,Prusa,Standard PLA,PLA,1.75 mm,190-220°C,50-60°C,1.24 g/cm³
3,Prusa PETG,Prusa,Standard PETG,PETG,1.75 mm,220-250°C,70-85°C,1.27 g/cm³
```

**Filaments (with flattened type information):**
```bash
~> filament list --output csv
ID,Name,Manufacturer,Type,Diameter,Nozzle Temp,Bed Temp,Density,Color,Price,Weight
1,Prusa PLA,Prusa,PLA,1.75 mm,190-220°C,50-60°C,1.24,Natural,€24.99,1000g
9,Fiberlogy Easy PLA,Fiberlogy,PLA,1.75 mm,190-220°C,50-60°C,1.24,Mineral marble,€25.00,850g
```

**Single filament (same format as list):**
```bash
~> filament get 9 -o csv
ID,Name,Manufacturer,Type,Diameter,Nozzle Temp,Bed Temp,Density,Color,Price,Weight
9,Fiberlogy Easy PLA,Fiberlogy,PLA,1.75 mm,190-220°C,50-60°C,1.24,Mineral marble,€25.00,850g
```

#### Adding and Listing Filament Spools

```bash
# Add a filament spool (references filament type by ID)
~> filament add "Mineral marble" 4 25.00 850
Filament added successfully:
┌─────────────────┬──────────────────┐
│ID               │9                 │
├─────────────────┼──────────────────┤
│Color            │Mineral marble    │
├─────────────────┼──────────────────┤
│Price            │€25.00            │
├─────────────────┼──────────────────┤
│Weight           │850g              │
├─────────────────┼──────────────────┤
│Filament Type ID │4                 │
├─────────────────┼──────────────────┤
│Type Name        │Fiberlogy Easy PLA│
├─────────────────┼──────────────────┤
│Manufacturer     │Fiberlogy         │
├─────────────────┼──────────────────┤
│Description      │Easy to print PLA │
├─────────────────┼──────────────────┤
│Type             │PLA               │
├─────────────────┼──────────────────┤
│Diameter         │1.75 mm           │
├─────────────────┼──────────────────┤
│Nozzle Temp      │190-220°C         │
├─────────────────┼──────────────────┤
│Bed Temp         │50-60°C           │
├─────────────────┼──────────────────┤
│Density          │1.24 g/cm³        │
└─────────────────┴──────────────────┘
```

#### Listing Filament Spools (with type information)

```bash
~> filament list
┌──┬──────────────────┬────────────┬──────┬────────┬───────────┬─────────┬────────┬──────────────┬──────┬──────┐
│ID│Name              │Manufacturer│Type  │Diameter│Nozzle Temp│Bed Temp │Density │Color         │Price │Weight│
├──┼──────────────────┼────────────┼──────┼────────┼───────────┼─────────┼────────┼──────────────┼──────┼──────┤
│1 │Prusa PLA         │Prusa       │PLA   │1.75 mm │190-220°C  │50-60°C  │1.24    │Natural       │€24.99│1000g │
├──┼──────────────────┼────────────┼──────┼────────┼───────────┼─────────┼────────┼──────────────┼──────┼──────┤
│9 │Fiberlogy Easy PLA│Fiberlogy   │PLA   │1.75 mm │190-220°C  │50-60°C  │1.24    │Mineral marble│€25.00│850g  │
└──┴──────────────────┴────────────┴──────┴────────┴───────────┴─────────┴────────┴──────────────┴──────┴──────┘
```

#### Calculating Print Cost

```bash
~> filament calculate 1 4200
┌────────────┬───────┐
│Filament ID │1      │
├────────────┼───────┤
│Weight      │125.02 g│
├────────────┼───────┤
│Cost        │€ 3.12│
└────────────┴───────┘
```

## Building & Running

### Build with Gradle

```bash
# Build JAR
./gradlew build

# Run with Spring Boot
./gradlew bootRun

# Run tests
./gradlew test
```

### Build Native Image (GraalVM)

```bash
# Build native executable
./gradlew nativeCompile

# Copy to PATH (optional)
cp build/native/nativeCompile/filament ~/bin/
```

### Running

```bash
# JAR
java -jar build/libs/filament-*.jar

# Native executable
./build/native/nativeCompile/filament

# Or if copied to PATH
filament
```

## Data Storage

Filament data is stored as JSON files in `~/.filament/`:
- `filaments.json` - Filament spools inventory
- `filament-types.json` - Filament type definitions

## License

This is a personal learning project exploring Java 21, Spring Boot 3, and functional error handling patterns.