package com.yingling.devconfig.action.button;

import com.intellij.openapi.ui.Messages;
import com.yingling.abs.AbstractButtonAction;
import com.yingling.abs.AbstractDialog;
import com.yingling.base.BusinessException;
import com.yingling.base.NccEnvSettingService;
import com.yingling.debug.util.CreatApplicationConfigurationUtil;
import com.yingling.devconfig.DevConfigDialog;
import com.yingling.devconfig.util.DataSourceUtil;
import com.yingling.devconfig.util.TableModelUtil;
import com.yingling.library.util.LibrariesUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.JTextField;
import java.awt.event.ActionEvent;

/**
 * 设置面板确认按钮
 */
public class OKAction extends AbstractButtonAction {
    public OKAction(AbstractDialog dialog) {
        super(dialog);
    }

    @Override
    public void doAction(ActionEvent event) throws BusinessException {
        boolean homeChanged = false;
        String homePath = getDialog().getComponent(JTextField.class, "homeText").getText();
        if (StringUtils.isBlank(homePath)) {
            return;
        }
        //如果homePath未发生变化，则不提示是否更新类路径
        if (StringUtils.isNotBlank(homePath) && !homePath.equals(NccEnvSettingService.getInstance().getNcHomePath())) {
            homeChanged = true;
        }
        setServiceConfig(homeChanged);
        setLibraries(homeChanged);
        saveApp(homeChanged);
        saveDataSource(homeChanged);
        saveModuleConfig(homeChanged);
        getDialog().dispose();
    }

    /**
     * 保存调试程序上的home
     *
     * @param homeChanged
     */
    private void saveApp(boolean homeChanged) {

    }

    /**
     * 保存启动module
     *
     * @param homeChanged
     */
    private void saveModuleConfig(boolean homeChanged) {
        TableModelUtil.saveModuleConfig(getDialog());
    }

    /**
     * 保存工程依赖
     *
     * @param homeChanged
     */
    private void setLibraries(boolean homeChanged) throws BusinessException {

        boolean setLibFlag = ((DevConfigDialog) getDialog()).isLibFlag();
        if (!homeChanged || setLibFlag) {
            return;
        }

        String homePath = NccEnvSettingService.getInstance().getNcHomePath();
        int opt = Messages.showYesNoDialog("是否更新类路径？"
                , "询问", Messages.getQuestionIcon());
        if (opt == Messages.OK) {
            try {
                LibrariesUtil.setLibraries(homePath);
            } catch (BusinessException e) {
                Messages.showErrorDialog(e.getMessage(), "出错了");
            }
        }

        //更新application上的home路径
        CreatApplicationConfigurationUtil.updateHome();

    }


    /**
     * 保存数据源
     */
    private void saveDataSource(boolean homeChanged) {
        DataSourceUtil.saveDesignDataSourceMeta((DevConfigDialog) getDialog());
    }


    /**
     * 保存home和tables路径
     */
    private void setServiceConfig(boolean homeChanged) {
        String homePath = getDialog().getComponent(JTextField.class, "homeText").getText();
        String tablesPath = getDialog().getComponent(JTextField.class, "tablesText").getText();

        NccEnvSettingService.getInstance().setNcHomePath(homePath);
        NccEnvSettingService.getInstance().setTablesPath(tablesPath);
    }
}
