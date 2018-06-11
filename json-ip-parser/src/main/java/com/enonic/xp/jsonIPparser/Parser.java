package com.enonic.xp.jsonIPparser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Parser
{

    private JSONArray addresses;


    public Parser( File json )
        throws IOException
    {
        this( new String( Files.readAllBytes( json.toPath() ) ) );
    }

    public Parser( String json )
    {
        JSONObject all = new JSONObject( json );
        addresses = all.getJSONArray( "prefixes" );
    }

    public ArrayList<String> getIpAdresses( ArrayList<String> ipList, String region )
    {
        for ( int i = 0; i < addresses.length(); i++ )
        {
            JSONObject ip = addresses.getJSONObject( i );
            if ( ip.get( "region" ).toString().equals( region ) )
            {
                ipList.add( ip.get( "ip_prefix" ).toString() );
            }
        }
        return ipList;
    }

    public String createApacheConfig( ArrayList<String> ips )
    {
        StringBuilder sb = new StringBuilder( String.format( "  <Location \"/\">%n" ) );
        sb.append( String.format( "    Order deny,allow%n" ) );
        sb.append( String.format( "    Deny from all%n" ) );
        for ( String ip : ips )
        {
//            sb.append( String.format( "    Allow from %1$ %n", ip ) );
            sb.append( "    Allow from " );
            sb.append( ip );
            sb.append( System.lineSeparator() );
        }
        sb.append( String.format( "  </Location>%n" ) );
        return sb.toString();
    }

    public static void main( String... args )
        throws IOException
    {
        String filename = args[0];
        Parser parser = new Parser( new File( filename ) );
        ArrayList<String> ips = new ArrayList<>(  );
        ips = parser.getIpAdresses( ips, "eu-west-2" );
        ips = parser.getIpAdresses( ips, "ap-south-1" );
        System.out.println("Found : " + ips.size() + " IP adresses!");
        System.out.print( parser.createApacheConfig( ips ) );
    }
}
