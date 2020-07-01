package com.yonyou.common.tablestruct.service;

import com.pub.exception.BusinessException;
import com.yonyou.common.tablestruct.service.impl.URLZipFileQueryService;

public class CommonTableStructQueryServiceFactory {
    public static ICommonTableStructQueryService getService() throws BusinessException {
        return URLZipFileQueryService.getSingleton();
    }
}
