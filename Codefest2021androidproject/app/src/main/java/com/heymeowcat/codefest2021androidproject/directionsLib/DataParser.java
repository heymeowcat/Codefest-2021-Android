package com.heymeowcat.codefest2021androidproject.directionsLib;

import com.google.android.gms.maps.model.LatLng;
import com.heymeowcat.codefest2021androidproject.pojo.MapDistanceObj;
import com.heymeowcat.codefest2021androidproject.pojo.MapTimeObj;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vishal on 10/20/2018.
 */

public class DataParser {

    public MapDistanceObj distanceParse(JSONObject jObject){
        JSONArray jRoutes;
        JSONArray jLegs;


        try {
            jRoutes = jObject.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                int distanceValue  = (Integer) ((JSONObject) ((JSONObject) jLegs.get(i)).get("distance")).get("value");
                String distanceText = (String) ((JSONObject) ((JSONObject) jLegs.get(i)).get("distance")).get("text");

                return  new MapDistanceObj(distanceValue,distanceText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MapTimeObj timeParse(JSONObject jObject){
        JSONArray jRoutes;
        JSONArray jLegs;


        try {
            jRoutes = jObject.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                int durationValue  = (Integer) ((JSONObject) ((JSONObject) jLegs.get(i)).get("duration")).get("value");
                String durationText = (String) ((JSONObject) ((JSONObject) jLegs.get(i)).get("duration")).get("text");

                return new MapTimeObj(durationValue,durationText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {

        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        JSONArray jDistance;


        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();


                String distance = "";
                String duration = "";
                distance = (String) ((JSONObject) ((JSONObject) jLegs.get(i)).get("distance")).get("text");
                duration = (String) ((JSONObject) ((JSONObject) jLegs.get(i)).get("duration")).get("text");
                System.out.println("aMap distance - > "+distance);
                System.out.println("aMap time - > "+duration);



                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");


                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");


                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);


                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }


    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}