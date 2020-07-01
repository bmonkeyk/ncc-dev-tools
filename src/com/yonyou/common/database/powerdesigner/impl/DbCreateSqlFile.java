package com.yonyou.common.database.powerdesigner.impl;

import com.yonyou.common.database.powerdesigner.core.ScriptType;
import com.yonyou.common.database.powerdesigner.itf.ISqlFile;

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
