# KITCHEN ASSISTANT

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
A mobile app that keeps track of your food to recommend recipes, minimize number of grocery trips, and keep your kitchen organized  

### App Evaluation

- **Category:** Tracking
- **Mobile:** 
    * Kitchen Assistant allows user to scan all food products with their phone and organize their whole kitchen just with a tap each meal. It can also generate shopping list item for user to plan their grocery trip ahead of time and notify them of products that need to be restocked
- **Story:** 
    * As the pandemic is becoming a new normal, Kitchen Assistant helps answer 3 most common questions within everyone's kitchens: how to manage a huge food stock, what to cook with a limited source of ingredients, and when do we really need to go out for food. 
- **Market:**
    * The app targets every homecook (especially new homecook) with a smartphone, which is most of adults in this challenging time
- **Habit:** 
    * The app itself is basically habit-based: user scans their food after each trip, gets suggested reipes for each meal, and modifies their food status when needed. All the work is closely connected to their normal kitchen activities, and therefore can easily become a part of users' daily routine.
- **Scope:** 
    * The idea itself is quite straightforward and tracking-centered, but it can also be expanded to a social platform where users share their recipes and cooking tips.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* [x] Allow user to register, login and logout
* [x] Be able to read food information via barcode
* [x] Be able to store food list of each user
* [x] Be able to store recipe list of each user
* [x] Be able to store shopping list of each user
* [x] Allow user to update their current food (add, delete, edit)
* [x] Allow user to update their recipes (add, delete, edit)
* [x] Allow user to update their shopping list items (add, delete, edit)
* [x] Automatically update food status over time and after each input meal
* [x] Support converting current food to shopping list item 
* [x] Support converting recipe's ingredient to shopping list item
* [x] Be able to suggest recipes based on selected food item
* [x] Be able to suggest recipes based on current food list

**Optional Nice-to-have Stories**

* [x] Allow user to search for specific current food
* [x] Allow user to search for specific recipe
* [ ] Allow user to change product/recipe main image
* [ ] Allow user to rate a recipe
* [ ] Allow user to review a recipe
* [x] Display average rating of each recipe
* [ ] Display all reviews of a recipe
* Convert user setting account to a social, interactable account (with avatar, name, description, and the ability to follow/block)
* Have a newsfeed screen for users to share their recipes & food-related posts
* Allow users to like, comment, and share newsfeed post
* Allow users to add other's recipes to their recipe list
* [x] Get suggested recipe from external databse
* Allow users to login using Facebook & Google account
* Have a daily calorie tracking screen

### 2. Screen Archetypes

* Login
    * Allow user to register, login and logout
* Current food
    * Be able to store food list of each user
    * Automatically update food status over time and after each input meal
    * Be able to suggest recipes based on current food list
    * Allow user to search for specific current food
* Food detail
    * Be able to store food list of each user
    * Support converting current food to shopping list item
    * Be able to suggest recipes based on selected food item 
* Food scanner
    * Be able to read food information via QR code
* New food detail
    * Be able to read food information via QR code
    * Allow user to update their current food (add, delete, edit)
* Recipes
    * Be able to store recipe list of each user
    * Allow user to update their recipes (add, delete, edit)
    * Allow user to search for specific recipe
* Add recipe
    * Allow user to update their recipes (add, delete, edit)
* Recipe detail
    * Allow user to update their recipes (add, delete, edit)
    * Support converting recipe's ingredient to shopping list item
* Shopping list
    * Be able to store shopping list of each user
    * Allow user to update their shopping list items (add, delete, edit)
* Shopping item detail
    * Allow user to update their shopping list items (add, delete, edit)

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Current food
* Recipes
* Shopping list

**Flow Navigation** (Screen to Screen)

* Current food
   * Food scanner
   * Food detail
* Food scanner
    * New food detail
* Food deatil
    * Shopping item detail
* Recipes
    * Add recipe
    * Recipe detail
* Shopping list
    * Shopping item detail

## Wireframes
<img src="https://github.com/truonghh99/Kitchen-Assistant/blob/master/Wireframes%202.png" width=600>

## Schema & Action plan: 

https://docs.google.com/document/d/16sKpI7_NuL9bB57lofKlBzqmXrVLnfgtCZSrUolncIQ/edit?usp=sharing

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype
