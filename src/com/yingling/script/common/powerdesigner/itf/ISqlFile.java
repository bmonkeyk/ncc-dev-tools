package com.yingling.script.common.powerdesigner.itf;

import com.yingling.script.common.powerdesigner.core.ScriptType;

import java.io.File;

public interface ISqlFile {
    File getFile();

    ScriptType getScriptType();
}
