package nc.uap.plugin.studio.ui.preference.prop;

public class SingleServerInfo {
    private String javaHome = System.getProperty("java.home", "D:/j2sdk1.4.2_10");

    private String name = "server";

    private String jvmArgs = "-Xms768m -Xmx768m";

    private int servicePort = 8005;

    private IpAndPort[] ajp;

    private IpAndPort[] http;

    private IpAndPort[] https;

    public static SingleServerInfo getNullServerInfo() {
        SingleServerInfo nullinfo = new SingleServerInfo();
        nullinfo.setName("");
        nullinfo.setJavaHome("");
        nullinfo.setJvmArgs("");
        nullinfo.setServicePort(0);
        return nullinfo;
    }

    public static SingleServerInfo getDefualtMasterServerInfo() {
        SingleServerInfo nullinfo = new SingleServerInfo();
        nullinfo.setName("Master");
        nullinfo.setJavaHome("");
        nullinfo.setJvmArgs("");
        nullinfo.setServicePort(80);
        return nullinfo;
    }

    public IpAndPort[] getAjp() {
        return this.ajp;
    }

    public void setAjp(IpAndPort[] ajp) {
        this.ajp = ajp;
    }

    public IpAndPort[] getHttp() {
        return this.http;
    }

    public void setHttp(IpAndPort[] http) {
        this.http = http;
    }

    public IpAndPort[] getHttps() {
        return this.https;
    }

    public void setHttps(IpAndPort[] https) {
        this.https = https;
    }

    public String getJavaHome() {
        return this.javaHome;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public String getJvmArgs() {
        return this.jvmArgs;
    }

    public void setJvmArgs(String jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getServicePort() {
        return this.servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public String toString() {
        return getName();
    }
}
