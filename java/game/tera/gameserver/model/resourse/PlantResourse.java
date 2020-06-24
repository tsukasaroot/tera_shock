package tera.gameserver.model.resourse;

import tera.Config;
import tera.gameserver.model.playable.Player;
import tera.gameserver.model.skillengine.Formulas;
import tera.gameserver.network.serverpackets.ResourseIncreaseLevel;
import tera.gameserver.templates.ResourseTemplate;

/**
 * Модель ресурса растений.
 *
 * @author Ronn
 */
public class PlantResourse extends ResourseInstance
{
	public PlantResourse(int objectId, ResourseTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public int getChanceFor(Player player)
	{
		// получаем формулы
		Formulas formulas = Formulas.getInstance();

		// рассчитываем шанс сбора
		return formulas.getChanceCollect(getTemplate().getReq(), player.getPlantLevel());
	}

	@Override
	public void increaseReq(Player player)
	{
		if(player.getPlantLevel() >= Config.WORLD_MAX_COLLECT_LEVEL)
			return;

		// увеличиваем уровень сбора
		player.setPlantLevel(player.getPlantLevel() + 1);

		if (player.getEnergyLevel() >= 4)
			player.sendMessage("You have reached max level for "+ getTemplate().getType() +"! Here a git!");

		// отправляю сообщение
		player.sendPacket(ResourseIncreaseLevel.getInstance(getTemplate().getType(), player.getPlantLevel(), player), true);

		// обновляю статы
		player.updateInfo();
	}
}
