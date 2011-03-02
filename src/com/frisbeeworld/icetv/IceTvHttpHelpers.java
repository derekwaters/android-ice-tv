package com.frisbeeworld.icetv;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class IceTvHttpHelpers
{
	private static final String 	CONTENT_TYPE = "Content-Type";
	private static final String 	MIME_FORM_ENCODED = "application/x-www-form-urlencoded";

	public static void PostRequest(String url, 
								   Map<String, String> params, 
								   String userName, 
								   String password,
								   Handler messageHandler)
	{
		HttpPost				postMethod = new HttpPost(url);
		List<NameValuePair>		nvps = null;
		DefaultHttpClient		client = new DefaultHttpClient();
				
		if ((userName != null) && (userName.length() > 0) && (password != null) && (password.length() > 0))
		{
			client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
		}

		final Map<String, String> sendHeaders = new HashMap<String, String>();
		sendHeaders.put(CONTENT_TYPE, MIME_FORM_ENCODED);
		
		client.addRequestInterceptor(new HttpRequestInterceptor()
		{
			@Override
			public void process(HttpRequest request, HttpContext context) throws HttpException, IOException
			{
               for (String key : sendHeaders.keySet())
               {
                  if (!request.containsHeader(key))
                  {
                     request.addHeader(key, sendHeaders.get(key));
                  }
               }
			}
		});
		
		if ((params != null) && (params.size() > 0))
		{
			nvps = new ArrayList<NameValuePair>();
			for (String key : params.keySet())
			{
				nvps.add(new BasicNameValuePair(key, params.get(key)));
			}
		}
		if (nvps != null)
		{
			try
			{
				postMethod.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		ExecutePostRequest(client, postMethod, GetResponseHandlerInstance(messageHandler));
	}
		
	private static void ExecutePostRequest(HttpClient client, HttpRequestBase method, ResponseHandler<String> responseHandler)
	{
		BasicHttpResponse	errorResponse = new BasicHttpResponse(new ProtocolVersion("HTTP_ERROR", 1, 1), 500, "ERROR");
		
		try
		{
			client.execute(method, responseHandler);
		}
		catch (Exception e)
		{
			errorResponse.setReasonPhrase(e.getMessage());
			try
			{
				responseHandler.handleResponse(errorResponse);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public static String GetUrl(String url) throws IceTvException
	{
		URL 		getURL;
		InputStream	theStream;
		String		returnVal = null;
		
		try
		{
			getURL = new URL(url);
			HttpURLConnection newConnection = (HttpURLConnection) getURL.openConnection();
	
			theStream = newConnection.getInputStream();
			if (theStream != null)
			{
				returnVal = InputStreamToString(theStream); 
			}
		}
		catch (MalformedURLException mue)
		{
			throw new IceTvException(IceTvException.Cause_BadWebAddress, mue.getMessage());
		}
		catch (UnknownHostException uhe)
		{
			throw new IceTvException(IceTvException.Cause_FailedToConnectToWebsite, uhe.getMessage());
		}
		catch (FileNotFoundException fnfe)
		{
			throw new IceTvException(IceTvException.Cause_FailedToReadWebsite, fnfe.getMessage());
		}
		catch(IOException e)
		{
			throw new IceTvException(IceTvException.Cause_FailedToReadWebsite, e.getMessage());
		}
		
		return returnVal;
	}
	
	public static String InputStreamToString(InputStream theInput)
	{
		BufferedInputStream	inputBuffer = new BufferedInputStream(theInput);
		int					byteRead = -1;
		StringBuilder		builder = new StringBuilder();
	
		try
		{
			while ((byteRead = inputBuffer.read()) != -1)
			{
				builder.append((char)byteRead);
	        }
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return builder.toString();
	}
	
	public static ResponseHandler<String> GetResponseHandlerInstance(final Handler handler)
	{
		final ResponseHandler<String> responseHandler = new ResponseHandler<String>()
		{
			@Override
			public String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException
			{
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				StatusLine status = response.getStatusLine();
				HttpEntity entity = response.getEntity();
				String result = null;
				if (entity != null)
				{
					try
					{
						result = InputStreamToString(entity.getContent());
						bundle.putString("RESPONSE", result);
						message.setData(bundle);
						handler.sendMessage(message);
					}
					catch (IOException e)
					{
						bundle.putString("RESPONSE", "Error - " + e.getMessage());
						message.setData(bundle);
						handler.sendMessage(message);
					}
				}
				else
				{
					bundle.putString("RESPONSE", "Error - " + response.getStatusLine().getReasonPhrase());
					message.setData(bundle);
					handler.sendMessage(message);
				}
				return result;
			}
		};
		
		return responseHandler;
	}
}
