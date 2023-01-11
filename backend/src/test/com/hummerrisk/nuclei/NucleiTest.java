package com.hummerrisk.nuclei;

import com.hummerrisk.commons.constants.RegionsConstants;
import org.apache.commons.exec.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NucleiTest {

    @org.junit.Test
    public void Test() throws Exception {
        try{
            String command = "nuclei -l /tmp/urls.txt -t /tmp/nuclei.yaml -o /tmp/result.txt";
            // 命令行
            CommandLine commandLine = CommandLine.parse(command);

            // 重定向stdout和stderr到文件
            FileOutputStream fileOutputStream = new FileOutputStream("/tmp/exec.log");
            PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(fileOutputStream);

            // 创建执行器
            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

            ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
            Executor executor = new DefaultExecutor();
            executor.setStreamHandler(pumpStreamHandler);
            executor.setExitValue(1);
            executor.setWatchdog(watchdog);
            executor.execute(commandLine, resultHandler);

            resultHandler.waitFor();

            // 关闭流
            fileOutputStream.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @org.junit.Test
    public void test2 () throws Exception {
        System.out.println(RegionsConstants.AwsMap.get("cn-north-1"));
    }

}
