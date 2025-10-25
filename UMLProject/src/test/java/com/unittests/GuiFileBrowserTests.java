package com.unittests;

import org.junit.jupiter.api.*;
import org.umlproject.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import org.umlproject.UI.GuiFileBrowser;

public class GuiFileBrowserTests {
    
    @Test
    void removeFileExtension_RemovesSimpleExtension() {
        //arange
        String file = "test";
        String fileWithExt = file + UMLDocument.FILEEXTENT_STRING;
        //act
        String newFileExt =  GuiFileBrowser.removeFileExtension(fileWithExt);
        //assert
        assertEquals(file, newFileExt);
    }
    @Test
    void removeFileExtension_NoFileExt() {
        //arange
        String file = "test";
        //act
        String newFileExt =  GuiFileBrowser.removeFileExtension(file);
        //assert
        assertEquals(file, newFileExt);
    }
    @Test
    void removeFileExtension_SmallString() {
        //arange
        String file = "t";
        //act
        String newFileExt =  GuiFileBrowser.removeFileExtension(file);
        //assert
        assertEquals(file, newFileExt);
    }
    @Test
    void removeFileExtension_OnlyExt() {
        //arange
        String file = ".json";
        //act
        String newFileExt =  GuiFileBrowser.removeFileExtension(file);
        //assert
        assertEquals(file, newFileExt);
    }
    @Test
    void removeFileExtension_NestedExt() {
        //arange
        String file = "WhatThe.json";
        String fileWithExt = file + UMLDocument.FILEEXTENT_STRING;
        //act
        String newFileExt =  GuiFileBrowser.removeFileExtension(fileWithExt);
        //assert
        assertEquals(file, newFileExt);
    }
}
