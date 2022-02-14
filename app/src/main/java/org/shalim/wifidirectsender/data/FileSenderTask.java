package org.shalim.wifidirectsender.data;

import java.net.InetAddress;
import lombok.Value;

public class FileSenderTask implements ITask<FileSenderTask.Inputs, FileSenderTask.Outputs> {
    private final String filePath;

    public FileSenderTask(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Outputs execute(Inputs inputs) {
        return null;
    }

    @Value
    public static class Inputs implements ITask.Inputs {
        InetAddress serverIp;
    }

    @Value
    public static class Outputs implements ITask.Outputs {
    }
}
