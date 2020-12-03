package APITests.Threads;

import APITests.AppTests;
import com.sun.management.OperatingSystemMXBean;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class CPULoadInstanceThread implements Runnable{

    private static final int TOTAL_INSTANCES = AppTests.TOTAL_INSTANCES;
    private static String type;
    private static String action;

    static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
            OperatingSystemMXBean.class);


    public CPULoadInstanceThread() {

    }

    @Override
    public void run() {
        boolean todo_cpu_flag = true;
        try {
            // setup csv file
            FileWriter csv;

            csv = new FileWriter("csv/" + type + "-CPU_LOAD-" + action + "-instances.csv");

            csv.append("Sample Time");
            csv.append(',');
            csv.append("Cpu Load");
            csv.append(',');
            csv.append("\n");

            System.out.println("CPU LOAD CSV CREATED");

            //double initial_time = Calendar.getInstance().getTimeInMillis();
            int tmp = TOTAL_INSTANCES;
            while (todo_cpu_flag) {
                if (tmp != TOTAL_INSTANCES)
                {
                    tmp = TOTAL_INSTANCES;
                    double cpu_load = osBean.getProcessCpuLoad();
                    if (cpu_load == 0.0) {
                        tmp--;
                        continue;
                    }
                    csv.append(String.valueOf(TOTAL_INSTANCES));
                    csv.append(',');
                    csv.append(String.valueOf(cpu_load));
                    csv.append(',');
                    csv.append("\n");
                }
                try {
                    Thread.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            csv.flush();
            csv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
