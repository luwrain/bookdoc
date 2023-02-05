package org.luwrain.io.filters.fb2;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

//http://www.fictionbook.org/index.php/Элемент_body
public final class Body
{
    protected String lang;
    protected String name;
    protected Title title;
    protected Image image;
    public final ArrayList<Section> sections = new ArrayList<>();
    protected final ArrayList<Epigraph> epigraphs = new ArrayList<>();

    Body(Node body)
    {
        final NamedNodeMap attrs = body.getAttributes();
        for (int index = 0; index < attrs.getLength(); index++)
	{
            final Node attr = attrs.item(index);
            if (attr.getNodeName().equals("name"))
                name = attr.getNodeValue();
            if (attr.getNodeName().equals("xml:lang")) 
                lang = attr.getNodeValue();
        }
        final NodeList map = body.getChildNodes();
        for (int index = 0; index < map.getLength(); index++)
	{
            final Node node = map.item(index);
            switch (node.getNodeName())
	    {
                case "section":
                    sections.add(new Section(node));
                    break;
                case "title":
                    title = new Title(node);
                    break;
                case "name":
                    name = node.getTextContent();
                    break;
	    case "image": 
                    image = new Image(node);
                    break;
                case "epigraph":
                    epigraphs.add(new Epigraph(node));
                    break;
            }
        }
    }

    @Nullable
    public Title getTitle() {
        return title;
    }

    @Nullable
    public ArrayList<Epigraph> getEpigraphs() {
        return epigraphs;
    }

    @Nullable
    public Image getImage() {
        return image;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nullable
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
