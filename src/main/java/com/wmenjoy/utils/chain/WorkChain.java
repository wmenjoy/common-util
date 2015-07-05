package com.wmenjoy.utils.chain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wmenjoy.utils.lang.ASCII;
import com.wmenjoy.utils.lang.Stack;
import com.wmenjoy.utils.lang.StringParser;
import com.wmenjoy.utils.lang.StringUtils;

/**
 * 解析
 *
 * @author jinliang.liu
 *
 */
public class WorkChain {

	static final String chainEx = "!node1,(!node2,node3)?{1:node5,2:node6,3:node7,*:node8},node1&node2,node1|node2";
	// static final String chainEx = "!node1,node2";
	// 理想结果 NorNode, GroupNode (NorNode, nornode), conditionNode Map<1, node1>,
	// orNode, | node2

	private transient StringParser parser;

	private Node chain = null;
	List<WorkNode> nodeList = null;
	ChainHandlerFactory factory = null;

	protected WorkChain(ChainHandlerFactory factory) {
		this.factory = factory;
	}

	public static WorkChain compile(final ChainHandlerFactory factory,
			final String chainExpr) {
		WorkChain workChain = new WorkChain(factory);
		workChain.parser = new StringParser(chainExpr);
		workChain.chain = workChain.sequence();
		workChain.init();
		return workChain;
	}

	/**
	 * 初始化所有的Node
	 * 
	 * @param nodeList2
	 */
	private void init() {
		if (nodeList == null || nodeList.size() == 0) {
			return;
		}

		for (WorkNode node : nodeList) {
			node.init(factory);
		}

	}

	/**
	 * 处理请求
	 * 
	 * @param contextParam
	 * @return
	 */
	public int handle(BaseContextParam contextParam) {
		if(contextParam == null){
			return 0;
		}
		
		
		Node node = this.chain;
		try {
			while (node != null) {
				int result = node.handle(contextParam);

				if (result == 1) {
					dealWith(contextParam, null, result);
					return 1;
				}

				node = node.next;
			}
		} catch (Exception e) {
			// 执行rollback

			dealWith(contextParam, e, -1);

			return 1;
		}

		return 0;
	}

	private void dealWith(BaseContextParam contextParam, Exception e, int result) {

		Stack<ChainHandler> rollbackStack = contextParam.getRockbackPath();
		// TODO Auto-generated method stub
		if (rollbackStack == null || rollbackStack.size() == 0) {
			return;
		}

		while (!rollbackStack.isEmpty()) {
			ChainHandler handler = rollbackStack.pop();
			handler.onException(contextParam, e);
		}
	}

	/***
	 * 获取单个的Node 包括 groupNode 和singleNode
	 *
	 *
	 *
	 * @param end
	 * @return
	 */
	private Node singleNode() {
		Node node = null;
		boolean noMode = false;
		LOOP: for (;;) {
			final int ch = this.parser.peek();
			switch (ch) {
			case '!':
				noMode = !noMode;
				this.parser.next();
				// 处理非node
				break;
			case '(':
				// Because group handles its own closure,
				// we need to treat it differently
				// it must delete the ) mark
				node = this.dealWithGroup();

				// Check for comment or flag group

				// 处理掉）
				break;
			case '?':
				// 处理条件选择
				node = this.dealWithConditionNode(node);
			case '{':
				//处理循环暂不支持
				node =this.dealWithForNode(node);
				continue;
			case ')': // 处理括号的情况
			case ',':
			case '|':
			case '&':
			case '}':
				// 读取一个node结束
				// 构造工作链
				// 清空当前的node
				if (noMode) {
					node = new NoNode(node);
				}
				return node;

			case 0:
				// 处理到行尾
				if (this.parser.readFinished()) {
					break LOOP;
				}

				// Failed

			default:
				node = this.atom(); // 已经指向下一个字符
				break;
			}
		}

		return node;
	}

	private Node dealWithForNode(Node node) {
		try{
			int ch = this.parser.next();
			
			int count = Integer.parseInt(this.parser.readStr(conditionNumberEndCharSet));
			if('{' == this.parser.peek()){
				throw new ParseException("格式有错误，｛应该匹配｝");
			}
			
			return new ForNode(node, count);
			
		} catch(Exception e){
			throw new ParseException("格式有错误，｛需要匹配一个整数｝");
		}
	}

