## WizCrypt

About the application:

Cross platform personal commandline encryption program. It runs in 2 modes:

1. Encryption mode: Whatever file is given as input, the same is encrypted, and saved with .wiz
   extension. You have to pass command line argument `-e' for operating in this mode.

2. Decryption mode: Files with .wiz extension are converted back into ordinary file. You have to pass command line argument `-d' for operating in this mode.

### Requirement:

JRE 1.6 or above.

### Usage:

```
$ java -jar wizcrypt-XX-jar-with-dependencies.jar --help
```

[Note: `XX' to be substituted with the version number.]

### Example usage:

```
$ java -jar wizcrypt-XX-jar-with-dependencies.jar -e -p mypwd *.jpg
$ java -jar wizcrypt-XX-jar-with-dependencies.jar -d -p mypwd *.jpg.wiz
```
