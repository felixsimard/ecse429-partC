package APITests.Threads;

import APITests.AppTests;

import java.io.FileWriter;
import java.io.IOException;

public class MemoryInstancesThread implements Runnable{

    private static final int TOTAL_INSTANCES = AppTests.TOTAL_INSTANCES;
    private static String name;
    private static String action;

    public MemoryInstancesThread() {

    }

    private static long getMemoryUse() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public MemoryInstancesThread(String name, String action){
        this.name = name;
        this.action = action;
    }

    @Override
    public void run() {
        boolean todo_cpu_flag = true;
        try {
            // setup csv file
            FileWriter csv;

            csv = new FileWriter("csv/" + name + "-MEM_USAGE-" + action + "-instances.csv");

            csv.append("Total instances");
            csv.append(',');
            csv.append("Memory Usage");
            csv.append(',');
            csv.append("\n");

            System.out.println("MEMORY USAGE CSV CREATED");

            //double initial_time = Calendar.getInstance().getTimeInMillis();
            int tmp = TOTAL_INSTANCES;
            while (todo_cpu_flag) {
                if (tmp != TOTAL_INSTANCES)
                {
                    tmp = TOTAL_INSTANCES;

                    long mem_used = getMemoryUse();
                    csv.append(String.valueOf(TOTAL_INSTANCES));
                    csv.append(',');
                    csv.append(String.valueOf(mem_used));
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
