package info.kgeorgiy.ja.novitskii.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * Implementor class represents objects that can implement given by user interfaces.
 *
 * @author Ilya Novitskiy
 * @version 1.0
 */
public class Implementor implements Impler, JarImpler {
    /**
     * This constant represents 4 spaces for specified java code format.
     */
    private static final String FOUR = "    ";

    /**
     * This constant represents additional suffix to the name of implemented class.
     */
    private static final String SUFFIX = "Impl";

    /**
     * Default constructor for {@link Implementor} class
     */
    public Implementor() {

    }

    /**
     * This method adds package part to .java file.
     * <p>
     * For example for  package1.package2.Example.java  <b>package package1.package2</b> will be added  to ExampleImpl.java file
     * will be added.
     * </p>
     *
     * @param code  a part of code to which is necessary to add package header
     * @param token a {@link Class type token} of class that  should implementation for be created
     */
    private static void addPackagePart(StringBuilder code, Class<?> token) {
        String packagePart = "package " + token.getPackageName()
                + ";";
        code.append(packagePart).append(System.lineSeparator()).append(System.lineSeparator());
    }

    /**
     * This method adds initial must-have code-words of every java class.
     * <p>
     * Necessary word in class definition:
     * <ul>
     *     <li>Class Access Modifiers such as : <b>private</b>, <b>protected</b>, <b>private</b>, <b>package-private</b></li>
     *     <li>Word <b>class</b></li>
     *     <li>Class name</li>
     * </ul>
     *
     * @param code  a part of code to which is necessary to add package header
     * @param token a {@link Class type token} of class that  should implementation for be created
     * @see <a href="https://www.oracle.com/java/technologies/jpl1-building-applications.html">Java Essentials</a>
     */
    private static void addClassStartSpecialNames(StringBuilder code, Class<?> token) {
        code.append("public ")
                .append("class")
                .append(" ").append(token.getSimpleName())
                .append(SUFFIX).append(" ").append("implements")
                .append(" ").append(token.getCanonicalName())
                .append("{");
    }

    /**
     * This method is used in {@link Implementor#addInnerMethodsImplementation(StringBuilder, Class)}
     * to add annotation to implementing method.
     * <p>
     * For example if we want to add {@code @Override} annotation we can call this method
     *
     * @return part of code with annotation
     */
    private static String addMethodAnnotation() {
        return System.lineSeparator() +
                FOUR +
                "@Override" +
                System.lineSeparator() +
                FOUR;
    }


    /**
     * This method is used in {@link Implementor#addInnerMethodsImplementation(StringBuilder, Class)}
     * to add modifiers, return value and arguments to implementing method in file.java.
     * <p>
     * For example if we want to add it for {@link List#add(int, Object)} we will get a
     * {@code String value = "public abstract void add(int arg1, Object arg2)"}
     *
     * @param method is the method for which to write signature for to file.java
     * @return the string consists of full method signature and return value
     */
    private static String addMethodSignatureAndModifiers(Method method) {
        StringBuilder code = new StringBuilder();
        if (Modifier.isPublic(method.getModifiers())) {
             code.append("public ");
        } else if (Modifier.isProtected(method.getModifiers())) {
            code.append("protected ");
        }
        code.append(method.getReturnType().getCanonicalName())
                .append(" ").append(method.getName())
                .append("(");
        String args = Arrays.stream(method.getParameters())
                .map(parameter -> parameter.getType().getCanonicalName() + " " + parameter.getName())
                .collect(Collectors.joining(", "));
        code.append(args).append(")");
        return code.toString();
    }

    /**
     * This method is used in {@link Implementor#addInnerMethodsImplementation(StringBuilder, Class)} to add
     * method implementation between { and }.
     *
     * @param method for implementation in  nameImpl.java
     * @return the string consists of the method definition
     */
    private static String addMethodBody(Method method) {
        StringBuilder code = new StringBuilder();
        code.append("{")
                .append(System.lineSeparator())
                .append(FOUR + FOUR)
                .append("return")
                .append(" ");
        if (method.getReturnType().isPrimitive()) {
            if (method.getReturnType() == boolean.class) {
                code.append("false");
            } else if (method.getReturnType() != void.class) {
                code.append("0");
            }
        } else {
            code.append("null");
        }
        code.append(";").append(System.lineSeparator())
                .append(FOUR)
                .append("}");
        return code.toString();
    }

    /**
     * This method generate code for the whole implementation of interface in the Impl class.
     * <p>
     * Particularly it creates a code with all methods and their bodies which are required for implementation
     * of interface
     *
     * @param code  a part of the code after the class declaration between first { and last }
     * @param token an interface type to which to create implementation
     */
    private static void addInnerMethodsImplementation(StringBuilder code, Class<?> token) {
        code.append(Arrays.stream(token.getMethods())
                .filter(method -> !(method.isDefault() ||
                        Modifier.isStatic(method.getModifiers()) || Modifier.isPrivate(method.getModifiers())))
                .map(method -> addMethodAnnotation() + addMethodSignatureAndModifiers(method)
                        + addMethodBody(method))
                .collect(Collectors.joining()));
    }

