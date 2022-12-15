package com.hummerrisk.commons.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.exec.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;

import static org.springframework.util.FileCopyUtils.BUFFER_SIZE;

/**
 * @author harris
 */
public class CommandUtils {
    /**
     * 返回值对象
     *
     * @param <T>
     */
    static class Result<T> {
        /**
         * 返回数据
         */
        private T data;
        /**
         * 正常200
         */
        private Integer code;

        /**
         * @param data 调用返回值
         * @param code 调用是否成功 200 成功其他失败
         */
        private Result(T data, Integer code) {
            this.data = data;
            this.code = code;
        }

        public static <T> Result<T> ok(T t) {
            Result<T> tResult = new Result<>(t, 200);
            return tResult;
        }

        public static <T> Result<T> error(T t) {
            Result<T> tResult = new Result<>(t, 200);
            return tResult;
        }

        public Integer getCode() {
            return code;
        }

        public T getData() {
            return data;
        }
    }

    /**
     * @param command shell语句
     * @param workDir 工作目录
     * @return
     * @throws Exception
     */
    public static Result<String> commonExecCmdWithResultNew(String command, String workDir) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        Process exec;
        if (StringUtils.isNotBlank(workDir)) {
            exec = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, new File(workDir));
        } else {
            exec = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});
        }
        Integer code = 200;
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(exec.getErrorStream()))
        ) {
            exec.waitFor();
            String line;
            if (exec.exitValue() != 0) {
                //错误执行返回信息
                while ((line = errorReader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                code = 500;
            } else {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
            }
        } catch (InterruptedException e) {
            throw e;
        }
        return code.equals(200) ? Result.ok(stringBuilder.toString()) : Result.error(stringBuilder.toString());
    }

    /**
     * @param command
     * @param workDir 工作路径
     * @throws Exception
     */
    public static String commonExecCmdWithResult(String command, String workDir) throws Exception {
        Result<String> stringResult = commonExecCmdWithResultNew(command, workDir);
        return stringResult.getData();
    }

    /**
     * 写入数据到文件
     *
     * @param content  写入内容
     * @param dirPath  文件目录
     * @param fileName 文件名称
     * @return 文件目录
     * @throws Exception Io异常
     */
    public static String saveAsFile(String content, String dirPath, String fileName, boolean append) throws Exception {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，false
            fwriter = new FileWriter(dirPath + "/" + fileName, append);
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                assert fwriter != null;
                fwriter.flush();
                fwriter.close();
            } catch (IOException e) {
                throw e;
            } finally {
                // empty!
            }
        }
        return dirPath;
    }

    /**
     * @param command
     * @param workDir 工作路径
     * @throws Exception
     * @Desc Nuclei 调用命令行工具，Java 调用 Runtime 执行 nuclei 命令会阻塞，所以找了apache-commons-exec异步执行
     */
    public static String commonExecCmdWithResultByNuclei(String command, String workDir) throws Exception {
        FileOutputStream fileOutputStream = null;
        try {
            // 命令行
            CommandLine commandLine = CommandLine.parse(command);

            // 重定向stdout和stderr到文件
            fileOutputStream = new FileOutputStream(workDir + "/exec.log");
            PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(fileOutputStream);

            // 创建执行器
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

            ExecuteWatchdog watchdog = new ExecuteWatchdog(60 * 1000);
            Executor executor = new DefaultExecutor();
            executor.setStreamHandler(pumpStreamHandler);
            executor.setExitValue(1);
            executor.setWatchdog(watchdog);
            executor.execute(commandLine, resultHandler);

            resultHandler.waitFor();

            return ReadFileUtils.readToBuffer(workDir + "/exec.log");
        } catch (Exception e) {
            throw e;
        } finally {
            // 关闭流
            fileOutputStream.close();
        }

    }

    public static boolean upzipTar(String fileName, String targetPath){
        Runtime run = Runtime.getRuntime();
        String cmd = "tar xZf " + fileName + " -C " + targetPath;
        try {

            Process process = run.exec(cmd);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            if(in.readLine() != null){ //解压缩失败
                LogUtil.error("unzip fail "+fileName);
                return false;
            }else{          //解压缩成功
                LogUtil.info(" success unzip "+ fileName);
            }
        } catch (IOException e) {
            LogUtil.error("IOException occured"+e.getMessage());
        }
        return true;
    }

    /**
     * 解压 tar.gz 文件到指定目录
     *
     * @param tarGzFile  tar.gz 文件路径
     * @param destDir  解压到 destDir 目录，如果没有则自动创建
     *
     */
    public static void extractTarGZ(File tarGzFile, String destDir) throws IOException {

        GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(new FileInputStream(tarGzFile));
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {
            TarArchiveEntry entry;

            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File f = new File(destDir + "/" + entry.getName());
                    boolean created = f.mkdirs();
                    if (!created) {
                        System.out.printf("Unable to create directory '%s', during extraction of archive contents.\n",
                                f.getAbsolutePath());
                    }
                } else {
                    int count;
                    byte [] data = new byte[BUFFER_SIZE];
                    FileOutputStream fos = new FileOutputStream(destDir + "/" + entry.getName(), false);
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE)) {
                        while ((count = tarIn.read(data, 0, BUFFER_SIZE)) != -1) {
                            dest.write(data, 0, count);
                        }
                    }
                }
            }
        }
    }

    /**
     * 解压 zip 文件到指定目录
     *
     * @param zipFile  zip 文件路径
     * @param destDir  解压到 destDir 目录，如果没有则自动创建
     *
     */
    public static void extractZip(File zipFile, String destDir) throws IOException {

        try (ZipArchiveInputStream zipIn = new ZipArchiveInputStream(new FileInputStream(zipFile))) {
            ZipArchiveEntry entry;

            while ((entry = (ZipArchiveEntry) zipIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File f = new File(destDir + "/" + entry.getName());
                    boolean created = f.mkdirs();
                    if (!created) {
                        System.out.printf("Unable to create directory '%s', during extraction of archive contents.\n",
                                f.getAbsolutePath());
                    }
                } else {
                    int count;
                    byte [] data = new byte[BUFFER_SIZE];
                    FileOutputStream fos = new FileOutputStream(destDir + "/" + entry.getName(), false);
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE)) {
                        while ((count = zipIn.read(data, 0, BUFFER_SIZE)) != -1) {
                            dest.write(data, 0, count);
                        }
                    }
                }
            }
        }
    }

}
