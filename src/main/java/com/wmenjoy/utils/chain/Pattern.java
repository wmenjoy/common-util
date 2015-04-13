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
    transient Node<?, ?> root;

    public void compile(final String chainExpr) {

        this.parser = new StringParser(chainExpr);
        for (;;) {
            final Node<?, ?> node = this.sequence(null);
        }
    }

    /**
     * Parsing of sequences between alternations.
     */
    private Node<?, ?> sequence(final Node<?, ?> end) {
        Node<?, ?> head = null;
        Node<?, ?> tail = null;
        Node node = null;
        this.root = null;

        boolean noMode = false;

        LOOP: for (;;) {
            final int ch = this.parser.peek();
            switch (ch) {
            case '(':
                // Because group handles its own closure,
                // we need to treat it differently
                node = this.dealWithGroup(end);

                // Check for comment or flag group

                //处理掉）
                break;
            case ')':
                //异常， 多余的)
            case '!':
                noMode = !noMode;
                //处理非node
                break;
            // Fall through
            case '?':
                //处理条件选择
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

    private Node dealWithGroup(final Node<?, ?> end) {
        this.root = null;
        //skip （
        final int ch = this.parser.next();

        final Node node = this.sequence(end);

        if (')' == this.parser.peek()) {
            this.parser.next();
            return node;
        } else {
            //语法错误
        }

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
            multiCondition = true;
        }

        if (multiCondition == false) {
            final Node firstNode = this.sequence(end);
            Node secondNode = null;
            ch = this.parser.peek();
            if (':' == ch) {
                secondNode = this.sequence(end);
            }
            return new ConditionNode(firstNode, secondNode);
        }

        int number = 0;
        boolean numberStart = false;

        final Map<Integer, Node> multiConditionNodeMap = new HashMap<Integer, Node>();
        LOOP: for (; '}' != ch;) {
            ch = this.parser.peek();
            switch (ch) {
            case '{':
                break;
            case ':':
                numberStart = false;
                final Node oneNode = this.sequence(end);
                multiConditionNodeMap.put(number, oneNode);
                break;
            case ',':
                //不作处理
                break;
            case '}':
                this.parser.peek();
                return new MultiConditionNode(multiConditionNodeMap);
                //整个ConditionNode结束
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                if (numberStart) {
                    numberStart = true;
                    number = (number * 10) + (ch - '0');
                } else {
                    number = ch - '0';
                }

            case 0:
                //处理到行尾
                if (this.parser.readFinished()) {
                    break LOOP;
                }

                //Failed

            default:

                break;
            }

            this.parser.next();
        }

        /**
         * 结构异常
         */
        return null;
    }

    String supported_character = "0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ_";

    private Node atom() {
        int first = 0;
        int ch = this.parser.peek();
        final StringBuilder charAppend = new StringBuilder();
        LOOP: for (;;) {
            switch (ch) {
            case '?':
            case ':':
            case ')':
            case '&':
            case '|':
                if (first > 1) {
                    first--;
                }
                this.parser.unread();
                break LOOP;
            case ',':
                if (first > 1) {
                    first--;
                }
                this.parser.next();
                //真个字符读取完成了
                break LOOP;
            case 0:
                if (this.parser.readFinished()) {
                    break LOOP;
                }
                // Fall through
            default:
                //首字母必须为字符--， 是否需要考虑
                //                    if(first == 0 && !isNodeNameBeginCharacter(ch)){
                //                        break;
                //                    }
                charAppend.append((char)ch);
                first++;
                ch = this.parser.next();
                continue;
            }

        }

        if (first == 0) {
            //抛异常
        }

        return new SliceNode(charAppend.toString());
    }

    public static void main(final String[] args) {
        final String pattern = "sfsfsf:";
        final StringParser parser = new StringParser(pattern);

        int first = 0;
        int ch = parser.peek();
        final StringBuilder charAppend = new StringBuilder();
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
                parser.unread();
                break;
            case ',':
                if (first > 1) {
                    first--;
                }
                //真个字符读取完成了
                break;
            case 0:
                if (parser.readFinished()) {
                    break;
                }
                // Fall through
            default:
                //首字母必须为字符--， 是否需要考虑
                //                    if(first == 0 && !isNodeNameBeginCharacter(ch)){
                //                        break;
                //                    }
                charAppend.append((char)ch);
                first++;
                ch = parser.next();
                continue;
            }
            break;
        }

        System.out.println(charAppend.toString());
        if (first == 0) {
            //抛异常
        }
    }
}
