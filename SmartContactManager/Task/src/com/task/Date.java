package com.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.PriorityQueue;

class LogEntry implements Comparable<LogEntry> {
    private String jobId;
    private LocalDateTime timestamp;

    public LogEntry(String jobId, LocalDateTime timestamp) {
        this.jobId = jobId;
        this.timestamp = timestamp;
    }

    public String getJobId() {
        return jobId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(LogEntry other) {
        return timestamp.compareTo(other.timestamp);
    }
}

public class Date {
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Create a priority queue to store the log entries
        PriorityQueue<LogEntry> logQueue = new PriorityQueue<>();

        // Add sample log entries
        logQueue.add(new LogEntry("J1", LocalDateTime.parse("2023-06-29 04:01:01", formatter)));
        logQueue.add(new LogEntry("J2", LocalDateTime.parse("2023-06-29 04:02:02", formatter)));
        logQueue.add(new LogEntry("J1", LocalDateTime.parse("2023-06-29 04:03:03", formatter)));
        logQueue.add(new LogEntry("J3", LocalDateTime.parse("2023-06-29 04:04:04", formatter)));
        logQueue.add(new LogEntry("J1", LocalDateTime.parse("2023-06-29 04:05:05", formatter)));

        // Define the target timestamp
        LocalDateTime targetTimestamp = LocalDateTime.parse("2023-06-29 04:04:04", formatter);

        // Find the first log of J1 after the target timestamp
        String firstLogJ1 = findFirstLogJ1AfterTimestamp(logQueue, targetTimestamp);

        // Print the result
        System.out.println("First log of J1 after " + targetTimestamp + ": " + firstLogJ1);
    }

    private static String findFirstLogJ1AfterTimestamp(PriorityQueue<LogEntry> logQueue, LocalDateTime targetTimestamp) {
        while (!logQueue.isEmpty()) {
            LogEntry logEntry = logQueue.poll();
            if (logEntry.getJobId().equals("J1") && logEntry.getTimestamp().isAfter(targetTimestamp)) {
                return logEntry.getJobId();
            }
        }
        return "None";
    }
}
