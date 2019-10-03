package tk.npecode.config.tests;

import org.junit.Test;
import tk.npecode.config.Configuration;
import tk.npecode.config.annotation.ConfigurationClass;
import tk.npecode.config.annotation.Option;

import java.io.File;

public class ConfigurationTests {
    private static final File JSON_TEST_FILE = new File("npecode.config.test.json");

    @Test
    public void testJson() throws Throwable {
        Throwable e = null;
        try {
            Configuration configuration = Configuration.newJsonConfig(JSON_TEST_FILE)
                    .addClass(TestConfiguration.class)
                    .addClass(TestConfiguration2.class)
                    .initialize();
        } catch (Throwable t) {
            e = t;
        }
        if (JSON_TEST_FILE.exists() && !JSON_TEST_FILE.delete()) {
            JSON_TEST_FILE.deleteOnExit();
        }
        if (e != null) {
            throw e;
        }
    }

    @ConfigurationClass("test")
    public static class TestConfiguration {
        @Option("test_1")
        private static TestObject testObj = new TestObject("ok", 91);
    }

    @ConfigurationClass
    public static class TestConfiguration2 {
        @Option
        private static TestObject test = new TestObject("test2", 42);

        @Option("test_value_2")
        private static int value2 = 30;
    }

    public static class TestObject {
        private final String foo;
        private final int bar;

        public TestObject(String foo, int bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public int getBar() {
            return bar;
        }
    }
}
