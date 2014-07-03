### Build laser-svg

1. install Maven 3.0.5
1. `git clone https://github.com/firepick1/laser-svg`
1. `cd laser-svg`
1. `mvn clean install`

### Clean SVG File

1. `java -jar target/laser-svg*jar myinput.svg > myoutput.svg`
1. Launch Inkscape on myoutput.svg
1. **View | Display | Outline**
1. Remove unwanted line segments (e.g., border waste cuts)
1. Drag select all objects (do not use CTRL-A)
1. **Path | Object to Path**
1. **Path | Combine**
1. Copy file to Ponoko template, etc.
