package com.example.kitchen_assistant.clients;

import android.util.Log;

import com.example.kitchen_assistant.models.Product;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenFoodFacts {

    public static String TAG = "OpenFoodFacts";
    public static String GET_PRODUCT_INFO_URL = "https://us.openfoodfacts.org/api/v0/product/";
    public static String HEADER = "KitchenAssistant - Android - Version 1.0 - https://github.com/truonghh99/Kitchen-Assistant/blob/master/README.md";
    private static Product product;

    public static Product getProductInfo(String productCode) throws IOException, InterruptedException {
        Log.i(TAG, "Start querying product info. Product code: " + productCode);
        product = new Product();
        String url = GET_PRODUCT_INFO_URL + productCode;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("UserAgent", HEADER)
                .build();

        // Request product info from OpenFoodFacts GET API
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
                            String jsonData = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(jsonData);
                                product = new Product(jsonObject);
                                Log.i(TAG, "Successfully extracted product info");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        // Wait for async request to complete
        while (product.getProductName() == null) {
            Thread.currentThread().sleep(10);
        }

        return product;
    }
}
