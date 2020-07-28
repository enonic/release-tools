package com.enonic.xp.changelog.zenhub.issues;

public class Issues
{
    private Pipeline[] pipelines;

    private Pipeline pipeline;

    private long repo_id;

    private Integer issue_number;

    private boolean is_epic;

    private Estimate estimate;

    public Pipeline[] getPipelines()
    {
        return pipelines;
    }

    public void setPipelines( Pipeline[] pipelines )
    {
        this.pipelines = pipelines;
    }

    public Pipeline getPipeline ()
    {
        return pipeline;
    }

    public void setPipeline (Pipeline pipeline)
    {
        this.pipeline = pipeline;
    }

    public long getRepo_id ()
    {
        return repo_id;
    }

    public void setRepo_id (long repo_id)
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
        return "ClassPojo [pipeline = " + pipeline + ", #pipelines = " + pipelines.length + ", repo_id = "+repo_id+", issue_number = "+issue_number+", is_epic = "+is_epic+"]";
    }
}