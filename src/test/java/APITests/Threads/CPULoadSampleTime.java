package APITests.Threads;

import com.sun.management.OperatingSystemMXBean;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Calendar;

public class CPULoadSampleTime implements Runnable{

    static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean.class);

    public CPULoadSampleTime() {

    }

    private static String getSampleTime() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" +
                cal.get(Calendar.SECOND) + ":" + cal.get(Calendar.MILLISECOND);
    }

    private static String type;
    private static String action;



    @Override
    public void run() {
        boolean todo_cpu_flag = true;
        try {
            // setup csv file
            FileWriter csv;

            csv = new FileWriter("csv/" + type + "-CPU_LOAD-" + action + ".csv");

            csv.append("Sample Time");
            csv.append(',');
            csv.append("Cpu Load");
            csv.append(',');
            csv.append("\n");

            System.out.println("CPU LOAD CSV CREATED");

            //double initial_time = Calendar.getInstance().getTimeInMillis();
            while (todo_cpu_flag) {
                String sample_time = getSampleTime();
                double cpu_load = osBean.getProcessCpuLoad();
                csv.append(sample_time);
                csv.append(',');
                csv.append(String.valueOf(cpu_load));
                csv.append(',');
                csv.append("\n");
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
