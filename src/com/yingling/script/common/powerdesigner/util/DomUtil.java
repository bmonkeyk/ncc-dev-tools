package com.yingling.script.common.powerdesigner.util;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class DomUtil {
    public static Node findChild(Node node, String childName) {
        List<Node> nodes = findChilds(node, childName);
        return nodes.isEmpty() ? null : (Node) nodes.get(0);
    }

    public static List<Node> findChilds(Node node, String childName) {
        List<Node> nodes = new ArrayList<Node>();
        if (node != null)
            for (Node subNode = node.getFirstChild(); subNode != null; subNode = subNode.getNextSibling()) {
                if (subNode.getNodeName().equalsIgnoreCase(childName))
                    nodes.add(subNode);
            }
        return nodes;
    }

    public static Node findNestedChild(Node node, String nestedChildName) {
        List<Node> nodes = findNestedChilds(node, nestedChildName);
        return nodes.isEmpty() ? null : (Node) nodes.get(0);
    }

    public static List<Node> findNestedChilds(Node node, String nestedChildName) {
        List<Node> nodes = new ArrayList<Node>();
        int firstSpliterIndex = nestedChildName.indexOf("/");
        if (firstSpliterIndex != -1) {
            String currChildName = nestedChildName.substring(0, firstSpliterIndex);
            String subChildName = nestedChildName.substring(firstSpliterIndex + 1);
            List<Node> currNodes = findChilds(node, currChildName);
            for (Node currNode : currNodes)
                nodes.addAll(findNestedChilds(currNode, subChildName));
            return nodes;
        }
        return findChilds(node, nestedChildName);
    }

    public static Node findNestedChild(Node node, String nestedChildName, String attrName, String attrValue) {
        int firstSpliterIndex = nestedChildName.indexOf("/");
        String currChildName = (firstSpliterIndex == -1) ? nestedChildName : nestedChildName.substring(0, firstSpliterIndex);
        List<Node> currNodes = findChilds(node, currChildName);
        if (firstSpliterIndex != -1) {
            String subChildName = nestedChildName.substring(firstSpliterIndex + 1);
            for (Node currNode : currNodes) {
                Node subNode = findNestedChild(currNode, subChildName, attrName, attrValue);
                if (subNode != null)
                    return subNode;
            }
        } else {
            for (Node currNode : currNodes) {
                if (StringUtils.equals(attrValue, ((Element) currNode).getAttribute(attrName)))
                    return currNode;
            }
        }
        return null;
    }

    public static String findChildContent(Node node, String childName) {
        if (node != null)
            for (Node subNode = node.getFirstChild(); subNode != null; subNode = subNode.getNextSibling()) {
                if (subNode.getNodeName().equalsIgnoreCase(childName))
                    return subNode.getFirstChild().getNodeValue();
            }
        return null;
    }

    public static String findChildAttr(Node node, String childName, String attrName) {
        if (node != null)
            for (Node subNode = node.getFirstChild(); subNode != null; subNode = subNode.getNextSibling()) {
                if (subNode.getNodeName().equalsIgnoreCase(childName))
                    return ((Element) subNode).getAttribute(attrName);
            }
        return null;
    }

    public static String findNestedChildAttr(Node node, String nestedChildName, String attrName) {
        Element ele = (Element) findNestedChild(node, nestedChildName);
        if (ele != null)
            return ele.getAttribute(attrName);
        return null;
    }
}
