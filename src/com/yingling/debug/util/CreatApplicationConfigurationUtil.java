package com.yingling.debug.util;

import com.intellij.execution.RunManager;
import com.intellij.execution.ShortenCommandLine;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.pub.exception.BusinessException;
import com.yingling.extensions.service.NccEnvSettingService;
import nc.uap.plugin.studio.ui.preference.prop.PropInfo;
import nc.uap.plugin.studio.ui.preference.xml.PropXml;
import nc.uap.plugin.studio.ui.preference.xml.XMLToObject;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CreatApplicationConfigurationUtil {
    private static String serverClass = "ufmiddle.start.tomcat.StartDirectServer";
    private static String clientClass = "nc.starter.test.JStarter";

    /**
     * 设置启动application
     *
     * @param event
     * @param serverFlag
     */
    public static void createApplicationConfiguration(AnActionEvent event, boolean serverFlag) throws BusinessException {

        String configName = serverFlag ? " - server" : " - client";
        Project project = event.getProject();
        RunManagerImpl runManager = (RunManagerImpl) RunManager.getInstance(null == project ? ProjectManager.getInstance().getDefaultProject() : project);
        List<RunConfiguration> configurationsList = runManager.getConfigurationsList(ApplicationConfigurationType.getInstance());

        //当前选择module
        Module selectModule = event.getData(LangDataKeys.MODULE);
        String modulePath = selectModule.getModuleFile().getParent().getPath();
        if (!new File(modulePath + File.separator + "META-INF" + File.separator + "module.xml").exists()) {
            throw new BusinessException("please select ncc module !");
        }
        configName = selectModule.getName() + configName;

        //循环判断当前配置中有没有当前模块的启动配置
        boolean hasFlag = false;
        if (null != configurationsList && !configurationsList.isEmpty()) {
            for (RunConfiguration configuration : configurationsList) {
                if (configuration.getName().equals(configName)) {
                    hasFlag = true;
                    ApplicationConfiguration conf = (ApplicationConfiguration) configuration;
                    setConfiguration(selectModule, conf, serverFlag);
                    break;
                }
            }
        }
        //新增config
        if (!hasFlag) {
            ApplicationConfiguration conf = new ApplicationConfiguration(configName, project, ApplicationConfigurationType.getInstance());
            setConfiguration(selectModule, conf, serverFlag);
            RunnerAndConfigurationSettingsImpl runnerAndConfigurationSettings = new RunnerAndConfigurationSettingsImpl(runManager, conf);
            runManager.addConfiguration(runnerAndConfigurationSettings);
        }
    }

    private static void setConfiguration(Module selectModule, ApplicationConfiguration conf, boolean serverFlag) throws BusinessException {

        //检查并设置nc home
        String homePath = NccEnvSettingService.getInstance().getNcHomePath();
        int port = 80 ;
        String msg = "";
        try {
            PropXml propXml = new PropXml();
            String filename = homePath + "/ierp/bin/prop.xml";
            File file = new File(filename);
            if (!file.exists()) {
                throw new BusinessException("");
            }
            port = propXml.loadPropInfo(filename).getDomain().getServer().getServicePort();
        } catch (Exception e) {
            msg = "Please check the nchome\n";
        }
        if (StringUtils.isNotBlank(msg)) {
            throw new BusinessException(msg);
        }

        Map<String, String> envs = conf.getEnvs();
        if (serverFlag) {
            conf.setMainClassName(serverClass);
            String exModulesStr = NccEnvSettingService.getInstance().getEx_modules();
            envs.put("FIELD_EX_MODULES", exModulesStr);
            envs.put("FIELD_HOTWEBS","nccloud");
            envs.put("FIELD_ENCODING","UTF-8");
            conf.setVMParameters("-Dnc.exclude.modules=$FIELD_EX_MODULES$\n" +
                    "-Dnc.runMode=develop\n" +
                    "-Dnc.server.location=$FIELD_NC_HOME$\n" +
                    "-DEJBConfigDir=$FIELD_NC_HOME$/ejbXMLs\n" +
                    "-DExtServiceConfigDir=$FIELD_NC_HOME$/ejbXMLs\n" +
                    "-Duap.hotwebs=$FIELD_HOTWEBS$\n" +
                    "-Duap.disable.codescan=false\n" +
                    "-Djavax.xml.parsers.DocumentBuilderFactory=org.apache.xerces.jaxp.DocumentBuilderFactoryImpl\n" +
                    "-Xmx1024m\n" +
                    "-XX:MetaspaceSize=128m\n" +
                    "-XX:MaxMetaspaceSize=512m\n" +
                    "-Dorg.owasp.esapi.resources=$FIELD_NC_HOME$/ierp/bin/esapi\n" +
                    "-Dfile.encoding=$FIELD_ENCODING$");
        } else {

            conf.setMainClassName(clientClass);
            envs.put("FIELD_CLINET_IP", "127.0.0.1");
            envs.put("FIELD_CLINET_PORT", port + "");
            conf.setVMParameters("-Dnc.runMode=develop\n" +
                    " -Dnc.jstart.server=$FIELD_CLINET_IP$\n" +
                    " -Dnc.jstart.port=$FIELD_CLINET_PORT$\n " +
                    " -Xmx768m -XX:MaxPermSize=256m " +
                    " -Dnc.fi.autogenfile=N ");
        }
        envs.put("FIELD_NC_HOME", homePath);
        conf.setModule(selectModule);
        conf.setEnvs(envs);
        conf.setWorkingDirectory(homePath);
        conf.setShortenCommandLine(ShortenCommandLine.CLASSPATH_FILE);
    }

}
