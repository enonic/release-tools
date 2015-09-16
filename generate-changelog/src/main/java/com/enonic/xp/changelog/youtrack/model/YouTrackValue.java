package com.enonic.xp.changelog.youtrack.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.NONE)
public final class YouTrackValue
{
    private String value;

    private String type;

    private String role;

    private YouTrackIssue reference;

    @XmlValue
    public String getValue()
    {
        return this.value;
    }

    public void setValue( final String value )
    {
        this.value = value;
    }

    @XmlAttribute(name = "type", required = false)
    public String getType()
    {
        return this.type;
    }

    public void setType( final String type )
    {
        this.type = type;
    }

    @XmlAttribute(name = "role", required = false)
    public String getRole()
    {
        return this.role;
    }

    public void setRole( final String role )
    {
        this.role = role;
    }

    public YouTrackIssue getReference()
    {
        return this.reference;
    }

    public void setReference( final YouTrackIssue reference )
    {
        this.reference = reference;
    }

    public boolean isReference()
    {
        return ( this.type != null ) && ( this.type != null );
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
