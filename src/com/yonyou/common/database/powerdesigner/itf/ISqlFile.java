package com.yonyou.common.database.powerdesigner.itf;

import com.yonyou.common.database.powerdesigner.core.ScriptType;

import java.io.File;

public interface ISqlFile {
    File getFile();

    ScriptType getScriptType();
}
