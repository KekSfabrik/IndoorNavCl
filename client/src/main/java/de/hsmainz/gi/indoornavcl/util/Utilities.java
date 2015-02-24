/*
 * Copyright (C) 2015 Jan "KekS" M.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package de.hsmainz.gi.indoornavcl.util;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import de.hsmainz.gi.types.IndoorNavEntity;

import java.util.*;

/**
 package de.hsmainz.geoinform.util;

 import android.os.Bundle;
 import android.os.Parcel;
 import android.os.Parcelable;

 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;

 /**
 * Helper class with static methods to be called from the outside.
 *
 * @author  KekS (mailto:keks@keksfabrik.eu), 22.10.2014
 */
public class Utilities {

    public static final Random R = new Random();

    //----------------------------------------------------
    //                  Parceling Maps
    //----------------------------------------------------
    /*
        // read map into a HashMap<String,SomeClass>
        myMap = readMap(parcel, SomeClass.class);

        // or
        myMap = new SomeMap<String, SomeClass>;
        readMap(myMap, parcel, SomeClass.class);

        // write map out
        writeMap(myMap, parcel);
     */
    /**
     * Reads a {@link java.util.Map} from a {@link android.os.Parcel} that was stored using a
     * {@link String}[] and a {@link android.os.Bundle}.
     *
     * @param   in      the {@link android.os.Parcel} to retrieve the map from
     * @param   type    the class of the {@link java.util.Map}s Objects
     * @return  a {@link java.util.Map} containing the items retrieved from the parcel
     */
    public static <V extends Parcelable> Map<String, V> readMap(Parcel in, Class<? extends V> type) {
        Map<String, V> map = new HashMap<>();
        if (in != null) {
            String[] keys = in.createStringArray();
            Bundle bundle = in.readBundle(type.getClassLoader());
            for(String key: keys) {
                map.put(key, type.cast(bundle.getParcelable(key)));
            }
        }
        return map;
    }


    /**
     * Reads into an existing Map from a Parcel that was stored using a String array and a Bundle.
     *
     * @param   map     the {@link java.util.Map}&lt;String,V&gt; that will receive the items from the parcel
     * @param   in      the {@link android.os.Parcel} to retrieve the map from
     * @param   type    the class of the {@link java.util.Map}s Objects
     */
    public static <V extends Parcelable> void readMap(Map<String, V> map, Parcel in, Class<V> type) {
        if (map != null) {
            map.clear();
            if (in != null) {
                String[] keys = in.createStringArray();
                Bundle bundle = in.readBundle(type.getClassLoader());
                for (String key: keys) {
                    map.put(key, type.cast(bundle.getParcelable(key)));
                }
            }
        }
    }


    /**
     * Writes a {@link java.util.Map} to a {@link android.os.Parcel} using a String array and a
     * {@link android.os.Bundle}.
     *
     * @param   map     the {@link java.util.Map}&lt;String,V&gt; to store in the parcel
     * @param   out     the {@link android.os.Parcel} to store the map in
     */
    public static void writeMap(Map<String, ? extends Parcelable> map, Parcel out) {
        if (map != null && map.size() > 0) {
            Set<String> keySet = map.keySet();
            Bundle b = new Bundle();
            for(String key: keySet) {
                b.putParcelable(key, map.get(key));
            }
            String[] arr = keySet.toArray(new String[keySet.size()]);
            out.writeStringArray(arr);
            out.writeBundle(b);
        } else {
            out.writeStringArray(new String[0]);
            out.writeBundle(Bundle.EMPTY);
        }
    }

    /**
     * Attempt to cast an Object to a String and parse it as an Integer
     * @param   o   the Object to try to parse as Integer
     * @return  The parsed Integer or null
     */
    public static Integer tryParseInt(Object o) {
        try {
            return Integer.parseInt(o.toString());
        } catch (NumberFormatException ex) {
            Log.d("UTIL", "Couldn't parse as Integer", ex);
            return null;
        }
    }

    /**
     * Check through a {@link java.util.Collection} of {@link de.hsmainz.gi.types.IndoorNavEntity}s
     * and returns whether all all of their {@link de.hsmainz.gi.types.IndoorNavEntity#isVerified}
     * methods are {@link java.lang.Boolean#TRUE}.
     *
     * @param   entities    a Collection of IndoorNavEntities
     * @return  whether all entities are verified
     */
    public static boolean areAllVerified(Collection<? extends IndoorNavEntity> entities) {
        boolean verified = true;
        for (IndoorNavEntity entity: entities) {
            verified = verified && entity.isVerified();
        }
        return verified;
    }

    /**
     * Get a {@link java.util.Collection} of the same Type as the input-{@link java.util.Collection} where all
     * {@link de.hsmainz.gi.types.IndoorNavEntity} elements are verified or not as specified by
     * the second argument. Warning: does not work for {@link java.util.ArrayList}s generated with {@link java.util.Arrays#asList}
     * or any of the {@link java.util.Collections} specials such as {@link java.util.Collections#synchronizedList} or
     * {@link java.util.Collections#unmodifiableSet} (throws {@link java.lang.NoSuchMethodException}).
     * @param   entities            the collection to filter through
     * @param   shouldBeVerified    whether the output should contain the elements whose
     *          {@link de.hsmainz.gi.types.IndoorNavEntity#isVerified} returned true or false
     * @param   <T>                 the generic type of the entities
     * @return  all elements that satisfy the shouldBeVerified argument
     */
    public static <T extends Collection<U>, U extends IndoorNavEntity> T getAllVerified(T entities, boolean shouldBeVerified) throws IllegalAccessException, InstantiationException {
        T output = (T) entities.getClass().newInstance();
        for (U entity: entities) {
            if (entity.isVerified() == shouldBeVerified) {
                output.add(entity);
            }
        }
        return output;
    }
}
