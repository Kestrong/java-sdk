package com.xjbg.java.sdk.util;

import com.xjbg.java.sdk.enums.Encoding;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author kesc
 * @date 2020-08-17 11:08
 */
@Slf4j
public class FileUtil {

    public static boolean exist(String path) {
        return getFile(path).exists();
    }

    public static boolean isFile(String path) {
        return getFile(path).isFile();
    }

    public static boolean isDirectory(String path) {
        return getFile(path).isDirectory();
    }

    public static boolean isAbsolute(String path) {
        return getFile(path).isAbsolute();
    }

    public static boolean isHidden(String path) {
        return getFile(path).isHidden();
    }

    public static Path getPath(String first, String... more) {
        return Paths.get(first, more);
    }

    public static File getFile(String first, String... more) {
        return getPath(first, more).toFile();
    }

    public static URI getURI(String first, String... more) {
        return getPath(first, more).toUri();
    }

    public static String getPathString(String first, String... more) {
        return Paths.get(first, more).toString();
    }

    public static boolean createFile(File file) throws IOException {
        File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        return file.createNewFile();
    }

    public static boolean createFile(String path) throws IOException {
        return createFile(getFile(path));
    }

    public static Path copy(File src, File target) throws IOException {
        return copy(src, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static Path copy(File src, File target, CopyOption... options) throws IOException {
        return Files.copy(src.toPath(), target.toPath(), options);
    }

    public static Path copy(String src, String target, CopyOption... options) throws IOException {
        return copy(getFile(src), getFile(target), options);
    }

    public static Path copy(String src, String target) throws IOException {
        return copy(src, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static Path move(File src, File target, CopyOption... options) throws IOException {
        return Files.move(src.toPath(), target.toPath(), options);
    }

    public static Path move(String src, String target, CopyOption... options) throws IOException {
        return move(getFile(src), getFile(target), options);
    }

    public static Path move(File src, File target) throws IOException {
        return move(src, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static Path move(String src, String target) throws IOException {
        return move(src, target, StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean delete(String path) {
        return getFile(path).delete();
    }

    public static List<File> listFiles(String path) throws FileNotFoundException {
        return listFiles(path, false);
    }

    public static List<File> listFiles(File file) throws FileNotFoundException {
        return listFiles(file, false);
    }

    public static List<File> listFiles(File file, boolean isRecursive) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("%s not exist", file.getAbsolutePath()));
        }
        if (!file.isDirectory()) {
            return Collections.emptyList();
        }
        List<File> responses = new ArrayList<>();
        listFiles(responses, file, isRecursive);
        return responses;
    }

    public static List<File> listFiles(String path, boolean isRecursive) throws FileNotFoundException {
        return listFiles(getFile(path), isRecursive);
    }

    private static void listFiles(List<File> responses, File file, boolean isRecursive) {
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File f : files) {
            responses.add(f);
            if (f.isDirectory() && isRecursive) {
                listFiles(responses, f, true);
            }
        }
    }

    public static void write(String path, InputStream inputStream) throws IOException {
        write(path, inputStream, Boolean.FALSE);
    }

    public static void write(String path, InputStream inputStream, boolean append) throws IOException {
        write(getFile(path), inputStream, append);
    }

    public static void write(File file, InputStream inputStream) throws IOException {
        write(file, inputStream, Boolean.FALSE);
    }

    public static void write(File file, InputStream inputStream, boolean append) throws IOException {
        if (!file.exists()) {
            createFile(file);
        }
        write(new FileOutputStream(file, append), inputStream);
    }

    public static void write(OutputStream outputStream, InputStream inputStream) throws IOException {
        try (InputStream input = new BufferedInputStream(inputStream);
             OutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            int index;
            byte[] bytes = new byte[2048];
            while ((index = input.read(bytes)) != -1) {
                bufferedOutputStream.write(bytes, 0, index);
            }
        }
    }

    public static void download(String url, String file) throws IOException {
        download(url, getFile(file));
    }

    public static void download(String url, File file) throws IOException {
        if (!file.exists()) {
            createFile(file);
        }
        URL website = new URL(url);
        try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
             FileOutputStream fos = new FileOutputStream(file)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    public static boolean rename(String src, String target) {
        return rename(getFile(src), getFile(target));
    }

    public static boolean rename(File src, File target) {
        return src.renameTo(target);
    }

    public static void write(String file, String content) throws IOException {
        write(file, content, Boolean.FALSE);
    }

    public static void write(String file, String content, boolean append) throws IOException {
        write(file, content, Encoding.UTF_8, append);
    }

    public static void write(String file, String content, Encoding encoding) throws IOException {
        write(file, content, encoding, Boolean.FALSE);
    }

    public static void write(String file, String content, Encoding encoding, boolean append) throws IOException {
        write(file, new ByteArrayInputStream(content.getBytes(encoding.getEncoding())), append);
    }

    public static void write(String file, byte[] content) throws IOException {
        write(file, content, Boolean.FALSE);
    }

    public static void write(String file, byte[] content, boolean append) throws IOException {
        write(file, new ByteArrayInputStream(content), append);
    }

    public static String readAsString(String file) throws IOException {
        return readAsString(file, Encoding.UTF_8);
    }

    public static String readAsString(String file, Encoding encoding) throws IOException {
        return new String(readAsBytes(file), encoding.getEncoding());
    }

    public static String readAsString(InputStream inputStream) throws IOException {
        return readAsString(inputStream, Encoding.UTF_8);
    }

    public static String readAsString(InputStream inputStream, Encoding encoding) throws IOException {
        return new String(readAsBytes(inputStream), encoding.getEncoding());
    }

    public static byte[] readAsBytes(String file) throws IOException {
        return readAsBytes(new FileInputStream(getFile(file)));
    }

    public static byte[] readAsBytes(InputStream input) throws IOException {
        try (InputStream bufferedInputStream = new BufferedInputStream(input);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            int index;
            byte[] bytes = new byte[2048];
            while ((index = bufferedInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, index);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    public static String getSuffix(String file) {
        int index = file.lastIndexOf(".");
        return index > -1 ? file.substring(index) : StringUtil.EMPTY;
    }
}
