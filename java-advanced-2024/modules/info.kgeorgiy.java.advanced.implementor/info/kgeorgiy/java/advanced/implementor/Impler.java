package info.kgeorgiy.java.advanced.implementor;

import java.net.MalformedURLException;
import java.nio.file.Path;

/**
 * Generates class implementation by {@link Class type token} of class or interface.
 *
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Impler {
    /**
     * Produces code implementing class or interface specified by provided {@code token}.
     * <p>
     * Generated class' name should be the same as the class name of the type token with {@code Impl} suffix
     * added. Generated source code should be placed in the correct subdirectory of the specified
     * {@code root} directory and have correct file name. For example, the implementation of the
     * interface {@link java.util.List} should go to {@code $root/java/util/ListImpl.java}
     *
     *
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws info.kgeorgiy.java.advanced.implementor.ImplerException when implementation cannot be
     * generated.
     */
    void implement(Class<?> token, Path root) throws ImplerException;
}
