package com.example.fortourism.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("brf")
    public String level;

    @SerializedName("txt")
    public String suggestionInfo;

    public String type;
}
