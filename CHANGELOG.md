# 1.3.3
Setting a world record for new quickest release, this time we've fixed a major issue with JSON serialization.

- fix JSON serializers not apply custom serialized names on sections

# 1.3.2
Quilt Config `1.3.2` is an even quicker release than the last one.

- bump night-config to `3.8.1`

# 1.3.1
Quilt Config 1.3.1 is a quick bugfix release. One step closer to the bug free experience.

- add `min()` and `max()` API methods for getting those values on range constraints
- add pretty printing for range comments
- fix `default:` comments being surrounded by an `Optional[]` block

# 1.3.0
Quilt Config 1.3.0 brings a veritable treasure trove of new annotations, bringing a whopping 5 new metadata options! It also brings improvements to processors, fixes for the old `WrappedConfig` API, and some brand-new API to top it all off. With the new features added in this update, it should finally be viable to build the automatic config screen generator we've been dreaming of!

- add `@SerializedNameConvention` annotation
    - can be applied to classes, fields, and sections
    - similar to `@SerializedName`, allows you to define a different name to be used for your config
    - contrary to `@SerializedName`, you do not manually define the names: instead, the annotation will automatically convert your fields' names to match your chosen convention. for example, if you choose `snake_case`, as is the recommendation for TOML, `veryCuteField` becomes `very_cute_field` when serialized. `@SerializedName` will always take priority over converted names!
    - the ideal way to use this is to apply it to your entire class via just one annotation!
- add `@Alias` annotation
    - can be applied to fields and sections
    - defines a previous name for the field or section, allowing you to migrate old configs. for example, if you now want a config section to be named `GeorgeSection` after you remove `Joe` and add `George`, you can use `@Alias("JoeSection")` to automatically migrate old `Joe` configs to the new `George` name
- add `@DisplayName` annotation
    - can be applied to configs, sections, and fields
    - does not have any functionality in the base quilt config API. instead, this metadata is intended to be used by *metadata processors*, other programs making use of quilt config's information. an example metadata processor, and one we're planning to build, is an automatic config screen generator that works on all mods using quilt config!
    - defines the user-facing name of the config field, for metadata processors implementing visual config editors. allows translatability!
- add `@DisplayNameConvention` annotation
    - can be applied to configs, sections, and fields
    - similar to `@DisplayName`, has no functionality in base quilt config.
    - defines a convention for transforming field names into display names, pulling from the same convention options as `@SerializedNameConvention`. for example, use `Space Separated Uppercase` to turn `superAdorableField` into `Super Adorable Field`. `@DisplayName` will always take priority over transformed names!
- add `@ChangeWarning` annotation
    - can be applied to configs, sections, and fields
    - has no functionality in base quilt config
    - used to tell visual config editors that they should show a warning before applying changes to config fields. contains lots of options for warnings: `RequiresRestart`, `Unsafe`, `Experimental`, `CustomTranslatable`, or `Custom`.
- add an overload for `setValue` with the `serialize` parameter defaulted to `true` to allow for more concise code
- allow using the `@Processor` annotation on sections
- add extensive javadoc for `@Processor` (on top of the [tutorial](https://wiki.quiltmc.org/en/configuration/advanced-configuring#using-processors) on the developer wiki!)
- allow using non-final values as config fields in `WrappedConfig`
    - the previous system would simply not work for a few types, notably `String`: due to the field being final, the JVM would inline some references to it, making them unmodifiable for us
    - now, a warning will be shown for anyone using `final` modifiers in their `WrappedConfig` classes. we recommend moving to `ReflectiveConfig`, but you can also simply remove the modifier!
- add a new API for inheriting metadata in the `ConfigBuilder`: this allows adding metadata via processors to work the same way as adding it via annotations
    - when the new `inherited` parameter of `MetadataType` is set to true, that metadata will be propagated to all children of the section or class you apply the metadata to
- fix useless default comments being added for custom serializable values that do not override `toString`
    - `# default: MySerializableClass@fe34g6` is not exactly helpful to the user
- fix documentation mentioning primitive types (`int`, `double`, etc) when the `ReflectiveConfig` API calls for the usage of classes (`Integer`, `Double`)
- add checkstyle to clean up code a bit

# 1.2.0
`1.2.0` marks the grand return of Quilt Config, after a long drought of updates. It introduces much better support for using QConf outside of [Quilt Loader](https://github.com/QuiltMC/quilt-loader), a new annotation for customising your automatically generated config files, a couple new API methods, and fixes some important bugs!

-  published default serializers for **TOML** and **JSON5**, which you can use in your projects with [these instructions](<https://github.com/QuiltMC/quilt-config#usage>). these have a few benefits:
   - the user no longer has to implement their own serializers in a project using qconf. brilliant!
   - the builtin serializers will always support the latest annotations and features, instead of making you puzzle out how to add them to yours. neat!
- implemented a new annotation: `@SerializedName(String)`. this allows you to have different names in code than the ones in the generated config files. for an example usage, you could name a field in your code `superCoolField` and then serialize it as `super_cool_field`, to conform to both java and toml conventions at the same time. wild!
- migrated to the new [quilt parsers](<https://github.com/QuiltMC/quilt-parsers>) library instead of our deprecated quilt-json5 library for JSON5 parsing. cool!
- added two new API methods:
    - `Config#getNode(Iterable<String>)`, which will return a `ValueTreeNode` representing either a config section or a config value. this solves an API gap where there was no way to grab full sections, since the similar `getValue` method would always try to cast to a value and error on sections. awesome!
    - `MetadataContainer#metadata`, which will return a typed map of all metadata on a value. solid!
- fixed two extremely high profile bugs:
    - metadata on sections was ignored. this means that you can now add serial names, comment on sections, do any metadata shenanigans to your heart's desire! fun!
    - adding dots (`.`) to a key and saving to TOML would produce an unreadable disaster. no longer! yay!
