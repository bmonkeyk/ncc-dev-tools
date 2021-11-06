package com.yingling.script.studio.ui.preference.prop;

public class IpAndPort {
    private String address;

    private Integer port;

    public IpAndPort() {
    }

    public IpAndPort(Object address, Object port) {
        if (address != null)
            this.address = address.toString();
        if (port != null)
            if (port instanceof Integer) {
                this.port = (Integer) port;
            } else {
                this.port = Integer.valueOf(port.toString());
            }
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
