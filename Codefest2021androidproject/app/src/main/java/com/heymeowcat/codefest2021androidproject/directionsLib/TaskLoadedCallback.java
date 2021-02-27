package com.heymeowcat.codefest2021androidproject.directionsLib;


import com.heymeowcat.codefest2021androidproject.pojo.MapDistanceObj;
import com.heymeowcat.codefest2021androidproject.pojo.MapTimeObj;

/**
 * Created by Vishal on 10/20/2018.
 */

public interface TaskLoadedCallback {
    void onTaskDone(Object... values);
    void onDistanceTaskDone(MapDistanceObj distance);
    void onTimeTaskDone(MapTimeObj time);
}
