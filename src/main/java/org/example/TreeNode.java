package org.example;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    public TreeNode<T> firstChild=null, nextSibling=null;
    public T data;
    public short id;
    public int latitude;
    public int longitude;
    public String name;
    public String zone;
    public short rail;

    public TreeNode(T data) {
        this.data=data;
    }

    public void addChild(TreeNode<T> cn) {
        if (firstChild==null) firstChild=cn;
        else {
            TreeNode<T> temp=firstChild;
            while(temp.nextSibling!=null) temp=temp.nextSibling;
            temp.nextSibling=cn;
        }
    }
}
