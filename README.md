# SXBank CSV to Excel Converter

A Java Maven application for SXBank that reads CSV/flat files and generates formatted Excel files based on XML configuration.

## Features

- **XML Configuration**: Define workbook + sheet-level formatting entirely via XML
- **Multi-Sheet Workbooks**: Drive multiple tabs from independent CSV sources
- **Multiple Separators**: Support for comma, semicolon, pipe, and tab separators
- **Rich Formatting**: 
  - Column-specific formatting (bold, colors, alignment, width)
  - Header formatting (bold, background color, font color, font size)
  - Data type support (TEXT, NUMBER, DATE, CURRENCY)
  - Custom number and date formats
- **Extensible Design**: Built with future complex formatting options in mind

## Requirements

**Option 1: Local Installation**
- Java 17 or higher
- Maven 3.6+

**Option 2: Docker (No Local Installation Required)**
- Docker Desktop
- Use Docker commands to build and run (see below)

## Project Structure

```
src/main/java/com/sxbank/report/
├── Main.java                    # Entry point
├── config/
│   ├── ConfigReader.java        # XML configuration parser
│   ├── Configuration.java       # Main configuration model
│   ├── ColumnConfig.java        # Column configuration model
│   └── FormatConfig.java        # Formatting configuration model
├── parser/
│   ├── CsvParser.java           # CSV parser (OpenCSV)
│   └── FlatFileParser.java      # Flat file parser
├── excel/
│   ├── ExcelGenerator.java      # Excel file generator
│   └── ExcelStyler.java         # Cell and style formatting
└── util/
    └── DataTypeConverter.java   # Data type conversion utilities
```

## Building the Project

### Using Docker (No Java Installation Required)

**Build the project:**
```powershell
docker run --rm -v ${PWD}:/workspace -w /workspace maven:3.9-eclipse-temurin-17 mvn clean package
```

Or use the helper script:
```powershell
.\docker-build.bat
```

This will create a JAR file in the `target` directory: `csv-to-excel-converter-1.0.0.jar`

### Using Local Java Installation

```bash
mvn clean package
```

## Usage

### Using Docker (No Java Installation Required)

```powershell
docker run --rm -v ${PWD}:/workspace -w /workspace maven:3.9-eclipse-temurin-17 java -jar target/csv-to-excel-converter-1.0.0.jar config.xml
```

Or use the helper script:
```powershell
.\docker-run.bat config.xml
```

### Using Local Java Installation

```bash
java -jar target/csv-to-excel-converter-1.0.0.jar config.xml
```

This will generate `post_trade_profitability_report.xlsx` containing all configured sheets defined in `config.xml`.

## Configuration File Format

The `config.xml` file now supports multi-sheet workbooks:

- **workbook**: output `.xlsx` file path
- **sheets**: collection of `<sheet>` elements, each containing:
  - `name`: worksheet tab name
  - `title`: optional merged title row
  - `input`: file/separator/hasHeader for that sheet’s CSV
  - `totalsHeader`: optional merged “Totals” band above the table
  - `headerFormat`: styling for the table header row
  - `columns`: per-column format definitions

### Example Configuration

See `config.xml` for the full multi-sheet example used in this repo.

#### Totals Header Block

You can optionally add a scoreboard-style header by including:

```xml
<totalsHeader>
    <label>Totals</label>
    <startIndex>6</startIndex> <!-- zero-based -->
    <endIndex>8</endIndex>
    <backgroundColor>#0B4F8A</backgroundColor>
    <fontColor>#FFFFFF</fontColor>
</totalsHeader>
```

This renders a merged “Totals” cell above the metric columns while keeping the left-hand columns untouched.

### Supported Data Types

- `TEXT`: Plain text
- `NUMBER`: Numeric values
- `DATE`: Date values (requires dateFormat)
- `CURRENCY`: Currency values (supports numberFormat)

### Supported Separators

- `,` (comma)
- `;` (semicolon)
- `|` (pipe)
- `\t` or `\t` (tab)

### Formatting Options

**Column Formatting:**
- `bold`: true/false
- `backgroundColor`: Hex color code (e.g., #FFFF00)
- `fontColor`: Hex color code (e.g., #000000)
- `width`: Column width in characters
- `alignment`: LEFT, CENTER, RIGHT
- `includeTotals`: true/false (auto-sum column above the header)
- `numberFormat`: Excel number format string (e.g., $#,##0.00)
- `dateFormat`: Java date format (e.g., yyyy-MM-dd)

**Header Formatting:**
- `bold`: true/false
- `backgroundColor`: Hex color code
- `fontColor`: Hex color code
- `fontSize`: Font size in points

## Sample Files

- `config.xml`: Multi-sheet configuration driving the workbook
- `data_master.csv`: Master trade tape
- `data_crm.csv`: CRM-style client segmentation view
- `data_coverage.csv`: Coverage cluster financials
- `data_monthly.csv`: Month-to-date product mix

Each sheet demonstrates a different layout (trade-level, CRM summary, coverage metrics, product mix) with independent formatting rules.

## Error Handling

The application validates:
- XML structure and required fields
- File existence
- Separator validity
- Column index bounds
- Color code format

## Future Enhancements

Potential follow-ups:
- Conditional formatting rules
- Derived metrics and formulas
- Cell borders/pattern presets
- Advanced data validation
- Chart generation

## Dependencies

- Apache POI 5.2.5 (Excel manipulation)
- OpenCSV 5.8 (CSV parsing)
- JAXB 2.3.1 (XML parsing)
- SLF4J (Logging)

