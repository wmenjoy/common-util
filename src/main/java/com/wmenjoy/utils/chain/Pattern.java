package com.wmenjoy.utils.chain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.wmenjoy.utils.lang.ASCII;
import com.wmenjoy.utils.lang.StringParser;


/**
 * 解析
 *
 * @author jinliang.liu
 *
 */
public class Pattern {

    static final String chainEx = "!node1,(!node2,node3)?{1:node5,2:node6,3:node7},node1&node2,node1|node2";
    //static final String chainEx = "!node1,node2";
    //理想结果   NorNode, GroupNode (NorNode, nornode), conditionNode Map<1, node1>,  orNode, | node2

    private transient StringParser parser;

    /**
     * The starting point of state machine for the find operation. This allows a
     * match to start anywhere in the input.
     */
    transient Node root;

    public void compile(final String chainExpr) {

        this.parser = new StringParser(chainExpr);
        final Node node = this.sequence(null);
    }

    /***
     * 获取单个的Node 包括 groupNode 和singleNode
     *
     *
     *
     * @param end
     * @return
     */
    private Node singleNode(final Node end) {
        Node node = null;
        boolean noMode = false;
        LOOP: for (;;) {
            final int ch = this.parser.peek();
            switch (ch) {
            case '!':
                noMode = !noMode;
                this.parser.next();
                //处理非node
                break;
            case '(':
                // Because group handles its own closure,
                // we need to treat it differently
                // it must delete the ) mark
                node = this.dealWithGroup(end);

                // Check for comment or flag group

                //处理掉）
                break;
            case '?':
                //处理条件选择
                node = this.dealWithConditionNode(null);
            case ')': //处理括号的情况
            case ',':
            case '|':
            case '&':
            case '}':
                //读取一个node结束
                //构造工作链
                //清空当前的node
                if (noMode) {
                    node = new NoNode(node);
                }
                return node;

            case 0:
                //处理到行尾
                if (this.parser.readFinished()) {
                    break LOOP;
                }

                //Failed

            default:
                node = this.atom(); //已经指向下一个字符
                break;
            }
        }

        return node;
    }

    /**
     * Parsing of sequences between alternations.
     */
    private Node sequence(final Node end) {
        Node head = null;
        Node tail = null;
        Node node = null;
        this.root = null;
        Node next = null;
        final boolean noMode = false;

        LOOP: for (;;) {
            final int ch = this.parser.peek();
            switch (ch) {
            case '&':
                if (node == null) {
                    //抛异常
                }
                this.parser.next();
                next = this.singleNode(end);

                if (AndNode.class.isAssignableFrom(node.getClass())) {
                    ((AndNode)node).appendNode(next);
                } else {
                    node = new AndNode(node, next);
                }
                break;
            case '|':
                if (node == null) {
                    //抛异常
                }
                //跳过去
                this.parser.next();
                next = this.singleNode(end);

                if (OrNode.class.isAssignableFrom(node.getClass())) {
                    ((OrNode)node).appendNode(next);
                } else {
                    node = new OrNode(node, next);
                }
                break;
            case ')':
            case ',':
                //读取一个node结束
                //构造工作链
                //清空当前的node
                if (noMode) {
                    node = new NoNode(node);
                }
                if (head == null) {
                    head = tail = node;
                } else {
                    tail.next = node;
                    tail = node;
                }

                node = null;
                this.parser.next();
                if (')' == ch) {
                    return head;
                }
                break;
            case 0:
                //处理到行尾
                if (this.parser.readFinished()) {
                    break LOOP;
                }

                //Failed

            default:
                //保障不读到下一个字符
                node = this.singleNode(null);
                break;
            }
        }

        if (node != null) {

            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
        }

        return head;
    }

    private Node dealWithGroup(final Node end) {
        this.root = null;
        //skip （
        final int ch = this.parser.next();

        final Node node = this.sequence(end);

        return node;

    }

    /***
     *
     *
     * Node1,!Node,!(GroupNode, Node2,Node3),Node3
     *
     * Node = !()
     *
     *
     * !!!! ,, (, ), !, ?, &, |,
     *
     *
     * !: 开启否定模式
     *
     * , oneNode End ( groupNode ) --- ? 条件Node开启
     *
     * &：并且条件开启 |：或条件开启
     *
     *
     *
     *
     *
     */

    /**
     * 标点符号为控制字符，由上层控制。
     *
     * @param end
     * @return
     */
    private Node dealWithConditionNode(final Node end) {
        this.root = null;
        this.parser.next();
        int ch = this.parser.peek();
        boolean multiCondition = false;

        if ('{' == ch) {
            this.parser.next();
            multiCondition = true;
        }

        if (multiCondition == false) {
            final Node firstNode = this.singleNode(end);
            Node secondNode = null;
            ch = this.parser.peek();
            if (':' == ch) {
                secondNode = this.singleNode(end);
            }
            return new ConditionNode(firstNode, secondNode);
        }

        final Map<Integer, Node> multiConditionNodeMap = new HashMap<Integer, Node>();

        int index = -1;
        LOOP: for (;;) {
            ch = this.parser.peek();
            if (ASCII.isDigit(ch)) {
                index = this.parser.readInt(conditionNumberEndCharSet);
                ch = this.parser.peek();
            }

            switch (ch) {
            case '{':
            case '|':
            case '#':
                throw new IllegalArgumentException("不合法的语法");

            case ':':
                this.parser.next();
                final Node oneNode = this.singleNode(end);
                multiConditionNodeMap.put(index, oneNode);
                break;
            case ',':
                //不作处理
                this.parser.next();
                break;
            case '}':
                this.parser.next();
                return new MultiConditionNode(multiConditionNodeMap);
                //整个ConditionNode结束
            case 0:
                //处理到行尾
                if (this.parser.readFinished()) {
                    break LOOP;
                }

                //Failed

            default:

                break;
            }

        }

        /**
         * 结构异常
         */
        return null;
    }

    static Set<Character> conditionNumberEndCharSet = newHashSet(':');
    static Set<Character> conditionStrEndCharSet = newHashSet(',', '}');

    public static <T> Set<T> newHashSet(final T... ts) {

        final Set<T> resultSet = new HashSet<T>();
        if (ts == null) {
            return new HashSet<T>();
        } else {

            for (final T t : ts) {
                resultSet.add(t);
            }
        }

        return resultSet;
    }

    String supported_character = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ_";

    static Set<Character> nodeStrEndCharSet = newHashSet('?', ')', '&', '|', ',', '}');

    private Node atom() {
        //处理，具体task实例化的过程，待定
        return new SliceNode(this.parser.readStr(nodeStrEndCharSet));
    }

    public static void main(final String[] args) {
        new Pattern().compile(chainEx);

    }
}

