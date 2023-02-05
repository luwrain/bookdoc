package org.luwrain.io.filters.fb2;

import java.util.ArrayList;
import org.w3c.dom.Document;

public final class Description
{
    final TitleInfo titleInfo;
    final SrcTitleInfo srcTitleInfo;
    final DocumentInfo documentInfo;
    final PublishInfo publishInfo;
    final ArrayList<CustomInfo> customInfo = new ArrayList<>();

    Description(Document doc)
    {
        this.titleInfo = new TitleInfo(doc);
        this.srcTitleInfo = new SrcTitleInfo(doc);
        this.documentInfo = new DocumentInfo(doc);
        this.publishInfo = new PublishInfo(doc);
    }
}
