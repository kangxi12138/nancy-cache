package com.github.codeman.nancy.core.util;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import cn.hutool.core.util.ArrayUtil;
import org.apache.commons.lang3.StringUtils;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class FileUtil {
    private FileUtil() {
    }

    public static String getFileContent(String filePath) {
        return getFileContent(filePath, "UTF-8");
    }

    public static String getFileContent(String filePath, String charset) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                InputStream inputStream = new FileInputStream(file);
                Throwable var4 = null;

                String var5;
                try {
                    var5 = getFileContent((InputStream)inputStream, charset);
                } catch (Throwable var15) {
                    var4 = var15;
                    throw var15;
                } finally {
                    if (inputStream != null) {
                        if (var4 != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable var14) {
                                var4.addSuppressed(var14);
                            }
                        } else {
                            inputStream.close();
                        }
                    }

                }

                return var5;
            } catch (IOException var17) {
                throw new RuntimeException(var17);
            }
        } else {
            throw new RuntimeException(filePath + " is not exists!");
        }
    }

    public static String getFileContent(InputStream inputStream) {
        return getFileContent(inputStream, "UTF-8");
    }

    public static String getFileContent(File file, String charset) {
        try {
            InputStream inputStream = new FileInputStream(file);
            Throwable var3 = null;

            String var4;
            try {
                var4 = getFileContent((InputStream)inputStream, charset);
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if (inputStream != null) {
                    if (var3 != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        inputStream.close();
                    }
                }

            }

            return var4;
        } catch (IOException var16) {
            throw new RuntimeException(var16);
        }
    }

    public static String getFileContent(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            Throwable var2 = null;

            String var3;
            try {
                var3 = getFileContent((InputStream)inputStream);
            } catch (Throwable var13) {
                var2 = var13;
                throw var13;
            } finally {
                if (inputStream != null) {
                    if (var2 != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var12) {
                            var2.addSuppressed(var12);
                        }
                    } else {
                        inputStream.close();
                    }
                }

            }

            return var3;
        } catch (IOException var15) {
            throw new RuntimeException(var15);
        }
    }

    public static String getFileContent(InputStream inputStream, String charset) {
        Charset charsetVal = Charset.forName(charset);
        return getFileContent(inputStream, 0, Integer.MAX_VALUE, charsetVal);
    }

    public static String getFileContent(InputStream inputStream, int startIndex, int endIndex, Charset charset) {
        try {
            endIndex = Math.min(endIndex, inputStream.available());
            startIndex = Math.max(0, startIndex);
            inputStream.skip((long)startIndex);
            int count = endIndex - startIndex;
            byte[] bytes = new byte[count];

            for(int readCount = 0; readCount < count && readCount != -1; readCount += inputStream.read(bytes, readCount, count - readCount)) {
            }

            String var7 = new String(bytes, charset);
            return var7;
        } catch (IOException var16) {
            throw new RuntimeException(var16);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException var15) {
                var15.printStackTrace();
            }

        }
    }

    public static String getSuffix(String fileName) {
        return fileName.substring(fileName.lastIndexOf(46) + 1);
    }

    public static List<String> getFileContentEachLine(String filePath, int initLine) {
        File file = new File(filePath);
        return getFileContentEachLine(file, initLine);
    }

    public static List<String> getFileContentEachLine(String filePath) {
        File file = new File(filePath);
        return getFileContentEachLine((File)file, 0);
    }

    public static List<String> getFileContentEachLineTrim(String filePath, int initLine) {
        List<String> stringList = getFileContentEachLine(filePath, initLine);
        List<String> resultList = new LinkedList();
        Iterator var4 = stringList.iterator();

        while(var4.hasNext()) {
            String string = (String)var4.next();
            resultList.add(string.trim());
        }

        return resultList;
    }

    public static List<String> getFileContentEachLine(File file) {
        return getFileContentEachLine((File)file, 0);
    }

    public static List<String> getFileContentEachLine(File file, int initLine) {
        List<String> contentList = new LinkedList();
        if (!file.exists()) {
            return contentList;
        } else {
            String charset = "UTF-8";

            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Throwable var5 = null;

                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charset);
                    Throwable var7 = null;

                    try {
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        Throwable var9 = null;

                        try {
                            int lineNo;
                            String dataEachLine;
                            for(lineNo = 0; lineNo < initLine; dataEachLine = bufferedReader.readLine()) {
                                ++lineNo;
                            }

                            while((dataEachLine = bufferedReader.readLine()) != null) {
                                ++lineNo;
                                if (!Objects.equals("", dataEachLine)) {
                                    contentList.add(dataEachLine);
                                }
                            }
                        } catch (Throwable var57) {
                            var9 = var57;
                            throw var57;
                        } finally {
                            if (bufferedReader != null) {
                                if (var9 != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (Throwable var56) {
                                        var9.addSuppressed(var56);
                                    }
                                } else {
                                    bufferedReader.close();
                                }
                            }

                        }
                    } catch (Throwable var59) {
                        var7 = var59;
                        throw var59;
                    } finally {
                        if (inputStreamReader != null) {
                            if (var7 != null) {
                                try {
                                    inputStreamReader.close();
                                } catch (Throwable var55) {
                                    var7.addSuppressed(var55);
                                }
                            } else {
                                inputStreamReader.close();
                            }
                        }

                    }
                } catch (Throwable var61) {
                    var5 = var61;
                    throw var61;
                } finally {
                    if (fileInputStream != null) {
                        if (var5 != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable var54) {
                                var5.addSuppressed(var54);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }

                }

                return contentList;
            } catch (IOException var63) {
                throw new RuntimeException(var63);
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public static List<String> getFileContentEachLine(File file, int initLine, int endLine, String charset) {
        List<String> contentList = new LinkedList();
        if (!file.exists()) {
            return contentList;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                Throwable var6 = null;

                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, charset);
                    Throwable var8 = null;

                    try {
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        Throwable var10 = null;

                        try {
                            int lineNo;
                            String dataEachLine;
                            for(lineNo = 0; lineNo < initLine; dataEachLine = bufferedReader.readLine()) {
                                ++lineNo;
                            }

                            while((dataEachLine = bufferedReader.readLine()) != null && lineNo < endLine) {
                                ++lineNo;
                                contentList.add(dataEachLine);
                            }
                        } catch (Throwable var58) {
                            var10 = var58;
                            throw var58;
                        } finally {
                            if (bufferedReader != null) {
                                if (var10 != null) {
                                    try {
                                        bufferedReader.close();
                                    } catch (Throwable var57) {
                                        var10.addSuppressed(var57);
                                    }
                                } else {
                                    bufferedReader.close();
                                }
                            }

                        }
                    } catch (Throwable var60) {
                        var8 = var60;
                        throw var60;
                    } finally {
                        if (inputStreamReader != null) {
                            if (var8 != null) {
                                try {
                                    inputStreamReader.close();
                                } catch (Throwable var56) {
                                    var8.addSuppressed(var56);
                                }
                            } else {
                                inputStreamReader.close();
                            }
                        }

                    }
                } catch (Throwable var62) {
                    var6 = var62;
                    throw var62;
                } finally {
                    if (fileInputStream != null) {
                        if (var6 != null) {
                            try {
                                fileInputStream.close();
                            } catch (Throwable var55) {
                                var6.addSuppressed(var55);
                            }
                        } else {
                            fileInputStream.close();
                        }
                    }

                }

                return contentList;
            } catch (IOException var64) {
                throw new RuntimeException(var64);
            }
        }
    }

    public static List<String> readAllLines(File file, String charset, int initLine, int endLine, boolean ignoreEmpty) {

        if (!file.exists()) {
            throw new RuntimeException("File not exists!");
        } else {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                Throwable var6 = null;

                List var7;
                try {
                    var7 = readAllLines((InputStream)inputStream, charset, initLine, endLine, ignoreEmpty);
                } catch (Throwable var17) {
                    var6 = var17;
                    throw var17;
                } finally {
                    if (inputStream != null) {
                        if (var6 != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable var16) {
                                var6.addSuppressed(var16);
                            }
                        } else {
                            inputStream.close();
                        }
                    }

                }

                return var7;
            } catch (IOException var19) {
                throw new RuntimeException(var19);
            }
        }
    }

    public static List<String> readAllLines(InputStream inputStream, String charset, int initLine, int endLine, boolean ignoreEmpty) {

        List<String> contentList = new LinkedList();

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charset);
            Throwable var7 = null;

            try {
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                Throwable var9 = null;

                try {
                    int lineNo;
                    String dataEachLine;
                    for(lineNo = 0; lineNo < initLine; dataEachLine = bufferedReader.readLine()) {
                        ++lineNo;
                    }

                    while(true) {
                        do {
                            if ((dataEachLine = bufferedReader.readLine()) == null || lineNo >= endLine) {
                                return contentList;
                            }

                            ++lineNo;
                        } while(ignoreEmpty && StringUtils.isEmpty(dataEachLine));

                        contentList.add(dataEachLine);
                    }
                } catch (Throwable var54) {
                    var9 = var54;
                    throw var54;
                } finally {
                    if (bufferedReader != null) {
                        if (var9 != null) {
                            try {
                                bufferedReader.close();
                            } catch (Throwable var53) {
                                var9.addSuppressed(var53);
                            }
                        } else {
                            bufferedReader.close();
                        }
                    }

                }
            } catch (Throwable var56) {
                var7 = var56;
                throw var56;
            } finally {
                if (inputStreamReader != null) {
                    if (var7 != null) {
                        try {
                            inputStreamReader.close();
                        } catch (Throwable var52) {
                            var7.addSuppressed(var52);
                        }
                    } else {
                        inputStreamReader.close();
                    }
                }

            }
        } catch (IOException var58) {
            throw new RuntimeException(var58);
        } finally {
            try {
                inputStream.close();
            } catch (IOException var51) {
                var51.printStackTrace();
            }

        }
    }

    public static List<String> readAllLines(InputStream inputStream, String charset, int initLine, int endLine) {
        return readAllLines(inputStream, charset, initLine, endLine, true);
    }

    public static List<String> readAllLines(InputStream inputStream, String charset, int initLine) {
        return readAllLines(inputStream, charset, initLine, Integer.MAX_VALUE);
    }

    public static List<String> readAllLines(InputStream inputStream, String charset) {
        return readAllLines(inputStream, charset, 0);
    }

    public static List<String> readAllLines(InputStream inputStream) {
        return readAllLines(inputStream, "UTF-8");
    }

    public static List<String> readAllLines(String filePath, String charset, boolean ignoreEmpty) {
        File file = new File(filePath);
        return readAllLines((File)file, charset, 0, Integer.MAX_VALUE, ignoreEmpty);
    }

    public static List<String> readAllLines(File file, String charset, boolean ignoreEmpty) {
        return readAllLines((File)file, charset, 0, Integer.MAX_VALUE, ignoreEmpty);
    }

    public static List<String> readAllLines(File file, String charset) {
        return readAllLines(file, charset, false);
    }

    public static List<String> readAllLines(File file) {
        return readAllLines(file, "UTF-8");
    }

    public static List<String> readAllLines(String filePath, String charset) {
        return readAllLines(filePath, charset, false);
    }

    public static List<String> readAllLines(String filePath) {
        return readAllLines(filePath, "UTF-8");
    }

    public static void copyDir(String sourceDir, String targetDir) throws IOException {
        File file = new File(sourceDir);
        String[] filePath = file.list();
        if (!(new File(targetDir)).exists()) {
            (new File(targetDir)).mkdir();
        }

        if (ArrayUtil.isNotEmpty(filePath)) {
            String[] var4 = filePath;
            int var5 = filePath.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String aFilePath = var4[var6];
                if ((new File(sourceDir + File.separator + aFilePath)).isDirectory()) {
                    copyDir(sourceDir + File.separator + aFilePath, targetDir + File.separator + aFilePath);
                }

                if ((new File(sourceDir + File.separator + aFilePath)).isFile()) {
                    copyFile(sourceDir + File.separator + aFilePath, targetDir + File.separator + aFilePath);
                }
            }
        }

    }

    public static void copyFile(String sourceFile, String targetPath) throws IOException {
        File oldFile = new File(sourceFile);
        File file = new File(targetPath);
        FileInputStream in = new FileInputStream(oldFile);
        Throwable var5 = null;

        try {
            FileOutputStream out = new FileOutputStream(file);
            Throwable var7 = null;

            try {
                byte[] buffer = new byte[2097152];

                while(in.read(buffer) != -1) {
                    out.write(buffer);
                }
            } catch (Throwable var30) {
                var7 = var30;
                throw var30;
            } finally {
                if (out != null) {
                    if (var7 != null) {
                        try {
                            out.close();
                        } catch (Throwable var29) {
                            var7.addSuppressed(var29);
                        }
                    } else {
                        out.close();
                    }
                }

            }
        } catch (Throwable var32) {
            var5 = var32;
            throw var32;
        } finally {
            if (in != null) {
                if (var5 != null) {
                    try {
                        in.close();
                    } catch (Throwable var28) {
                        var5.addSuppressed(var28);
                    }
                } else {
                    in.close();
                }
            }

        }

    }

    public static void write(String filePath, CharSequence line, OpenOption... openOptions) {
        write(filePath, (Iterable)Collections.singletonList(line), openOptions);
    }

    public static void write(String filePath, Iterable<? extends CharSequence> lines, OpenOption... openOptions) {
        write(filePath, lines, "UTF-8", openOptions);
    }

    public static void write(String filePath, Iterable<? extends CharSequence> lines, String charset, OpenOption... openOptions) {
        try {

            CharsetEncoder encoder = Charset.forName(charset).newEncoder();
            Path path = Paths.get(filePath);
            Path pathParent = path.getParent();
            if (pathParent != null) {
                File parent = pathParent.toFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
            }

            OutputStream out = path.getFileSystem().provider().newOutputStream(path, openOptions);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, encoder));
            Throwable var9 = null;

            try {
                Iterator var10 = lines.iterator();

                while(var10.hasNext()) {
                    CharSequence line = (CharSequence)var10.next();
                    writer.append(line);
                    writer.newLine();
                }
            } catch (Throwable var20) {
                var9 = var20;
                throw var20;
            } finally {
                if (writer != null) {
                    if (var9 != null) {
                        try {
                            writer.close();
                        } catch (Throwable var19) {
                            var9.addSuppressed(var19);
                        }
                    } else {
                        writer.close();
                    }
                }

            }

        } catch (IOException var22) {
            throw new RuntimeException(var22);
        }
    }

    public static boolean createFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        } else if (exists(filePath)) {
            return true;
        } else {
            File file = new File(filePath);
            File dir = file.getParentFile();
            if (dir != null && notExists(dir)) {
                boolean mkdirResult = dir.mkdirs();
                if (!mkdirResult) {
                    return false;
                }
            }

            try {
                return file.createNewFile();
            } catch (IOException var4) {
                throw new RuntimeException(var4);
            }
        }
    }

    public static boolean exists(String filePath, LinkOption... options) {
        if (StringUtils.isEmpty(filePath)) {
            return false;
        } else {
            Path path = Paths.get(filePath);
            return Files.exists(path, options);
        }
    }

    public static boolean notExists(String filePath, LinkOption... options) {
        return !exists(filePath, options);
    }

    public static boolean notExists(File file) {

        return !file.exists();
    }

    public static boolean isEmpty(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return true;
        } else {
            File file = new File(filePath);
            return file.length() <= 0L;
        }
    }

    public static boolean isNotEmpty(String filePath) {
        return !isEmpty(filePath);
    }

    public static byte[] getFileBytes(File file) {


        try {
            FileInputStream fis = new FileInputStream(file);
            Throwable var2 = null;

            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
                Throwable var4 = null;

                try {
                    byte[] b = new byte[1024];

                    int n;
                    while((n = fis.read(b)) != -1) {
                        bos.write(b, 0, n);
                    }

                    byte[] var7 = bos.toByteArray();
                    return var7;
                } catch (Throwable var32) {
                    var4 = var32;
                    throw var32;
                } finally {
                    if (bos != null) {
                        if (var4 != null) {
                            try {
                                bos.close();
                            } catch (Throwable var31) {
                                var4.addSuppressed(var31);
                            }
                        } else {
                            bos.close();
                        }
                    }

                }
            } catch (Throwable var34) {
                var2 = var34;
                throw var34;
            } finally {
                if (fis != null) {
                    if (var2 != null) {
                        try {
                            fis.close();
                        } catch (Throwable var30) {
                            var2.addSuppressed(var30);
                        }
                    } else {
                        fis.close();
                    }
                }

            }
        } catch (IOException var36) {
            throw new RuntimeException(var36);
        }
    }

    public static byte[] getFileBytes(String filePath) {

        File file = new File(filePath);
        return getFileBytes(file);
    }

    public static void createFile(String filePath, byte[] bytes) {
        File file = createFileAssertSuccess(filePath);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            Throwable var4 = null;

            try {
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                Throwable var6 = null;

                try {
                    bos.write(bytes);
                } catch (Throwable var31) {
                    var6 = var31;
                    throw var31;
                } finally {
                    if (bos != null) {
                        if (var6 != null) {
                            try {
                                bos.close();
                            } catch (Throwable var30) {
                                var6.addSuppressed(var30);
                            }
                        } else {
                            bos.close();
                        }
                    }

                }
            } catch (Throwable var33) {
                var4 = var33;
                throw var33;
            } finally {
                if (fos != null) {
                    if (var4 != null) {
                        try {
                            fos.close();
                        } catch (Throwable var29) {
                            var4.addSuppressed(var29);
                        }
                    } else {
                        fos.close();
                    }
                }

            }

        } catch (Exception var35) {
            throw new RuntimeException(var35);
        }
    }

    public static File createFileAssertSuccess(String filePath) {

        File file = new File(filePath);
        if (file.exists()) {
            return file;
        } else {
            File dir = file.getParentFile();
            boolean createFile;
            if (notExists(dir)) {
                createFile = dir.mkdirs();
                if (!createFile) {
                    throw new RuntimeException("Parent file create fail " + filePath);
                }
            }

            try {
                createFile = file.createNewFile();
                if (!createFile) {
                    throw new RuntimeException("Create new file fail for path " + filePath);
                } else {
                    return file;
                }
            } catch (IOException var4) {
                throw new RuntimeException(var4);
            }
        }
    }

    public static void deleteFile(File file) {

        if (file.exists()) {
            boolean result = file.delete();
            if (!result) {
                throw new RuntimeException("Delete file fail for path " + file.getAbsolutePath());
            }
        }

    }

    public static File createTempFile(String name, String suffix) {
        try {

            return File.createTempFile(name, suffix);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static File createTempFile(String nameWithSuffix) {
        try {

            String[] strings = nameWithSuffix.split("\\.");
            return File.createTempFile(strings[0], strings[1]);
        } catch (IOException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static boolean isImage(String string) {
        if (StringUtils.isEmpty(string)) {
            return false;
        } else {
            return string.endsWith(".png") || string.endsWith(".jpeg") || string.endsWith(".jpg") || string.endsWith(".gif");
        }
    }






    public static String getFileName(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        } else {
            File file = new File(path);
            String name = file.getName();
            return name.substring(0, name.lastIndexOf(46));
        }
    }

    public static String getDirPath(String path) {
        Path path1 = Paths.get(path);
        return path1.getParent().toAbsolutePath().toString() + File.separator;
    }

    public static String trimWindowsSpecialChars(String name) {
        return StringUtils.isEmpty(name) ? name : name.replaceAll("[?/\\\\*<>|:\"]", "");
    }

    public static boolean rename(String sourcePath, String targetPath) {
        File sourceFile = new File(sourcePath);
        File targetFile = new File(targetPath);
        return sourceFile.renameTo(targetFile);
    }

    public static void merge(String result, String... sources) {


        try {
            OutputStream os = new FileOutputStream(result);
            Throwable var3 = null;

            try {
                String[] var4 = sources;
                int var5 = sources.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    String source = var4[var6];
                    byte[] bytes = getFileBytes(source);
                    os.write(bytes);
                }
            } catch (Throwable var17) {
                var3 = var17;
                throw var17;
            } finally {
                if (os != null) {
                    if (var3 != null) {
                        try {
                            os.close();
                        } catch (Throwable var16) {
                            var3.addSuppressed(var16);
                        }
                    } else {
                        os.close();
                    }
                }

            }

        } catch (IOException var19) {
            throw new RuntimeException(var19);
        }
    }

    public static void merge(String result, byte[]... byteArrays) {


        try {
            OutputStream os = new FileOutputStream(result);
            Throwable var3 = null;

            try {
                byte[][] var4 = byteArrays;
                int var5 = byteArrays.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    byte[] bytes = var4[var6];
                    os.write(bytes);
                }
            } catch (Throwable var16) {
                var3 = var16;
                throw var16;
            } finally {
                if (os != null) {
                    if (var3 != null) {
                        try {
                            os.close();
                        } catch (Throwable var15) {
                            var3.addSuppressed(var15);
                        }
                    } else {
                        os.close();
                    }
                }

            }

        } catch (IOException var18) {
            throw new RuntimeException(var18);
        }
    }

    public static void merge(String result, List<byte[]> byteArrayList) {


        try {
            OutputStream os = new FileOutputStream(result);
            Throwable var3 = null;

            try {
                Iterator var4 = byteArrayList.iterator();

                while(var4.hasNext()) {
                    byte[] bytes = (byte[])var4.next();
                    os.write(bytes);
                }
            } catch (Throwable var14) {
                var3 = var14;
                throw var14;
            } finally {
                if (os != null) {
                    if (var3 != null) {
                        try {
                            os.close();
                        } catch (Throwable var13) {
                            var3.addSuppressed(var13);
                        }
                    } else {
                        os.close();
                    }
                }

            }

        } catch (IOException var16) {
            throw new RuntimeException(var16);
        }
    }

    public static void write(String filePath, byte[] bytes) {


        try {
            OutputStream os = new FileOutputStream(filePath);
            Throwable var3 = null;

            try {
                os.write(bytes);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (os != null) {
                    if (var3 != null) {
                        try {
                            os.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        os.close();
                    }
                }

            }

        } catch (IOException var15) {
            throw new RuntimeException(var15);
        }
    }

    public static String escapeWindowsSpecial(String fileName) {
        return StringUtils.isEmpty(fileName) ? fileName : fileName.replaceAll("[\"<>/\\\\|:*?]", "");
    }

    public static boolean createDir(String dir) {
        if (StringUtils.isEmpty(dir)) {
            return false;
        } else {
            File file = new File(dir);
            return file.isDirectory() ? file.mkdirs() : false;
        }
    }

    public static void truncate(String filePath) {
        write(filePath, (CharSequence)"", StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void append(String filePath, String line) {
        write(filePath, (CharSequence)line, StandardOpenOption.APPEND);
    }

    public static void append(String filePath, Collection<String> collection) {
        write(filePath, (Iterable)collection, StandardOpenOption.APPEND);
    }
}