    /**
     * Closes the block of code whenever it is needed to balance the correct bracket sequence
     *
     * @param code block of code with -1 balance of correct bracket sequence
     */
    private static void closeLastBracket(StringBuilder code) {
        code.append(System.lineSeparator())
                .append("}");
    }

    /**
     * This method is used in {@link Implementor#createJavaSourceFile(Class, Path)} to give the whole inner code of
     * implemented class with Impl suffix.
     *
     * @param token the interface for which to create implementation
     * @return the code with all source code
     */
    private static StringBuilder getCode(Class<?> token) {
        StringBuilder code = new StringBuilder();
        if (!token.getPackageName().isEmpty()) {
            addPackagePart(code, token);
        }
        addClassStartSpecialNames(code, token);
        addInnerMethodsImplementation(code, token);
        closeLastBracket(code);
        return code;
    }

    /**
     * This method is used inside {@link Implementor#createJavaSourceFile(Class, Path)} to get the correctly parsed
     * root and implementation file name.
     * <p> Elements of {@link Map.Entry} are given in representation with changed separator
     * from "." to {@link File#separator}.
     *
     * @param token interface for which to create implementation
     * @param root  the root location of interface file
     * @return the pair with a first value as a path in needed form and a second value as a full implementation file name
     */
    private static Map.Entry<Path, String> getRootAndFile(Class<?> token, Path root) {
        Path mergedPath = root.resolve(token.getPackageName().replace('.', File.separator.charAt(0)));
        String javaFileName = mergedPath.resolve(token.getSimpleName()) +
                SUFFIX + ".java";
        return Map.entry(mergedPath, javaFileName);
    }

    /**
     * This method produces a code inside nameImpl.java class with all override methods. It is used in
     * {@link Implementor#implement(Class, Path)}.
     *
     * @param token interface for which to create implementation
     * @param root  root where the implementation file is
     */
    private static void createJavaSourceFile(Class<?> token, Path root) {
        try {
            Map.Entry<Path, String> rootAndFile = getRootAndFile(token, root);
            Files.createDirectories(rootAndFile.getKey());
            try (BufferedWriter writer = Files.newBufferedWriter(Path.of(rootAndFile.getValue()))) {
                StringBuilder code = getCode(token);
                for (final char c : code.toString().toCharArray()) {
                    writer.write(String.format("\\u%04x", (int) c));
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }


    /**
     * The general method in {@link Implementor} used to implement given interface by calling no-except
     * {@link Implementor#createJavaSourceFile(Class, Path)}.
     * <p>
     * The exception {@link ImplerException} can be thrown if:
     * <ul>
     *     <li>Given type token is <b>non-interface</b></li>
     *     <li>Given type token has <b>private</b> modifier</li>
     * </ul>
     *
     * @param token type token to create implementation for.
     * @param root  root directory where interface is located
     * @throws ImplerException if some type token restrictions are not provided
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (!token.isInterface() || Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Impossible to implement non-Interface");
        }
        createJavaSourceFile(token, root);
    }


    /**
     * This method is used in {@link Implementor#compile(Class, Path, String)} to get the path to dependency modules
     * that we could compile with -cp key.
     * <p>
     * {@link ImplerException} can be thrown while invoking {@link File#toURI()} if the provided format URI
     * instance is not correct.
     *
     * @param token type token to create implementation for.
     * @return the classpath to dependencies represented with string
     * @throws ImplerException in the described cases
     * @see URISyntaxException
     */
    private static String getClassPath(Class<?> token) throws ImplerException {
        try {
            if (token.getProtectionDomain() == null || token.getProtectionDomain().getCodeSource() == null) {
                return Path.of(".").toString();
            }
            return Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new ImplerException("Getting class path caused a trouble", e.getCause());
        }
    }

    /**
     * This method is used specifically in {@link Implementor#implementJar(Class, Path)} to compile generated {@code nameImpl.java}
     * file into bytecode for loading it with {@link ClassLoader} instances.
     *
     * @param token        type token to create implementation for.
     * @param temporaryDir dir to which to save generated .class files produces by javac
     * @param sourceFile   {@code nameImpl.java} file with implementation for provided {@link Class type token}
     * @throws ImplerException if exit code of the work of
     *                         {@link javax.tools.Tool#run(InputStream, OutputStream, OutputStream, String...)} is not 0
     */
    private void compile(Class<?> token, Path temporaryDir, String sourceFile) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        String classPath = temporaryDir + File.pathSeparator + getClassPath(token);
        int code = compiler.run(null, null, null, "-cp", classPath, sourceFile);
        if (code != 0) {
            throw new ImplerException("Compilation error while compiling implementation file");
        }
    }

    /**
     * This method is used in {@link Implementor#implementJar(Class, Path)} to create nameImpl.jar for implementation of
     * given {@link Class type token}. This method creates nameImpl.jar file and write {@code nameImpl.class} bytecode
     * into it by reading it.
     *
     * @param pathToClassFile the absolute path for nameImpl.class file of generated implementation of given token
     * @param jos             The stream that will create nameImpl.jar file and write {@code nameImpl.class} file into it
     * @param token           type token to create implementation for.
     * @throws IOException if some errors occurred while creating stream to read {@code nameImpl.class} or while reading it
     */
    private static void addToJarFile(String pathToClassFile, JarOutputStream jos, Class<?> token) throws IOException {
        String classFileName = token.getPackageName().replace(".", "/") + "/" +
                token.getSimpleName() + SUFFIX + ".class";
        JarEntry jarEntry = new JarEntry(classFileName);
        jos.putNextEntry(jarEntry);
        FileInputStream fis = new FileInputStream(pathToClassFile);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) > 0) {
            jos.write(buffer, 0, bytesRead);
        }
        jos.closeEntry();
        fis.close();
    }

