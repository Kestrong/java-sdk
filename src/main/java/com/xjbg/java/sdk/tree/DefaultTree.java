package com.xjbg.java.sdk.tree;

import com.xjbg.java.sdk.util.CollectionUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: huangpp02
 * @time: 2018-02-26 11:24
 */
@Data
public class DefaultTree<T> implements Tree<T> {
    private T value;

    private List<Tree<T>> children;

    public DefaultTree() {
    }

    public DefaultTree(T value) {
        this.value = value;
    }

    @Override
    public boolean isLeaf() {
        return CollectionUtil.isEmpty(children);
    }

    /**
     * 先根后依次遍历每棵子树
     *
     * @return
     */
    public List<T> iterate() {
        List<T> result = new ArrayList<>();
        iterate(this, result);
        return result;
    }

    private void iterate(Tree<T> tTree, List<T> result) {
        result.add(tTree.getValue());
        List<Tree<T>> children = tTree.getChildren();
        if (!CollectionUtil.isEmpty(children)) {
            for (Tree<T> child : children) {
                iterate(child, result);
            }
        }
    }

    public static void main(String[] args) {
        DefaultTree<String> tree = new DefaultTree<>("0");
        DefaultTree<String> tree1 = new DefaultTree<>("4");
        tree1.setChildren(Arrays.asList(new DefaultTree<>("7"), new DefaultTree<>("8"), new DefaultTree<>("5")));
        tree.setChildren(Arrays.asList(new DefaultTree<>("1"), tree1, new DefaultTree<>("3")));
        for (String s : tree.iterate()) {
            System.out.println(s);
        }
    }
}
