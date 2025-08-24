

//http://www.fictionbook.org/index.php/Элемент_author

package org.luwrain.io.filters.fb2;

import java.util.*;
import lombok.*;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static java.util.Objects.*;

@Data
@NoArgsConstructor
public class Person
{
    protected String id;
    protected String firstName, middleName, lastName, nickname;
    protected ArrayList<String> homePages;
    protected ArrayList<String> emails;

    Person(Node node)
    {
        var nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++)
	{
            Node author = nodeList.item(i);
            switch (author.getNodeName())
	    {
	    case "id":
		id = author.getTextContent();
		break;
	    case "home-page":
		if (homePages == null)
		    homePages = new ArrayList<>();
		homePages.add(author.getTextContent());
		break;
	    case "email":
		if (emails == null)
		    emails = new ArrayList<>();
		emails.add(author.getTextContent());
		break;
	    case "nickname":
		nickname = author.getTextContent();
		break;
	    case "first-name":
		firstName = author.getTextContent();
		break;
	    case "middle-name":
		middleName = author.getTextContent();
		break;
	    case "last-name":
		lastName = author.getTextContent();
		break;
            }
        }
    }

    public String getFullName()
    {
        return firstName != null ? firstName + " " : ""
	+ middleName != null ? middleName + " " : ""
	+ requireNonNullElse(lastName, "");
    }
}
