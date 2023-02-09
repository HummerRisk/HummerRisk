package com.hummer.common.core.proxy.server;

import com.hummerrisk.commons.utils.LogUtil;
import org.hyperic.sigar.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Server {

    private static final String osName = System.getProperty("os.name");

    /*获取内存*/
    public static String readRAM() throws Exception {
        Sigar sigar = new Sigar();
        //获取内存
        try {
            Mem mem = sigar.getMem();
            int memTotal = (int) Math.ceil((double) mem.getTotal() / (1024L * 1024L * 1024L));
            String memFree = String.format("%.1f", (double) mem.getFree() / (1024L * 1024L * 1024L));
            String memUser = String.format("%.0f", (memTotal - Double.parseDouble(memFree)) / memTotal * 100);
            return "内存总量:" + memTotal + "G; " + "内存剩余量:" + memFree + "G; " + "内存使用率:" + memUser + "%。";
        } catch (SigarException e1) {
            LogUtil.error(e1.getMessage());
        }
        return "";
    }

    //获取磁盘信息
    public static String readDisk() {
        Sigar sigar = new Sigar();
        //获取硬盘
        Long ypTotal = 0L;
        Long ypfree = 0L;
        try {
            FileSystem fslist[] = sigar.getFileSystemList();
            for (FileSystem fs : fslist) {
                FileSystemUsage usage = null;
                usage = sigar.getFileSystemUsage(fs.getDirName());
                switch (fs.getType()) {
                    case 0: // TYPE_UNKNOWN ：未知
                        break;
                    case 1: // TYPE_NONE
                        break;
                    case 2: // TYPE_LOCAL_DISK : 本地硬盘
                        // 文件系统总大小
                        ypTotal += usage.getTotal();
                        ypfree += usage.getFree();
                        break;
                    case 3:// TYPE_NETWORK ：网络
                        break;
                    case 4:// TYPE_RAM_DISK ：闪存
                        break;
                    case 5:// TYPE_CDROM ：光驱
                        break;
                    case 6:// TYPE_SWAP ：页面交换
                        break;
                }
            }
            int hdTotal = (int) ((double) ypTotal / (1024L * 1024L));
            String hdfree = String.format("%.1f", (double) ypfree / (1024L * 1024L));
            String hdUser = String.format("%.0f", (hdTotal - Double.parseDouble(hdfree)) / hdTotal * 100);
            return "硬盘总量:" + hdTotal + "G; " + "硬盘剩余量:" + hdfree + "G; " + "硬盘使用率:" + hdUser + "%。";
        } catch (SigarException e1) {
            e1.printStackTrace();
            LogUtil.error(e1.getMessage());
        }
        return "";
    }

    //获取windows系统cpu使用率
    public static double getCpuRatioForWindows() {
        try {
            String procCmd = System.getenv("windir")
                    + "//system32//wbem//wmic.exe process get Caption,CommandLine,"
                    + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";
            // 取进程信息
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));
            Thread.sleep(500);
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));
            if (c0 != null && c1 != null) {
                long idletime = c1[0] - c0[0];
                long busytime = c1[1] - c0[1];
                return Double.valueOf(100 * busytime / (busytime + idletime)).doubleValue();
            } else {
                return 0.0;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.error(ex.getMessage());
            return 0.0;
        }
    }

    //获取windows系统cpu信息
    private static long[] readCpu(final Process proc) {
        long[] retn = new long[2];
        try {
            proc.getOutputStream().close();
            InputStreamReader ir = new InputStreamReader(proc.getInputStream(), "gbk");
            LineNumberReader input = new LineNumberReader(ir);
            String line = input.readLine();
            if (line == null || line.length() < 10) {
                return null;
            }
            int capidx = line.indexOf("Caption");
            int cmdidx = line.indexOf("CommandLine");
            int rocidx = line.indexOf("ReadOperationCount");
            int umtidx = line.indexOf("UserModeTime");
            int kmtidx = line.indexOf("KernelModeTime");
            int wocidx = line.indexOf("WriteOperationCount");
            long idletime = 0;
            long kneltime = 0;
            long usertime = 0;
            while ((line = input.readLine()) != null) {
                if (line.length() < wocidx) {
                    continue;
                }
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,
                // ThreadCount,UserModeTime,WriteOperation
                String caption = Bytes.substring(line, capidx, cmdidx - 1).trim();
                String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();
                if (cmd.contains("wmic.exe")) {
                    continue;
                }
                // log.info("line="+line);
                if (caption.equals("System Idle Process") || caption.equals("System")) {
                    idletime += Long.valueOf(Bytes.substring(line, kmtidx, rocidx - 1).trim()).longValue();
                    idletime += Long.valueOf(Bytes.substring(line, umtidx, wocidx - 1).trim()).longValue();
                    continue;
                }
                kneltime += Long.valueOf(Bytes.substring(line, kmtidx, rocidx - 1).trim()).longValue();
                usertime += Long.valueOf(Bytes.substring(line, umtidx, wocidx - 1).trim()).longValue();
            }
            retn[0] = idletime;
            retn[1] = kneltime + usertime;
            return retn;
        } catch (Exception ex) {
            ex.printStackTrace();
            LogUtil.error(ex.getMessage());
        } finally {
            try {
                proc.getInputStream().close();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.error(e.getMessage());
            }
        }
        return new long[2];
    }

    public static class Bytes {
        /** */
        /**
         * 由于String.subString对汉字处理存在问题（把一个汉字视为一个字节)，因此在
         * 包含汉字的字符串时存在隐患，现调整如下：
         *
         * @param src       要截取的字符串
         * @param start_idx 开始坐标（包括该坐标)
         * @param end_idx   截止坐标（包括该坐标）
         * @return
         * @throws UnsupportedEncodingException
         */
        public static String substring(String src, int start_idx, int end_idx) throws UnsupportedEncodingException {
            byte[] b = src.getBytes();
            StringBuilder tgt = new StringBuilder();
            for (int i = start_idx; i <= end_idx; i++) {
                tgt.append((char) b[i]);
            }
            return tgt.toString();
        }
    }

    /**
     * 功能：获取Linux系统cpu使用率
     */
    public static int cpuUsage() {
        try {
            Map<?, ?> map1 = cpuinfo();
            Thread.sleep(500);
            Map<?, ?> map2 = cpuinfo();
            if (map1.size() > 0 && map2.size() > 0) {
                long user1 = Long.parseLong(map1.get("user").toString());
                long nice1 = Long.parseLong(map1.get("nice").toString());
                long system1 = Long.parseLong(map1.get("system").toString());
                long idle1 = Long.parseLong(map1.get("idle").toString());

                long user2 = Long.parseLong(map2.get("user").toString());
                long nice2 = Long.parseLong(map2.get("nice").toString());
                long system2 = Long.parseLong(map2.get("system").toString());
                long idle2 = Long.parseLong(map2.get("idle").toString());

                long total1 = user1 + system1 + nice1;
                long total2 = user2 + system2 + nice2;
                float total = total2 - total1;

                long totalIdle1 = user1 + nice1 + system1 + idle1;
                long totalIdle2 = user2 + nice2 + system2 + idle2;
                float totalidle = totalIdle2 - totalIdle1;

                float cpusage = (total / totalidle) * 100;
                return (int) cpusage;
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
        }
        return 0;
    }

    /**
     * 功能：CPU使用信息
     */
    public static Map<?, ?> cpuinfo() {
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        Map<String, Object> map = new HashMap<>();
        try {
            inputs = new InputStreamReader(Files.newInputStream(Paths.get("/proc/stat")));
            buffer = new BufferedReader(inputs);
            String line;
            while (true) {
                line = buffer.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("cpu")) {
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    List<String> temp = new ArrayList<>();
                    while (tokenizer.hasMoreElements()) {
                        String value = tokenizer.nextToken();
                        temp.add(value);
                    }
                    map.put("user", temp.get(1));
                    map.put("nice", temp.get(2));
                    map.put("system", temp.get(3));
                    map.put("idle", temp.get(4));
                    map.put("iowait", temp.get(5));
                    map.put("irq", temp.get(6));
                    map.put("softirq", temp.get(7));
                    map.put("stealstolen", temp.get(8));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                    inputs.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                LogUtil.error(e2.getMessage());
            }
        }
        return map;
    }

    /**
     * Linux 得到磁盘的使用率
     *
     * @return
     * @throws Exception
     */
    public static int getDiskUsage() throws Exception {
        double totalHD = 0;
        double usedHD = 0;
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec("df -hl");// df -hl 查看硬盘空间
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String str;
            String[] strArray;
            while ((str = in.readLine()) != null) {
                int m = 0;
                strArray = str.split(" ");
                for (String tmp : strArray) {
                    if (tmp.trim().length() == 0)
                        continue;
                    ++m;
                    if (tmp.contains("G")) {
                        if (m == 2) {
                            if (!tmp.equals("0"))
                                totalHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1)) * 1024;
                        }
                        if (m == 3) {
                            if (!tmp.equals("none") && !tmp.equals("0"))
                                usedHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1)) * 1024;
                        }
                    }
                    if (tmp.contains("M")) {
                        if (m == 2) {
                            if (!tmp.equals("0"))
                                totalHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1));
                        }
                        if (m == 3) {
                            if (!tmp.equals("none") && !tmp.equals("0"))
                                usedHD += Double.parseDouble(tmp.substring(0, tmp.length() - 1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
        } finally {
            in.close();
        }
        // 保留2位小数
        double precent = (usedHD / totalHD) * 100;
        return (int) precent;
    }

    /**
     * 功能：内存使用率
     */
    public static int memoryUsage() {
        Map<String, Object> map = new HashMap<>();
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        try {
            inputs = new InputStreamReader(Files.newInputStream(Paths.get("/proc/meminfo")));
            buffer = new BufferedReader(inputs);
            String line;
            while (true) {
                line = buffer.readLine();
                if (line == null)
                    break;
                int beginIndex = 0;
                int endIndex = line.indexOf(":");
                if (endIndex != -1) {
                    String key = line.substring(beginIndex, endIndex);
                    beginIndex = endIndex + 1;
                    endIndex = line.length();
                    String memory = line.substring(beginIndex, endIndex);
                    String value = memory.replace("kB", "").trim();
                    map.put(key, value);
                }
            }

            long memTotal = Long.parseLong(map.get("MemTotal").toString());
            long memFree = Long.parseLong(map.get("MemFree").toString());
            long memused = memTotal - memFree;
            long buffers = Long.parseLong(map.get("Buffers").toString());
            long cached = Long.parseLong(map.get("Cached").toString());

            double usage = (double) (memused - buffers - cached) / memTotal * 100;
            return (int) usage;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
        } finally {
            try {
                assert buffer != null;
                buffer.close();
                inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
                LogUtil.error(e2.getMessage());
            }
        }
        return 0;
    }

}
