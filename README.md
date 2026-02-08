Getting acquainted with Java 21 and Springboot 3

Experiment to implement a [Result<T, E> class](src/main/java/dev/gertjanassies/filament/util/Result.java) like in Rust to avoid having to throw exceptions

The application itself is a command line util that allows you to maintain a list of filaments and do cost calculation, based on the length of filament used in a certain model. most slicers will give you this after they've sliced a 3D model

```
~> filament help
AVAILABLE COMMANDS

Built-In Commands
       help: Display help about available commands

Calculate Command
       calculate: Calculates the costs for a print. Usage: calculate <code> <length in cm>

Filament Commands
       add: Adds a new filament to the collection. Usage: add <code> <manufacturer> <type> <color> <diameter (mm)> <price (euro)> <weight (grams)
       get: Gets a filament by its code. Usage: get <code>
       list: Lists all filaments in the collection
       delete: Deletes a filament by its code. Usage: delete <code>

Version Command
       version: Displays the application version

~> filament add FL_PLA Fiberlogy PLA "Mineral marble" 1.75 25 750
Filament added successfully:
┌────────────┬──────────────┐
│Code        │FL_PLA        │
├────────────┼──────────────┤
│Manufacturer│Fiberlogy     │
├────────────┼──────────────┤
│Type        │PLA           │
├────────────┼──────────────┤
│Color       │Mineral marble│
├────────────┼──────────────┤
│Diameter    │1.75 mm       │
├────────────┼──────────────┤
│Price       │€25.00        │
├────────────┼──────────────┤
│Weight      │750g          │
└────────────┴──────────────┘

 ~> filament list
┌───────┬────────────┬────┬──────────────┬────────┬──────┬──────┐
│Code   │Manufacturer│Type│Color         │Diameter│Price │Weight│
├───────┼────────────┼────┼──────────────┼────────┼──────┼──────┤
│CF_nGEN│ColorFabb   │nGEN│Lightgrey     │1.75 mm │€35.00│750g  │
├───────┼────────────┼────┼──────────────┼────────┼──────┼──────┤
│FL_PLA │Fiberlogy   │PLA │Mineral marble│1.75 mm │€25.00│750g  │
├───────┼────────────┼────┼──────────────┼────────┼──────┼──────┤
│PPETG  │Prusa       │PETG│green         │1.75 mm │€27.50│750g  │
├───────┼────────────┼────┼──────────────┼────────┼──────┼──────┤
│PPLA   │Prusa       │PLA │Natural       │1.75 mm │€25.00│750g  │
└───────┴────────────┴────┴──────────────┴────────┴──────┴──────┘

~> filament calculate PPLA 42
┌─────────────┬──────┐
│Filament Code│PPLA  │
├─────────────┼──────┤
│Weight       │1.25 g│
├─────────────┼──────┤
│Cost         │€ 0.04│
└─────────────┴──────┘

```