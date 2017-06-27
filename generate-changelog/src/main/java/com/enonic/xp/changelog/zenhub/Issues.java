package com.enonic.xp.changelog.zenhub;

class Issues
{
    private Pipeline pipeline;

    private Integer repo_id;

    private Integer issue_number;

    private boolean is_epic;

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

    Integer getIssue_number()
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

    @Override
    public String toString()
    {
        return "ClassPojo [pipeline = "+pipeline+", repo_id = "+repo_id+", issue_number = "+issue_number+", is_epic = "+is_epic+"]";
    }
}