package com.example.JacocoExample;

import org.jacoco.core.data.*;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This example starts a socket server to collect coverage from agents that run
 * in output mode <code>tcpclient</code>. The collected data is dumped to a
 * local file.
 *
 * 此示例启动一个套接字服务器，从运行的代理收集覆盖率在输出模式tcpclient收集到的数据被转储到本地文件。
 */
public class ExecutionDataServer {
    private static final String DESTFILE = "jacoco-server.exec";

    private static final String ADDRESS = "192.168.0.203";

    private static final int PORT = 2014;

    /**
     * Start the server as a standalone program.
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final ExecutionDataWriter fileWriter = new ExecutionDataWriter(
                new FileOutputStream(DESTFILE));
        final ServerSocket server = new ServerSocket(PORT, 0,
                InetAddress.getByName(ADDRESS));
        while (true) {
            final Handler handler = new Handler(server.accept(), fileWriter);
            new Thread(handler).start();
        }
    }

    private static class Handler
            implements Runnable, ISessionInfoVisitor, IExecutionDataVisitor {

        private final Socket socket;

        private final RemoteControlReader reader;

        private final ExecutionDataWriter fileWriter;

        Handler(final Socket socket, final ExecutionDataWriter fileWriter)
                throws IOException {
            this.socket = socket;
            this.fileWriter = fileWriter;

            // Just send a valid header:
            new RemoteControlWriter(socket.getOutputStream());

            reader = new RemoteControlReader(socket.getInputStream());
            reader.setSessionInfoVisitor(this);
            reader.setExecutionDataVisitor(this);
        }

        @Override
        public void run() {
            try {
                while (reader.read()) {
                }
                socket.close();
                synchronized (fileWriter) {
                    fileWriter.flush();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void visitSessionInfo(final SessionInfo info) {
            System.out.printf("Retrieving execution Data for session: %s%n",
                    info.getId());
            synchronized (fileWriter) {
                fileWriter.visitSessionInfo(info);
            }
        }

        @Override
        public void visitClassExecution(final ExecutionData data) {
            synchronized (fileWriter) {
                fileWriter.visitClassExecution(data);
            }
        }
    }

    private ExecutionDataServer() {
    }
}
