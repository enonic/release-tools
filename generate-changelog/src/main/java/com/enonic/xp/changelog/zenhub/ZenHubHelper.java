package com.enonic.xp.changelog.zenhub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class ZenHubHelper
{

    private static OkHttpClient httpClient;

    public static List<Integer> getIssuesInEpic( final Integer epic, final Integer repoId, final String zenHubToken )
        throws IOException
    {
        return getChildren( sendRequest( epic, repoId, zenHubToken ) );
    }

    private static String sendRequest( final Integer epic, Integer repoId, String zenHubToken )
        throws IOException
    {
        String url = "https://api.zenhub.io/p1/repositories/" + repoId.toString() + "/epics/" + epic.toString() + "?access_token=TOKEN";
        Request request = new Request.Builder().url( url ).header( "X-Authentication-Token", zenHubToken ).build();
        Response response = getHttpClient().newCall( request ).execute();
        return response.body().string();
    }

    private static List<Integer> getChildren( String data )
        throws IOException
    {
        List<Integer> childrenList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        IssuePojo json = ( mapper.readValue( data, IssuePojo.class ) );
        for ( Issues issue : json.getIssues() )
        {
            childrenList.add( issue.getIssue_number() );
        }
        return childrenList;
    }

    private static OkHttpClient getHttpClient()
    {
        if ( httpClient == null )
        {
            httpClient = new OkHttpClient();
        }
        return httpClient;
    }
}
