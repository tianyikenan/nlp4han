package com.lc.nlp4han.constituent.unlex;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * 表示词性标注产生单词的规则
 * 
 * @author 王宁
 * 
 */
public class PreterminalRule extends Rule
{
	String word;
	LinkedList<Double> scores = new LinkedList<Double>();
	double[] countExpectation = null;

	public PreterminalRule(short parent, String word)
	{
		super.parent = parent;
		this.word = word;
	}

	@Override
	public void split()
	{
		// split father
		int pNumSubSymbol = scores.size();
		for (int i = pNumSubSymbol - 1; i >= 0; i--)
		{
			scores.add(i + 1, BigDecimal.valueOf(scores.get(i))
					.divide(BigDecimal.valueOf(2.0), 15, BigDecimal.ROUND_HALF_UP).doubleValue());
			scores.set(i, scores.get(i + 1));
		}
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	public int chidrenHashcode()
	{
		return word.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PreterminalRule other = (PreterminalRule) obj;
		if (word == null)
		{
			if (other.word != null)
				return false;
		}
		else if (!word.equals(other.word))
			return false;
		return true;
	}

	public String getWord()
	{
		return word;
	}

	public void setWord(String word)
	{
		this.word = word;
	}

	public LinkedList<Double> getScores()
	{
		return scores;
	}

	public void setScores(LinkedList<Double> scores)
	{
		this.scores = scores;
	}

	public double[] getCountExpectation()
	{
		return countExpectation;
	}

	public void setCountExpectation(double[] countExpectation)
	{
		this.countExpectation = countExpectation;
	}

	@Override
	boolean withIn(HashSet<? extends Rule> rules)
	{
		if (rules.contains(this))
			return true;
		else
			return false;
	}

	@Override
	public String[] toStringRules()
	{
		String[] strs = new String[scores.size()];
		for (int i = 0; i < scores.size(); i++)
		{
			String parentStr;
			if (nonterminalTable.getNumSubsymbolArr().get(parent) == 1)
				parentStr = nonterminalTable.stringValue(parent);
			else
				parentStr = nonterminalTable.stringValue(parent) + "_" + i;
			String childStr = word;
			String str = parentStr + "->" + childStr + " " + scores.get(i);
			strs[i] = str;
		}
		return strs;
	}

	@Override
	public String toStringRule(short... labels)
	{
		if (labels.length != 1)
			throw new Error("参数错误。");
		String parentStr = nonterminalTable.stringValue(parent);
		String childStr = word;
		String str = parentStr + "_" + labels[0] + "->" + childStr + " " + scores.get(labels[0]);
		return str;
	}

	public TreeMap<String, Double> getParent_i_ScoceSum()
	{
		TreeMap<String, Double> A_iWordRuleSum = new TreeMap<>();
		if (scores.size() == 1)
		{
			A_iWordRuleSum.put(nonterminalTable.stringValue(parent), scores.get(0));
		}
		else
		{
			for (int i = 0; i < scores.size(); i++)
			{
				A_iWordRuleSum.put(nonterminalTable.stringValue(parent) + "_" + i, scores.get(i));
			}
		}
		return A_iWordRuleSum;
	}
}
