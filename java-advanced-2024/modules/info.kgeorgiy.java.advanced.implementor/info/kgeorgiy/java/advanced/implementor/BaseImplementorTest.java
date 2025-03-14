package info.kgeorgiy.java.advanced.implementor;

import info.kgeorgiy.java.advanced.base.BaseTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class BaseImplementorTest extends BaseTest {
    /* package-private */ static final Path DIR = Path.of("__Test__Implementor__");

    private static final SimpleFileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    private static final List<Charset> CHARSETS = List.of(
            StandardCharsets.UTF_8,
            Charset.forName("windows-1251"),
            Charset.forName("KOI8-R"),
            Charset.forName("IBM866")
    );

    private static final Random RANDOM = new Random(2317402983750294387L);

    protected BaseImplementorTest() {
    }

//    @AfterAll
//    public static void cleanUp() {
//        clean(DIR);
//    }

    protected static void check(final URLClassLoader loader, final Class<?> token) {
        final String name = getImplName(token);
        System.err.println("\tLoading class " + name);
        final Class<?> impl;
        try {
            impl = loader.loadClass(name);
        } catch (final ClassNotFoundException e) {
            throw new AssertionError("Error loading class " + name, e);
        } catch (final IllegalAccessError e) {
            // Ok
            System.err.println("\t\t\t" + e.getMessage());
            return;
        }

        if (token.isInterface()) {
            Assertions.assertTrue(token.isAssignableFrom(impl), name + " should implement " + token);
        } else {
            Assertions.assertSame(token, impl.getSuperclass(), name + " should extend " + token);
        }
        Assertions.assertFalse(Modifier.isAbstract(impl.getModifiers()), name + " should not be abstract");
        Assertions.assertFalse(Modifier.isInterface(impl.getModifiers()), name + " should not be interface");
    }

    private static String getImplName(final Class<?> token) {
        return token.getPackageName() + "." + token.getSimpleName() + "Impl";
    }

//    public static void clean(final Path root) {
//        if (Files.exists(root)) {
//            try {
//                Files.walkFileTree(root, DELETE_VISITOR);
//            } catch (final IOException e) {
//                throw new UncheckedIOException(e);
//            }
//        }
//    }

    public static Path getFile(final Path root, final Class<?> clazz) {
        return root.resolve(getImplName(clazz).replace(".", File.separator) + ".java").toAbsolutePath();
    }

    private void implement(final boolean shouldFail, final Path root, final Class<?>... classes) {
        Impler implementor;
        try {
            implementor = createCUT();
        } catch (final Exception e) {
            Assertions.fail("Instantiation error", e);
            implementor = null;
        }
        for (final Class<?> clazz : classes) {
            try {
                implement(root, implementor, clazz);

                Assertions.assertFalse(shouldFail, "You may not implement " + clazz);
            } catch (final ImplerException e) {
                if (shouldFail) {
                    continue;
                }
                throw new AssertionError("Error implementing " + clazz, e);
            } catch (final Throwable e) {
                throw new AssertionError("Error implementing " + clazz, e);
            }
            final Path file = getFile(root, clazz);
            Assertions.assertTrue(Files.exists(file), "Error implementing clazz: File '" + file + "' not found");
        }
    }

    protected void implement(final Path root, final Impler implementor, final Class<?> clazz) throws ImplerException {
        implementor.implement(clazz, root);
    }

    public static void check(final Path root, final Class<?>... classes) {
        final URLClassLoader loader = getClassLoader(root);
        for (final Class<?> token : classes) {
            check(loader, token);
        }
    }

    public static void compileFiles(final Path root, final List<String> files) {
        compile(root, files, CHARSETS.get(RANDOM.nextInt(CHARSETS.size())));
    }

    private static void compile(final Path root, final List<String> files, final Charset charset) {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Assertions.assertNotNull(compiler, "Could not find java compiler, include tools.jar to classpath");
        final String classpath = root + File.pathSeparator + getClassPath();
        final String[] args = Stream.concat(files.stream(), Stream.of("-cp", classpath, "-encoding", charset.name())).toArray(String[]::new);
        final int exitCode = compiler.run(null, null, null, args);
        Assertions.assertEquals(0, exitCode, "Compiler exit code");
    }

    private static String getClassPath() {
        try {
            return Path.of(BaseImplementorTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new AssertionError(e);
        }
    }

    public void compile(final Path root, final Class<?>... classes) {
        final List<String> files = new ArrayList<>();
        for (final Class<?> token : classes) {
            files.add(getFile(root, token).toString());
        }
        compileFiles(root, files);
    }

    public static URLClassLoader getClassLoader(final Path root) {
        try {
            return new URLClassLoader(new URL[]{root.toUri().toURL()});
        } catch (final MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    private void test(final boolean shouldFail, final Class<?>[] classes) {
        final Path root = DIR.resolve(testName);
        try {
            implement(shouldFail, root, classes);
            if (!shouldFail) {
                compile(root, classes);
                check(root, classes);
            }
        } finally {
            //clean(root);
        }
    }

    public static void assertConstructor(final Class<?>... ifaces) {
        final Class<?> token = loadClass();
        for (final Class<?> iface : ifaces) {
            Assertions.assertTrue(iface.isAssignableFrom(token),
                                  token.getName() + " should implement " + iface.getName() + " interface");
        }
        checkConstructor("public default constructor", token);
    }

    protected void testOk(final Class<?>... classes) {
        test(false, classes);
    }

    protected void testFail(final Class<?>... classes) {
        test(true, classes);
    }

    protected void test(final Class<?>[] ok, final Class<?>[] failed) {
        testOk(ok);
        testFail(failed);
    }
}
