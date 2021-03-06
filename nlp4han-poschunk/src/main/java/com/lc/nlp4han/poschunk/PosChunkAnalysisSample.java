package com.lc.nlp4han.poschunk;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.Chunk;

/**
 * 基于词的词性标注和组块分析样本类
 */
public class PosChunkAnalysisSample extends AbstractChunkAnalysisSample
{

	/**
	 * 构造方法
	 * 
	 * @param words
	 *            词语数组
	 * @param posChunks
	 *            词语对应词性和组块标记数组
	 */
	public PosChunkAnalysisSample(String[] words, String[] posChunks)
	{
		super(words, posChunks, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param words
	 *            词语列表
	 * @param posChunks
	 *            词语对应词性和组块标记数组
	 */
	public PosChunkAnalysisSample(List<String> words, List<String> posChunks)
	{
		super(words, posChunks, null);
	}

	@Override
	public Chunk[] toChunk()
	{
		if (scheme.equals("BIEOS"))
			return toChunkFromBIEOS();
		else if (scheme.equals("BIEO"))
			return toChunkFromBIEO();
		else
			return toChunkFromBIO();
	}

	private Chunk[] toChunkFromBIEOS()
	{
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;

		List<String> poses = new ArrayList<>();
		for (int i = 0; i < tags.size(); i++)
		{
			String chunkTag = tags.get(i).split("-")[1];
			String pos = tags.get(i).split("-")[0];
			poses.add(pos);
			if (chunkTag.equals("O") || chunkTag.split("_")[1].equals("B"))
			{
				if (isChunk)
					chunks.add(new Chunk(type, join(tokens, poses, start, i), start, i - 1));

				isChunk = false;
				if (!chunkTag.equals("O"))
				{
					start = i;
					isChunk = true;
					type = chunkTag.split("_")[0];
				}
			}
			else if (chunkTag.split("_")[1].equals("S"))
			{
				if (isChunk)
					chunks.add(new Chunk(type, join(tokens, poses, start, i), start, i - 1));

				type = chunkTag.split("_")[0];
				chunks.add(new Chunk(type, tokens.get(i) + "/" + pos, i, i - 1));
				isChunk = false;
			}
		}

		if (isChunk)
			chunks.add(new Chunk(type, join(tokens, poses, start, tags.size()), start, tags.size() - 1));

		return chunks.toArray(new Chunk[chunks.size()]);
	}

	private Chunk[] toChunkFromBIEO()
	{
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;
		List<String> poses = new ArrayList<>();
		for (int i = 0; i < tags.size(); i++)
		{
			String chunkTag = tags.get(i).split("-")[1];
			String pos = tags.get(i).split("-")[0];
			poses.add(pos);

			if (chunkTag.equals("O") || chunkTag.split("_")[1].equals("B"))
			{
				if (isChunk)
					chunks.add(new Chunk(type, join(tokens, poses, start, i), start, i - 1));

				isChunk = false;
				if (!chunkTag.equals("O"))
				{
					start = i;
					isChunk = true;
					type = chunkTag.split("_")[0];
				}
			}
		}

		if (isChunk)
			chunks.add(new Chunk(type, join(tokens, poses, start, tags.size()), start, tags.size() - 1));

		return chunks.toArray(new Chunk[chunks.size()]);
	}

	private Chunk[] toChunkFromBIO()
	{
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;

		List<String> poses = new ArrayList<>();
		for (int i = 0; i < tags.size(); i++)
		{
			String chunkTag = tags.get(i).split("-")[1];
			String pos = tags.get(i).split("-")[0];
			poses.add(pos);

			if (chunkTag.equals("O") || chunkTag.split("_")[1].equals("B"))
			{
				if (isChunk)
					chunks.add(new Chunk(type, join(tokens, poses, start, i), start, i - 1));

				isChunk = false;
				if (!chunkTag.equals("O"))
				{
					start = i;
					isChunk = true;
					type = chunkTag.split("_")[0];
				}
			}
		}

		if (isChunk)
			chunks.add(new Chunk(type, join(tokens, poses, start, tags.size()), start, tags.size() - 1));

		return chunks.toArray(new Chunk[chunks.size()]);
	}

	/**
	 * 拼接字符串word/pos word/pos ...
	 * 
	 * @param words
	 *            带拼接的词组
	 * @param poses
	 *            词组对应的词性
	 * @param start
	 *            拼接的开始位置
	 * @param end
	 *            拼接的结束位置
	 * @return 拼接后的字符串
	 */
	private List<String> join(List<String> words, List<String> poses, int start, int end)
	{
		List<String> string = new ArrayList<>();
		for (int i = start; i < end; i++)
			string.add(words.get(i) + "/" + poses.get(i));

		return string;
	}

	@Override
	public String toString()
	{
		String string = "";

		Chunk[] chunks = toChunk();
		int j = 0;
		for (int i = 0; i < tokens.size(); i++)
		{
			if (j < chunks.length && i >= chunks[j].getStart() && i <= chunks[j].getEnd())
			{
				if (i == chunks[j].getEnd())
					string += chunks[j++] + "  ";
			}
			else
				string += tokens.get(i) + "/" + tags.get(i).split("-")[0] + "  ";
		}

		return string.trim();
	}
}
