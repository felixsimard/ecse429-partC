package APITests.Threads;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class MemorySampleTimeThread implements Runnable{

    private static String name;
    private static String action;

    public MemorySampleTimeThread() {

    }

    private static String getSampleTime() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" +
                cal.get(Calendar.SECOND) + ":" + cal.get(Calendar.MILLISECOND);
    }

    @Override
    public void run() {
        boolean todo_cpu_flag = true;
        try {
            // setup csv file
            FileWriter csv;

            csv = new FileWriter("csv/" + name + "-MEM_USAGE-" + action + ".csv");

            csv.append("Sample Time");
            csv.append(',');
            csv.append("Memory Usage");
            csv.append(',');
            csv.append("\n");

            System.out.println("MEMORY USAGE CSV CREATED");

            //double initial_time = Calendar.getInstance().getTimeInMillis();
            while (todo_cpu_flag) {
                String sample_time = getSampleTime();
                long mem_used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                csv.append(sample_time);
                csv.append(',');
                csv.append(String.valueOf(mem_used));
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
