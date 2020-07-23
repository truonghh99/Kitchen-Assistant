package com.example.kitchen_assistant.applications;

import android.app.Application;

import com.example.kitchen_assistant.models.FoodItem;
import com.example.kitchen_assistant.models.Ingredient;
import com.example.kitchen_assistant.models.Product;
import com.example.kitchen_assistant.models.Rating;
import com.example.kitchen_assistant.models.Recipe;
import com.example.kitchen_assistant.models.Review;
import com.example.kitchen_assistant.models.ShoppingItem;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Product.class);
        ParseObject.registerSubclass(Recipe.class);
        ParseObject.registerSubclass(FoodItem.class);
        ParseObject.registerSubclass(ShoppingItem.class);
        ParseObject.registerSubclass(Ingredient.class);
        ParseObject.registerSubclass(Rating.class);
        ParseObject.registerSubclass(Review.class);

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("kitchen-assistant") // should correspond to APP_ID env variable
                .clientKey("kitchen-assistant-hatruong99")  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server("https://kitchen-assistant-hatruong99.herokuapp.com/parse/").build());
    }
}