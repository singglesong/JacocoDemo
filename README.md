# JacocoDemo

#### 被测试项目
- 1.准备jacocoagent.jar 包，通过[jar包下载地址](https://www.eclemma.org/jacoco/)，下载后解压即可。这个是启动应用时主要用来插桩的jar包
- 2.编辑javaagent参数   -javaagent:$jacocoJarPath=includes=$package,output=tcpserver,port=$port,address=$ip
- - $jacocoJarPath : jacocoagent.jar的存放路径,例如：/home/admin/jacoco/jacocoagent.jar
- - $package:启动时需要进行字节码插桩的包过滤，\*代表所有的class文件加载都需要进行插桩。假如代码都有相同的包前缀:com.example,你可以写成: includes=com.example.\*
- - output=tcpserver:代表以tcpserver方式启动应用并进行插桩,一般不需要改动
- - $port：jacoco开启的tcpserver的端口，请注意这个端口不能被占用
- - $ip:这是对外开发的tcpserver的访问地址。配置为127.0.0.1的时候，dump数据只能在这台服务器上进行dump，就不能通过远程方式dump数据
- 3.war启动方式，在tomcat的catalina.sh文件中添加以下内容
> JAVA_OPTS="$JAVA_OPTS -javaagent:$jacocoJarPath=includes=*,output=tcpserver,port=2014,address=192.168.110.1"

- 4.java -jar 启动
> java -javaagent: $jacocoJarPath=includes=*,output=tcpserver,port=2014,address=192.168.110.1 -jar  xxxxxxxxxx.jar
>java -javaagent:F:\Download\demo\demo\src\main\resources\jacocoagent.jar=includes=com.example.demo.controller.*,output=tcpserver,port=2014,address=192.168.0.203 -jar  target/demo-0.0.1-SNAPSHOT.jar

- 5.maven 启动
> mvn clean install
  export MAVEN_OPTS="-javaagent:$jacocoJarPath=includes=*,output=tcpserver,port=2014,address=192.168.110.1"
  mvn tomcat7:run -Dport=xxx
  export MAVEN_OPTS=""
- - mvn spring-boot 启动
> mvn clean install
  export MAVEN_OPTS="-javaagent:$jacocoJarPath=includes=*,output=tcpserver,port=2014,address=192.168.110.1"
  mvn spring-boot:run -Dport=xxx
  export MAVEN_OPTS=""

- jacococli.jar 收集覆盖率
> java -jar jacococli.jar dump --address localhost --port 6300 --destfile ./jacoco-demo.exec

- jacococli.jar 转报告 
> java -jar jacococli.jar report ./jacoco-demo.exec --classfiles /Users/oukotoshuu/IdeaProjects/demo/target/classes/com  --sourcefiles /Users/oukotoshuu/IdeaProjects/demo/src/main/java --html report --xml report.xml
- - --classfiles 必须项,是编译后target 文件夹下的classes里面的com