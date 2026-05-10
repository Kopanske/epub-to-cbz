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

Linux/Mac:

````shell
java -jar ePubToCbz-1.3.0-all.jar ./myEbooks ./myComicBooks
````

Windows:

````shell
java -jar ePubToCbz-1.3.0-all.jar E:\myEbooks E:\myComicBooks
````

- **Input directory**: contains `.epub` files
- **Output directory**: CBZ files will be written there

---

## Output

Example:  
Success:  
📖 MyEpub.epub 📤 📄 (32) 📦 ✅  
Error:  
📖 MyEpub.epub 📤 📄 (32) 📦 ❌ Error: r:\comics\MyEpub.epub.cbz (Access is denied)

| Symbol | Description                          |
|--------|--------------------------------------|
| 📖     | Processing of eBook started          |
| 📤     | Picture extraction successful        |
| 📄     | Amount of found pictures             |
| 📦     | Creation of Archive started          |
| ✅      | Creation of the comic was successful |
| ❌      | Error during creation                |

---

## Installation

1. Download the latest release from the GitHub releases page:
   https://github.com/Kopanske/epub-to-cbz/releases
2. Run it using `java -jar`

---

## Troubleshooting

### Windows Command Prompt

If you encounter issues with character encoding in the Windows Command Prompt, you can switch to UTF-8 encoding by
running:

```shell
chcp 65001
```

This should work for PowerShell ann cmd.

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

This software is published under the MIT License. See the `LICENSE` file for details.
