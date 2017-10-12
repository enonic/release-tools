package com.enonic.xp.changelog.zenhub.issues;

public class Issues
{
    private Pipeline pipeline;

    private Integer repo_id;

    private Integer issue_number;

    private boolean is_epic;

    private Estimate estimate;

    public Pipeline getPipeline ()
    {
        return pipeline;
    }

    public void setPipeline (Pipeline pipeline)
    {
        this.pipeline = pipeline;
    }

    public Integer getRepo_id ()
    {
        return repo_id;
    }

    public void setRepo_id (Integer repo_id)
    {
        this.repo_id = repo_id;
    }

    public Integer getIssue_number()
    {
        return issue_number;
    }

    public void setIssue_number (Integer issue_number)
    {
        this.issue_number = issue_number;
    }

    public boolean getIs_epic ()
    {
        return is_epic;
    }

    public void setIs_epic (boolean is_epic)
    {
        this.is_epic = is_epic;
    }

    public Estimate getEstimate () { return estimate; }

    public void setEstimate (Estimate estimate) { this.estimate = estimate; }

    @Override
    public String toString()
    {
        return "ClassPojo [pipeline = "+pipeline+", repo_id = "+repo_id+", issue_number = "+issue_number+", is_epic = "+is_epic+"]";
    }
}