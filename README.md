
# epub-to-cbz

A simple command-line tool to convert eBooks in **EPUB** format into **CBZ**
(Comic Book ZIP) archives.

The tool extracts **only the embedded images** from EPUB files and packs them
into CBZ files, making them suitable for comic book readers.

---

## Features

- Converts EPUB files to CBZ
- Extracts images only (no text, metadata, or styling)
- Works on whole directories
- Simple CLI usage
- Self-contained fat JAR

---

## Requirements

- Java Runtime **21 or higher**

Check your Java version with:

    java --version

---

## Usage

    java -jar ePubToCbz-<version>-all.jar <input directory> <output directory>

### Example

    java -jar ePubToCbz-1.0.5-all.jar ./myEbooks ./myComicBooks

- **Input directory**: contains `.epub` files
- **Output directory**: CBZ files will be written there

---

## Installation

1. Download the latest release from the GitHub releases page:
   https://github.com/Kopanske/epub-to-cbz/releases
2. Run it using `java -jar`

---

## Release Process (for maintainers)

To create a new release:

    git tag v1.0.6
    git push origin v1.0.6

A GitHub Actions workflow will automatically:

- build the fat JAR
- generate a SHA-256 checksum
- create a GitHub release with attached artifacts

---

## License

MIT
