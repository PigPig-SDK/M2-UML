package org.umlproject.UI;

import com.google.gson.Gson;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GuiClassCosmeticTable {
    
    private record Pair<A, B>(A first, B second) {}
    
    //<Classname, Image_location>
    public HashMap<String, GuiClassCosmetic> cosmetics;
    transient public List<Pair<Pattern, GuiClassCosmetic>> cosmeticsMapper;
    
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
        
        //Compile all regex strings.
        cosmeticsMapper = new ArrayList<>();
        for(String regexString : this.cosmetics.keySet())
        {
            this.cosmeticsMapper.add(new Pair(Pattern.compile(regexString), this.cosmetics.get(regexString)));
        }
    }
    
    public static GuiClassCosmeticTable getInstance()
    {
        if(instance == null)
        {
            instance = new GuiClassCosmeticTable("/org/umlproject/cosmetics.json");
        }
        return instance;
    }
    
    public static GuiClassCosmetic getCosmetic(String classname)
    {
        classname = classname.toLowerCase().strip();
        for(Pair<Pattern, GuiClassCosmetic> pair : getInstance().cosmeticsMapper)
        {
            if(pair.first().matcher(classname).matches()) return pair.second;
        }
        return null;
    }
}
