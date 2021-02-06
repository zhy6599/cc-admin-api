package cc.admin.modules.monitor.service.impl;

import cc.admin.modules.monitor.controller.ServerMonitorController;
import cc.admin.modules.monitor.service.ServerMonitorService;
import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author Zhang houying
 * @date 2019-11-03
 */
@Service("serverMonitorService")
@Slf4j
public class ServerMonitorServiceImpl implements ServerMonitorService {

	private static final int WAIT_SECOND = 1000;
	private static SystemInfo systemInfo = new SystemInfo();
	private static HardwareAbstractionLayer hardware = systemInfo.getHardware();
	private static OperatingSystem operatingSystem = systemInfo.getOperatingSystem();

	@Override
	public Map<String, Object> getServerInfo() {
		Map<String, Object> resultMap = new HashMap<>(8);
		try {
			resultMap.put("cpuInfo", getCpuInfo());
			resultMap.put("jvmInfo", getJvmInfo());
			resultMap.put("memInfo", getMemInfo());
			resultMap.put("sysFileInfo", getSysFileInfo());
			resultMap.put("sysInfo", getSysInfo());
			resultMap.put("sysTime", DateUtil.format(new Date(),"HH:mm:ss"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}


	public static void main(String[] args) {
		System.out.println(JSONObject.toJSONString(new ServerMonitorController().getServerInfo()));
		;
	}

	/**
	 * CPU信息
	 */
	public static JSONObject getCpuInfo() {
		JSONObject cpuInfo = new JSONObject();
		CentralProcessor processor = hardware.getProcessor();
		// CPU信息
		long[] prevTicks = processor.getSystemCpuLoadTicks();
		Util.sleep(WAIT_SECOND);
		long[] ticks = processor.getSystemCpuLoadTicks();
		long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
		long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
		long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
		long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
		long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
		long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
		long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
		long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
		long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
		//cpu核数
		cpuInfo.put("cpuNum", processor.getLogicalProcessorCount());
		//cpu系统使用率
		cpuInfo.put("cSys", NumberUtil.round((cSys * 1.0 / totalCpu) * 100,2));
		//cpu用户使用率
		cpuInfo.put("user", NumberUtil.round((user * 1.0 / totalCpu) * 100,2));
		//cpu当前等待率
		cpuInfo.put("ioWait", NumberUtil.round((iowait * 1.0 / totalCpu) * 100,2));
		//cpu当前使用率
		cpuInfo.put("idle", NumberUtil.round((1.0 - (idle * 1.0 / totalCpu)) * 100,2));

		return cpuInfo;
	}

	/**
	 * 系统jvm信息
	 */
	public static JSONObject getJvmInfo() {
		JSONObject cpuInfo = new JSONObject();
		Properties props = System.getProperties();
		Runtime runtime = Runtime.getRuntime();
		long jvmTotalMemoryByte = runtime.totalMemory();
		long freeMemoryByte = runtime.freeMemory();
		//jvm总内存
		cpuInfo.put("total", formatByte(runtime.totalMemory()));
		//空闲空间
		cpuInfo.put("free", formatByte(runtime.freeMemory()));
		//jvm最大可申请
		cpuInfo.put("max", formatByte(runtime.maxMemory()));
		//vm已使用内存
		cpuInfo.put("user", formatByte(jvmTotalMemoryByte - freeMemoryByte));
		//jvm内存使用率
		cpuInfo.put("usageRate", NumberUtil.round(((jvmTotalMemoryByte - freeMemoryByte) * 1.0 / jvmTotalMemoryByte) * 100,2));
		//jdk版本
		cpuInfo.put("jdkVersion", props.getProperty("java.version"));
		//jdk路径
		cpuInfo.put("jdkHome", props.getProperty("java.home"));
		return cpuInfo;
	}

	/**
	 * 系统内存信息
	 */
	public static JSONObject getMemInfo() {
		JSONObject memInfo = new JSONObject();
		GlobalMemory memory = systemInfo.getHardware().getMemory();
		//总内存
		long totalByte = memory.getTotal();
		//剩余
		long availableByte = memory.getAvailable();
		//总内存
		memInfo.put("total", formatByte(totalByte));
		//使用
		memInfo.put("used", formatByte(totalByte - availableByte));
		//剩余内存
		memInfo.put("free", formatByte(availableByte));
		//使用率
		memInfo.put("usageRate", NumberUtil.round(((totalByte - availableByte) * 1.0 / totalByte) * 100,2));
		return memInfo;
	}

	/**
	 * 系统盘符信息
	 */
	public static JSONObject getSysFileInfo() {
		JSONObject sysFileInfo = new JSONObject();
		JSONArray sysFiles = new JSONArray();
		FileSystem fileSystem = operatingSystem.getFileSystem();
		OSFileStore[] fsArray = fileSystem.getFileStores();
		long totalDist = 0;
		for (OSFileStore fs : fsArray) {
			JSONObject distInfo = new JSONObject();
			//盘符路径
			distInfo.put("dirName", fs.getMount());
			//盘符类型
			distInfo.put("sysTypeName", fs.getType());
			//文件类型
			distInfo.put("typeName", fs.getName());
			//总大小
			totalDist += fs.getTotalSpace();
			distInfo.put("total", formatByte(fs.getTotalSpace()));
			//剩余大小
			distInfo.put("free", formatByte(fs.getUsableSpace()));
			//已经使用量
			distInfo.put("used", formatByte(fs.getTotalSpace() - fs.getUsableSpace()));
			if (fs.getTotalSpace() == 0) {
				//资源的使用率
				distInfo.put("usage", 0);
			} else {
				distInfo.put("usage", NumberUtil.round((fs.getTotalSpace() - fs.getUsableSpace()) * 1.0 / fs.getTotalSpace() * 100,2));
			}
			sysFiles.add(distInfo);
		}
		sysFileInfo.put("diskTotal", formatByte(totalDist));
		sysFileInfo.put("diskList", sysFiles);
		return sysFileInfo;
	}

	/**
	 * 系统信息
	 */
	public static JSONObject getSysInfo() throws UnknownHostException {
		JSONObject sysInfo = new JSONObject();
		Properties props = System.getProperties();
		//操作系统名
		sysInfo.put("osName", props.getProperty("os.name"));
		//系统架构
		sysInfo.put("osArch", props.getProperty("os.arch"));
		//服务器名称
		sysInfo.put("computerName", InetAddress.getLocalHost().getHostName());
		//服务器Ip
		sysInfo.put("computerIp", InetAddress.getLocalHost().getHostAddress());
		//项目路径
		sysInfo.put("userDir", props.getProperty("user.dir"));
		// jvm 运行时间
		long startTime = ManagementFactory.getRuntimeMXBean().getStartTime();
		// 计算项目持续运行时间
		String runningTime = DateUtil.formatBetween(new Date(startTime), new Date(), BetweenFormater.Level.HOUR);
		sysInfo.put("runningTime", runningTime);

		return sysInfo;
	}

	/**
	 * 单位转换
	 */
	private static String formatByte(long byteNumber) {
		//换算单位
		double FORMAT = 1024.0;
		double kbNumber = byteNumber / FORMAT;
		if (kbNumber < FORMAT) {
			return new DecimalFormat("#.##KB").format(kbNumber);
		}
		double mbNumber = kbNumber / FORMAT;
		if (mbNumber < FORMAT) {
			return new DecimalFormat("#.##MB").format(mbNumber);
		}
		double gbNumber = mbNumber / FORMAT;
		if (gbNumber < FORMAT) {
			return new DecimalFormat("#.##GB").format(gbNumber);
		}
		double tbNumber = gbNumber / FORMAT;
		return new DecimalFormat("#.##TB").format(tbNumber);
	}
}
