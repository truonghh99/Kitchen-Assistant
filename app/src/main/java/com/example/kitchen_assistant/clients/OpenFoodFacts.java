package com.example.kitchen_assistant.clients;

import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.kitchen_assistant.models.Product;

import okhttp3.Headers;

public class OpenFoodFacts {

    public static String TAG = "OpenFoodFacts";

    public static String GET_PRODUCT_INFO_URL = "https://us.openfoodfacts.org/api/v0/product/";
    public static String HEADER = "KitchenAssistant - Android - Version 1.0 - https://github.com/truonghh99/Kitchen-Assistant/blob/master/README.md";

    public static Product getProductInfo(String productCode) {
        Product product = new Product();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String url = GET_PRODUCT_INFO_URL + productCode;
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, json.toString());
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });

        return product;
    }
}
