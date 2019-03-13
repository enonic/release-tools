package com.enonic.xp.changelog.zenhub.issues;

class Pipeline
{
    private String name;

    private String workspace_id;

    private String pipeline_id;

    public String getName ()
    {
        return name;
    }

    public void setName ( final String name)
    {
        this.name = name;
    }

    public String getWorkspace_id()
    {
        return workspace_id;
    }

    public void setWorkspace_id( final String workspace_id )
    {
        this.workspace_id = workspace_id;
    }

    public String getPipeline_id()
    {
        return pipeline_id;
    }

    public void setPipeline_id( final String pipeline_id )
    {
        this.pipeline_id = pipeline_id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [name = "+name+"]";
    }
}