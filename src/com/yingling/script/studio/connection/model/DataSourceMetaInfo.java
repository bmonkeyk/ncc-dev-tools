package com.yingling.script.studio.connection.model;

public class DataSourceMetaInfo {
    private String name;

    private String url;

    private String driver;

    private String user;

    private String pwd;

    private String oidMark;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return this.driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getOidMark() {
        return this.oidMark;
    }

    public void setOidMark(String oidMark) {
        this.oidMark = oidMark;
    }
}
