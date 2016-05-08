package com.daalitoy.apps.tcp.tls;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

/**
 * Created by ragha on 5/8/2016.
 */
public class Hex {

    private static HexBinaryAdapter adapter=new HexBinaryAdapter();

    public static String toString(byte[] data){
        return adapter.marshal(data);

    }

    public static byte[] fromString(String s){
        return adapter.unmarshal(s);
    }
}
