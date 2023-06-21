package com.bamisu.gamelib.entities;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

import java.lang.reflect.Method;
import java.util.Map;

public class TokenResourcePackage extends ResourcePackage { //Resource
    private String id;
    private Object value;

    public TokenResourcePackage(){}

    public TokenResourcePackage(String id, Object value){
        this.id = id;
        this.value = value;
    }

    public TokenResourcePackage(TokenResourcePackage resource) {
        this.value = resource.value;
        this.id = resource.id;
    }
    public SFSObject toSFSObject(SFSObject sfsObject){
        sfsObject.putUtfString(Params.MONEY_TYPE, id);
        putSFSObjectData(sfsObject, Params.AMOUNT, this.value);
        return sfsObject;
    }

    public SFSObject toSFSObject2(){
        SFSObject sfsObject = new SFSObject();
        sfsObject.putUtfString(Params.ID, id);
        putSFSObjectData(sfsObject, Params.AMOUNT, this.value);
        return sfsObject;
    }

    public ISFSObject toSFSObjects(){
        SFSObject sfsObject = new SFSObject();
        sfsObject.putUtfString(Params.MONEY_TYPE, id);
        putSFSObjectData(sfsObject, Params.AMOUNT, this.value);
        return sfsObject;
    }

    public SFSObject toSFSObject(Map<String, Object> map) {
        SFSObject object = new SFSObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            putSFSObjectData(object, key, value);
        }

        return toSFSObject(object);
    }


    public TokenResourcePackage cloneNew() {
        return new TokenResourcePackage(this.id, this.value);
    }

    public static void putSFSObjectData(SFSObject object, String key, Object value) {
        try {
            Method method = null;
            String dataType = value.getClass().getSimpleName().toLowerCase();
            switch (dataType) {
                case "integer":
                    method = object.getClass().getMethod("putInt", String.class, int.class);
                    break;
                case "long":
                    method = object.getClass().getMethod("putLong", String.class, long.class);
                    break;
                case "double":
                    method = object.getClass().getMethod("putDouble", String.class, double.class);
                    break;
                case "string":
                    method = object.getClass().getMethod("putText", String.class, String.class);
                    break;
                case "float":
                    method = object.getClass().getMethod("putFloat", String.class, float.class);
                    break;
                case "byte":
                    method = object.getClass().getMethod("putByte", String.class, byte.class);
                    break;
                case "boolean":
                    method = object.getClass().getMethod("putBool", String.class, boolean.class);
                    break;
            }

            if (method != null) {
                method.invoke(object, key, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String readId() {
        return id;
    }

    @Override
    public String readHash() {
        return null;
    }

    @Override
    public int readAmount() {
        return 0;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getId() {
        return this.id;
    }
}