    /**
     * This method specifically used in {@link Implementor#implementJar(Class, Path)} to send {@link Manifest} instance
     * for creation of jar file.
     *
     * @return base {@link Manifest} with version attribute
     * @see JarOutputStream#JarOutputStream(OutputStream, Manifest)
     */
    private static Manifest createManifest() {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        return manifest;
    }

    /**
     * One of the general methods of {@link Implementor} that creates nameImpl.jar file for implemented
     * nameImpl class that is the implementation of given type token.
     * <p> This method serves nameImpl.class and connected .class
     * in the standard way to created  nameImpl.jar file
     * <p>
     * The exception {@link ImplerException} can be thrown if:
     * <ul>
     *     <li>Given temporary directory for serving nameImpl.class and connected .class files
     *      can not be created.</li>
     *     <li>{@link JarOutputStream} creation throws {@link IOException}</li>
     *     <li>calling {@link Implementor#implement(Class, Path)} or {@link Implementor#compile(Class, Path, String)}</li>
     * </ul>
     *
     * @param token   type token to create implementation for.
     * @param jarFile Path to target .jar file.
     * @throws ImplerException if creation of temporary directory or {@link JarOutputStream} failed, calling
     *                         {@link Implementor#compile(Class, Path, String)}, {@link Implementor#implement(Class, Path)}
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        final Path temporaryDir;
        try {
            temporaryDir = Files.createTempDirectory(jarFile.toAbsolutePath().getParent(), "temporary");
        } catch (IOException ex) {
            throw new ImplerException("Impossible to create temporary directory", ex.getCause());
        }
        Map.Entry<Path, String> pathAndFile = getRootAndFile(token, temporaryDir);
        String javaFileName = pathAndFile.getValue();
        implement(token, temporaryDir);
        compile(token, temporaryDir, javaFileName);
        try (JarOutputStream jarStream = new JarOutputStream(
                new FileOutputStream(jarFile.toString()), createManifest())) {
            String pathToClassFile = javaFileName.substring(0, javaFileName.length() -
                    ".java".length()) + ".class";
            addToJarFile(pathToClassFile, jarStream, token);
        } catch (IOException ex) {
            throw new ImplerException("During creation of JarOutPutStream an error occurred", ex.getCause());
        }
    }

    /**
     * The main entry point that creates instance of {@link Implementor} and calls its general methods such as
     * {@link Implementor#implement(Class, Path)} or {@link Implementor#compile(Class, Path, String)} on args argument.
     * <p>
     * args {@link ImplerException} can consist of two or three arguments:
     * <ul>
     *     <li>if two arguments are provided then  {@link Implementor#implement(Class, Path)} on instance is called</li>
     *     <li>if three arguments are provided then {@link Implementor#implementJar(Class, Path)} on instance is called</li>
     * </ul>
     * <p>
     *     It is also expected that
     *     <ul>
     *          <li>In the first case {@code args[0]} is full interface name
     *              for which to create implementation, {@code args[1]} is path to it</li>
     *           <li>In the second case {@code args[0]} is -jar, {@code args[1]} is the full interface name
     *           for which to create implementation, {@code args[2]} is file.jar</li>
     *     </ul>
     *
     * @param args arguments of command line on which we call general {@link Implementor} methods
     */
    public static void main(String[] args) {
        if (args == null) {
            return;
        }
        if (args.length < 2 || args.length > 3) {
            System.err.println("No option to start creation of implementation because of wrong number of args");
        } else {
            if (args[0] == null) {
                System.err.println("First argument -jar is not give");
                return;
            }
            if (args[1] == null) {
                System.err.println("Class name to implement is not given");
            }
            Implementor implementor = new Implementor();
            try {
                if (args.length == 2) {
                    implementor.implement(Class.forName(args[0]), Path.of(args[1]));
                } else {
                    implementor.implementJar(Class.forName(args[1]), Path.of(args[2]));
                }
            } catch (ClassNotFoundException | ImplerException ex) {
                System.err.println(ex.getLocalizedMessage());
            }

        }
    }
}
