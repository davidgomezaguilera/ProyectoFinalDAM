package com.example.tourguide.controladores;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase implementa la traducción JSON a un servidor web
 */
public class JSONParser
{
    public static final String TAG = "JSONParser";

    /**
     * Constructor de la clase
     */
    public JSONParser() {}

    /**
     * Conecta con el servidor y devuelve un JSONArray con los datos obtenidos
     * @param direccionurl URL del servidor
     * @param parametros Parámetros de la consulta
     * @return JSONArray con los resultados de la consulta al servidor
     */
    public JSONArray getJSONArrayFromUrl(String direccionurl, HashMap<String, String> parametros) throws JSONException, IOException
    {
        URL url;
        StringBuilder response = new StringBuilder();
        url = new URL(buildURL(direccionurl, parametros));

        HttpURLConnection urlConnection  = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(15000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-type", "application/json");

        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK)
        {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

        }
        urlConnection.disconnect();

        return new JSONArray(response.toString());
    }

    /**
     * Conecta con el servidor y devuelve un JSONObject con los datos obtenidos
     * @param direccionurl URL del servidor
     * @param parametros Parámetros de la consulta
     * @return JSONArray con los resultados de la consulta al servidor
     */
    public JSONObject getJSONObjectFromUrl(String direccionurl, HashMap<String, String> parametros) throws JSONException, IOException
    {
        URL url;
        StringBuilder response = new StringBuilder();
        url = new URL(buildURL(direccionurl, parametros));

        HttpURLConnection urlConnection  = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(15000);
        urlConnection.setConnectTimeout(15000);
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Content-type", "application/json");

        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK)
        {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null)
            {
                response.append(line);
            }

        }
        urlConnection.disconnect();

        return new JSONObject(response.toString());
    }

    /**
     * Crea una URL válida con parámetros
     * @param url URL base
     * @param params Parámetros para la URL
     * @return URL formateada con sus parámetros
     */
    private String buildURL(String url, HashMap<String, String> params)
    {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (params != null)
        {
            for (Map.Entry<String, String> entry : params.entrySet())
            {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        return builder.build().toString();
    }
}