# epub-to-cbz
Tool to convert a ebook to a comic book.

This tool converts ebooks in ePub format to the cbz (Comic Book reader, compressed with zip).  
Its converts only the pictures.

## Requirements
* Java runtime 21 or higher

## Usage
java -jar ePubToCbz-1.0.5-all.jar [input directory] [output directory]

Example:
```bash
java -jar ePubToCbz-1.0.5-all.jar c:\myEbooks c:\myComicBooks
```

## Release
To create a new release create a tag and push it to GitHub.

Example:
```bash
git tag v1.0.6
git push origin v1.0.6
```

Releases can be found on the releases page of this project:  
https://github.com/Kopanske/epub-to-cbz/releases
