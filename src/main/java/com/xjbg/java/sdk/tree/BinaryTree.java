package com.xjbg.java.sdk.tree;

import com.xjbg.java.sdk.util.CollectionUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kesc
 * @since 2019/6/25
 */
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class BinaryTree<T> extends DefaultTree<T> {
    private T value;

    private List<Tree<T>> children;

    public BinaryTree() {
    }

    public BinaryTree(T value) {
        this.value = value;
    }

    @Override
    public boolean isLeaf() {
        return CollectionUtil.isEmpty(children);
    }

    @Override
    public void setChildren(List<Tree<T>> children) {
        if (CollectionUtil.isNotEmpty(children) && children.size() > 2) {
            throw new IllegalArgumentException("binary tree's children can't more than 2");
        }
        this.children = children;
    }

    public List<T> preOrder() {
        List<T> result = new ArrayList<>();
        preOrder(result, this);
        return result;
    }

    private void preOrder(List<T> result, Tree<T> tTree) {
        result.add(tTree.getValue());
        List<Tree<T>> children = tTree.getChildren();
        if (!CollectionUtil.isEmpty(children)) {
            for (Tree<T> child : children) {
                preOrder(result, child);
            }
        }
    }

    public List<T> postOrder() {
        List<T> result = new ArrayList<>();
        postOrder(result, this);
        return result;
    }

    private void postOrder(List<T> result, Tree<T> tTree) {
        List<Tree<T>> children = tTree.getChildren();
        if (!CollectionUtil.isEmpty(children)) {
            for (Tree<T> child : children) {
                postOrder(result, child);
            }
        }
        result.add(tTree.getValue());
    }

    public List<T> midOrder() {
        List<T> result = new ArrayList<>();
        midOrder(result, this);
        return result;
    }

    private void midOrder(List<T> result, Tree<T> tTree) {
        List<Tree<T>> children = tTree.getChildren();
        if (CollectionUtil.isNotEmpty(children)) {
            midOrder(result, children.get(0));
        }
        result.add(tTree.getValue());
        if (CollectionUtil.isNotEmpty(children) && children.size() == 2) {
            midOrder(result, children.get(1));
        }
    }

    public static void main(String[] args) {
        BinaryTree<String> tree = new BinaryTree<>("0");
        BinaryTree<String> tree1 = new BinaryTree<>("4");
        tree1.setChildren(Arrays.asList(new BinaryTree<>("7"), new BinaryTree<>("8")));
        tree.setChildren(Arrays.asList(new BinaryTree<>("1"), tree1));
        for (String s : tree.iterate()) {
            System.out.println(s);
        }
        System.out.println("---------");
        for (String s : tree.preOrder()) {
            System.out.println(s);
        }
        System.out.println("---------");
        for (String s : tree.midOrder()) {
            System.out.println(s);
        }
        System.out.println("---------");
        for (String s : tree.postOrder()) {
            System.out.println(s);
        }
    }
}
