package nc.uap.plugin.studio.ui.preference.dbdriver;

public class DriverInfo {
    private String driverType;

    private String driverLib;

    private String driverClass;

    private String driverUrl;

    private Integer maxCon;

    private Integer minCon;

    public String getDriverClass() {
        return this.driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDriverLib() {
        return this.driverLib;
    }

    public void setDriverLib(String driverLib) {
        this.driverLib = driverLib;
    }

    public String getDriverType() {
        return this.driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    public String getDriverUrl() {
        return this.driverUrl;
    }

    public void setDriverUrl(String driverUrl) {
        this.driverUrl = driverUrl;
    }

    public Integer getMaxCon() {
        return this.maxCon;
    }

    public void setMaxCon(Integer maxCon) {
        this.maxCon = maxCon;
    }

    public Integer getMinCon() {
        return this.minCon;
    }

    public void setMinCon(Integer minCon) {
        this.minCon = minCon;
    }

    public String toString() {
        return getDriverType();
    }
}
