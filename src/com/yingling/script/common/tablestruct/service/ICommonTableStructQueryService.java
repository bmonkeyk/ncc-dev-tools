package com.yingling.script.common.tablestruct.service;

import com.yingling.base.BusinessException;
import com.yingling.script.common.tablestruct.model.MainTableCfg;

import java.util.List;
import java.util.Properties;

public interface ICommonTableStructQueryService {
    List<MainTableCfg> getCommonMainTableCfgs() throws BusinessException;

    Properties getCommonMapping();
}
