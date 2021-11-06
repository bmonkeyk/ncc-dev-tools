package com.yingling.script.studio.ui.preference.prop;

public class ClusterInfo {
    private String name = "name";

    private String protocol = "http";

    private SingleServerInfo mgr;

    private SingleServerInfo[] peer;

    public ClusterInfo() {
    }

    public ClusterInfo(String name, String protocol) {
        this.name = name;
        this.protocol = protocol;
    }

    public SingleServerInfo getMgr() {
        return this.mgr;
    }

    public void setMgr(SingleServerInfo mgr) {
        this.mgr = mgr;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SingleServerInfo[] getPeer() {
        return this.peer;
    }

    public void setPeer(SingleServerInfo[] peer) {
        this.peer = peer;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
