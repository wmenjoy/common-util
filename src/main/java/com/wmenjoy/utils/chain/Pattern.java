package com.wmenjoy.utils.chain;

import com.wmenjoy.utils.lang.IntAppender;
import com.wmenjoy.utils.lang.StringParser;

/**
 * 解析
 * 
 * @author jinliang.liu
 *
 */
public class Pattern {

    String chainEx = "!node1,(!node2,node3)?(1:node5,2:node6,3:node7), node1&node2, node1|node2";

    //理想结果   NorNode, GroupNode (NorNode, nornode), conditionNode Map<1, node1>,  orNode, | node2

    private transient StringParser parser;

    /**
     * The starting point of state machine for the find operation. This allows a
     * match to start anywhere in the input.
     */
    transient Node root;

    public void compile(final String chainExpr) {

        this.parser = new StringParser(chainExpr);
        for (;;) {
            final Node node = this.sequence(null);
        }
    }

    /**
     * Parsing of sequences between alternations.
     */
    private Node sequence(final Node end) {
        Node head = null;
        Node tail = null;
        Node node = null;
        this.root = null;
        boolean dealWithGroup = false;
        final boolean dealWithCondition = false;
        final boolean dealWithAnd = false;
        LOOP: for (;;) {
            final int ch = this.parser.peek();
            switch (ch) {
            case '(':
                // Because group handles its own closure,
                // we need to treat it differently
                this.parser.next();
                dealWithGroup = true;
                node = this.sequence(end);
                // Check for comment or flag group

                //处理掉）
                break;
            case ')':

                if (!dealWithGroup) {
                    //出问题
                    return null;
                }

                if (node != null) {
                    node = new GroupNode(node);
                    if (node == null) {
                        break;
                    }
                    if (head == null) {
                        head = node;
                    } else {
                        tail.next = node;
                    }
                    // Double return: Tail was returned in root
                    tail = this.root;
                }
                break;

            case '!':
                this.parser.next();
                tail = this.root;
                final Node subNode = this.sequence(tail);
                if (node == null) {
                    break;
                }
                if (head == null) {
                    head = node;
                } else {
                    tail.next = node;
                }
                // Double return: Tail was returned in root
                tail = this.root;
                //处理非node
                break;
            // Fall through
            case '?':
                //如果node不等与null
                this.parser.next();
                tail = this.root;
                node = this.dealWithConditionNode(tail);
            case ',':
                //读取一个node结束
                break;
            case '&':
                if (node == null) {
                    //抛异常
                }

                final Node next = this.sequence(null);

            case '|':
                if (node == null) {
                    //抛异常
                }
                node = this.sequence(end);
            case 0:
                //处理到行尾
                if (this.parser.readFinished()) {
                    break LOOP;
                }

                //Failed

            default:
                node = this.atom();
                break;
            }

            //                node = closure(node);

            if (head == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }

            this.parser.next();
        }

        this.root = tail;      //double return
        return head;
    }

    private Node dealWithConditionNode(final Node end) {
        final Node con1Node = this.sequence(end);
        Node con2Node = null;
        final int ch = this.parser.peek();
        if (':' == ch) {
            this.parser.next();
            con2Node = this.sequence(end);
        } else {
            //抛异常
        }

        return new ConditionNode(con1Node, con2Node);

    }

    String supported_character = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ_";

    private Node atom() {
        int first = 0;
        final int prev = -1;
        final boolean hasSupplementary = false;
        int ch = this.parser.peek();
        final IntAppender appender = new IntAppender();
        for (;;) {
            switch (ch) {
            case '?':
            case ':':
            case '(':
            case '&':
            case '|':
                if (first > 1) {
                    first--;
                }
                this.parser.unread();
                break;
            case ',':
                if (first > 1) {
                    first--;
                }
                //真个字符读取完成了
                break;
            case 0:
                if (this.parser.readFinished()) {
                    break;
                }
                // Fall through
            default:
                //首字母必须为字符--， 是否需要考虑
                //                    if(first == 0 && !isNodeNameBeginCharacter(ch)){
                //                        break;
                //                    }
                appender.append(ch, first);
                first++;
                ch = this.parser.next();
                continue;
            }
            break;
        }

        if (first == 0) {
            //抛异常
        }

        return new SliceNode(appender.getBuffer());
    }

}
