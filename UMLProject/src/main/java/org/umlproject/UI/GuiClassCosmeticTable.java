package org.umlproject.UI;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class GuiClassCosmeticTable {
    
    //<Classname, Image_location>
    public HashMap<String, GuiClassCosmetic> cosmetics;
    
    private static GuiClassCosmeticTable instance = null;
    
    private GuiClassCosmeticTable(){}
    
    private GuiClassCosmeticTable(String path)
    {
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            throw new RuntimeException("Failed to load cosmetics.json");
        }

        Gson gson = new Gson();

        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);

        GuiClassCosmeticTable table = gson.fromJson(reader, GuiClassCosmeticTable.class);
        this.cosmetics = table.cosmetics;
    }
    
    public static GuiClassCosmeticTable getInstance()
    {
        if(instance == null)
        {
            instance = new GuiClassCosmeticTable("/org/umlproject/cosmetics.json");
        }
        return instance;
    }
}
