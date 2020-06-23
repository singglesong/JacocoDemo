package com.example.JacocoExample;

import org.jacoco.core.data.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

/**
 * This example reads execution data files given as program arguments and dumps
 * their content.
 * 这个示例读取作为程序参数和转储提供的执行数据文件内容。
 *
 */
public final  class ExecDump {
    private final PrintStream out;

    /**
     * Creates a new example instance printing to the given stream.
     * 创建打印到给定流的新示例实例
     * @param out
     *            stream for outputs
     */
    public ExecDump(final PrintStream out) {
        this.out = out;
    }

    /**
     * Run this example with the given parameters.
     * 使用给定的参数运行此示例
     * @param args
     *            command line parameters
     * @throws IOException
     *             in case of error reading a input file
     */
    public void execute(final String[] args) throws IOException {
        for (final String file : args) {
            dump(file);
        }
    }

    private void dump(final String file) throws IOException {
        out.printf("exec file: %s%n", file);
        out.println("CLASS ID         HITS/PROBES   CLASS NAME");

        final FileInputStream in = new FileInputStream(file);
        final ExecutionDataReader reader = new ExecutionDataReader(in);
        reader.setSessionInfoVisitor(new ISessionInfoVisitor() {
            @Override
            public void visitSessionInfo(final SessionInfo info) {
                out.printf("Session \"%s\": %s - %s%n", info.getId(),
                        new Date(info.getStartTimeStamp()),
                        new Date(info.getDumpTimeStamp()));
            }
        });
        reader.setExecutionDataVisitor(new IExecutionDataVisitor() {
            @Override
            public void visitClassExecution(final ExecutionData data) {
                out.printf("%016x  %3d of %3d   %s%n",
                        Long.valueOf(data.getId()),
                        Integer.valueOf(getHitCount(data.getProbes())),
                        Integer.valueOf(data.getProbes().length),
                        data.getName());
            }
        });
        reader.read();
        in.close();
        out.println();
    }

    private int getHitCount(final boolean[] data) {
        int count = 0;
        for (final boolean hit : data) {
            if (hit) {
                count++;
            }
        }
        return count;
    }

    /**
     * Entry point to run this examples as a Java application.
     *
     * @param args
     *            list of program arguments
     * @throws IOException
     *             in case of errors executing the example
     */
    public static void main(final String[] args) throws IOException {
        String str[] = new String[]
        {"E:/IdeaProjects/JacocoDemo/jacoco-client.exec"
        };
        new ExecDump(System.out).execute(str);
    }
}
