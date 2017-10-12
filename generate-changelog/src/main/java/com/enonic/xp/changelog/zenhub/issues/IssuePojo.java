package com.enonic.xp.changelog.zenhub.issues;

public class IssuePojo
{
    private Pipeline pipeline;

    private Issues[] issues;

    private Total_epic_estimates total_epic_estimates;

    public Pipeline getPipeline()
    {
        return pipeline;
    }

    public void setPipeline( Pipeline pipeline )
    {
        this.pipeline = pipeline;
    }

    public Issues[] getIssues()
    {
        return issues;
    }

    public void setIssues( Issues[] issues )
    {
        this.issues = issues;
    }

    public Total_epic_estimates getTotal_epic_estimates()
    {
        return total_epic_estimates;
    }

    public void setTotal_epic_estimates( Total_epic_estimates total_epic_estimates )
    {
        this.total_epic_estimates = total_epic_estimates;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [pipeline = " + pipeline + ", #issues = " + issues.length + ", total_epic_estimates = " + total_epic_estimates + "]";
    }
}
