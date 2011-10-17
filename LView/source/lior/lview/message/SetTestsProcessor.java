package lior.lview.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.nio.ByteBuffer;

import lior.lview.LViewException;
import lior.lview.data.Test;
import lior.lview.jdo.JDOUtils;

public class SetTestsProcessor extends MessageProcessor
{
  enum Meta {
    name, info
  }

  @Override
  public void process(String msg) throws LViewException {
    try {
      ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
      Vector<String>[] linkData = readMessage(bb);

      resolve(linkData);
    }
    catch (IOException e) {
      throw new LViewException(
          "failed to read meta-data from package TOC message. reason=["
              + e.getMessage() + "]");
    }
  }

  private void resolve(Vector<String>[] testData) throws LViewException {
    // cq_id,component,bucket,test,test_description
    // R0001,MAG,bucketName,test1,11111111111111111111
    HashMap<String, HashSet<String>> req2tests =
        new HashMap<String, HashSet<String>>();
    HashMap<String, HashSet<String>> comp2tests =
        new HashMap<String, HashSet<String>>();
    Vector<Test> testList = new Vector<Test>();

    String rid, component, bucket;
    while (testData[0].size() > 0) {
      int i = 0;
      rid = testData[i++].remove(0);
      component = testData[i++].remove(0);
      bucket = testData[i++].remove(0);
      String test = testData[i++].remove(0);
      String desc = testData[i++].remove(0);
      Test t = new Test(component, bucket, test, desc);
      HashSet<String> tests = req2tests.get(component);
      if (tests == null) {
        tests = new HashSet<String>();
        req2tests.put(rid, tests);
      }
      tests.add(t.getId());
      tests = comp2tests.get(component);
      if (tests == null) {
        tests = new HashSet<String>();
        comp2tests.put(component, tests);
      }
      tests.add(t.getId());
      testList.add(t);
    }

    JDOUtils.persist(testList);
  }

  private Vector<String>[] readMessage(ByteBuffer bb) throws IOException {
    byte[] msg = MessageProcessor.readSimpleMessage(bb);

    StringReader sr = new StringReader(new String(msg));
    BufferedReader br2 = new BufferedReader(sr);
    String header = br2.readLine();
    String[] headerData = header.split(",", -1);

    @SuppressWarnings("unchecked")
    Vector<String>[] data = (Vector<String>[]) new Vector[headerData.length];

    for (int i = 0; i < data.length; i++) {
      data[i] = new Vector<String>();
    }

    for (String line = br2.readLine(); line != null; line = br2.readLine()) {
      String[] row = line.split(",", -1);
      for (int i = 0; i < data.length; i++) {
        data[i].add(row[i]);
      }
    }
    return data;
  }

}
