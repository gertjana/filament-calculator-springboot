Getting acquainted with Java 21 and Springboot 3

Experiment to implement a [Result<T, E> class](src/main/java/dev/gertjanassies/filament/util/Result.java) like in Rust to avoid having to throw exceptions

The application itself is a command line util that allows you to maintain a list of filaments and do cost calculation, based on the length of filament used in a certain model. most slicers will give you this after they've sliced a 3D model

```
~> filament help
AVAILABLE COMMANDS

Built-In Commands
       help: Display help about available commands

Calculate Command
       calculate: Calculates the costs for a print. Usage: calculate <id> <length in cm>

Filament Commands
       add: Adds a new filament to the collection. Usage: add <color> <filamentTypeId> <price> <weight>
       get: Gets a filament by its id. Usage: get <id>
       list: Lists all filaments in the collection
       delete: Deletes a filament by its id. Usage: delete <id>

Filament Type Commands
       type-add: Adds a new filament type. Usage: type-add <name> <manufacturer> <description> <type> <diameter> <nozzleTemp> <bedTemp> <density>
       type-delete: Deletes a filament type by its id. Usage: type-delete <id>
       type-get: Gets a filament type by its id. Usage: type-get <id>
       type-list: Lists all filament types

Version Command
       version: Displays the application version

# First, add a filament type (ID is auto-generated)
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

# View all filament types
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

# List all filament spools (joins with type information)
~> filament list
┌──┬──────────────────┬────────────┬──────┬────────┬───────────┬─────────┬────────┬──────────────┬──────┬──────┐
│ID│Name              │Manufacturer│Type  │Diameter│Nozzle Temp│Bed Temp │Density │Color         │Price │Weight│
├──┼──────────────────┼────────────┼──────┼────────┼───────────┼─────────┼────────┼──────────────┼──────┼──────┤
│1 │Prusa PLA         │Prusa       │PLA   │1.75 mm │190-220°C  │50-60°C  │1.24    │Natural       │€24.99│1000g │
├──┼──────────────────┼────────────┼──────┼────────┼───────────┼─────────┼────────┼──────────────┼──────┼──────┤
│2 │Prusa PLA         │Prusa       │PLA   │1.75 mm │190-220°C  │50-60°C  │1.24    │Copper        │€24.99│1000g │
├──┼──────────────────┼────────────┼──────┼────────┼───────────┼─────────┼────────┼──────────────┼──────┼──────┤
│3 │Prusa PLA         │Prusa       │PLA   │1.75 mm │190-220°C  │50-60°C  │1.24    │Black         │€24.99│1000g │
├──┼──────────────────┼────────────┼──────┼────────┼───────────┼─────────┼────────┼──────────────┼──────┼──────┤
│4 │ColorFabb nGEN    │ColorFabb   │nGEN  │1.75 mm │220-250°C  │70-85°C  │1.28    │Lightgrey     │€25.54│1000g │
├──┼──────────────────┼────────────┼──────┼────────┼───────────┼─────────┼────────┼──────────────┼──────┼──────┤
│5 │Prusa PETG        │Prusa       │PETG  │1.75 mm │220-250°C  │70-85°C  │1.27    │Green         │€24.99│1000g │
├──┼──────────────────┼────────────┼──────┼────────┼───────────┼─────────┼────────┼──────────────┼──────┼──────┤
│9 │Fiberlogy Easy PLA│Fiberlogy   │PLA   │1.75 mm │190-220°C  │50-60°C  │1.24    │Mineral marble│€25.00│850g  │
└──┴──────────────────┴────────────┴──────┴────────┴───────────┴─────────┴────────┴──────────────┴──────┴──────┘

# Calculate cost for a print
~> filament calculate 1 4200
┌────────────┬───────┐
│Filament ID │1      │
├────────────┼───────┤
│Weight      │125.02 g│
├────────────┼───────┤
│Cost        │€ 3.12│
└────────────┴───────┘

```