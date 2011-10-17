package lior.lview.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.nio.ByteBuffer;

import lior.lview.LViewException;
import lior.lview.data.Feature;
import lior.lview.data.Requirement;
import lior.lview.data.Test;
import lior.lview.jdo.JDOUtils;

public class SetFeaturesProcessor extends MessageProcessor
{
  enum Meta {
    name, info
  }

  @Override
  public void process(String msg) throws LViewException {
    try {
      ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
      Vector<String>[] featureData = readMessage(bb);
      Vector<String>[] reqData = readMessage(bb);
      Vector<String>[] linkData = readMessage(bb);
      
      resolve(featureData, reqData, linkData);
    }
    catch (IOException e) {
      throw new LViewException(
          "failed to read meta-data from package TOC message. reason=["
              + e.getMessage() + "]");
    }
  }

  @SuppressWarnings("unused")
  private void resolve(Vector<String>[] featureData, Vector<String>[] reqData,
      Vector<String>[] testData) throws LViewException
  {
    // cq_id,component,bucket,test,test_description
    // R0001,MAG,bucketName,test1,11111111111111111111
    HashMap<String, HashSet<String>> req2tests = new HashMap<String, HashSet<String>>();
    HashMap<String, HashSet<String>> comp2tests = new HashMap<String, HashSet<String>>();
    Vector<Test> testList = new Vector<Test>();
    
    while(testData[0].size() > 0) {
      int i=0;
      String rid = testData[i++].remove(0);
      String component= testData[i++].remove(0);
      String bucket = testData[i++].remove(0);
      String test = testData[i++].remove(0);
      String desc = testData[i++].remove(0);
      Test t = new Test(component, bucket, test, desc);
      HashSet<String> tests = req2tests.get(component);
      if(tests == null) {
        tests = new HashSet<String>();
        req2tests.put(rid, tests);
      }
      tests.add(t.getId());
      tests = comp2tests.get(component);
      if(tests == null) {
        tests = new HashSet<String>();
        comp2tests.put(component, tests);
      }
      tests.add(t.getId());
      testList.add(t);
    }
      
    // cq_id,feature_id,short,state,owner,component,type
    // R0001,F001,do CVA in MAG,StateA,Lior,MAG,REQ
    Vector<Requirement> reqList = new Vector<Requirement>();
    HashMap<String, HashSet<String>> feature2reqs = new HashMap<String, HashSet<String>>();
    
    while(reqData[0].size() > 0) {
      int i=0;
      String rid = reqData[i++].remove(0);
      String fid = reqData[i++].remove(0);
      String desc = reqData[i++].remove(0);
      String state = reqData[i++].remove(0);
      String owner = reqData[i++].remove(0);
      String component = reqData[i++].remove(0);
      String type = reqData[i++].remove(0);
    
      Requirement r = new Requirement(rid, fid, component, desc, req2tests.get(rid));
      HashSet<String> reqs = feature2reqs.get(fid);
      if(reqs == null) {
        reqs = new HashSet<String>();
        feature2reqs.put(fid, reqs);
      }
      reqs.add(r.getId());
      reqList.add(r);
    }

    // feature_id,short,state,owner
    // F001,do CVA,StateX,OwnerY
    Vector<Feature> featureList = new Vector<Feature>();
    
    while(featureData[0].size() > 0) {
      int i=0;
      String fid = featureData[i++].remove(0);
      String desc = featureData[i++].remove(0);
      String state = featureData[i++].remove(0);
      String owner = featureData[i++].remove(0);
    
      Feature f = new Feature(fid, desc, feature2reqs.get(fid));
      featureList.add(f);
    }

    JDOUtils.persist(reqList);
    JDOUtils.persist(testList);
    JDOUtils.persist(featureList);
  }

  private Vector<String>[] readMessage(ByteBuffer bb) throws IOException {    
    byte[] msg = MessageProcessor.readSimpleMessage(bb);
    
    StringReader sr = new StringReader(new String(msg));
    BufferedReader br2 = new BufferedReader(sr);
    String header = br2.readLine();
    String[] headerData = header.split(",", -1);
    
    @SuppressWarnings("unchecked")
    Vector<String>[] data = (Vector<String>[]) new Vector[headerData.length];
    
    for(int i=0; i<data.length; i++) {
      data[i] = new Vector<String>();
    }
   
    for(String line = br2.readLine(); line != null; line = br2.readLine()) {
      String[] row = line.split(",", -1);
      for(int i=0; i<data.length; i++) {
        data[i].add(row[i]);
      }
    }
    return data;
  }
  
}
