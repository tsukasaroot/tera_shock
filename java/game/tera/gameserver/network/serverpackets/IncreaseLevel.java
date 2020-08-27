package tera.gameserver.network.serverpackets;

import tera.gameserver.manager.ObjectEventManager;
import tera.gameserver.model.playable.Player;
import tera.gameserver.network.ServerPacketType;

import tera.gameserver.tables.ItemTable;
import tera.gameserver.templates.ItemTemplate;
import tera.gameserver.model.inventory.Inventory;
import tera.gameserver.model.items.ItemInstance;

/**
 * Серверный пакет, описыающий обновление уровня игрока.
 *
 * @author Ronn
 */
public class IncreaseLevel extends ServerPacket
{
	private static final ServerPacket instance = new IncreaseLevel();

	public static IncreaseLevel getInstance(Player player)
	{
		ItemTable itemTable = ItemTable.getInstance();
		IncreaseLevel packet = (IncreaseLevel) instance.newInstance();
		ItemTemplate template = null;

		packet.objectId = player.getObjectId();
		packet.subId = player.getSubId();
		packet.level = player.getLevel();
		int classId = player.getClassId();

		if (player.getLevel() == 60 || player.getLevel() == 2) {
			player.sendMessage("You reached lvl " + player.getLevel() + "! Here some starting equipment ! " + classId);
			switch (classId) {
				case 7: // mystic
					template = itemTable.getItem(10400);
					break;
				case 6: // priest
					template = itemTable.getItem(13260);
					break;
				case 5: // archer
					template = itemTable.getItem(10494);
					break;
				case 4: // sork
					template = itemTable.getItem(10493);
					break;
				case 3: // berserk
					template = itemTable.getItem(13217);
					break;
				case 2: // slayer
					template = itemTable.getItem(10267);
					break;
				case 1: // lancer
					template = itemTable.getItem(13303);
					break;
				case 0: // warrior
					template = itemTable.getItem(10489);
					break;
			}
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
		}

		return packet;
	}

	/** ид игрока */
	private int objectId;
	/** под ид игрока */
	private int subId;
	/** уровень игрока */
	private int level;

	@Override
	public ServerPacketType getPacketType()
	{
		return ServerPacketType.PLAYER_INCREASE_LEVEL;
	}

	@Override
	protected final void writeImpl()
	{
		writeOpcode();
		writeInt(objectId); // наш ИД
		writeInt(subId);
		writeInt(level); // преобретаемый лвл
	}
}