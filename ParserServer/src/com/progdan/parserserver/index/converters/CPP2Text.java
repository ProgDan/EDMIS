package com.progdan.parserserver.index.converters;

import com.progdan.logengine.Logger;
import java.io.File;

public class CPP2Text extends Converter{
    private static Logger logger = Logger.getLogger(TXT2Text.class.getName());
    public CPP2Text(String reppath, String cmdpath) {
        super(reppath, cmdpath);
        logger.debug(">>> Start of CPP2Text.TXT2Text()***");
        logger.debug("<<< End of CPP2Text.TXT2Text()***");
    }
    public void convertFile(String id) {
        logger.debug(">>> Start of CPP2Text.convertFile()***");
        File source = new File(reppath, id + ".cpp");
        File target = new File(reppath + System.getProperty("file.separator") + "body", id + ".txt");
        source.renameTo(target);
        source.delete();
        logger.debug("<<< End of CPP2Text.convertFile()***");
    }
}
