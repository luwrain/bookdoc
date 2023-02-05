
package org.luwrain.io.filters.fb2;

import java.util.ArrayList;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sun.istack.internal.NotNull;

//http://www.fictionbook.org/index.php/Элемент_annotation
public class Annotation extends IdElement
{
    public String lang = "";
    public final ArrayList<Element> elements = new ArrayList<>();

    Annotation(Node node) {
        super(node);
        final NamedNodeMap map = node.getAttributes();
        for (int index = 0; index < map.getLength(); index++)
	{
            Node attr = map.item(index);
            if (attr.getNodeName().equals("xml:lang")) {
                lang = attr.getNodeValue();
            }
        }
        final NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++)
	{
            final Node paragraph = nodeList.item(i);
            switch (paragraph.getNodeName()) {
	    case "p":
		elements.add(new P(paragraph));
		break;
	    case "poem":
		elements.add(new Poem(paragraph));
		break;
	    case "cite":
		elements.add(new Cite(paragraph));
		break;
	    case "subtitle":
		elements.add(new Subtitle(paragraph));
		break;
	    case "empty-line":
		elements.add(new EmptyLine());
		break;
	    case "table":
		elements.add(new Table());
		break;
            }
        }
    }
}
