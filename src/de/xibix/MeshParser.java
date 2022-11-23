package de.xibix;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import de.xibix.model.Element;
import de.xibix.model.Mesh;
import de.xibix.model.Node;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MeshParser {

    private final String meshFilename;

    public MeshParser(String meshFilename) {
        this.meshFilename = meshFilename;
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("need 2 arguments: meshFile and numberOfSpots");
        }
        String meshFilename = args[0];
        String numSpots = args[1];
        try {
            long start = System.currentTimeMillis();
            int iNumSpots = Integer.parseInt(numSpots);
            MeshParser mp = new MeshParser(meshFilename);
            Mesh mesh = mp.parse();
            List<Element> viewSpots = mesh.findSpots();
            long end = System.currentTimeMillis();
            System.out.println(
                    "#nodes=" + mesh.getNodeMap().size()
                            + " #elements=" + mesh.getElementMap().size()
                            + " #viewSpots=" + viewSpots.size()
                            + " Total time = " + (end - start) + "ms");

            System.out.println("[");
            double iMax = Math.min(viewSpots.size(), iNumSpots);
            for (int i = 0; i < iMax; i++) {
                Element e = viewSpots.get(i);
                System.out.print("{element_id_ " + e.getId() + ", value: " + e.getValue() + "}");
                if (i < iMax - 1) {
                    System.out.print(",");
                }
                System.out.println();
            }
            System.out.println("]");


        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("could not parse integer argument numberOfSpots: " + numSpots, e);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error parsing meshFile " + meshFilename + ":" + e.getMessage());
        }
    }

    private Mesh parse() throws IOException {

        Mesh mesh = new Mesh();

        JsonParser parser = new JsonFactory().createParser(new File(meshFilename));
        parser.nextToken(); //start object

        int count = 0;
        while (true) {
            JsonToken token = parser.nextToken();
            if (token == null) {
                break;
            }

            if (token == JsonToken.START_ARRAY) {
                if (count == 0) {
                    parseNodes(parser, mesh);
                } else if (count == 1) {
                    parseElements(parser, mesh);
                } else if (count == 2) {
                    parseValues(parser, mesh);
                }
                count++;
            }

        }
        return mesh;
    }

    private void parseNodes(JsonParser parser, Mesh mesh) throws IOException {
        while (true) {
            JsonToken token5 = parser.nextToken();//START_OBJECT
            if (token5 != JsonToken.START_OBJECT) {
                break;
            }
            parser.nextToken(); //START_OBJECT
            mesh.addNode(parseNode(parser));
            parser.nextToken(); //end object
        }
    }

    private void parseElements(JsonParser parser, Mesh mesh) throws IOException {
        while (true) {
            JsonToken token5 = parser.nextToken();//START_OBJECT
            if (token5 != JsonToken.START_OBJECT) {
                break;
            }
            parser.nextToken(); //START_OBJECT
            mesh.addElement(parseElement(parser, mesh));
            parser.nextToken(); //end object
        }
    }


    private void parseValues(JsonParser parser, Mesh mesh) throws IOException {
        while (true) {
            JsonToken token5 = parser.nextToken();//START_OBJECT
            if (token5 != JsonToken.START_OBJECT) {
                break;
            }
            parser.nextToken(); //START_OBJECT
            int id = readInt(parser);
            double value = readDouble(parser);
            mesh.getElement(id).setValue(value);
            parser.nextToken(); //end object
        }
    }

    private Node parseNode(JsonParser parser) throws IOException {
        Node n = new Node();
        n.setId(readInt(parser));
        n.setX(readInt(parser));
        n.setY(readInt(parser));
        return n;
    }


    private int readInt(JsonParser parser) throws IOException {
        parser.nextValue();
        if (parser.getCurrentToken() == JsonToken.END_ARRAY) {
            return -1;
        }
        return parser.getIntValue();
    }

    private double readDouble(JsonParser parser) throws IOException {
        parser.nextValue();
        return parser.getDoubleValue();
    }

    private Element parseElement(JsonParser parser, Mesh mesh) throws IOException {
        Element e = new Element();
        e.setId(readInt(parser));
        parser.nextToken();//field_name
        parser.nextToken();//start_array

        while (true) {
            int nodeId = readInt(parser);
            if (nodeId == -1) {
                break;
            }
            Node n = mesh.getNode(nodeId);
            if (n != null) {
                e.addNode(n);
            } else {
                throw new IllegalStateException("node not found: " + nodeId + "element id=" + e.getId());
            }
        }
        return e;
    }

}
