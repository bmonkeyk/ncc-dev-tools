package com.yingling.script.common.tablestruct.service;

import com.yingling.base.BusinessException;
import com.yingling.script.common.tablestruct.service.impl.URLZipFileQueryService;

public class CommonTableStructQueryServiceFactory {
    public static ICommonTableStructQueryService getService() throws BusinessException {
        return URLZipFileQueryService.getSingleton();
    }
}
