package com.xjbg.java.sdk.tree;

import java.util.List;

/**
 * @author: huangpp02
 * @time: 2018-02-26 11:18
 */
public interface Tree<T> {
    /**
     * 自身属性
     *
     * @return
     */
    T getValue();

    /**
     * 子属性
     *
     * @return
     */
    List<Tree<T>> getChildren();

    /**
     * 是否是叶子节点
     * @return
     */
    boolean isLeaf();
}
