# NPEConfig
An annotation-based configuration system built for use in NPECode projects.
## Usage
To use NPEConfig, you need a configuration class with option fields, for example:
```java
@ConfigurationClass("category" /* this is optional; if not present the fields aren't in a category */)
public class Configuration {
    @Option("option name")
    public static int theOption = 42;
    @Option // use the field name
    public static int option2 = 92;
}
```
To use this configuration class, you have to create a `Configuration` object and add the classes.
If you're using JSON-based configurations, you can use the following code:
```java
Configuration configuration = Configuration.newJsonConfig(new File("config.json"))
        .addClass(Configuration.class)
        .initialize();
configuration.save(); // the initial save is done in initialize() but you have to save manually on exit
```

### Custom configuration types
You can also use custom configuration types, like MySQL and PostgreSQL.
To do this, you need to create a configuration type class that extends `ConfigurationType`.
For examples, refer to `JSONConfigurationType`.
After doing this, the code is basically the same, except for initializing the configuration.
Instead of `Configuration.newJsonConfig(...)` you need to use `new Configuration(new CustomConfigurationType())`.