package tera.gameserver.network.serverpackets;

import com.google.common.base.Objects;
import rlib.logging.Loggers;
import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;
import tera.gameserver.model.resourse.ResourseType;
import tera.gameserver.network.ServerPacketType;

import tera.gameserver.model.playable.Player;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.tables.ItemTable;
import tera.Config;

import java.nio.ByteBuffer;


/**
 * Серверный пакет с увеличением уровня сбора ресурсов.
 * 
 * @author Ronn
 */
public class ResourseIncreaseLevel extends ServerPacket
{
	private static final ServerPacket instance = new ResourseIncreaseLevel();

	public static ResourseIncreaseLevel getInstance(ResourseType type, int level, Player player)
	{
		ItemTable itemTable = ItemTable.getInstance();
		ResourseIncreaseLevel packet = (ResourseIncreaseLevel) instance.newInstance();

		packet.level = level;
		packet.type = type;

		if (level == Config.WORLD_MAX_COLLECT_LEVEL) {
			ItemTemplate template = null;
			if (type.name() == "MINING")
				template = itemTable.getItem(50066);
			else if (type.name() == "PLANT")
				template = itemTable.getItem(50051);
			else if (type.name() == "ENERGY")
				template = itemTable.getItem(50010);

			ItemInstance newItem = template.newInstance();

			newItem.setAutor("GM_Create_Item");

			Inventory inventory = player.getInventory();

			if(inventory.putItem(newItem))
			{
				// получаем менеджера событий
				ObjectEventManager eventManager = ObjectEventManager.getInstance();

				// обновляемся
				eventManager.notifyInventoryChanged(player);
			}

			player.sendMessage("You have reached max level " + level + " for " + type + "! Here a gift!");
		}

		return packet;
	}
	
	/** тип ресурса */
	private ResourseType type;
	
	/** уровень навыка */
	private int level;
	
	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.RESOURSE_INCREASE_LEVEL;
	}
	
	@Override
	public boolean isSynchronized()
	{
		return false;
	}

	@Override
	protected void writeImpl(ByteBuffer buffer)
	{
		writeOpcode(buffer);
		writeInt(buffer, type.ordinal());
		writeShort(buffer, level); //06 00 кол-во
	}
}
