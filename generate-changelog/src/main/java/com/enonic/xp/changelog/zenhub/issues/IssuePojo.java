package com.enonic.xp.changelog.zenhub.issues;

public class IssuePojo
{
    private Pipeline[] pipelines;

    private Pipeline pipeline;

    private Issues[] issues;

    private Integer issue_number;

    private Integer repo_id;

    private Total_epic_estimates total_epic_estimates;

    private String message;

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

    public Integer getIssue_number()
    {
        return issue_number;
    }

    public void setIssue_number (Integer issue_number)
    {
        this.issue_number = issue_number;
    }

    public Integer getRepo_id()
    {
        return repo_id;
    }

    public void setRepo_id (Integer repo_id)
    {
        this.repo_id = repo_id;
    }

    public Pipeline[] getPipelines()
    {
        return pipelines;
    }

    public void setPipelines( Pipeline[] pipelines )
    {
        this.pipelines = pipelines;
    }

    public Total_epic_estimates getTotal_epic_estimates()
    {
        return total_epic_estimates;
    }

    public void setTotal_epic_estimates( Total_epic_estimates total_epic_estimates )
    {
        this.total_epic_estimates = total_epic_estimates;
    }

    public String getMessage() { return message; }

    public void setMessage( String message ) {this.message = message; }

    @Override
    public String toString()
    {
        return "ClassPojo [pipeline = " + pipeline + ", #pipelines = " + pipelines.length + ", issue_number = " + issue_number + ", #issues = " + issues.length + ", total_epic_estimates = " + total_epic_estimates + "]";
    }
}
