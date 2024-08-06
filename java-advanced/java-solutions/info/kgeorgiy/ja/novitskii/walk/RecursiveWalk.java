package info.kgeorgiy.ja.novitskii.walk;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;


public class RecursiveWalk {

    private static final String EMPTY_DEFAULT_OUTPUT = String.format("%08x", 0);
    private static final byte[] ERROR_CONTENT = new byte[]{0, 1, 2, 3};
    private static final int LENGTH = 1024;

    private static class RecursiveWalkTreeVisitor extends SimpleFileVisitor<Path> {
        private final BufferedWriter writer;

        private final byte[] buff = new byte[LENGTH];

        public RecursiveWalkTreeVisitor(BufferedWriter writer) {
            this.writer = writer;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
            String hash = jenkinsHashSum(buff, new FileInputStream(String.valueOf(file.toAbsolutePath())));
            writer.write(String.format("%s %s" + System.lineSeparator(), hash, file.toAbsolutePath()));
            return FileVisitResult.CONTINUE;
        }
    }

    private static String jenkinsHashSum(byte[] buff, InputStream inputBytes) throws IOException {
        int read = inputBytes.read(buff, 0, LENGTH);
        if (buff == ERROR_CONTENT || read == -1) {
            return EMPTY_DEFAULT_OUTPUT;
        }
        int hash = 0;
        while (read != -1) {
            for (int i = 0; i < read; i++) {
                hash += buff[i] & 0xff;
                hash += hash << 10;
                hash ^= hash >>> 6;
            }
            buff = new byte[LENGTH];
            read = inputBytes.read(buff, 0, LENGTH);
        }
        hash += hash << 3;
        hash ^= hash >>> 11;
        hash += hash << 15;
        return String.format("%08x", hash);
    }

    private static void fileCountHash(String currentFileSystemElement, byte[] buff, BufferedWriter writer)
            throws IOException {
        try (InputStream inputBytes = new FileInputStream(
                currentFileSystemElement)) {
            String hashSumHexString = jenkinsHashSum(buff, inputBytes);
            writer.write(hashSumHexString + " " + currentFileSystemElement + System.lineSeparator());
        } catch (IOException ex) {
            writer.write(EMPTY_DEFAULT_OUTPUT + " " + currentFileSystemElement + System.lineSeparator());
        }
    }

    public static void main(String[] args) {
        if (args == null) {
            return;
        }
        if (args.length != 2) {
            System.err.println("Number of arguments is not 2");
            return;
        } else {
            if (args[0] == null || args[1] == null) {
                System.err.println("One of the arguments is null");
                return;
            }
        }

        try {
            Path inputPath = Path.of(args[0]).normalize();
            try {
                Path outputPath = Path.of(args[1]).normalize();
                Path parent = outputPath.getParent();
                if (parent != null) {
                    Files.createDirectories(outputPath.getParent());
                }
                final byte[] BUFF = new byte[LENGTH];
                try (BufferedReader reader = Files.newBufferedReader(inputPath)) {
                    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
                        String currentFileSystemElement = reader.readLine();
                        while (currentFileSystemElement != null) {
                            try {
                                currentFileSystemElement = String.valueOf(Path.of(currentFileSystemElement).normalize());
                                if (new File(currentFileSystemElement).isDirectory()) {
                                    writer.write(EMPTY_DEFAULT_OUTPUT + " " + currentFileSystemElement + "\n");
                                } else {
                                    try {
                                        fileCountHash(currentFileSystemElement, BUFF, writer);
                                    } catch (IOException ex) {
                                        System.err.println("Failure while writing hash to output : " + ex.getLocalizedMessage());
                                    }
                                }
                            } catch (InvalidPathException ex) {
                                writer.write(EMPTY_DEFAULT_OUTPUT + " " + currentFileSystemElement + System.lineSeparator());
                                System.err.println("The file for hash counting doesn't exists: " + ex.getLocalizedMessage());
                            }
                            currentFileSystemElement = reader.readLine();
                        }
                    } catch (FileNotFoundException ex) {
                        System.err.println("Error while opening file for read: " + ex.getLocalizedMessage());
                    } catch (IOException ex) {
                        System.err.println("Error while opening file for write: " + ex.getLocalizedMessage());
                    }
                } catch (InvalidPathException ex) {
                    System.err.println("Our output path is incorrect");
                }
            } catch (InvalidPathException ex) {
                System.err.println("Our output path is incorrect");
            } catch (FileNotFoundException e) {
                System.err.println("The file that is considered in path doesn't exists");
            } catch (IOException e) {
                System.err.println("The the failure occured while writing");
            }
        } catch (InvalidPathException ex) {
            System.err.println("Our input path is incorrect");
        }
    }
}
