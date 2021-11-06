package com.yingling.script.common.powerdesigner.impl;

import com.yingling.script.common.powerdesigner.exception.PDMParseRuntimeException;
import com.yingling.script.common.powerdesigner.itf.IDbCreateService;
import com.yingling.script.common.powerdesigner.core.Pdm;
import com.yingling.script.common.powerdesigner.itf.IDdlGenerator;
import com.yingling.script.pub.db.model.IFkConstraint;
import com.yingling.script.pub.db.model.IIndex;
import com.yingling.script.pub.db.model.ITable;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DdlGeneratorFactory {
    //    protected static Logger logger = LoggerFactory.getLogger(DdlGeneratorFactory.class.getName());
    private static final String CREATE_TABLE_TEMPL_NAME = "createTable.templ";

    private static final String CREATE_INDEX_TEMPL_NAME = "createIndex.templ";

    private static final String CREATE_VIEW_TEMPL_NAME = "createView.templ";

    private static final String CREATE_REFERENCE_TEMPL_NAME = "reference.templ";

    private static final String TEMPL_PATH_SQLSERVER = "/vm/sqlserver/";

    private static final String TEMPL_PATH_ORACEL = "/vm/oracle/";

    private static final String TEMPL_PATH_DB2 = "/vm/db2/";

    private static Map<IDbCreateService.DatabaseType, IDdlGenerator> ddlGeneratorMap = new HashMap();

    public static IDdlGenerator getInstance(IDbCreateService.DatabaseType dbType) {
        IDdlGenerator generator = (IDdlGenerator) ddlGeneratorMap.get(dbType);
        if (generator == null)
            synchronized (ddlGeneratorMap) {
                if ((generator = (IDdlGenerator) ddlGeneratorMap.get(dbType)) == null) {
                    DdlGeneratorVelocityImpl temp = new DdlGeneratorVelocityImpl();
                    try {
                        if (IDbCreateService.DatabaseType.SQLSERVER == dbType) {
                            temp.createTableTempl = getVMFile("/vm/sqlserver/createTable.templ");
                            temp.createIndexTempl = getVMFile("/vm/sqlserver/createIndex.templ");
                            temp.referenceTempl = getVMFile("/vm/sqlserver/reference.templ");
                            temp.createViewTempl = getVMFile("/vm/sqlserver/createView.templ");
                        } else if (IDbCreateService.DatabaseType.ORACLE == dbType) {
                            temp.createTableTempl = getVMFile("/vm/oracle/createTable.templ");
                            temp.createIndexTempl = getVMFile("/vm/oracle/createIndex.templ");
                            temp.referenceTempl = getVMFile("/vm/oracle/reference.templ");
                            temp.createViewTempl = getVMFile("/vm/oracle/createView.templ");
                        } else if (IDbCreateService.DatabaseType.DB2 == dbType) {
                            temp.createTableTempl = getVMFile("/vm/db2/createTable.templ");
                            temp.createIndexTempl = getVMFile("/vm/db2/createIndex.templ");
                            temp.referenceTempl = getVMFile("/vm/db2/reference.templ");
                            temp.createViewTempl = getVMFile("/vm/db2/createView.templ");
                        } else {
                            throw new IllegalArgumentException("Unsupported dbType: " + dbType);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    generator = temp;
                    ddlGeneratorMap.put(dbType, generator);
                }
            }
        return generator;
    }

    private static String getVMFile(String path) throws IOException {
        return DdlGeneratorFactory.class.getResource(path).getFile();
//        return new File(path).getAbsolutePath();
//        return FileLocator.resolve(DBPowerDesignerPlugin.getDefault().getBundle().getResource(path)).getFile();
//        return new ConfigureFileUtil().getVMFile(path);
    }

    static class DdlGeneratorVelocityImpl implements IDdlGenerator {
        private String createTableTempl;

        private String createIndexTempl;

        private String createViewTempl;

        private String referenceTempl;

        private VelocityEngine ve;

        public DdlGeneratorVelocityImpl() {
            initVelocityEngine();
        }

        public DdlGeneratorVelocityImpl(String createTableTempl, String createIndexTempl, String createViewTempl, String referenceTempl) {
            this.createTableTempl = createTableTempl;
            this.createIndexTempl = createIndexTempl;
            this.createViewTempl = createViewTempl;
            this.referenceTempl = referenceTempl;
            initVelocityEngine();
        }

        public void geneCreateIndexDdl(List<IIndex> indexs, Writer writer) {
            try {
                Template template = this.ve.getTemplate(this.createIndexTempl, "UTF-8");
                VelocityContext vc = new VelocityContext();
                vc.put("indexs", indexs);
                template.merge(vc, writer);
            } catch (ResourceNotFoundException e) {
//                logger.error("Generating index sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Template file {0} doesn't exist.", new Object[]{this.createIndexTempl}));
            } catch (ParseErrorException e) {
//                logger.error("Generating index sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Parsing template file {0} failed.", new Object[]{this.createIndexTempl}));
            } catch (Exception e) {
//                logger.error("Generating index sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Parsing template file {0} failed.", new Object[]{this.createIndexTempl}));
            }
        }

        public void geneCreateTableDdl(List<ITable> tables, Writer writer) {
            try {
                Template template = this.ve.getTemplate(this.createTableTempl, "UTF-8");
                VelocityContext vc = new VelocityContext();
                vc.put("tables", tables);
                template.merge(vc, writer);
            } catch (ResourceNotFoundException e) {
//                logger.error("Generating sql of creating table failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Template file {0} doesn't exist.", new Object[]{this.createTableTempl}));
            } catch (ParseErrorException e) {
//                logger.error("Generating sql of creating table failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Parsing template file {0} failed.", new Object[]{this.createTableTempl}));
            } catch (Exception e) {
//                logger.error("Generating sql of creating table failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Parsing template file {0} failed.", new Object[]{this.createTableTempl}));
            }
        }

        public void geneCreateViewDdl(List<Pdm.ViewInfo> views, Writer writer) {
            try {
                Template template = this.ve.getTemplate(this.createViewTempl, "UTF-8");
                VelocityContext vc = new VelocityContext();
                vc.put("views", views);
                template.merge(vc, writer);
            } catch (ResourceNotFoundException e) {
//                logger.error("Generating view sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Template file {0} doesn't exist.", new Object[]{this.createViewTempl}));
            } catch (ParseErrorException e) {
//                logger.error("Generating view sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Parsing template file {0} failed.", new Object[]{this.createViewTempl}));
            } catch (Exception e) {
//                logger.error("Generating view sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Parsing template file {0} failed.", new Object[]{this.createViewTempl}));
            }
        }

        public void geneAddConstraintDdl(List<IFkConstraint> constraints, Writer writer) {
            try {
                Template template = this.ve.getTemplate(this.referenceTempl, "UTF-8");
                VelocityContext vc = new VelocityContext();
                vc.put("fkConstraints", constraints);
                template.merge(vc, writer);
            } catch (ResourceNotFoundException e) {
//                logger.error("Genertating foreign key constraint sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Template file {0} doesn't exist.", new Object[]{this.referenceTempl}));
            } catch (ParseErrorException e) {
//                logger.error("Genertating foreign key constraint sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Parsing template file {0} failed.", new Object[]{this.referenceTempl}));
            } catch (Exception e) {
//                logger.error("Genertating foreign key constraint sql failed.", e);
                throw new PDMParseRuntimeException(MessageFormat.format("Parsing template file {0} failed.", new Object[]{this.referenceTempl}));
            }
        }

        private void initVelocityEngine() {
            this.ve = new VelocityEngine();
            Properties prop = new Properties();
            prop.setProperty("file.resource.loader.path", "");
            try {
                this.ve.init(prop);
            } catch (Exception e) {
                String msg = e.getMessage();
                throw new PDMParseRuntimeException("Initializing VelocityEngine failed:\n" + msg);
            }
        }
    }
}
