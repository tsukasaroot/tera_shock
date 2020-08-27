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
				case 7:
					break;
				case 6:
					break;
				case 5:
					break;
				case 4:
					break;
				case 3:
					break;
				case 2:
					break;
				case 1:
					break;
				case 0:
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