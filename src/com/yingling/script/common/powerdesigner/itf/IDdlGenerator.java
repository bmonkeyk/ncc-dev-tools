package com.yingling.script.common.powerdesigner.itf;

import com.yingling.script.common.powerdesigner.core.Pdm;
import com.yingling.script.pub.db.model.IFkConstraint;
import com.yingling.script.pub.db.model.IIndex;
import com.yingling.script.pub.db.model.ITable;

import java.io.Writer;
import java.util.List;

public interface IDdlGenerator {
    void geneCreateTableDdl(List<ITable> paramList, Writer paramWriter);

    void geneCreateIndexDdl(List<IIndex> paramList, Writer paramWriter);

    void geneAddConstraintDdl(List<IFkConstraint> paramList, Writer paramWriter);

    void geneCreateViewDdl(List<Pdm.ViewInfo> paramList, Writer paramWriter);
}
