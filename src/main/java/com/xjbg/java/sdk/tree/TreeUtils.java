package com.xjbg.java.sdk.tree;


import com.xjbg.java.sdk.util.CollectionUtil;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author: huangpp02
 * @time: 2018-02-26 11:21
 */
public class TreeUtils {
    /**
     * @param initValue    树初始化节点
     * @param childrenFunc 根据自身获取子节点列表
     * @param <T>          tree对应的value值
     * @return 树
     */
    public static <T> Tree<T> buildTree(T initValue, Function<T, List<T>> childrenFunc) {
        DefaultTree<T> result = new DefaultTree<>();
        result.setValue(initValue);
        List<T> children = childrenFunc.apply(initValue);
        if (!CollectionUtil.isEmpty(children)) {
            result.setChildren(CollectionUtil.map(children, child -> buildTree(child, childrenFunc)));
        }
        return result;
    }

    public static <F, T> Tree<T> transform(Tree<F> from, Function<F, T> func) {
        DefaultTree<T> result = new DefaultTree<>();
        if (from == null) {
            return result;
        }
        result.setValue(func.apply(from.getValue()));
        List<Tree<F>> children = from.getChildren();
        if (!CollectionUtil.isEmpty(children)) {
            result.setChildren(CollectionUtil.map(children, child -> transform(child, func)));
        }
        return result;
    }

    public static <F, R> R reduce(Tree<F> tree, R initValue, BiFunction<F, R, R> resultBuilder) {
        if (tree == null) {
            return initValue;
        }
        R result = initValue;
        result = resultBuilder.apply(tree.getValue(), result);
        if (!tree.isLeaf()) {
            for (Tree<F> child : tree.getChildren()) {
                result = reduce(child, result, resultBuilder);
            }
        }
        return result;
    }

    /**
     * 查找子树
     */
    public static <T> Tree<T> findChildTree(Tree<T> tree, Predicate<? super T> predicate) {
        T value = tree.getValue();
        if (predicate.test(value)) {
            return tree;
        }
        List<Tree<T>> children = tree.getChildren();
        if (!CollectionUtil.isEmpty(children)) {
            for (Tree<T> child : children) {
                return findChildTree(child, predicate);
            }
        }
        return null;
    }

    public static <T> void forEach(Tree<T> tree, Consumer<Tree<T>> treeConsumer) {
        if (tree == null) {
            return;
        }
        if (!tree.isLeaf()) {
            CollectionUtil.forEach(tree.getChildren(), child -> forEach(child, treeConsumer));
        }
        treeConsumer.accept(tree);
    }
}
