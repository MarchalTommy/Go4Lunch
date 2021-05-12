package com.aki.go4lunch.events;

import com.aki.go4lunch.models.ResultDetails;

public class FromSearchToFragment {

    public ResultDetails result = new ResultDetails();

    public FromSearchToFragment(ResultDetails place) {
        result = place;
    }
}
