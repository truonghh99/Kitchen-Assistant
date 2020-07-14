package com.example.kitchen_assistant.clients;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.kitchen_assistant.models.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenFoodFacts {

    public static String TAG = "OpenFoodFacts";

    public static String GET_PRODUCT_INFO_URL = "https://us.openfoodfacts.org/api/v0/product/";
    public static String HEADER = "KitchenAssistant - Android - Version 1.0 - https://github.com/truonghh99/Kitchen-Assistant/blob/master/README.md";
    private static Product product;

    public static Product getProductInfo(String productCode) throws IOException, InterruptedException {
        Log.e(TAG, "Start querying product info " + productCode);
        product = new Product();
        String url = GET_PRODUCT_INFO_URL + productCode;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("UserAgent", HEADER)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            Log.e(TAG, "Successfully extracted");
                            String jsonData = response.body().string();
                            Log.e(TAG, jsonData);
                            try {
                                JSONObject jsonObject = new JSONObject(jsonData);
                                product = new Product(jsonObject);
                                Log.e(TAG, "Created new product object");
                                Log.e(TAG, jsonObject.toString());
                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        while (product.getProductName() == null) {
            Thread.currentThread().sleep(10);
        }
        return product;
    }
}
