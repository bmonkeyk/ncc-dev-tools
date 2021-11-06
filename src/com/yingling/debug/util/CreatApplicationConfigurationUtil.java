package com.yingling.debug.util;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.application.ApplicationConfiguration;
import com.intellij.execution.application.ApplicationConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.yingling.base.BusinessException;
import com.yingling.base.NccEnvSettingService;
import com.yingling.base.ProjectManager;
import com.yingling.script.studio.ui.preference.xml.PropXml;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.intellij.execution.ShortenCommandLine.CLASSPATH_FILE;

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
        RunManagerImpl runManager = (RunManagerImpl) RunManager.getInstance(project);
        List<RunConfiguration> configurationsList = runManager.getAllConfigurationsList();

        //当前选择module
        Module selectModule = event.getData(LangDataKeys.MODULE);

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
            RunnerAndConfigurationSettings runnerAndConfigurationSettings = new RunnerAndConfigurationSettingsImpl(runManager, conf);
            runManager.addConfiguration(runnerAndConfigurationSettings);
        }
    }

    private static void setConfiguration(Module selectModule, ApplicationConfiguration conf, boolean serverFlag) throws BusinessException {

        //检查并设置nc home
        String homePath = NccEnvSettingService.getInstance().getNcHomePath();
        int port = 80;
        try {
            PropXml propXml = new PropXml();
            String filename = new File(homePath).getPath() + "/ierp/bin/prop.xml";
            File file = new File(filename);
            if (!file.exists()) {
                throw new BusinessException("");
            }
            port = propXml.loadPropInfo(filename).getDomain().getServer().getServicePort();
        } catch (Exception e) {
            throw new BusinessException("please check the file :prop.xml\n" + e.getMessage());
        }


        Map<String, String> envs = conf.getEnvs();
        if (serverFlag) {
            conf.setMainClassName(serverClass);
            String exModulesStr = NccEnvSettingService.getInstance().getEx_modules();
            envs.put("FIELD_EX_MODULES", exModulesStr);
            envs.put("FIELD_HOTWEBS", "nccloud");
            envs.put("FIELD_ENCODING", "UTF-8");
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
        conf.setShortenCommandLine(CLASSPATH_FILE);
    }

    /**
     * 更新application中的home路径
     *
     * @throws BusinessException
     */
    public static void updateHome() throws BusinessException {

        Project project = ProjectManager.getInstance().getProject();
        RunManagerImpl runManager = (RunManagerImpl) RunManager.getInstance(project);
        List<RunConfiguration> configurationsList = runManager.getAllConfigurationsList();
        if (null != configurationsList && !configurationsList.isEmpty()) {
            for (RunConfiguration configuration : configurationsList) {
                if (configuration instanceof ApplicationConfiguration) {
                    ApplicationConfiguration conf = (ApplicationConfiguration) configuration;
                    RunnerAndConfigurationSettings runnerAndConfigurationSettings = new RunnerAndConfigurationSettingsImpl(runManager, conf);
                    runManager.removeConfiguration(runnerAndConfigurationSettings);

                    ApplicationConfiguration newConf = (ApplicationConfiguration) conf.clone();
                    setConfiguration(newConf.getConfigurationModule().getModule(), newConf, serverClass.equals(conf.getMainClass()));
                    runnerAndConfigurationSettings = new RunnerAndConfigurationSettingsImpl(runManager, newConf);
                    runManager.addConfiguration(runnerAndConfigurationSettings);
                }
            }
        }
    }

}
