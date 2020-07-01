package nc.uap.studio.pub.db.script.init.impl;

import nc.uap.studio.pub.db.script.ISqlFile;
import nc.uap.studio.pub.db.script.ScriptType;

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
