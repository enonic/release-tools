package com.enonic.xp.changelog.zenhub.issues;

public class Estimate
{
    private Integer value;

    public Integer getValue ()
    {
        return value;
    }

    public void setValue (Integer name)
    {
        this.value = name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [value = "+value+"]";
    }
}