	/**
	 * Parsing of sequences between alternations.
	 */
	private Node sequence() {
		Node head = null;
		Node tail = null;
		Node node = null;
		Node next = null;
		final boolean noMode = false;

		LOOP: for (;;) {
			final int ch = this.parser.peek();
			switch (ch) {
			case '&':
				if (node == null) {
					// 抛异常
				}
				this.parser.next();
				next = this.singleNode();

				if (AndNode.class.isAssignableFrom(node.getClass())) {
					((AndNode) node).appendNode(next);
				} else {
					node = new AndNode(node, next);
				}
				break;
			case '|':
				if (node == null) {
					// 抛异常
				}
				// 跳过去
				this.parser.next();
				next = this.singleNode();

				if (OrNode.class.isAssignableFrom(node.getClass())) {
					((OrNode) node).appendNode(next);
				} else {
					node = new OrNode(node, next);
				}
				break;
			case ')':
			case ',':
				// 读取一个node结束
				// 构造工作链
				// 清空当前的node
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
				// 处理到行尾
				if (this.parser.readFinished()) {
					break LOOP;
				}

				// Failed

			default:
				// 保障不读到下一个字符
				node = this.singleNode();
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

	private Node dealWithGroup() {
		// skip （
		final int ch = this.parser.next();

		final Node node = this.sequence();

		return new GroupNode(node);

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

	final static Set<Character> conditionNumberEndCharSet = newHashSet("0123456789");
	/**
	 * 标点符号为控制字符，由上层控制。
	 *
	 * @param end
	 * @return
	 */
	private Node dealWithConditionNode(final Node conditionNode) {

		this.parser.next();
		int ch = this.parser.peek();
		boolean multiCondition = false;

		if ('{' == ch) {
			this.parser.next();
			multiCondition = true;
		}

		if (multiCondition == false) {
			final Node firstNode = this.singleNode();
			Node secondNode = null;
			ch = this.parser.peek();
			if (':' == ch) {
				secondNode = this.singleNode();
			}
			return new ConditionNode(conditionNode, firstNode, secondNode);
		}
		

		final Map<Integer, Node> multiConditionNodeMap = new HashMap<Integer, Node>();
		Node defaultNode = null;
		int index = -1;
		boolean defaultMode = false;
		LOOP: for (;;) {
			ch = this.parser.peek();
			System.out.println((char) ch);
			if (ASCII.isDigit(ch)) {
				index = Integer.parseInt(this.parser.readStr(conditionNumberEndCharSet));
				ch = this.parser.peek();
			} else if('*' == ch){
				defaultMode = true;
				ch = this.parser.next();
			} else if(ASCII.isSpace(ch)){
				ch = this.parser.next();
			}

			switch (ch) {
			case '{':
			case '|':
			case '#':
				throw new IllegalArgumentException("不合法的语法");

			case ':':
				this.parser.next();
				if(defaultMode){
					defaultNode = this.singleNode();
					continue;
				}
				final Node oneNode = this.singleNode();
				multiConditionNodeMap.put(index, oneNode);
				break;
			case ',':
				// 不作处理
				this.parser.next();
				break;
			case '}':
				this.parser.next();
				return new MultiConditionNode(conditionNode,
						multiConditionNodeMap, defaultNode);
				// 整个ConditionNode结束
			case 0:
				// 处理到行尾
				if (this.parser.readFinished()) {
					break LOOP;
				}

				// Failed

			default:
				throw new IllegalArgumentException("不合法的语法");
			}

		}

		/**
		 * 结构异常
		 */
		return null;
	}



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
	
	public static Set<Character> newHashSet(final String charStr) {

		final Set<Character> resultSet = new HashSet<Character>();
		if (StringUtils.isBlank(charStr)) {
			return new HashSet<Character>();
		} else {

			for (final char t : charStr.toCharArray()) {
				resultSet.add(t);
			}
		}

		return resultSet;
	}

	static String supported_character = 
			"0123456789abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ_";

	final static Set<Character> nodeSupportedCharSet = newHashSet(supported_character);

	private Node atom() {
		// 处理，具体task实例化的过程，待定
		return new SliceNode(this.parser.readStr(nodeSupportedCharSet));
		
	}

	public static void main(final String[] args) {
		WorkChain.compile(new ChainHandlerFactory() {

			@Override
			public ChainHandler get(String handlerName) {
				// TODO Auto-generated method stub
				return null;
			}
		}, chainEx).handle(null);

	}
}
