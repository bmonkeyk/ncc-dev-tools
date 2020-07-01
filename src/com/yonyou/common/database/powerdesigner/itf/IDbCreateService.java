package com.yonyou.common.database.powerdesigner.itf;

import com.intellij.openapi.vfs.VirtualFile;
import com.yonyou.common.database.powerdesigner.exception.PDMParseRuntimeException;

public interface IDbCreateService {
    void validatePdm(VirtualFile paramFile, boolean paramBoolean) throws PDMParseRuntimeException;

    void geneSqlFile(VirtualFile paramFile1, boolean paramBoolean, VirtualFile paramFile2) throws PDMParseRuntimeException;

    void geneSqlFile(VirtualFile paramFile1, boolean paramBoolean, VirtualFile paramFile2, DatabaseType paramDatabaseType) throws PDMParseRuntimeException;

    void geneDataDictionary(VirtualFile paramFile1, boolean paramBoolean, VirtualFile paramFile2) throws PDMParseRuntimeException;

    public enum DatabaseType {
        ORACLE, SQLSERVER, DB2;
    }
}
