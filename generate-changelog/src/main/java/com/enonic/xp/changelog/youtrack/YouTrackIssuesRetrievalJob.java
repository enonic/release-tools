package com.enonic.xp.changelog.youtrack;

import java.io.IOException;
import java.io.StringReader;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import com.enonic.xp.changelog.git.model.GitCommit;
import com.enonic.xp.changelog.youtrack.model.YouTrackIssue;
import com.enonic.xp.changelog.youtrack.model.YouTrackIssueLink;
import com.enonic.xp.changelog.youtrack.model.YouTrackIssueLinks;

public class YouTrackIssuesRetrievalJob
{
    private static final Logger LOGGER = LoggerFactory.getLogger( YouTrackIssuesRetrievalJob.class );

    private static final String LOGIN = "sys_rest";

    private static final String PASSWORD = "p2uqufU7";

    private static final String SUBTASK_TYPE_NAME = "Subtask";

    private static final String SUBTASK_ROLE_NAME = "subtask of";

    private final Collection<GitCommit> gitCommits;

    private final Predicate<YouTrackIssue> filter;

    private OkHttpClient okHttpClient;

    private Map<String, YouTrackIssue> youTrackIssueMap;

    public YouTrackIssuesRetrievalJob( final Collection<GitCommit> gitCommits, final Predicate<YouTrackIssue> filter )
    {
        this.gitCommits = gitCommits;
        this.filter = filter;
        this.youTrackIssueMap = new HashMap<>();
    }

    public Set<YouTrackIssue> run()
        throws Exception
    {
        LOGGER.info( "Retrieving YouTrack Issues..." );

        createOkHttpClient();
        login();

        final Set<YouTrackIssue> filteredYouTrackIssues = retrieveFilteredYouTrackIssues();

        LOGGER.info( filteredYouTrackIssues.size() + " Filtered YouTrack Issues retrieved." );
        return filteredYouTrackIssues;
    }

    private void createOkHttpClient()
    {
        System.setProperty( "jsse.enableSNIExtension", "false" );
        okHttpClient = new OkHttpClient();
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy( CookiePolicy.ACCEPT_ALL );
        okHttpClient.setCookieHandler( cookieManager );
    }

    private void login()
        throws IOException
    {
        final RequestBody requestBody = RequestBody.create( MediaType.parse( "application/x-www-form-urlencoded; charset=utf-8" ),
                                                            "login=" + LOGIN + "&password=" + PASSWORD );
        sendRequest( "https://youtrack.enonic.net/rest/user/login?" + LOGIN + "&" + PASSWORD, requestBody );
    }

    private Set<YouTrackIssue> retrieveFilteredYouTrackIssues()
        throws Exception
    {
        return gitCommits.stream().
            map( gitCommit -> this.retrieveYouTrackIssue( gitCommit.getYouTrackId(), gitCommit.getShortMessage() ) ).
            filter( Objects::nonNull ).
            filter( filter ).
            collect( Collectors.toSet() );
    }

    private YouTrackIssue retrieveYouTrackIssue( final String youTrackId, final String shortMessage )
    {
        try
        {
            YouTrackIssue youTrackIssue = youTrackIssueMap.get( youTrackId );

            if ( youTrackIssue == null )
            {
                final String responseBody = sendRequest( "https://youtrack.enonic.net/rest/issue/" + youTrackId, null );
                youTrackIssue = parseXml( YouTrackIssue.class, responseBody );
                youTrackIssueMap.put( youTrackId, youTrackIssue );

                final YouTrackIssue parentYouTrackIssue = retrieveParentYouTrackIssue( youTrackId );
                youTrackIssue.setParent( parentYouTrackIssue );
            }

            return youTrackIssue;
        }
        catch ( Exception e )
        {
            LOGGER.error( "Could not retrieve YouTrack issue \"" + youTrackId + "\" (" + shortMessage + ")" );
        }
        return null;
    }

    private YouTrackIssue retrieveParentYouTrackIssue( final String youTrackId )
        throws Exception
    {
        //Retrieves the links to this Issue
        final String responseBody = sendRequest( "https://youtrack.enonic.net/rest/issue/" + youTrackId + "/link", null );
        final YouTrackIssueLinks youTrackIssueLinks = parseXml( YouTrackIssueLinks.class, responseBody );

        //Retrieves the parent ID from these links if there is one
        final String parentYouTrackId = retrieveParentYouTrackId( youTrackId, youTrackIssueLinks );

        if ( parentYouTrackId != null )
        {
            return retrieveYouTrackIssue( parentYouTrackId, "" );
        }

        return null;
    }

    private String retrieveParentYouTrackId( final String youTrackId, final YouTrackIssueLinks youTrackIssueLinks )
    {
        if ( youTrackIssueLinks.getIssueLinks() != null )
        {
            //For each link to this Issue
            for ( YouTrackIssueLink youTrackIssueLink : youTrackIssueLinks.getIssueLinks() )
            {
                //If the link is a Subtask link and that this issue is a subtask
                if ( SUBTASK_TYPE_NAME.equals( youTrackIssueLink.getTypeName() ) )
                {
                    if ( youTrackIssueLink.getSource().equals( youTrackId ) &&
                        SUBTASK_ROLE_NAME.equals( youTrackIssueLink.getTypeOutward() ) )
                    {
                        return youTrackIssueLink.getTarget();
                    }
                    if ( youTrackIssueLink.getTarget().equals( youTrackId ) &&
                        SUBTASK_ROLE_NAME.equals( youTrackIssueLink.getTypeInward() ) )
                    {
                        return youTrackIssueLink.getSource();
                    }
                }
            }
        }

        return null;
    }

    private <T> T parseXml( final Class<T> type, final String content )
        throws Exception
    {
        final JAXBContext context = JAXBContext.newInstance( type );
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal( new StringReader( content ) );
    }

    private String sendRequest( final String url, final RequestBody post )
        throws IOException
    {
        //Builds the request
        Request.Builder requestBuilder = new Request.Builder().url( url );
        if ( post != null )
        {
            requestBuilder.post( post );
        }
        Request request = requestBuilder.build();

        //Executes the request
        Response response = okHttpClient.newCall( request ).execute();
        LOGGER.debug( "Message: " + response.message() );
        final String responseBody = response.body().string();
        LOGGER.debug( "ResponseBody: " + responseBody );
        return responseBody;
    }
}
