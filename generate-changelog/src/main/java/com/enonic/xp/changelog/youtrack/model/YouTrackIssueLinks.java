package com.enonic.xp.changelog.youtrack.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "issueLinks")
@XmlAccessorType(XmlAccessType.NONE)
public final class YouTrackIssueLinks
{
    private List<YouTrackIssueLink> issueLinks;

    @XmlElement(name = "issueLink")
    public List<YouTrackIssueLink> getIssueLinks()
    {
        return issueLinks;
    }

    public void setIssueLinks( final List<YouTrackIssueLink> issueLinks )
    {
        this.issueLinks = issueLinks;
    }
}
