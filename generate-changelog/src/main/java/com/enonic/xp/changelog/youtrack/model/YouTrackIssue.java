package com.enonic.xp.changelog.youtrack.model;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "issue")
@XmlAccessorType(XmlAccessType.NONE)
public final class YouTrackIssue
{
    public static final String SUMMARY_FIELD_NAME = "summary";

    public static final String TYPE_FIELD_NAME = "Type";

    public static final String CHANGE_LOG_FIELD_NAME = "Change Log";

    public static final String EPIC_TYPE = "Epic";

    public static final String FEATURE_TYPE = "Feature";

    public static final String IMPROVEMENT_TYPE = "Improvement";

    public static final String BUG_TYPE = "Bug";

    private String id;

    private List<YouTrackField> fields;

    private YouTrackIssue parent;

    private List<YouTrackIssue> children;

    public YouTrackIssue()
    {
        children = new LinkedList<>();
    }

    @XmlAttribute(name = "id")
    public String getId()
    {
        return this.id;
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    @XmlElement(name = "field")
    public List<YouTrackField> getFields()
    {
        return this.fields;
    }

    public void setFields( final List<YouTrackField> fields )
    {
        this.fields = fields;
    }

    public YouTrackField getField( String fieldName )
    {
        if ( fields != null )
        {
            for ( YouTrackField youTrackField : fields )
            {
                if ( youTrackField.getName().equals( fieldName ) )
                {
                    return youTrackField;
                }
            }
        }
        return null;
    }

    public YouTrackIssue getParent()
    {
        return parent;
    }

    public void setParent( final YouTrackIssue parent )
    {
        if ( this.parent != null )
        {
            this.parent.children.remove( this );
        }
        if ( parent != null )
        {
            parent.children.add( this );
        }

        this.parent = parent;
    }

    public List<YouTrackIssue> getChildren()
    {
        return children;
    }

    public String getType()
    {
        return getField( TYPE_FIELD_NAME ).toString();
    }

    public String getSummary()
    {
        return getField( SUMMARY_FIELD_NAME ).toString().trim();
    }


    public boolean isEpic()
    {
        return EPIC_TYPE.equals( getType() );
    }

    public boolean mustBeLogged()
    {
        final YouTrackField changeLogField = getField( CHANGE_LOG_FIELD_NAME );
        return "True".equals( changeLogField.toString() );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final YouTrackIssue that = (YouTrackIssue) o;

        return id.equals( that.id );

    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}
