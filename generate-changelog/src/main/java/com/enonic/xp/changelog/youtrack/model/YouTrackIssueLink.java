package com.enonic.xp.changelog.youtrack.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public final class YouTrackIssueLink
{
    private String typeName;

    private String typeInward;

    private String typeOutward;

    private String target;

    private String source;

    @XmlAttribute(name = "typeName", required = false)
    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName( final String typeName )
    {
        this.typeName = typeName;
    }

    @XmlAttribute(name = "typeInward", required = false)
    public String getTypeInward()
    {
        return typeInward;
    }

    public void setTypeInward( final String typeInward )
    {
        this.typeInward = typeInward;
    }

    @XmlAttribute(name = "typeOutward", required = false)
    public String getTypeOutward()
    {
        return typeOutward;
    }

    public void setTypeOutward( final String typeOutward )
    {
        this.typeOutward = typeOutward;
    }

    @XmlAttribute(name = "target", required = false)
    public String getTarget()
    {
        return target;
    }

    public void setTarget( final String target )
    {
        this.target = target;
    }

    @XmlAttribute(name = "source", required = false)
    public String getSource()
    {
        return source;
    }

    public void setSource( final String source )
    {
        this.source = source;
    }
}
