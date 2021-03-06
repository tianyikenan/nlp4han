package com.lc.nlp4han.ml.naivebayes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

import com.lc.nlp4han.ml.model.AbstractModel;
/**
 * Model writer that saves models in plain text format.
 */
public class PlainTextNaiveBayesModelWriter extends NaiveBayesModelWriter {
  private BufferedWriter output;

  /**
   * Constructor which takes a NaiveBayesModel and a File and prepares itself to
   * write the model to that file. Detects whether the file is gzipped or not
   * based on whether the suffix contains ".gz".
   *
   * @param model The NaiveBayesModel which is to be persisted.
   * @param f     The File in which the model is to be persisted.
   */
  public PlainTextNaiveBayesModelWriter(AbstractModel model, File f)
      throws IOException {

    super(model);
    if (f.getName().endsWith(".gz")) {
      output = new BufferedWriter(new OutputStreamWriter(
          new GZIPOutputStream(new FileOutputStream(f))));
    } else {
      output = new BufferedWriter(new FileWriter(f));
    }
  }

  /**
   * Constructor which takes a NaiveBayesModel and a BufferedWriter and prepares
   * itself to write the model to that writer.
   *
   * @param model The NaiveBayesModel which is to be persisted.
   * @param bw    The BufferedWriter which will be used to persist the model.
   */
  public PlainTextNaiveBayesModelWriter(AbstractModel model, BufferedWriter bw) {
    super(model);
    output = bw;
  }

  public void writeUTF(String s) throws java.io.IOException {
    output.write(s);
    output.newLine();
  }

  public void writeInt(int i) throws java.io.IOException {
    output.write(Integer.toString(i));
    output.newLine();
  }

  public void writeDouble(double d) throws java.io.IOException {
    output.write(Double.toString(d));
    output.newLine();
  }

  public void close() throws java.io.IOException {
    output.flush();
    output.close();
  }

}
