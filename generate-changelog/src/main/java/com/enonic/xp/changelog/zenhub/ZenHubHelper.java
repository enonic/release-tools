package com.enonic.xp.changelog.zenhub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import com.enonic.xp.changelog.zenhub.issues.IssuePojo;
import com.enonic.xp.changelog.zenhub.issues.Issues;

public class ZenHubHelper
{

    private static OkHttpClient httpClient;

    private static ObjectMapper mapper;

    private static List<Integer> epics;

    private static List<Integer> getIssuesInEpic( final Integer epic, final Integer repoId, final String zenHubToken )
        throws IOException
    {
        return getChildren( sendRequest( epic, repoId, zenHubToken ) );
    }

    public static List<Integer> getAllIssuesInAllEpics( Integer repoId, String zenHubToken )
        throws IOException
    {
        List<Integer> result = new ArrayList<>();
        for ( Integer epic : getAllEpics( repoId, zenHubToken ) )
        {
            result.addAll( getIssuesInEpic( epic, repoId, zenHubToken ) );
        }
        return result;
    }

    public static HashMap<Integer, Integer> getAllIssuesInEpicsWithEpic( Integer repoId, String zenHubToken )
        throws IOException
    {
        HashMap<Integer, Integer> result = new HashMap<>();
        List<Integer> epics = getAllEpics( repoId, zenHubToken );
        for ( Integer epic : epics )
        {
            List<Integer> children = getIssuesInEpic( epic, repoId, zenHubToken );
            for ( Integer child : children )
            {
                result.put( child, epic );
            }
        }
        return result;
    }

    private static List<Integer> getAllEpics( Integer repoId, String zenHubToken )
        throws IOException
    {
        if ( epics == null )
        {
            ObjectMapper mapper = getObjectMapper();
            List<Integer> result = new ArrayList<>();

            String url = "https://api.zenhub.io/p1/repositories/" + repoId.toString() + "/epics?access_token=TOKEN";
            Request request = new Request.Builder().url( url ).header( "X-Authentication-Token", zenHubToken ).build();
            Response response = getHttpClient().newCall( request ).execute();

            JsonNode root = mapper.readTree( response.body().string() );
            for ( JsonNode epicNode : root.get( "epic_issues" ) )
            {
                result.add( epicNode.get( "issue_number" ).asInt() );
            }
            epics = result;
        }
        return epics;
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
        ObjectMapper mapper = getObjectMapper();
        IssuePojo json = ( mapper.readValue( data, IssuePojo.class ) );
        for ( Issues issue : json.getIssues() )
        {
            childrenList.add( issue.getIssue_number() );
        }
        return childrenList;
    }

    private static ObjectMapper getObjectMapper()
    {
        if ( mapper == null )
        {
            mapper = new ObjectMapper();
        }
        return mapper;
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
