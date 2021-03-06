package ddwu.mobile.finalproject.ma01_20180988;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeXmlParser {
    public enum TagType { NONE, NAME, TYPE, IMAGE, ING, MANUAL, MANUAL_IMG };

    final static String TAG_ROW = "row";
    final static String TAG_NAME = "RCP_NM";
    final static  String TAG_TYPE = "RCP_PAT2";
    final static  String TAG_IMAGE = "ATT_FILE_NO_MAIN";
    final static  String TAG_ING = "RCP_PARTS_DTLS";
    final static  String TAG_MANUAL = "MANUAL";
    final static  String TAG_MANUAL_IMG = "MANUAL_IMG";
    int imageStepNum, conStepNum;

    public RecipeXmlParser() {
    }

    public ArrayList<Recipe> parse(String xml) {
        ArrayList<Recipe> resultList = new ArrayList<>();
        Recipe dto = null;
        HashMap<Integer, String> mContents = new HashMap<>();
        HashMap<Integer, String> mImageLinks = new HashMap<>();

        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.END_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals(TAG_ROW)) {
                            dto = new Recipe();
                        } else if (parser.getName().equals(TAG_NAME)) {
                            if (dto != null) tagType = TagType.NAME;
                        } else if (parser.getName().equals(TAG_TYPE)) {
                            if (dto != null) tagType = TagType.TYPE;
                        } else if (parser.getName().equals(TAG_IMAGE)) {
                            if (dto != null) tagType = TagType.IMAGE;
                        } else if (parser.getName().equals(TAG_ING)) {
                            if (dto != null) tagType = TagType.ING;
                        } else if (parser.getName().contains(TAG_MANUAL_IMG)) {
                            if (dto != null) tagType = TagType.MANUAL_IMG;
                            imageStepNum = Integer.parseInt(parser.getName().substring(10));
                        } else if (parser.getName().contains(TAG_MANUAL)) {
                            if (dto != null) tagType = TagType.MANUAL;
                            conStepNum = Integer.parseInt(parser.getName().substring(6));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (parser.getName().equals(TAG_ROW)) {
                            Manual m;
                            for (int i = 1; i <= mContents.size(); i++) {
                                m = new Manual();
                                if (mImageLinks.containsKey(i)) {
                                    m.setImageLink(mImageLinks.get(i));
                                }
                                m.setContent(mContents.get(i));
                                m.setStep(i);
                                dto.getManuals().add(m);
                            }
                            resultList.add(dto);
                            dto = null;
                            mContents.clear();
                            mImageLinks.clear();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType) {
                            case NAME:
                                dto.setName(parser.getText());
                                break;
                            case TYPE:
                                dto.setType(parser.getText());
                                break;
                            case IMAGE:
                                dto.setImageLink(parser.getText());
                                break;
                            case ING:
                                dto.getIngredients().addAll(separateIng(parser.getText()));
                                break;
                            case MANUAL:
                                String mTemp = parser.getText();
                                if (mTemp.length() != 5) {
                                    if (mTemp.charAt(mTemp.length()-1) == 'a'  || mTemp.charAt(mTemp.length()-1) == 'b' || mTemp.charAt(mTemp.length()-1) == 'c' || mTemp.charAt(mTemp.length()-1) == 'd') {
                                        mTemp = mTemp.substring(0, mTemp.length() - 1);
                                    }
                                    mTemp = mTemp.substring(3);
                                    mTemp = mTemp.replace("\n", "");
                                    mContents.put(conStepNum, mTemp);
                                }
                                break;
                            case MANUAL_IMG:
                                if (parser.getText().length() != 5) {
                                    mImageLinks.put(imageStepNum, parser.getText());
                                }
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
    
    public List<String> separateIng(@NotNull String ingText) {
        List<String> ingredients = new ArrayList<>();

        if (ingText.contains("\n")) {
            String[] temp1 = ingText.split("\n");
            for (int  i = 1; i < temp1.length; i += 2) {
                String[] temp2 = temp1[i].split(",");
                for (String s :  temp2) {
                    ingredients.add(s.trim());
                }
            }
        }
        else {
            String[] temp1 = ingText.split(",");
            for (String s :  temp1) {
                ingredients.add(s.trim());
            }
        }


        return ingredients;
    }
}
