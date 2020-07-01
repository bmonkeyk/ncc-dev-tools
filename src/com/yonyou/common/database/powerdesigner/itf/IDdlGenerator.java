package com.yonyou.common.database.powerdesigner.itf;

import com.yonyou.common.database.powerdesigner.core.Pdm;
import nc.uap.studio.pub.db.model.IFkConstraint;
import nc.uap.studio.pub.db.model.IIndex;
import nc.uap.studio.pub.db.model.ITable;

import java.io.Writer;
import java.util.List;

public interface IDdlGenerator {
    void geneCreateTableDdl(List<ITable> paramList, Writer paramWriter);

    void geneCreateIndexDdl(List<IIndex> paramList, Writer paramWriter);

    void geneAddConstraintDdl(List<IFkConstraint> paramList, Writer paramWriter);

    void geneCreateViewDdl(List<Pdm.ViewInfo> paramList, Writer paramWriter);
}
