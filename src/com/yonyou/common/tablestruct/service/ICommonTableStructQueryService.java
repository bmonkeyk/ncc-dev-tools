package com.yonyou.common.tablestruct.service;

import com.pub.exception.BusinessException;
import com.yonyou.common.tablestruct.model.MainTableCfg;

import java.util.List;
import java.util.Properties;

public interface ICommonTableStructQueryService {
    List<MainTableCfg> getCommonMainTableCfgs() throws BusinessException;

    Properties getCommonMapping();
}
