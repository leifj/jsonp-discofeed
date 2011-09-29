

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author stefan
 */
public final class MetaData {

    private Document doc;
    private List<String> entityIds;
    private Map<String, Map> idpMap;
    private boolean initialized = false;
    private String discoJson;
    private static final String LF = System.getProperty("line.separator");

    public MetaData(File xmlFile) {
        byte[] xmlData = Utils.readBinaryFile(xmlFile);
        start(xmlData);
    }

    public void start(byte[] xmlData) {
        try {
            InputStream is = new ByteArrayInputStream(xmlData);

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(is);
            doc.getDocumentElement().normalize();
            parseXML();
            discoJson = buildJson();

        } catch (Exception ex) {
            Logger.getLogger(MetaData.class.getName()).log(Level.WARNING, null, ex);
        }

    }

    private void parseXML() {
        try {
            entityIds = new ArrayList<String>();
            idpMap = new HashMap<String, Map>();
            Map<String, String> idpDisplName;

            String[] elementDescID = new String[]{"EntityDescriptor", "md:EntityDescriptor"};
            for (String elNodeID : elementDescID) {

                NodeList entityNodes = doc.getElementsByTagName(elNodeID);
                for (int i = 0; i < entityNodes.getLength(); i++) {
                    Node entityNode = entityNodes.item(i);
                    NodeList entityElements = entityNode.getChildNodes();

                    if (entityElements.item(1).getNodeName().indexOf("IDPSSODescriptor") != -1) {
                        String entityID = entityNode.getAttributes().getNamedItem("entityID").getTextContent();
                        entityIds.add(entityID);
                        idpDisplName = new HashMap<String, String>();
                        idpMap.put(entityID, idpDisplName);
                    }
                }
            }

            String[] orgDispID = new String[]{"OrganizationDisplayName", "md:OrganizationDisplayName"};
            for (String orgNodeID : orgDispID) {

                NodeList orgNodes = doc.getElementsByTagName(orgNodeID);
                for (int i = 0; i < orgNodes.getLength(); i++) {
                    Node orgNode = orgNodes.item(i);
                    Node p = orgNode.getParentNode();

                    while (p != null && p.getNodeName().indexOf("EntityDescriptor") == -1) {
                        p = p.getParentNode();
                    }

                    if (p != null) {
                        String entityID = p.getAttributes().getNamedItem("entityID").getTextContent();
                        if (entityIds.contains(entityID)) {
                            String lang = orgNode.getAttributes().getNamedItem("xml:lang").getTextContent();
                            String orgDisp = orgNode.getTextContent();
                            //Store Idp Name
                            idpDisplName = idpMap.get(entityID);
                            idpDisplName.put(lang, orgDisp);
                            //Store map from Idp name to entity ID
                        }
                    }
                }
            }
            initialized = true;
        } catch (NullPointerException ex) {
            initialized = false;
        }
    }

    private String buildJson() {
        StringBuilder b = new StringBuilder();
        if (!initialized || entityIds.isEmpty()) {
            b.append("[]");
            return b.toString();
        }
        b.append("[").append(LF);

        int i = 0, size = entityIds.size();
        for (String entityID : entityIds) {
            b.append("{\"entityID\": \"");
            b.append(entityID).append("\",").append(LF);
            b.append(" \"DisplayNames\": [").append(LF);
            if (idpMap.containsKey(entityID)) {
                Map<String, String> dispNames = idpMap.get(entityID);
                List<String> orderedLang = getOrderedLangList(dispNames);
                int orgIdx = 0, orgSize = orderedLang.size();
                for (String lang : orderedLang) {
                    b.append("  {\"value\": \"");
                    b.append(dispNames.get(lang)).append("\",").append(LF);
                    b.append("   \"lang\": \"");
                    b.append(lang).append("\"}");
                    if (++orgIdx < orgSize) {
                        b.append(",");
                    }
                    b.append(LF);
                }
                b.append(" ]}");
                if (++i < size) {
                    b.append(",");
                }
                b.append(LF);
            }
        }
        b.append("]");
        return b.toString();
    }

    private List<String> getOrderedLangList(Map<String, String> dispNames) {
        List<String> langList = new ArrayList<String>();
        Set<String> keySet = dispNames.keySet();
        //Look for english first
        for (String lang : keySet) {
            if (lang.equalsIgnoreCase("en")) {
                langList.add(lang);
            }
        }
        //Add rest
        for (String lang : keySet) {
            if (!lang.equalsIgnoreCase("en")) {
                langList.add(lang);
            }
        }
        return langList;

    }

    public List<String> getEntityIds() {
        return entityIds;
    }

    public Map<String, Map> getIdpMap() {
        return idpMap;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public String getDiscoJson() {
        return discoJson;
    }
}
