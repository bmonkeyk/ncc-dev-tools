package com.yingling.script.pub.db.script.init.impl;

import com.yingling.script.pub.db.script.ISqlFile;
import com.yingling.script.pub.db.script.ScriptType;

import java.io.File;

public class DbRecordSqlFile implements ISqlFile {
    private File file;

    public DbRecordSqlFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public ScriptType getScriptType() {
        return ScriptType.RECORD;
    }
}
