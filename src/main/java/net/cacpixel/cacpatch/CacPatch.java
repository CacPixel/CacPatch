package net.cacpixel.cacpatch;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;


@Mod(modid = CacPatch.MODID, name = CacPatch.NAME, version = CacPatch.VERSION, dependencies = CacPatch.DEPENDENCIES)
public class CacPatch
{
    public static final String MODID = "cacpatch";
    public static final String NAME = "CacPatch";
    public static final String VERSION = "0.1";
    public static final String DEPENDENCIES = "required-after:rtm;required:ngtlib";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        logger.info("#############################");
        logger.info(NAME + " is Starting!");
        logger.info("#############################");
    }
}
