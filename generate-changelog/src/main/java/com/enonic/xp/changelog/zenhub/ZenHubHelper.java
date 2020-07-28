package com.enonic.xp.changelog.zenhub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.changelog.zenhub.issues.IssuePojo;
import com.enonic.xp.changelog.zenhub.issues.Issues;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ZenHubHelper
{

    private static OkHttpClient httpClient;

    private static ObjectMapper mapper;

    private static List<Integer> epics;

    private static HashMap<Integer, List<Integer>> issuesInEpics = new HashMap<>(  );

    private static List<Integer> getIssuesInEpic( final Integer epic, final long repoId, final String zenHubToken )
        throws IOException
    {
        List<Integer> issues = issuesInEpics.get( epic );
        if (issues == null)
        {
            System.out.println("getIssuesInEpic(): Fetching children for epic: " + epic + " in repo: " + repoId + " from ZenHub.");
            issues = getChildren( sendRequest( epic, repoId, zenHubToken ), repoId );
            issuesInEpics.put( epic, issues );
        }
        return issues;
    }

    public static List<Integer> getAllIssuesInAllEpics( long repoId, String zenHubToken )
        throws IOException
    {
        System.out.println("getAllIssuesInAllEpics()");
        List<Integer> result = new ArrayList<>();
        for ( Integer epic : getAllEpics( repoId, zenHubToken ) )
        {
            result.addAll( getIssuesInEpic( epic, repoId, zenHubToken ) );
        }
        return result;
    }

    public static HashMap<Integer, Integer> getAllIssuesInEpicsWithEpic( long repoId, String zenHubToken )
        throws IOException
    {
        System.out.println("getAllIssuesInEpicsWithEpic");
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

    private static List<Integer> getAllEpics( long repoId, String zenHubToken )
        throws IOException
    {
        if ( epics == null )
        {
            System.out.println("getAllEpics(): Fetching epics for the first time");
            ObjectMapper mapper = getObjectMapper();
            List<Integer> result = new ArrayList<>();

            String url = "https://api.zenhub.io/p1/repositories/" + repoId + "/epics?access_token=TOKEN";
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

    private static String sendRequest( final Integer epic, long repoId, String zenHubToken )
        throws IOException
    {
        String url = "https://api.zenhub.io/p1/repositories/" + repoId + "/epics/" + epic.toString() + "?access_token=TOKEN";
        Request request = new Request.Builder().url( url ).header( "X-Authentication-Token", zenHubToken ).build();
//        System.out.println( "API request: " + request.toString() );
        Response response = getHttpClient().newCall( request ).execute();
        return response.body().string();

    }

    private static List<Integer> getChildren( String data, long repoId )
        throws IOException
    {
        List<Integer> childrenList = new ArrayList<>();
        ObjectMapper mapper = getObjectMapper();
        IssuePojo json = null;
        try
        {
            json = ( mapper.readValue( data, IssuePojo.class ) );
        }
        catch ( JsonProcessingException e )
        {
            System.out.println( "JSon exception, parsing: " + data );
            throw e;
        }
        if ( json != null && json.getIssues() != null )
        {
            for ( Issues issue : json.getIssues() )
            {
                if ( issue.getRepo_id() == repoId  )
                {
                    childrenList.add( issue.getIssue_number() );
                }
            }
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
