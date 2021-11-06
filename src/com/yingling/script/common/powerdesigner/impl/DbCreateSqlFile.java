package com.yingling.script.common.powerdesigner.impl;

import com.yingling.script.common.powerdesigner.core.ScriptType;
import com.yingling.script.common.powerdesigner.itf.ISqlFile;

import java.io.File;

public class DbCreateSqlFile implements ISqlFile {
    private File file;

    public DbCreateSqlFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public ScriptType getScriptType() {
        return ScriptType.CREATE;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
