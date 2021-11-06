package com.yingling.base;


import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(name = "NccEnvSetting", storages = {@com.intellij.openapi.components.Storage(value = "$PROJECT_CONFIG_DIR$/nccEnvSetting.xml")})
public class NccEnvSettingService implements PersistentStateComponent<Element> {

    private String ncHomePath;
    private String tablesPath;
    private String ex_modules;
    private String must_modules;
    private String connected_data_source;
    private String nccloudJar;
    private String ncchrJAR;
    private String lastPatcherPath;

    private final String ATTR_NCHOME_PATH = "ncHomePath";
    private final String ATTR_TABLES_PATH = "tablesPath";
    private final String ATTR__EX_MODULES = "ex_modules";
    private final String ATTR_MUST_MODULES = "must_modules";
    private final String ATTR_CONNECTD_DATA_SOURCE = "connectd_data_source";
    private final String ATTR_NCCLOUDJAR = "nccloudJar";
    private final String ATTR_NCCHRJAR = "ncchrJar";
    private final String ATTR_LASTPATCHERPATH = "lastPatcherPath";


    public NccEnvSettingService() {
    }

    public static NccEnvSettingService getInstance() {
        Project pro = ProjectManager.getInstance().getProject();
        return getInstance(pro);
    }

    public static NccEnvSettingService getInstance(Project project) {
        if (project == null) {
            Messages.showMessageDialog("please open a project", "error", Messages.getErrorIcon());
            return null;
        } else {
            return ServiceManager.getService(project, NccEnvSettingService.class);
        }

    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("NccEnvSetting");
        element.setAttribute(ATTR_NCHOME_PATH, getNcHomePath());
        element.setAttribute(ATTR_TABLES_PATH, getTablesPath());
        element.setAttribute(ATTR__EX_MODULES, getEx_modules());
        element.setAttribute(ATTR_MUST_MODULES, getMust_modules());
        element.setAttribute(ATTR_CONNECTD_DATA_SOURCE, getConnected_data_source());
        element.setAttribute(ATTR_NCCLOUDJAR, getNccloudJar());
        element.setAttribute(ATTR_NCCHRJAR, getNcchrJAR());
        element.setAttribute(ATTR_LASTPATCHERPATH, getLastPatcherPath());
        return element;
    }


    @Override
    public void loadState(@NotNull Element element) {
        this.setNcHomePath(element.getAttributeValue(ATTR_NCHOME_PATH) == null ? "" : element.getAttributeValue(ATTR_NCHOME_PATH));
        this.setTablesPath(element.getAttributeValue(ATTR_TABLES_PATH) == null ? "" : element.getAttributeValue(ATTR_TABLES_PATH));
        this.setEx_modules(element.getAttributeValue(ATTR__EX_MODULES) == null ? "" : element.getAttributeValue(ATTR__EX_MODULES));
        this.setMust_modules(element.getAttributeValue(ATTR_MUST_MODULES) == null ? "" : element.getAttributeValue(ATTR_MUST_MODULES));
        this.setConnected_data_source(element.getAttributeValue(ATTR_CONNECTD_DATA_SOURCE) == null ? "" : element.getAttributeValue(ATTR_CONNECTD_DATA_SOURCE));
        this.setNccloudJar(element.getAttributeValue(ATTR_NCCLOUDJAR) == null ? "" : element.getAttributeValue(ATTR_NCCLOUDJAR));
        this.setNcchrJAR(element.getAttributeValue(ATTR_NCCHRJAR) == null ? "" : element.getAttributeValue(ATTR_NCCHRJAR));
        this.setLastPatcherPath(element.getAttributeValue(ATTR_LASTPATCHERPATH) == null ? "" : element.getAttributeValue(ATTR_LASTPATCHERPATH));
    }

    public String getNcHomePath() {
        return StringUtils.isBlank(ncHomePath) ? "" : ncHomePath;
    }

    public void setNcHomePath(String ncHomePath) {
        this.ncHomePath = ncHomePath;
    }

    public String getTablesPath() {
        return StringUtils.isBlank(tablesPath) ? "" : tablesPath;
    }

    public void setTablesPath(String tablesPath) {
        this.tablesPath = tablesPath;
    }

    public String getEx_modules() {
        return StringUtils.isBlank(ex_modules) ? "" : ex_modules;
    }

    public void setEx_modules(String ex_modules) {
        this.ex_modules = ex_modules;
    }

    public String getMust_modules() {
        return StringUtils.isBlank(must_modules) ? "" : must_modules;
    }

    public void setMust_modules(String must_modules) {
        this.must_modules = must_modules;
    }

    public void setConnected_data_source(String connected_data_source) {
        this.connected_data_source = connected_data_source;
    }

    public String getConnected_data_source() {
        return StringUtils.isBlank(connected_data_source) ? "" : connected_data_source;
    }

    public String getNccloudJar() {
        return StringUtils.isBlank(nccloudJar) ? "" : nccloudJar;
    }

    public void setNccloudJar(String nccloudJar) {
        this.nccloudJar = nccloudJar;
    }

    public String getNcchrJAR() {
        return StringUtils.isBlank(ncchrJAR) ? "" : ncchrJAR;
    }

    public void setNcchrJAR(String ncchrJAR) {
        this.ncchrJAR = ncchrJAR;
    }

    public String getLastPatcherPath() {
        return StringUtils.isBlank(lastPatcherPath) ? "" : lastPatcherPath;
    }

    public void setLastPatcherPath(String lastPatcherPath) {
        this.lastPatcherPath = lastPatcherPath;
    }

}
