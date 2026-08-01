package name.modid;

import net.fabricmc.api.ModInitializer;
import name.modid.base.Client; // Импортируем наш клиент
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		// Инициализируем наш чит-базис
		Client.INSTANCE.init();
	}
}