package com.lc.nlp4han.dependency.tb;

import java.util.LinkedList;

/**
 * Arc Standard方法配置
 * 
 * 三种动作:
 * 左弧:栈顶第一个和第二次词形成主从关系,第二个出栈
 * 右弧:栈顶第二个和第一个词形成主从关系,第一个出栈
 * shift:缓冲区头的词入栈
 *
 */
public class ConfigurationArcStandard extends Configuration
{

	public ConfigurationArcStandard(LinkedList<Vertice> wordsBuffer)
	{
		super(wordsBuffer);
	}

	public ConfigurationArcStandard(String[] words, String[] pos)
	{
		super(words, pos);
	}

	public ConfigurationArcStandard()
	{
	}

	/**
	 * 当栈顶两个单词有右弧关系时，判断是否right_reduce
	 * 
	 * @return 当wordBuffer中有单词是栈顶单词的依存词时返回false
	 */
	public boolean canReduce(String[] dependencyIndices)
	{
		int indexOfWord_Bi;// 该单词在words中索引
		int indexOfWord_S1 = stack.peek().getIndexOfWord();
		int headIndexOfWord_Bi;// buffer第i个单词中心词在words中的索引
		for (int i = 0; i < wordsBuffer.size(); i++)
		{
			indexOfWord_Bi = wordsBuffer.get(i).getIndexOfWord();// 该单词在words中索引
			headIndexOfWord_Bi = Integer.parseInt(dependencyIndices[indexOfWord_Bi - 1]);// buffer第i个单词中心词在words中的索引
			if (indexOfWord_S1 == headIndexOfWord_Bi)
				return false;
		}
		return true;
	}

	// 共三类基本操作RIGHTARC_REDUCE、LEFTARC_REDUCE、SHIFT
	public void transfer(Action actType)
	{
		Vertice S1 = stack.pop();
		Vertice S2 = stack.peek();
		stack.push(S1);
		switch (actType.getBaseAction())
		{
		case "RIGHTARC_REDUCE":
			addArc(new Arc(actType.getRelation(), S2, S1));
			reduce(actType);
			break;
		case "LEFTARC_REDUCE":
			addArc(new Arc(actType.getRelation(), S1, S2));
			reduce(actType);
			break;
		case "SHIFT":
			shift();
			break;
		default:
			throw new IllegalArgumentException("参数不合法!");
		}
	}

	public void reduce(Action actType)
	{
		if (actType.getBaseAction().equals("RIGHTARC_REDUCE"))
		{
			stack.pop();
		}
		else if (actType.getBaseAction().equals("LEFTARC_REDUCE"))
		{
			Vertice S1 = stack.pop();
			stack.pop();
			stack.push(S1);
		}
	}
}
