package nc.uap.plugin.studio.ui.preference.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.StringTokenizer;

public class ObjectToXML {
    public static Class[] classA = {
            Boolean.class, Character.class, Integer.class, Long.class, Double.class, Float.class, String.class,
            java.math.BigDecimal.class, int.class, char.class,
            boolean.class, long.class, double.class, float.class};

    public static final String DOC_TYPE = "(Java lang)Middleware depoly parameter";

    private void appendChild(Document doc, Node parent, Node child) {
        if (parent == null) {
            doc.appendChild(child);
        } else {
            parent.appendChild(child);
        }
    }

    private Class getArrayItemClass(Class arrayClass) throws Exception {
        if (arrayClass == null)
            return null;
        String className = arrayClass.getName();
        int key = className.indexOf("[L");
        if (key >= 0) {
            int lastLoc = className.indexOf(";");
            String classPureName = className.substring(key + 2, lastLoc);
            Class pureClass = Class.forName(classPureName, false, arrayClass.getClassLoader());
            if (key == 0)
                return pureClass;
            int[] arrayList = new int[key];
            for (int i = 0; i < arrayList.length; i++)
                arrayList[i] = 1;
            return Array.newInstance(pureClass, arrayList).getClass();
        }
        String[] id = {"[B", "[C", "[I", "[J"};
        Class[] type = {byte.class, char.class, int.class, long.class};
        for (int i = 0; i < id.length; i++) {
            key = className.indexOf(id[i]);
            if (key >= 0) {
                Class pureClass = type[i];
                if (key == 0)
                    return pureClass;
                int[] arrayList = new int[key];
                for (int j = 0; j < arrayList.length; j++)
                    arrayList[j] = 1;
                return Array.newInstance(pureClass, arrayList).getClass();
            }
        }
        return Class.forName(className);
    }

    private Node getDocument(Document doc, Element nod, Object o, int deepSet, Class defaultClass, String arrayName) throws Exception {
        int deep = deepSet + 1;
        if (o == null) {
            if (!defaultClass.isArray()) {
                nod.setAttribute("value", "null");
            } else {
                nod.setAttribute("arrayValue", "null");
            }
            return nod;
        }
        if (isPrimitive(o.getClass())) {
            nod.appendChild(doc.createTextNode(o.toString()));
            return nod;
        }
        if (o.getClass().isArray()) {
            int length = Array.getLength(o);
            Class itemType = getArrayItemClass(defaultClass);
            for (int j = 0; j < length; j++) {
                if (arrayName == null)
                    arrayName = "NODE";
                Element arrayList = doc.createElement(arrayName);
                getDocument(doc, arrayList, Array.get(o, j), deep, itemType, arrayName);
                appendChild(doc, nod, arrayList);
            }
        } else {
            if (defaultClass != o.getClass())
                if (nod != null)
                    nod.setAttribute("ClassType", o.getClass().getName());
            Field[] fa = o.getClass().getDeclaredFields();
            for (int i = 0; i < fa.length; i++) {
                boolean isAccessible = fa[i].isAccessible();
                fa[i].setAccessible(true);
                if (!Modifier.isFinal(fa[i].getModifiers())) {
                    Element child = doc.createElement(fa[i].getName());
                    Object oc = fa[i].get(o);
                    if (oc != null && oc.getClass() != fa[i].getType() && !isPrimitive(oc.getClass()))
                        child.setAttribute("ClassType", fa[i].getType().getName());
                    if (oc != null)
                        if (isPrimitive(oc.getClass())) {
                            child.appendChild(doc.createTextNode(oc.toString()));
                            appendChild(doc, nod, child);
                        } else if (oc.getClass().isArray()) {
                            getDocument(doc, nod, oc, deep, fa[i].getType(), fa[i].getName());
                        } else {
                            getDocument(doc, child, oc, deep, fa[i].getType(), null);
                            appendChild(doc, nod, child);
                        }
                    fa[i].setAccessible(isAccessible);
                }
            }
        }
        return nod;
    }

    private boolean isPrimitive(Class cl) {
        for (int i = 0; i < classA.length; i++) {
            if (classA[i] == cl)
                return true;
        }
        return false;
    }

    public static void saveAsXmlFile(String fileName, Object o) throws Exception {
        saveAsXmlFile(fileName, o, Object.class);
    }

    private static void saveAsXmlFile(String fileName, Object o, Class defaultClass) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element nod = doc.createElement("root");
        Node root = (new ObjectToXML()).getDocument(doc, nod, o, 0, defaultClass, null);
        doc.appendChild(root);
        String pathName = fileName;
        String tmpDirectory = "";
        pathName = pathName.replace('\\', '/');
        pathName = pathName.substring(0, pathName.lastIndexOf("/"));
        StringTokenizer st = new StringTokenizer(pathName, "/");
        while (st.hasMoreTokens()) {
            tmpDirectory = String.valueOf(tmpDirectory) + st.nextToken() + "/";
            File f = new File(tmpDirectory);
            if (!f.canRead())
                f.mkdir();
        }
        FileWriter fileOutStream = new FileWriter(fileName);
        PrintWriter dataOutStream = new PrintWriter(fileOutStream);
        XMLPrinter.printDOMTree(dataOutStream, doc, 0);
        dataOutStream.close();
        fileOutStream.close();
    }
}
