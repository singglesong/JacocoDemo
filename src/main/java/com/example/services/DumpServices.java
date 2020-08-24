package com.example.services;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.RemoteControlReader;
import org.jacoco.core.runtime.RemoteControlWriter;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

@Service
public class DumpServices {


    /**
     * 连接到以输出模式tcpserver运行的覆盖率代理，请求执行数据。收集的数据是转储到本地文件。
     * @param Destinfile  存储的文件名
     * @param ip          服务的ip地址
     * @param port        tcpserver 开放的port端口
     * @return
     */
    public boolean dumpFromTcpserver(String Destinfile ,String ip,int port) {
        String DESTFILE = Destinfile;
        String ADDRESS = ip;
        int PORT = port;

        final FileOutputStream localFile ;
        final ExecutionDataWriter localWriter ;
        final Socket socket;
        try {
            localFile = new FileOutputStream(DESTFILE);
            localWriter = new ExecutionDataWriter(localFile);
            // Open a socket to the coverage agent:
            socket = new Socket(InetAddress.getByName(ADDRESS), PORT);
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
        }catch (IOException e){
            return false;
        }
        return true;
    }

}
