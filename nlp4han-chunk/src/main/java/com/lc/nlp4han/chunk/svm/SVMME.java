package com.lc.nlp4han.chunk.svm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.Chunk;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.Chunker;
import com.lc.nlp4han.chunk.svm.liblinear.InvalidInputDataException;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSample;
import com.lc.nlp4han.chunk.wordpos.ChunkAnalysisWordPosSampleEvent;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.ObjectStream;

public abstract class SVMME implements Chunker
{
	private SVMStandardInput ssi = null;
	private ChunkAnalysisContextGenerator contextgenerator;
	private Object model;
	private String label;

	public SVMME()
	{

	}

	public SVMME(ChunkAnalysisContextGenerator contextgenerator, String label)
	{
		super();
		this.contextgenerator = contextgenerator;
		this.label = label;
	}

	public SVMME(ChunkAnalysisContextGenerator contextgenerator, String filePath, String encoding, String label)
	{
		this(contextgenerator, label);
		try
		{
			ssi = new SVMStandardInput();
			ssi.readConversionInfo(filePath, encoding);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setModel(Object model)
	{
		this.model = model;
	}
	
	public Object getModel()
	{
		return this.model;
	}

	public void setSVMStandardInput(SVMStandardInput ssi)
	{
		this.ssi = ssi;
	}
	
	public void setSVMStandardInput(String filePath)
	{
		try
		{
			this.ssi = new SVMStandardInput();
			this.ssi.readConversionInfo(filePath, "utf-8");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void setContextgenerator(ChunkAnalysisContextGenerator contextgenerator)
	{
		this.contextgenerator = contextgenerator;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public abstract void setModel(String modelPath);

	@Override
	public Chunk[] parse(String sentence)
	{
		String[] wordTags = sentence.split(" +");
		List<String> words = new ArrayList<>();
		List<String> poses = new ArrayList<>();

		for (String wordTag : wordTags)
		{
			words.add(wordTag.split("/")[0]);
			poses.add(wordTag.split("/")[1]);
		}

		String[] chunkTypes = null;
		try
		{
			chunkTypes = tag(words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		AbstractChunkAnalysisSample sample = new ChunkAnalysisWordPosSample(words.toArray(new String[words.size()]),
				poses.toArray(new String[poses.size()]), chunkTypes);
		sample.setTagScheme(label);

		return sample.toChunk();
	}

	public String[] tag(String[] words, String[] poses) throws IOException
	{
		String[] chunkTags = new String[words.length];
		String line = null;

		for (int i = 0; i < words.length; i++)
		{
			String[] context = contextgenerator.getContext(i, words, chunkTags, poses);

			line = "1 " + SVMStandardInput.getSVMStandardFeaturesInput(context, ssi); // <label> <index1>:<value1>
																						// <index2>:<value2> ...
																						// 预测时，label可以为任意值
			String tag = predict(line, getModel());
			chunkTags[i] = tag;

		}
		return chunkTags;
	}

	/**
	 * 根据模型model，调用libsvm预测line的结果，line为libsvm的标准输入格式，形如 2 4:5 7:3 8:2....
	 */
	public String predict(String line, Object model) throws IOException
	{
		double v = predictOneLine(line, model);
		String result = transform(String.valueOf(v));
		return result;
	}

	public abstract double predictOneLine(String line, Object model) throws IOException;

	/**
	 * 将libsvm预测的结果（数字）转换成组块标注
	 */
	private String transform(String v)
	{
		int t = str2int(v);
		String result = ssi.getClassificationResults().get(t - 1);
		return result;
	}

	private int str2int(String str)
	{
		return Integer.valueOf(str.trim().split("\\.")[0]);
	}

	public void train(ObjectStream<AbstractChunkAnalysisSample> sampleStream, String[] arg,
			ChunkAnalysisContextGenerator contextGen) throws IOException, InvalidInputDataException
	{
		generateTrainDatum(sampleStream, arg, contextGen);
		train(arg);
	}

	private void generateTrainDatum(ObjectStream<AbstractChunkAnalysisSample> sampleStream, String[] arg,
			ChunkAnalysisContextGenerator contextGen) throws RuntimeException, IOException
	{
		ObjectStream<Event> es = new ChunkAnalysisWordPosSampleEvent(sampleStream, contextGen);
		init(es);
		es.reset();
		String[] input = SVMStandardInput.standardInput(es, ssi);
		SVMStandardInput.writeToFile(arg[arg.length - 2], input, "utf-8");
	}

	public abstract void train(String[] arg);

	private void init(ObjectStream<Event> es)
	{
		this.ssi = new SVMStandardInput();
		try
		{
			ssi.init(es);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
