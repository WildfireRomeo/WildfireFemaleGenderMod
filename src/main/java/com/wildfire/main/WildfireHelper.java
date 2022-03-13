package com.wildfire.main;
/*
Wildfire's Female Gender Mod is a female gender mod created for Minecraft.
Copyright (C) 2022  WildfireRomeo

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WildfireHelper {

    public static final String SYNC_URL = "https://wildfiremod.tk";

    public class Obfuscation {

        //NetworkPlayerInfo.java
        public static final String NETWORK_PLAYER_INFO = "field_175157_a";
        public static final String PLAYER_TEXTURES = "field_187107_a";
        public static final String LAYER_RENDERERS = "field_177097_h";
    }


    public static String post(String urlThing, List<NameValuePair> urlParameters) throws IOException, InterruptedException, KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
        //BIG THANKS TO FirstPersonModel for this fix. I spent ages trying to figure this out.
        //https://github.com/tr7zw/FirstPersonModel/blob/2e3da6fca5f2a5290b3509f350e5cca1db3b4218/Core/src/main/java/dev/tr7zw/firstperson/sync/SyncManager.java#L116

        if(WildfireGender.SYNCING_ENABLED) {
            try (CloseableHttpClient client = HttpClientBuilder.create().setSslcontext(new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    return true;
                }
            }).build()).build()) {

                String request = SYNC_URL + "/" + urlThing;

                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(request);

                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    // writing error to Log
                    e.printStackTrace();
                }
                CloseableHttpResponse httpResponse = client.execute(httpPost);
                BufferedReader in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            }
        } else {
            return "";
        }
    }

    public static int randInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    public static float randFloat(float min, float max) {
        return (float) ThreadLocalRandom.current().nextDouble((double) min, (double) max + 1);
    }
}
