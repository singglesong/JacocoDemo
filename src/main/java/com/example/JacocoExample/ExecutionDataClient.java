package com.example.JacocoExample;


import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This example connects to a coverage agent that run in output mode
 * <code>tcpserver</code> and requests execution data. The collected data is
 * dumped to a local file.
 *
 * 此示例连接到以输出模式tcpserver运行的覆盖率代理，请求执行数据。收集的数据是转储到本地文件。
 */
public class ExecutionDataClient {
    private static final String DESTFILE = "jacoco-tcpserver.exec";

    private static final String ADDRESS = "192.168.0.203";

    private static final int PORT = 2014;

    /**
     * Starts the execution data request.
     *
     * 启动执行数据请求
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final FileOutputStream localFile = new FileOutputStream(DESTFILE);
        final ExecutionDataWriter localWriter = new ExecutionDataWriter(
                localFile);

        // Open a socket to the coverage agent:
        final Socket socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
        final RemoteControlWriter writer = new RemoteControlWriter(
                socket.getOutputStream());
        final RemoteControlReader reader = new RemoteControlReader(
                socket.getInputStream());
        reader.setSessionInfoVisitor(localWriter);
        reader.setExecutionDataVisitor(localWriter);

        // Send a dump command and read the response:
        writer.visitDumpCommand(true, false);
        if (!reader.read()) {
            throw new IOException("Socket closed unexpectedly.");
        }

        socket.close();
        localFile.close();
    }

    private ExecutionDataClient() {
    }
}
