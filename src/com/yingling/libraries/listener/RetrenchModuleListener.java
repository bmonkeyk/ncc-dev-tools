package com.yingling.libraries.listener;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 精简nchome
 */
public class RetrenchModuleListener {
    public static void retrench(String homePath) {
        if (StringUtils.isBlank(homePath)) {
            return;
        }
        //精简modules
        String modulePath = homePath + File.separator + "modules";
        Set<String> moduleSet = getModuleSet();
        File moduleFile = new File(modulePath);
        if (!moduleFile.exists()) {
            return;
        }
        File[] oldModules = moduleFile.listFiles();
        for (File module : oldModules) {
            String moduleName = module.getName();
            if (!moduleSet.contains(moduleName)) {
                delete(module);
            }
        }

//        //精简nccloud
//        Set<String> libSet = getLibSet();
//        String libPath = homePath + File.separator + "hotwebs"
//                + File.separator + "nccloud" + File.separator
//                + "WEB-INF" + File.separator + "lib";
//        File libFile = new File(libPath);
//        if (!libFile.exists()) {
//            return;
//        }
//        File[] oldLibs = libFile.listFiles();
//        for (File lib : oldLibs) {
//            String libName = lib.getName();
//            if (!libSet.contains(libName) && !libName.startsWith("uihr")) {
//                lib.delete();
//            }
//        }
    }

    /**
     * 递归删除module
     *
     * @param module
     */
    private static void delete(File module) {
        if (module.isDirectory()) {
            File[] files = module.listFiles();
            for (File file : files) {
                delete(file);
            }
        }
        module.delete();
    }

    /**
     * 精简后的module
     *
     * @return
     */
    private static Set<String> getModuleSet() {
        Set<String> moduleSet = new HashSet();
        moduleSet.add("baseapp");
        moduleSet.add("iuap");
        moduleSet.add("opm");
        moduleSet.add("platform");
        moduleSet.add("pubapp");
        moduleSet.add("pubapputil");
        moduleSet.add("riaaam");
        moduleSet.add("riaadp");
        moduleSet.add("riaam");
        moduleSet.add("riacc");
        moduleSet.add("riadc");
        moduleSet.add("riamm");
        moduleSet.add("riaorg");
        moduleSet.add("riart");
        moduleSet.add("riasm");
        moduleSet.add("riawf");
        moduleSet.add("uapbd");
        moduleSet.add("uapbs");
        moduleSet.add("uapec");
        moduleSet.add("uapfw");
        moduleSet.add("uapfwjca");
        moduleSet.add("uapmw");
        moduleSet.add("uapportal");
        moduleSet.add("uapsc");
        moduleSet.add("uapss");
        moduleSet.add("workbench");
        return moduleSet;
    }

