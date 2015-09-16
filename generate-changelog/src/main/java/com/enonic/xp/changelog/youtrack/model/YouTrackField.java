package com.enonic.xp.changelog.youtrack.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public final class YouTrackField
{
    private String name;

    private List<YouTrackValue> values;

    @XmlAttribute(name = "name")
    public String getName()
    {
        return this.name;
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    @XmlElement(name = "value")
    public List<YouTrackValue> getValues()
    {
        return this.values;
    }

    public void setValues( final List<YouTrackValue> values )
    {
        this.values = values;
    }

    @Override
    public String toString()
    {
        if ( values != null && !values.isEmpty() )
        {
            return values.get( 0 ).toString();
        }
        return "";
    }
}
