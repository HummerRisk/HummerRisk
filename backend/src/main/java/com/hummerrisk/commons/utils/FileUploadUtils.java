package com.hummerrisk.commons.utils;

import com.hummerrisk.commons.constants.PackageConstants;
import com.hummerrisk.commons.exception.FileNameLengthLimitExceededException;
import com.hummerrisk.commons.exception.FileSizeLimitExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 文件上传工具类
 *
 * maguohao
 */
public class FileUploadUtils
{

    private static int counter = 0;

    public static final String uploadSubff(String baseDir, MultipartFile file) throws IOException
    {
        try
        {
            return upload(baseDir, file);
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }
    /**
     * 文件上传
     *
     * @param baseDir 相对应用的基目录
     * @param file 上传的文件
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException 比如读写文件出错时
     */
    public static final String upload(String baseDir, MultipartFile file)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException
    {

        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > PackageConstants.DEFAULT_FILE_NAME_LENGTH)
        {
            throw new FileNameLengthLimitExceededException(PackageConstants.DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file);

        String fileName = extractFilename(file);

        File desc = getAbsoluteFile(baseDir, fileName);
        file.transferTo(desc.toPath().toAbsolutePath());

        return fileName;
    }

    /**
     * 文件上传
     *
     * @param baseDir 相对应用的基目录
     * @param file 上传的文件
     * @return 返回上传成功的文件名
     * @throws FileSizeLimitExceededException 如果超出最大大小
     * @throws FileNameLengthLimitExceededException 文件名太长
     * @throws IOException 比如读写文件出错时
     */
    public static final String uploadForFs(String baseDir, MultipartFile file)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException
    {

        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > PackageConstants.DEFAULT_FILE_NAME_LENGTH)
        {
            throw new FileNameLengthLimitExceededException(PackageConstants.DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file);

        String first = file.getOriginalFilename();
        String second = DateUtils.datePath() + "/" + encodingFilename(first) + "/";
        String fileName = second + first;

        File desc = getAbsoluteFile(baseDir, fileName);
        file.transferTo(desc.toPath().toAbsolutePath());

        return second;
    }

    public static final String extractFilename(MultipartFile file)
    {
        String filename = file.getOriginalFilename();
        filename = DateUtils.datePath() + "/" + encodingFilename(filename) + "/" + filename;
        return filename;
    }

    private static final File getAbsoluteFile(String uploadDir, String filename) throws IOException
    {
        File desc = new File(uploadDir + filename);

        if (!desc.getParentFile().exists())
        {
            desc.getParentFile().mkdirs();
        }
        if (!desc.exists())
        {
            desc.createNewFile();
        }
        return desc;
    }

    /**
     * 编码文件名
     */
    private static final String encodingFilename(String filename)
    {
        filename = filename.replace("_", " ");
        filename = Md5Utils.hash(filename + System.nanoTime() + counter++);
        return filename;
    }

    /**
     * 文件大小校验
     *
     * @param file 上传的文件
     * @return
     * @throws FileSizeLimitExceededException 如果超出最大大小
     */
    public static final void assertAllowed(MultipartFile file) throws FileSizeLimitExceededException
    {
        long size = file.getSize();
        if (PackageConstants.DEFAULT_MAX_SIZE != -1 && size > PackageConstants.DEFAULT_MAX_SIZE)
        {
            throw new FileSizeLimitExceededException(PackageConstants.DEFAULT_MAX_SIZE / 1024 / 1024);
        }
    }

    //删除文件
    public static final void delete(String pathName) throws Exception
    {
        try{
            File file = new File(pathName);
            if(file.delete()){
                System.out.println(file.getName() + " 文件已被删除！");
            }else{
                System.out.println("文件删除失败！");
            }
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    private static void deleteFile(String pathName) throws Exception {
        try{
            File file = new File(pathName);
                if (file.isFile()){//判断是否为文件，是，则删除
                    System.out.println(file.getAbsoluteFile());//打印路径
                    file.delete();
                }else{//不为文件，则为文件夹
                    String[] childFilePath = file.list();//获取文件夹下所有文件相对路径
                    for (String path:childFilePath){
                        deleteFile(file.getAbsoluteFile()+"/"+path);//递归，对每个都进行判断
                    }
                    System.out.println(file.getAbsoluteFile());
                    file.delete();
                }
        }catch(Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public static final String trans(String path) throws Exception {
        String pathTrans = path.replace("files/", "");
        return pathTrans;
    }

    //直接读取文件内容
    public static final String uploadFileToString(MultipartFile file) throws IOException
    {
        try
        {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
            StringBuffer buffer = new StringBuffer();
            String lineTxt;
            while ((lineTxt=bufferedReader.readLine())!=null){
                buffer.append(lineTxt);
                buffer.append("\r\n");
            }
            return buffer.toString();
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    public static final String uploadCertificate(String baseDir, MultipartFile file, String extension)
            throws FileSizeLimitExceededException, IOException, FileNameLengthLimitExceededException
    {

        int fileNamelength = file.getOriginalFilename().length();
        if (fileNamelength > PackageConstants.DEFAULT_FILE_NAME_LENGTH)
        {
            throw new FileNameLengthLimitExceededException(PackageConstants.DEFAULT_FILE_NAME_LENGTH);
        }

        assertAllowed(file);

        String fileName = extractCertificateName(file, extension);

        File desc = getAbsoluteFile(baseDir, fileName);
        file.transferTo(desc.toPath().toAbsolutePath());

        return fileName;
    }

    public static final String extractCertificateName(MultipartFile file, String extension)
    {
        String filename = file.getOriginalFilename();
        filename = DateUtils.datePath() + "/" + encodingFilename(filename) + extension;
        return filename;
    }

}