    public static void main(String[] strs) {
        File file = new File("/Users/liuchao/Documents/program/nchome/ncc/test_home/modules");
        List<String> list = new ArrayList<>();
        for (File f : file.listFiles()) {
            list.add(f.getName());

        }
        //排序
        java.util.Collections.sort(list);
        for (String s : list) {
            System.out.println("moduleSet.add(\"" + s + "\");");
        }

    }
//    private static Set<String> getLibSet() {
//        Set<String> libSet = new HashSet();
//        libSet.add("pubplatform_baseLevel-1.jar");
//        libSet.add("uiaim_nccloud.jar");
//        libSet.add("uiampub_nccloud.jar");
//        libSet.add("uiarap_nccloud.jar");
//        libSet.add("uiaum_nccloud.jar");
//        libSet.add("uibaseapp_nccloudLevel-1.jar");
//        libSet.add("uibond_nccloud.jar");
//        libSet.add("uiccc_nccloud.jar");
//        libSet.add("uicdmc_nccloud.jar");
//        libSet.add("uicmp_nccloud.jar");
//        libSet.add("uicredit_nccloud.jar");
//        libSet.add("uiepmp_nccloud.jar");
//        libSet.add("uierm_nccloud.jar");
//        libSet.add("uifa_nccloud.jar");
//        libSet.add("uifbm_nccloud.jar");
//        libSet.add("uifct_nccloud.jar");
//        libSet.add("uifiarc_nccloud.jar");
//        libSet.add("uifipub_nccloud.jar");
//        libSet.add("uifip_nccloud.jar");
//        libSet.add("uifmc_nccloud.jar");
//        libSet.add("uifmr_nccloud.jar");
//        libSet.add("uifts_nccloud.jar");
//        libSet.add("uigl_nccloud.jar");
//        libSet.add("uigpmc_nccloud.jar");
//        libSet.add("uihrcm_nccloud.jar");
//        libSet.add("uihrhi_nccloud.jar");
//        libSet.add("uihrjf_nccloud.jar");
//        libSet.add("uihrkq_nccloud.jar");
//        libSet.add("uihrpub_nccloud.jar");
//        libSet.add("uihrp_nccloud.jar");
//        libSet.add("uihrrpt_nccloud.jar");
//        libSet.add("uihrtrn_nccloud.jar");
//        libSet.add("uihrwa_nccloud.jar");
//        libSet.add("uihryf_nccloud.jar");
//        libSet.add("uihrzz_nccloud.jar");
//        libSet.add("uiia_nccloud.jar");
//        libSet.add("uiic_nccloud.jar");
//        libSet.add("uiifm_nccloud.jar");
//        libSet.add("uiimag_nccloud.jar");
//        libSet.add("uilappreportrt_nccloudLevel-1.jar");
//        libSet.add("uimapub_nccloud.jar");
//        libSet.add("uiobm_nccloud.jar");
//        libSet.add("uiplatform_nccloudLevel-1.jar");
//        libSet.add("uiprice_nccloud.jar");
//        libSet.add("uipurp_nccloud.jar");
//        libSet.add("uipu_nccloud.jar");
//        libSet.add("uipvinv_nccloud.jar");
//        libSet.add("uiresa_nccloud.jar");
//        libSet.add("uiriaam_nccloudLevel-1.jar");
//        libSet.add("uiriacc_nccloudLevel-1.jar");
//        libSet.add("uiriadc_nccloudLevel-1.jar");
//        libSet.add("uiriamm_nccloudLevel-1.jar");
//        libSet.add("uiriaorg_nccloud.jar");
//        libSet.add("uiriaorg_nccloudLevel-1.jar");
//        libSet.add("uiriart_nccloudLevel-1.jar");
//        libSet.add("uiriasm_nccloudLevel-1.jar");
//        libSet.add("uiriawf_nccloudLevel-1.jar");
//        libSet.add("uiscmpub_nccloud.jar");
//        libSet.add("uisf_nccloud.jar");
//        libSet.add("uisn_nccloud.jar");
//        libSet.add("uiso_nccloud.jar");
//        libSet.add("uisscpfm_nccloud.jar");
//        libSet.add("uisscqm_nccloud.jar");
//        libSet.add("uisscrp_nccloud.jar");
//        libSet.add("uissctp_nccloud.jar");
//        libSet.add("uisscwo_nccloud.jar");
//        libSet.add("uitam_nccloud.jar");
//        libSet.add("uitmpub_nccloud.jar");
//        libSet.add("uito_nccloud.jar");
//        libSet.add("uiuapbd_nccloudLevel-1.jar");
//        libSet.add("uiuapbd_nccloud_am.jar");
//        libSet.add("uiuapbd_nccloud_fi.jar");
//        libSet.add("uiuapbd_nccloud_pm.jar");
//        libSet.add("uiuapbd_nccloud_scm.jar");
//        libSet.add("uiuapbd_nccloud_uap.jar");
//        libSet.add("uiufesbexpress_nccloudLevel-1.jar");
//        return libSet;
//    }
}
