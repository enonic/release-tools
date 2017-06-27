package com.enonic.xp.changelog.zenhub;

class Total_epic_estimates
{
    private String value;

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [value = "+value+"]";
    }
}