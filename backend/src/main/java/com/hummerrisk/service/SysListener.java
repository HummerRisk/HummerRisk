package com.hummerrisk.service;

import com.hummerrisk.commons.utils.LogUtil;
import com.hummerrisk.commons.utils.SigarUtil;
import com.hummerrisk.i18n.Translator;
import org.hyperic.sigar.*;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SysListener {

    private static ConcurrentHashMap<String, String> maps = new ConcurrentHashMap<>();

    // System信息，从jvm获取
    public static void property() throws UnknownHostException {
        try {
            Runtime r = Runtime.getRuntime();
            Properties props = System.getProperties();
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress();
            Map<String, String> map = System.getenv();
            String userName = map.get("USERNAME");// 获取用户名
            String computerName = map.get("COMPUTERNAME");// 获取计算机名
            String userDomain = map.get("USERDOMAIN");// 获取计算机域名
            maps.put("system.userName", "用户名:    " + userName);
            maps.put("system.computerName", "计算机名:    " + computerName);
            maps.put("system.userDomain", "计算机域名:    " + userDomain);
            maps.put("system.ip", "本地ip地址:    " + ip);
            maps.put("system.hostName", "本地主机名:    " + addr.getHostName());
            maps.put("system.totalMemory", "JVM可以使用的总内存:    " + changeFlowFormat(r.totalMemory()));
            maps.put("system.freeMemory", "JVM可以使用的剩余内存:    " + changeFlowFormat(r.freeMemory()));
            maps.put("system.availableProcessors", "JVM可以使用的处理器个数:    " + r.availableProcessors());
            maps.put("system.java.version", "Java的运行环境版本：    " + props.getProperty("java.version"));
            maps.put("system.java.vendor", "Java的运行环境供应商：    " + props.getProperty("java.vendor"));
            maps.put("system.java.vendor.url", "Java供应商的URL：    " + props.getProperty("java.vendor.url"));
            maps.put("system.java.home", "Java的安装路径：    " + props.getProperty("java.home"));
            maps.put("system.java.vm.specification.version", "Java的虚拟机规范版本：    " + props.getProperty("java.vm.specification.version"));
            maps.put("system.java.vm.specification.vendor", "Java的虚拟机规范供应商：    " + props.getProperty("java.vm.specification.vendor"));
            maps.put("system.java.vm.specification.name", "Java的虚拟机规范名称：    " + props.getProperty("java.vm.specification.name"));
            maps.put("system.java.vm.version", "Java的虚拟机实现版本：    " + props.getProperty("java.vm.version"));
            maps.put("system.java.vm.name", "Java的虚拟机实现名称：    " + props.getProperty("java.vm.name"));
            maps.put("system.java.specification.version", "Java运行时环境规范版本：    " + props.getProperty("java.specification.version"));
            maps.put("system.java.specification.vender", "Java运行时环境规范供应商：    " + props.getProperty("java.specification.vender"));
            maps.put("system.java.specification.name", "Java运行时环境规范名称：    " + props.getProperty("java.specification.name"));
            maps.put("system.java.class.version", "Java的类格式版本号：    " + props.getProperty("java.class.version"));
            maps.put("system.java.library.path", "Java加载库时搜索的路径列表：    " + props.getProperty("java.library.path"));
            maps.put("system.java.io.tmpdir", "默认的临时文件路径：    " + props.getProperty("java.io.tmpdir"));
            maps.put("system.java.ext.dirs", "一个或多个扩展目录的路径：    " + props.getProperty("java.ext.dirs"));
            maps.put("system.os.name", "操作系统的名称：    " + props.getProperty("os.name"));
            maps.put("system.os.arch", "操作系统的构架：    " + props.getProperty("os.arch"));
            maps.put("system.os.version", "操作系统的版本：    " + props.getProperty("os.version"));
            maps.put("system.file.separator", "文件分隔符：    " + props.getProperty("file.separator"));
            maps.put("system.path.separator", "路径分隔符：    " + props.getProperty("path.separator"));
            maps.put("system.line.separator", "行分隔符：    " + props.getProperty("line.separator"));
            maps.put("system.user.name", "用户的账户名称：    " + props.getProperty("user.name"));
            maps.put("system.user.home", "用户的主目录：    " + props.getProperty("user.home"));
            maps.put("system.user.dir", "用户的当前工作目录：    " + props.getProperty("user.dir"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 内存信息
    public static void memory() throws SigarException {
        try {
            Sigar sigar = SigarUtil.getInstance();
            Mem mem = sigar.getMem();
            // 内存总量
            maps.put("system.memTotal", "内存总量:    " + changeFlowFormat(mem.getTotal()) + " av");
            // 当前内存使用量
            maps.put("system.memUsed", "当前内存使用量:    " + changeFlowFormat(mem.getUsed()) + " used");
            // 当前内存剩余量
            maps.put("system.menFree", "当前内存剩余量:    " + changeFlowFormat(mem.getFree()) + " free");
            Swap swap = sigar.getSwap();
            // 交换区总量
            maps.put("system.swapTotal", "交换区总量:    " + changeFlowFormat(swap.getTotal()) + " av");
            // 当前交换区使用量
            maps.put("system.swapUsed", "当前交换区使用量:    " + changeFlowFormat(swap.getUsed()) + " used");
            // 当前交换区剩余量
            maps.put("system.swapFree", "当前交换区剩余量:    " + changeFlowFormat(swap.getFree()) + " free");
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    // cpu信息
    public static void cpu() throws SigarException {
        try {
            Sigar sigar = SigarUtil.getInstance();
            CpuInfo infos[] = sigar.getCpuInfoList();
            CpuPerc cpuList[];
            cpuList = sigar.getCpuPercList();
            for (int i = 0; i < infos.length; i++) {// 不管是单块CPU还是多CPU都适用
                CpuInfo info = infos[i];
                maps.put("system.cpuNo" + i, "第" + (i + 1) + "块CPU信息");
                maps.put("system.cpuMhz" + i, "CPU的总量MHz:    " + info.getMhz());// CPU的总量MHz
                maps.put("system.cpuVendor" + i, "CPU生产商:    " + info.getVendor());// 获得CPU的卖主，如：Intel
                maps.put("system.cpuModel" + i, "CPU类别:    " + info.getModel());// 获得CPU的类别，如：Celeron
                maps.put("system.cpuCacheSize" + i, "CPU缓存数量:    " + info.getCacheSize());// 缓冲存储器数量
                printCpuPerc(cpuList[i]);
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    public static void printCpuPerc(CpuPerc cpu) {
        maps.put("system.cpuUse", "CPU用户使用率:    " + CpuPerc.format(cpu.getUser()));// 用户使用率
        maps.put("system.cpuSys", "CPU系统使用率:    " + CpuPerc.format(cpu.getSys()));// 系统使用率
        maps.put("system.cpuWait", "CPU当前等待率:    " + CpuPerc.format(cpu.getWait()));// 当前等待率
        maps.put("system.cpuNice", "CPU当前错误率:    " + CpuPerc.format(cpu.getNice()));//
        maps.put("system.cpuIdle", "CPU当前空闲率:    " + CpuPerc.format(cpu.getIdle()));// 当前空闲率
        maps.put("system.cpuCombined", "CPU总的使用率:    " + CpuPerc.format(cpu.getCombined()));// 总的使用率
    }

    // 用户信息
    public static void who() throws SigarException {
        try {
            Sigar sigar = SigarUtil.getInstance();
            Who who[] = sigar.getWhoList();
            if (who != null && who.length > 0) {
                int i = 0;
                for (Who _who : who) {
                    i++;
                    maps.put("system.whoDevice" + i, "用户控制台:    " + _who.getDevice());
                    maps.put("system.whoHost" + i, "用户host:    " + _who.getHost());
                    // 当前系统进程表中的用户名
                    maps.put("system.whoUser" + i, "当前系统进程表中的用户名:    " + _who.getUser());
                }
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    // 文件系统信息
    public static void file() throws Exception {
        try {
            Sigar sigar = SigarUtil.getInstance();
            FileSystem fslist[] = sigar.getFileSystemList();
            for (int i = 0; i < fslist.length; i++) {
                maps.put("system.fs" + i, "分区的盘符名称" + i);
                FileSystem fs = fslist[i];
                // 分区的盘符名称
                maps.put("system.fsDevName" + i, "盘符名称:    " + fs.getDevName());
                // 分区的盘符名称
                maps.put("system.fsDirName" + i, "盘符路径:    " + fs.getDirName());
                maps.put("system.fsFlags" + i, "盘符标志:    " + fs.getFlags());//
                // 文件系统类型，比如 FAT32、NTFS
                maps.put("system.fsSysTypeName" + i, "盘符类型:    " + fs.getSysTypeName());
                // 文件系统类型名，比如本地硬盘、光驱、网络文件系统等
                maps.put("system.fsTypeName" + i, "盘符类型名:    " + fs.getTypeName());
                // 文件系统类型
                maps.put("system.fsType" + i, "盘符文件系统类型:    " + fs.getType());
                FileSystemUsage usage = null;
                switch (fs.getType()) {
                    case 0: // TYPE_UNKNOWN ：未知
                        break;
                    case 1: // TYPE_NONE
                        break;
                    case 2: // TYPE_LOCAL_DISK : 本地硬盘
                        // 文件系统总大小
                        usage = sigar.getFileSystemUsage(fs.getDirName());
                        maps.put("system.fsUsageTotal" + i, fs.getDevName() + "总大小:    " + usage.getTotal() + "KB");
                        // 文件系统剩余大小
                        maps.put("system.fsUsageFree" + i, fs.getDevName() + "剩余大小:    " + usage.getFree() + "KB");
                        // 文件系统可用大小
                        maps.put("system.fsUsageAvail" + i, fs.getDevName() + "可用大小:    " + usage.getAvail() + "KB");
                        // 文件系统已经使用量
                        maps.put("system.totalMemory" + i, fs.getDevName() + "已经使用量:    " + usage.getUsed() + "KB");
                        double usePercent = usage.getUsePercent() * 100D;
                        // 文件系统资源的利用率
                        maps.put("system.fsUsePercent" + i, fs.getDevName() + "资源的利用率:    " + usePercent + "%");
                        maps.put("system.fsDiskReads" + i, fs.getDevName() + "读出：    " + usage.getDiskReads());
                        maps.put("system.fsDiskWrites" + i, fs.getDevName() + "写入：    " + usage.getDiskWrites());
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
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 网络信息
    public static void net() throws Exception {
        try {
            Sigar sigar = SigarUtil.getInstance();
            String ifNames[] = sigar.getNetInterfaceList();
            int i = 0;
            for (String name : ifNames) {
                i++;
                NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
                maps.put("system.netName" + i, "网络设备名:    " + name);// 网络设备名
                maps.put("system.netAddress" + i, "IP地址:    " + ifconfig.getAddress());// IP地址
                maps.put("system.netNetmask" + i, "子网掩码:    " + ifconfig.getNetmask());// 子网掩码
                if ((ifconfig.getFlags() & 1L) <= 0L) {
                    maps.put("system.totalMemory" + i, "!IFF_UP...skipping getNetInterfaceStat");
                    continue;
                }
                NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                maps.put("system.ifstatRxPackets" + i, name + "接收的总包裹数:" + ifstat.getRxPackets());// 接收的总包裹数
                maps.put("system.ifstatTxPackets" + i, name + "发送的总包裹数:" + ifstat.getTxPackets());// 发送的总包裹数
                maps.put("system.ifstatRxBytes" + i, name + "接收到的总字节数:" + ifstat.getRxBytes());// 接收到的总字节数
                maps.put("system.ifstatTxBytes" + i, name + "发送的总字节数:" + ifstat.getTxBytes());// 发送的总字节数
                maps.put("system.ifstatRxErrors" + i, name + "接收到的错误包数:" + ifstat.getRxErrors());// 接收到的错误包数
                maps.put("system.ifstatTxErrors" + i, name + "发送数据包时的错误数:" + ifstat.getTxErrors());// 发送数据包时的错误数
                maps.put("system.ifstatRxDropped" + i, name + "接收时丢弃的包数:" + ifstat.getRxDropped());// 接收时丢弃的包数
                maps.put("system.ifstatTxDropped" + i, name + "发送时丢弃的包数:" + ifstat.getTxDropped());// 发送时丢弃的包数
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 以太网信息
    public static void ethernet() throws SigarException {
        try {
            Sigar sigar = SigarUtil.getInstance();
            String[] ifaces = sigar.getNetInterfaceList();
            for (String iface : ifaces) {
                NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(iface);
                if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress()) || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                        || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                    continue;
                }
                maps.put("system.cfgAddress", cfg.getName() + "IP地址:" + cfg.getAddress());// IP地址
                maps.put("system.cfgBroadcast", cfg.getName() + "网关广播地址:" + cfg.getBroadcast());// 网关广播地址
                maps.put("system.cfgHwaddr", cfg.getName() + "网卡MAC地址:" + cfg.getHwaddr());// 网卡MAC地址
                maps.put("system.cfgNetmask", cfg.getName() + "子网掩码:" + cfg.getNetmask());// 子网掩码
                maps.put("system.cfgDescription", cfg.getName() + "网卡描述信息:" + cfg.getDescription());// 网卡描述信息
                maps.put("system.cfgType", cfg.getName() + "网卡类型" + cfg.getType());//
            }
        } catch (SigarException e) {
            e.printStackTrace();
        }
    }

    public static ConcurrentHashMap<String, String> getMaps() throws Exception {
        try {
//            memory();
//            readRAM();
//            readDisk();
//            cpuUsage();
//            getDiskUsage();
//            memoryUsage();
            property();
        } catch (Exception e) {
            LogUtil.error(e.getMessage());
        }
        return maps;
    }

    /*获取内存*/
    public static void readRAM() throws Exception {
        Sigar sigar = SigarUtil.getInstance();
        //获取内存
        try {
            Mem mem = sigar.getMem();
            int memTotal = (int) Math.ceil((double) mem.getTotal() / (1024L * 1024L * 1024L));
            String memFree = String.format("%.1f", (double) mem.getFree() / (1024L * 1024L * 1024L));
            String memUser = String.format("%.0f", (memTotal - Double.parseDouble(memFree)) / memTotal * 100);
            maps.put("system.readRAM", "内存总量:" + memTotal + "G; " + "内存剩余量:" + memFree + "G; " + "内存使用率:" + memUser + "%。");
        } catch (SigarException e1) {
            LogUtil.error(e1.getMessage());
        }
    }

    //获取磁盘信息
    public static void readDisk() {
        Sigar sigar = SigarUtil.getInstance();
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
            maps.put("system.readDisk", "硬盘总量:" + hdTotal + "G; " + "硬盘剩余量:" + hdfree + "G; " + "硬盘使用率:" + hdUser + "%。");
        } catch (SigarException e1) {
            e1.printStackTrace();
            LogUtil.error(e1.getMessage());
        }
    }

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
    public static String substring(String src, int start_idx, int end_idx) {
        byte[] b = src.getBytes();
        StringBuilder tgt = new StringBuilder();
        for (int i = start_idx; i <= end_idx; i++) {
            tgt.append((char) b[i]);
        }
        return tgt.toString();
    }

    /**
     * 功能：获取Linux系统cpu使用率
     */
    public static void cpuUsage() {
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
                maps.put("system.cpuUsage", "cpu使用率: " + cpusage);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
        }
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
    public static void getDiskUsage() throws Exception {
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
        maps.put("system.cpuUsage", "磁盘的使用率: " + precent);
    }

    /**
     * 功能：内存使用率
     */
    public static void memoryUsage() {
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
            maps.put("system.cpuUsage", "内存使用率: " + usage);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.error(e.getMessage());
        } finally {
            try {
                assert buffer != null;
                if (buffer != null) buffer.close();
                if (inputs != null) inputs.close();
            } catch (Exception e2) {
                e2.printStackTrace();
                LogUtil.error(e2.getMessage());
            }
        }
    }

    /**
     * 转换格式为xxGBxxMBxxKB
     */
    public static String changeFlowFormat(long flows) {
        if (flows < 1024) {
            return flows + "B";
        }
        flows = flows / 1024;//默认是1024KB
        if (flows > 0 && flows < 1024) {//小于1M
            return flows + "KB";
        } else if (flows >= 1024 && flows < 1048576) {//大于1M小于1G
            int changeM = (int) Math.floor(flows / 1024);//整M数
            int surplusM = (int) Math.floor(flows % 1024);//除M后的余数
            if (surplusM > 0) {//余数大于0KB
                return changeM + 1 + "MB";
            } else {//整M，没有余数
                return changeM + "MB";
            }
        } else if (flows >= 1048576) {//大于1G
            int changeG = (int) Math.floor(flows / 1048576);//整G数
            int surplusG = (int) Math.floor(flows % 1048576);//除G后的余数
            if (surplusG >= 1024) {//余数大于大于1M
                int changeM = (int) Math.floor(surplusG / 1024);
                int surplusM = (int) Math.floor(surplusG % 1024);
                if (surplusM > 0) {//余数大于0KB
                    return changeG + 1 + "GB";
                } else {//整M，没有余数
                    return changeG + 1 + "GB";
                }
            } else if (surplusG < 1024 && surplusG > 0) {//余数小于1M，大于0K
                int surplusM = (int) Math.floor(surplusG % 1024);
                return changeG + 1 + "GB";
            } else {
                return changeG + "GB";
            }
        }
        return Translator.get("i18n_no_data");
    }
}

