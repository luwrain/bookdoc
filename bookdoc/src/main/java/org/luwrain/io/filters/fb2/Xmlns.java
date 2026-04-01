package org.luwrain.io.filters.fb2;

import lombok.*;

import org.w3c.dom.Node;

@Data
@NoArgsConstructor
public class Xmlns
{
    protected String name;
    protected String value;

    Xmlns(Node node)
    {
        this.name = node.getNodeName();
        this.value = node.getNodeValue();
    }
}
