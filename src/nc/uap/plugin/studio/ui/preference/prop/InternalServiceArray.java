package nc.uap.plugin.studio.ui.preference.prop;

import java.io.Serializable;

public class InternalServiceArray implements Serializable {
    private static final long serialVersionUID = 1774004595340815038L;

    private String name;

    private String serviceClassName;

    private int accessDemandRight;

    private boolean startService;

    private boolean keyService;

    private String serviceOptions;

    public InternalServiceArray() {
    }

    public InternalServiceArray(String name, String className, int accessDemandRight, boolean startService, boolean keyService, String serviceOptions) {
        this.name = name;
        this.serviceClassName = className;
        this.accessDemandRight = accessDemandRight;
        this.startService = startService;
        this.keyService = keyService;
        this.serviceOptions = serviceOptions;
    }

    public int getAccessDemandRight() {
        return this.accessDemandRight;
    }

    public void setAccessDemandRight(int accessDemandRight) {
        this.accessDemandRight = accessDemandRight;
    }

    public boolean isKeyService() {
        return this.keyService;
    }

    public void setKeyService(boolean keyService) {
        this.keyService = keyService;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServiceClassName() {
        return this.serviceClassName;
    }

    public void setServiceClassName(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }

    public String getServiceOptions() {
        return this.serviceOptions;
    }

    public void setServiceOptions(String serviceOptions) {
        this.serviceOptions = serviceOptions;
    }

    public boolean isStartService() {
        return this.startService;
    }

    public void setStartService(boolean startService) {
        this.startService = startService;
    }
}
