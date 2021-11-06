package com.yingling.script.pub.db.script;

import java.io.File;

public interface ISqlFile {
    File getFile();

    ScriptType getScriptType();
}
