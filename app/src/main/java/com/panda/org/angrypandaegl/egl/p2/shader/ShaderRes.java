package com.panda.org.angrypandaegl.egl.p2.shader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import android.content.Context;
import android.text.TextUtils;

import com.panda.org.angrypandaegl.egl.p2.file.AssetsFile;

/**
 * Created by rd0348 on 2017/10/17 0017.
 */

public class ShaderRes {

    private static final String TAG = ShaderRes.class.getSimpleName();

    private String mMaterialName = "default.mat";
    private String mMaterialDirectory = "";
    private Properties property = new Properties();
    private Context mContext;
    private HashMap<String, String> mMaterialUniformMap = new HashMap<String, String>();

    class Key {
        public static final String VERTEX_FILE = "VertexFile";
        public static final String FRAGMENT_FILE = "FragmentFile";
        public static final String VERSION = "Version";
        public static final String UNIFORM = "Uniform";
    }

    /**
     * Initialize the MaterialAid object
     * <p>
     * the default material file will not be changed until we invoke setMaterialName
     *
     * @param context
     */
    public ShaderRes(Context context) {
        mContext = context;
//        try {
//            property.load(AssetsFile.getInputStreamFromAsset(mContext, mMaterialName));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * set the material file we want to use
     *
     * @param name
     */
    public void setMaterialName(String name) {
        mMaterialName = name;
        try {
            property.load(AssetsFile.getInputStreamFromAsset(mContext, mMaterialName));
            int lastSep = name.lastIndexOf(File.separator);
            if (lastSep != -1) {
                mMaterialDirectory = name.substring(0, lastSep);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the material file name under assets
     *
     * @return
     */
    public String getMaterialName() {
        return mMaterialName;
    }

    /**
     * get the vertex shader file name in the material file
     *
     * @return
     */
    public String getMaterialVertexName() {
        String vertexFile = property.getProperty(Key.VERTEX_FILE);
        if (TextUtils.isEmpty(mMaterialDirectory)) {
            return vertexFile;
        }
        return mMaterialDirectory + File.separator + vertexFile;
    }

    /**
     * get the fragment shader file name in the material file
     *
     * @return
     */
    public String getMaterialFragmentName() {
        String fragmentFile = property.getProperty(Key.FRAGMENT_FILE);
        if (TextUtils.isEmpty(mMaterialDirectory)) {
            return fragmentFile;
        }
        return mMaterialDirectory + File.separator + fragmentFile;
    }

    /**
     * get the version of the material file
     *
     * @return
     */
    public String getMaterialVersion() {
        String version = property.getProperty(Key.VERSION);
        return version;
    }

    public void setMaterialProperty(String key, float value) {
        if (key.startsWith("u")) {// uniform
            mMaterialUniformMap.put(key, String.valueOf(value));
        } else if (key.startsWith("a")) {// attribute

        }
    }

    public HashMap<String, String> getMaterialUniformMapFromClient() {
        return mMaterialUniformMap;
    }

    public HashMap<String, String> getMaterialUniformKeyAndType() {
        HashMap<String, String> result = new HashMap<String, String>();
        String[] typeKeys = getMaterialUniforms();
        if (typeKeys.length == 0) {
            return null;
        }
        for (int i = 0; i < typeKeys.length; i++) {
            String typeKey = typeKeys[i];
            String[] typeAndKey = typeKey.split("_");
            String type = typeAndKey[0];
            String key = typeAndKey[1];
            result.put(key, type);
        }
        return result;
    }

    public String[] getMaterialUniforms() {
        String uniform = getMaterialUniformFromFile();
        String[] typeKey = uniform.split(",");
        return typeKey;
    }

    public String getMaterialUniformFromFile() {
        String uniform = property.getProperty(Key.UNIFORM);
        return uniform;
    }
}
