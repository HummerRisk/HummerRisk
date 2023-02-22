package com.hummer.modules.monitor.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AppStartListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        try {
            loading(1);
            System.out.println();
            System.out.println("\u001b[30m 绿蚁新醅酒， \u001b[31m 红泥小火炉。 \u001b[32m 晚来天欲雪， \u001b[33m 能饮一杯无？ \u001b[0m");
            System.out.println();
            System.out.println("\u001b[34m 众鸟高飞尽， \u001b[35m 孤云独去闲。 \u001b[36m 相看两不厌， \u001b[37m 只有敬亭山。 \u001b[0m");
            System.out.println();
            System.out.println("\u001b[30;1m 移舟泊烟渚， \u001b[31;1m 日暮客愁新。 \u001b[32;1m 野旷天低树， \u001b[33;1m 江清月近人。 \u001b[0m");
            System.out.println();
            System.out.println("\u001b[34;1m 人闲桂花落， \u001b[35;1m 夜静春山空。 \u001b[36;1m 月出惊山鸟， \u001b[37;1m 时鸣春涧中。 \u001b[0m");
            System.out.println();
            System.out.println("================= 应用已启动 =================");
            System.out.println();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void loading(int count) throws InterruptedException {
        System.out.print(String.join("", Collections.nCopies(count, "\n"))); // 初始化进度条所占的空间
        List<Integer> allProgress = new ArrayList<>(Collections.nCopies(count, 0));
        while (true) {
            Thread.sleep(10);

            // 随机选择一个进度条，增加进度
            List<Integer> unfinished = new LinkedList<>();
            for (int i = 0; i < allProgress.size(); i++) {
                if (allProgress.get(i) < 100) {
                    unfinished.add(i);
                }
            }
            if (unfinished.isEmpty()) {
                break;
            }
            int index = unfinished.get(new Random().nextInt(unfinished.size()));
            allProgress.set(index, allProgress.get(index) + 1); // 进度+1

            // 绘制进度条
            System.out.print("\u001b[1000D"); // 移动到最左边
            System.out.print("\u001b[" + count + "A"); // 往上移动
            for (Integer progress : allProgress) {
                int width = progress / 4;
                String left = "[" + String.join("", Collections.nCopies(width, "#"));
                String right = String.join("", Collections.nCopies(25 - width, " ")) + "]";
                System.out.flush();
                System.out.println(left + right);
            }
        }
    }
}
