package lior.lview.message.soap;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import lior.lview.data.Feature;
import lior.lview.data.Requirement;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FeaturesAllSaxHandler extends DefaultHandler
{
  public Vector<Feature> getFeatures() {
    return featureList;
  }

  public Vector<Requirement> getRequirements() {
    return reqList;
  }

  int lastIndent;
  int numElems;

  int currentReccordType = -1;
  int currentElem = -1;
  String[] strings = new String[10];
  Vector<Feature> featureList = new Vector<Feature>();
  Vector<Requirement> reqList = new Vector<Requirement>();

  enum RecordType {
    Feature, Requirement
  }

  enum FeatureMeta {
    FeatureID, FeatureName, LAST
  }

  @Override
  public void startDocument() throws SAXException {
    super.startDocument();

  }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();
  }

  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException
  {
    if (qName.equals(RecordType.Feature.name()))
      currentReccordType = RecordType.Feature.ordinal();
    else if (qName.equals(RecordType.Requirement.name()))
      currentReccordType = RecordType.Requirement.ordinal();
    else if (qName.equals(FeatureMeta.FeatureID.name()))
      currentElem = FeatureMeta.FeatureID.ordinal();
    else if (qName.equals(FeatureMeta.FeatureName.name()))
      currentElem = FeatureMeta.FeatureName.ordinal();
    else
      currentElem = -1;
  }

  public void endElement(String uri, String localName, String qName)
      throws SAXException
  {
    if (qName.equals(RecordType.Requirement.name()))
      currentReccordType = RecordType.Feature.ordinal();
    if (qName.equals(RecordType.Feature.name())) {
      currentReccordType = -1;

      String fid = strings[FeatureMeta.FeatureID.ordinal()];
      String fname = strings[FeatureMeta.FeatureName.ordinal()];
      Set<String> reqNames = new HashSet<String>();
      Feature f = new Feature(fid, fname, reqNames);
      featureList.add(f);
    }
    currentElem = -1;
  }

  public void characters(char ch[], int start, int length) throws SAXException {
    String text = new String(ch, start, length);
    if (currentElem >= 0) {
      strings[currentElem] = text;
    }
  }
}