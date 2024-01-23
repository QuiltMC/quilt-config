# Quilt Config

---

Quilt Config is a library designed to facilitate the creation and
management of config files.

## Usage

Quilt Config can be imported from the [Quilt maven](https://maven.quiltmc.org/repository/release/) using the following code in your `build.gradle`:

```groovy
repositories {
    // tell gradle where to find quilt config's artifacts
    maven { url = "https://maven.quiltmc.org/repository/release/" }
}

dependencies {
    // the main quilt config API. replace <version> with the latest version
    implementation("org.quiltmc:quilt-config:<version>")

    // optional: a serializer to use when saving and reading the config
    // you can omit this and implement your own serializer
    // replace <serializer> with either "json5" or "toml" and <version> with the latest version
    implementation("org.quiltmc.quilt-config.serializers:<serializer>:<version>")
}
```
