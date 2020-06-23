package com.example.JacocoExample;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.IClassCoverage;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;
import org.jacoco.core.runtime.RuntimeData;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Example usage of the JaCoCo core API. In this tutorial a single target class
 * will be instrumented and executed. Finally the coverage information will be
 * dumped.
 * 工具执行和分析单个目标类。最后，行覆盖信息被打印到控制台。
 */
public final class CoreTutorial {
    /**
     * 查看代码覆盖率的测试目标.
     */
    public static class TestTarget implements Runnable {

        @Override
        public void run() {
            isPrime(7);
        }

        private boolean isPrime(final int n) {
            for (int i = 2; i * i <= n; i++) {
                if ((n ^ i) == 0) {
                    return false;
                }
            }
            return true;
        }
    }


    /**
     * 从内存数据中装入类的类装入器
     */
    public static class MemoryClassLoader extends ClassLoader {

        private final Map<String, byte[]> definitions = new HashMap<>();

        /**
         * 添加类的内存表示.
         *
         * @param name
         *            name of the class
         * @param bytes
         *            class definition
         */
        public void addDefinition(final String name, final byte[] bytes) {
            definitions.put(name, bytes);
        }

        @Override
        protected Class<?> loadClass(final String name, final boolean resolve)
                throws ClassNotFoundException {
            final byte[] bytes = definitions.get(name);
            if (bytes != null) {
                return defineClass(name, bytes, 0, bytes.length);
            }
            return super.loadClass(name, resolve);
        }
    }

    private final PrintStream out;

    /**
     * Creates a new example instance printing to the given stream.
     *
     * @param out
     *            stream for outputs
     */
    public CoreTutorial(final PrintStream out) {
        this.out = out;
    }

    /**
     * Run this example.
     *
     * @throws Exception
     *             in case of errors
     */
    public void execute() throws Exception {
        final String targetName = TestTarget.class.getName();

        // 对于运行时的插桩，需要使用IRuntime 实例来收集执行数据
        final IRuntime runtime = new LoggerRuntime();

        // 插住器(Instrumenter)新建一个包含用于记录执行数据的额外探针的测试目标
        final Instrumenter instr = new Instrumenter(runtime);
        InputStream original = getTargetClass(targetName);
        final byte[] instrumented = instr.instrument(original, targetName);
        original.close();

        //现在我们准备运行我们的插装类(instrumented)，需要首先启动运行时:
        final RuntimeData data = new RuntimeData();
        runtime.startup(data);

        // In this tutorial we use a special class loader to directly load the instrumented class definition from a byte[] instances.
        // 在本教程中，我们使用一个特殊的类装入器从byte[]实例直接装入插装的类定义。
        final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
        memoryClassLoader.addDefinition(targetName, instrumented);
        final Class<?> targetClass = memoryClassLoader.loadClass(targetName);

        // Here we execute our test target class through its Runnable interface:
        // 这里我们通过它的Runnable接口来执行我们的测试目标类:
        final Runnable targetInstance = (Runnable) targetClass.newInstance();
        targetInstance.run();

        // At the end of test execution we collect execution data and shutdown the runtime:
        // 在测试执行结束时，我们收集执行数据并关闭运行时
        final ExecutionDataStore executionData = new ExecutionDataStore();
        final SessionInfoStore sessionInfos = new SessionInfoStore();
        data.collect(executionData, sessionInfos, false);
        runtime.shutdown();

        // Together with the original class definition we can calculate coverage information:
        // 结合原始的类定义，我们可以计算覆盖率信息:
        final CoverageBuilder coverageBuilder = new CoverageBuilder();
        final Analyzer analyzer = new Analyzer(executionData, coverageBuilder);
        original = getTargetClass(targetName);
        analyzer.analyzeClass(original, targetName);
        original.close();

        // 转储一些度量和行覆盖率信息:
        for (final IClassCoverage cc : coverageBuilder.getClasses()) {
            out.printf("Coverage of class %s%n", cc.getName());

            // 获取指令级别 覆盖情况
            printCounter("instructions", cc.getInstructionCounter());
            // 代码分支级别 覆盖情况
            printCounter("branches", cc.getBranchCounter());
            // 行数级别 覆盖情况
            printCounter("lines", cc.getLineCounter());
            // 方法级别 覆盖情况
            printCounter("methods", cc.getMethodCounter());
            // 圈级别  覆盖情况
            printCounter("complexity", cc.getComplexityCounter());

            for (int i = cc.getFirstLine(); i <= cc.getLastLine(); i++) {
                out.printf("Line %s: %s%n", Integer.valueOf(i),
                        getColor(cc.getLine(i).getStatus()));
            }
        }
    }

    private InputStream getTargetClass(final String name) {
        final String resource = '/' + name.replace('.', '/') + ".class";
        return getClass().getResourceAsStream(resource);
    }

    private void printCounter(final String unit, final ICounter counter) {
        final Integer missed = Integer.valueOf(counter.getMissedCount());
        final Integer total = Integer.valueOf(counter.getTotalCount());
        out.printf("%s of %s %s missed%n", missed, total, unit);
    }

    private String getColor(final int status) {
        switch (status) {
            case ICounter.NOT_COVERED:
                return "red";
            case ICounter.PARTLY_COVERED:
                return "yellow";
            case ICounter.FULLY_COVERED:
                return "green";
        }
        return "";
    }

    /**
     * Entry point to run this examples as a Java application.
     *
     * @param args
     *            list of program arguments
     * @throws Exception
     *             in case of errors
     */
    public static void main(final String[] args) throws Exception {
        new CoreTutorial(System.out).execute();
    }
}
