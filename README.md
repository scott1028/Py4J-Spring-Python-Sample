Run
===

    mvn clean package -Dmaven.test.skip=true
    mvn exec:java -Dexec.mainClass="com.taisys.server.GatewayApp"
    python main.py

